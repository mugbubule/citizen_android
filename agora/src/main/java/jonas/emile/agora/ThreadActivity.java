package jonas.emile.agora;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.ReceiveArray;
import com.navispeed.greg.common.ReceiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.graphics.Color.BLACK;
import static jonas.emile.agora.AgoraModule.MODULE_PATH;

public class ThreadActivity extends AppCompatActivity {

    private static final int pageSize = 7;
    private int lastFetchedIndex = -1;
    private int lastTotalSize = pageSize;
    private String threadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        init();
    }

    private void init() {

        threadId = getIntent().getStringExtra("id");
        String threadTopic = getIntent().getStringExtra("topic");
        ((TextView) findViewById(R.id.txtPosts)).setText(getResources().getString(R.string.posts, threadTopic));
        final ScrollView scrollView = (ScrollView) findViewById(R.id.postsScrollView);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
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
        });

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

    private void getNextPage(final Runnable retrievalEndRunnable) {
        APICaller.get("threads/thread/" + threadId + "/posts/count", new ReceiveData() {
            @Override
            public void onReceiveData(String data) {
                final int nbPosts = Integer.parseInt(data);
                // get next page of old posts
                if (nbPosts > lastFetchedIndex + 1) {
                    int pageToGet = (lastFetchedIndex + 1) / pageSize;
                    getPosts(pageToGet, -1, nbPosts, retrievalEndRunnable);
                } else if (retrievalEndRunnable != null) {
                    retrievalEndRunnable.run();
                }
            }
        });
    }

    private void getNewPosts(final Runnable retrievalEndRunnable) {
        APICaller.get("threads/thread/" + threadId + "/posts/count", new ReceiveData() {
            @Override
            public void onReceiveData(String data) {
                int nbPosts = Integer.parseInt(data);
                getNewPosts(nbPosts, retrievalEndRunnable);
            }
        });
    }

    private void getNewPosts(int nbPosts, final Runnable retrievalEndRunnable) {
        int nbNewPosts = nbPosts - lastTotalSize;
        if (lastFetchedIndex != -1 && lastTotalSize < nbPosts) {
            getPosts(0, nbNewPosts, nbPosts, retrievalEndRunnable);
        } else if (retrievalEndRunnable != null) {
            retrievalEndRunnable.run();
        }
    }

    private void getPosts(final int pageNb, final int newPosts, final int totalSize, final Runnable retrievalEndRunnable) {
        APICaller.get(MODULE_PATH + "thread/" + threadId + "/posts?pageNb=" + pageNb + "&pageSize=" + pageSize,
                new ReceiveArray() {
                    @Override
                    public void onReceiveData(JSONArray data) {
                        boolean firstLoad = false;
                        if (lastFetchedIndex == -1) {
                            firstLoad = true;
                            ((ViewGroup) findViewById(R.id.postsLayout)).removeAllViews();
                        }
                        addPosts(data, newPosts);
                        lastFetchedIndex += data.length();
                        lastTotalSize = totalSize;
                        if (firstLoad) {
                            // first page might be partial: get next page to have enough posts to show
                            getNextPage(retrievalEndRunnable);
                        } else if (retrievalEndRunnable != null) {
                            retrievalEndRunnable.run();
                        }
                    }
                });
    }

    private void addPosts(JSONArray jsonPosts, int newPosts) {
        ViewGroup layout = (ViewGroup) findViewById(R.id.postsLayout);
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
                addPost(layout, jsonPosts.getJSONObject(i), newPosts > 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (newPosts > 0) {
            final ScrollView scrollView = (ScrollView) findViewById(R.id.postsScrollView);
            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

    private void addPost(ViewGroup layout, JSONObject jsonPost, boolean addAtEnd) throws JSONException {
        LinearLayout hv = new LinearLayout(this);
        hv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        hv.setOrientation(LinearLayout.HORIZONTAL);
        if (addAtEnd)
            layout.addView(hv);
        else
            layout.addView(hv, 0);
        // author
        TextView authorText = new TextView(this);
        LinearLayout.LayoutParams authorTextLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        authorTextLayout.setMargins(8, 0, 24, 0);
        authorText.setLayoutParams(authorTextLayout);
        authorText.setText(jsonPost.getString("author"));
        hv.addView(authorText);
        // post text
        TextView postText = new TextView(this);
        LinearLayout.LayoutParams postTextLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        postText.setLayoutParams(postTextLayout);
        postText.setText(jsonPost.getString("message"));
        postText.setTextColor(BLACK);
        hv.addView(postText);
    }

    public void btnClick(View v) {
        getNewPosts(null);
    }
}
