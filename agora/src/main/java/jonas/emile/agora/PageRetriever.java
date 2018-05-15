package jonas.emile.agora;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import com.navispeed.greg.common.ReceiveArray;
import com.navispeed.greg.common.ReceiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * PageRetriever manages scrolling in a ScrollView displaying paged information in a ViewGroup.
 * - Old information is fetched automatically when scrolling.
 * - To fetch new information, call getNewEntries.
 * - You don't need to fetch new information right after creating the PageRetriever object.
 * - The ScrollView and ViewGroup passed to the constructor belong to the PageRetriever object.
 * If you wish to access them from outside, you should synchronize on the PageRetriever object.
 * - The ScrollView should have one and only one child.
 */
public class PageRetriever {

    private int pageSize;
    private int lastFetchedIndex = -1;
    private int lastTotalSize = pageSize;
    private ScrollView scrollView;
    private ViewGroup layout;

    private GetEntryCount getCount;
    private GetEntries getEntries;
    private AddToView addToView;

    public PageRetriever(int pageSize, ScrollView scrollView, ViewGroup layout,
                         GetEntryCount getCount, GetEntries getEntries, AddToView addToView) {
        this.pageSize = pageSize;
        this.scrollView = scrollView;
        this.layout = layout;
        this.getCount = getCount;
        this.getEntries = getEntries;
        this.addToView = addToView;
        synchronized (this) {
            init();
            getFirstPage();
        }
    }

    private void init() {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                synchronized (this) {
                    if (scrollView.getScrollY() == 0) {
                        final int prevSize = scrollView.getChildAt(0).getMeasuredHeight();
                        getNextPage(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.post(new Runnable() {
                                    public void run() {
                                        final int newSize = scrollView.getChildAt(0).getMeasuredHeight();
                                        scrollView.scrollTo(0, newSize - prevSize);
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }

    public synchronized void getNewEntries(final Runnable retrievalEndAction) {
        getCount.get(new ReceiveData() {
            @Override
            public void onReceiveData(String data) {
                int nbPosts = Integer.parseInt(data);
                getNewEntries(nbPosts, retrievalEndAction);
            }
        });
    }

    private void getFirstPage() {
        getNextPage(new Runnable() {
            @Override
            public void run() {
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
    }

    private void getNextPage(final Runnable retrievalEndAction) {
        getCount.get(new ReceiveData() {
            @Override
            public void onReceiveData(String data) {
                final int nbPosts = Integer.parseInt(data);
                // get next page of old posts
                if (nbPosts > lastFetchedIndex + 1) {
                    int pageToGet = (lastFetchedIndex + 1) / pageSize;
                    getPosts(pageToGet, -1, nbPosts, retrievalEndAction);
                } else if (retrievalEndAction != null) {
                    retrievalEndAction.run();
                }
            }
        });
    }

    private void getNewEntries(int nbPosts, final Runnable retrievalEndAction) {
        int nbNewPosts = nbPosts - lastTotalSize;
        if (lastFetchedIndex != -1 && lastTotalSize < nbPosts) {
            getPosts(0, nbNewPosts, nbPosts, retrievalEndAction);
        } else if (retrievalEndAction != null) {
            retrievalEndAction.run();
        }
    }

    private void getPosts(final int pageNb, final int newPosts, final int totalSize, final Runnable retrievalEndAction) {
        getEntries.get(pageNb, pageSize, new ReceiveArray() {
                    @Override
                    public void onReceiveData(JSONArray data) {
                        boolean firstLoad = false;
                        if (lastFetchedIndex == -1) {
                            firstLoad = true;
                            layout.removeAllViews();
                        }
                        addPosts(data, newPosts);
                        lastFetchedIndex += data.length();
                        lastTotalSize = totalSize;
                        if (firstLoad) {
                            // first page might be partial: get next page to have enough posts to show
                            getNextPage(retrievalEndAction);
                        } else if (retrievalEndAction != null) {
                            retrievalEndAction.run();
                        }
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
        }
        if (newPosts > 0) {
            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

    interface AddToView {
        void add(ViewGroup viewGroup, JSONObject entry, boolean addAtEnd) throws JSONException;
    }

    interface GetEntryCount {
        void get(ReceiveData handler);
    }

    interface GetEntries {
        void get(int pageNb, int pageSize, ReceiveArray handler);
    }
}
