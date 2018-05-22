package com.navispeed.greg.common.paging;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

/**
 * PageRetriever manages scrolling in a ScrollView displaying paged information in a ViewGroup.
 * - Old information is fetched automatically when scrolling.
 * - You don't need to fetch new information right after creating the PageRetriever object.
 * - The ScrollView and ViewGroup passed to the constructor belong to the PageRetriever object.
 * If you wish to access them from outside, you should synchronize on the PageRetriever object.
 * - The ScrollView should have one and only one child: the ViewGroup.
 * - Always call destroy before this object is destroyed
 */
public class PageRetriever {

    private static final int FETCH_OLD_SCROLL_POS = 20;
    private static final int ERROR_MESSAGE_SHOW_FREQ = 20000;

    private int pageSize;
    private int lastFetchedIndex = -1;
    private int lastTotalSize = pageSize;
    private Semaphore lock = new Semaphore(1);
    private Semaphore fetchOldLock = new Semaphore(1);
    private long lastTimeErrorMessageShown = 0;
    private Timer timer = null;

    private Context context;
    private ScrollView scrollView;
    private ViewGroup layout;

    private PagedService service;
    private AddToView addToView;

    public PageRetriever(Context context, int pageSize, ScrollView scrollView, ViewGroup layout,
                         PagedService service, AddToView addToView) {
        this.context = context;
        this.pageSize = pageSize;
        this.scrollView = scrollView;
        this.layout = layout;
        this.service = service;
        this.addToView = addToView;
        initScrollView();
        layout.removeAllViews();
        getFirstPage();
    }

    /**
     * Start fetching new entries automatically.
     * Don't forget to call stopAutoFetch.
     *
     * @param intervalMillis Interval of time between every fetch.
     */
    public synchronized void startAutoFetch(int intervalMillis) {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    getNewEntries(retrievalSuccessful -> {
                        if (!retrievalSuccessful) {
                            showErrorMessage(true);
                        }
                    }, false);
                }
            }, intervalMillis, intervalMillis);
        }
    }

    public synchronized void stopAutoFetch() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initScrollView() {
        scrollView.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            int oldSize = oldBottom - oldTop;
            int newSize = bottom - top;
            int reduction = oldSize - newSize;
            if (reduction > 0) {
                scrollView.setScrollY(scrollView.getScrollY() + reduction);
            }
        });
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (scrollView.getScrollY() > FETCH_OLD_SCROLL_POS)
                return;
            if (fetchOldLock.tryAcquire()) {
                if (scrollView.getScrollY() > FETCH_OLD_SCROLL_POS) {
                    fetchOldLock.release();
                    return;
                }
                new Thread(() -> {
                    try {
                        lock.acquire();
                        final int prevSize = scrollView.getChildAt(0).getMeasuredHeight();
                        getNextPage((successful) -> {
                            if (successful) {
                                scrollView.post(() -> {
                                    final int newSize = scrollView.getChildAt(0).getMeasuredHeight();
                                    scrollView.scrollTo(0, newSize - prevSize + scrollView.getScrollY());
                                    lock.release();
                                    fetchOldLock.release();
                                });
                            } else {
                                showErrorMessage(true);
                                lock.release();
                                fetchOldLock.release();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });
    }

    private void getFirstPage() {
        try {
            lock.acquire();
            getNextPage((successful) -> {
                if (successful) {
                    scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                    // first page might be partial: get a second page to have enough posts to show
                    getNextPage((successful2) -> {
                        if (successful2) {
                            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                        }
                        lock.release();
                    });
                } else {
                    lock.release();
                    showErrorMessage(false);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetch new entries that are not in the ViewGroup yet.
     *
     * @param retrievalEndAction Runnable to execute once entries are added to the ViewGroup.
     */
    public void getNewEntries(final RetrievalEndAction retrievalEndAction) {
        new Thread(() -> getNewEntries(retrievalSuccessful -> {
            if (!retrievalSuccessful) {
                showErrorMessage(false);
            }
            if (retrievalEndAction != null) {
                retrievalEndAction.run(retrievalSuccessful);
            }
        }, true)).start();
    }

    private void getNewEntries(final RetrievalEndAction retrievalEndAction, boolean force) {
        if (force) {
            try {
                lock.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        } else if (!lock.tryAcquire()) {
            return;
        }
        service.getEntryCount().accept(data -> {
            int nbPosts = data;
            getNewEntries(nbPosts, (successful) -> {
                if (retrievalEndAction != null) {
                    retrievalEndAction.run(successful);
                }
                lock.release();
            });
        }, error -> {
            if (retrievalEndAction != null) {
                retrievalEndAction.run(false);
            }
            lock.release();
        });
    }

    private void getNewEntries(int nbPosts, final RetrievalEndAction retrievalEndAction) {
        int nbNewPosts = nbPosts - lastTotalSize;
        if (lastTotalSize < nbPosts) {
            getPosts(0, nbNewPosts, nbPosts, retrievalSuccessful -> {
                if (scrollView.getScrollY() == scrollView.getChildAt(0).getMeasuredHeight() - scrollView.getMeasuredHeight()) {
                    scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                }
                if (retrievalEndAction != null) {
                    retrievalEndAction.run(retrievalSuccessful);
                }
            });
        } else if (retrievalEndAction != null) {
            retrievalEndAction.run(true);
        }
    }

    // get next page of old posts
    private void getNextPage(final RetrievalEndAction retrievalEndAction) {
        service.getEntryCount().accept(data -> {
            final int nbPosts = data;
            if (nbPosts > lastFetchedIndex + 1) {
                int pageToGet = (lastFetchedIndex + 1) / pageSize;
                getPosts(pageToGet, -1, nbPosts, retrievalEndAction);
            } else if (retrievalEndAction != null) {
                retrievalEndAction.run(true);
            }
        }, error -> {
            if (retrievalEndAction != null) {
                retrievalEndAction.run(false);
            }
        });
    }

    private void getPosts(final int pageNb, final int newPosts, final int totalSize, final RetrievalEndAction retrievalEndAction) {
        service.getEntries(pageNb, pageSize).accept(data -> {
            addPosts(data, newPosts);
            lastTotalSize = totalSize;
            if (retrievalEndAction != null) {
                retrievalEndAction.run(true);
            }
        }, error -> {
            if (retrievalEndAction != null) {
                retrievalEndAction.run(false);
            }
        });
    }

    private void addPosts(JSONArray jsonPosts, int newPosts) {
        int start;
        int end; // not included
        int inc;
        if (newPosts > 0) {
            // adding new posts
            start = newPosts - 1;
            end = -1;
            inc = -1;
        } else {
            // adding old posts
            start = (lastFetchedIndex + 1) % pageSize;
            end = jsonPosts.length();
            inc = 1;
        }
        for (int i = start; i != end; i += inc) {
            try {
                addToView.add(layout, jsonPosts.getJSONObject(i), newPosts > 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            lastFetchedIndex++;
        }
    }

    public void showNetworkErrorMessage() {
        showErrorMessage(false);
    }

    private void showErrorMessage(boolean repetitive) {
        if (!repetitive || System.currentTimeMillis() - lastTimeErrorMessageShown >= ERROR_MESSAGE_SHOW_FREQ) {
            lastTimeErrorMessageShown = System.currentTimeMillis();
            Toast t = Toast.makeText(context, "No connection", Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
        }
    }

    public interface AddToView {
        void add(ViewGroup viewGroup, JSONObject entry, boolean addAtEnd) throws JSONException;
    }

    public interface RetrievalEndAction {
        void run(boolean retrievalSuccessful);
    }
}
