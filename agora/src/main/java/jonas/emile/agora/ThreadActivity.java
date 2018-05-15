package jonas.emile.agora;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.ReceiveArray;
import com.navispeed.greg.common.ReceiveData;

import org.json.JSONException;
import org.json.JSONObject;

import static android.graphics.Color.BLACK;
import static jonas.emile.agora.AgoraModule.MODULE_PATH;

public class ThreadActivity extends AppCompatActivity {

    private String threadId;
    PageRetriever pr;

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
        PageRetriever.AddToView addToView = new PageRetriever.AddToView() {
            @Override
            public void add(ViewGroup viewGroup, JSONObject entry, boolean addAtEnd) throws JSONException {
                addPost(viewGroup, entry, addAtEnd);
            }
        };
        PageRetriever.GetEntryCount getCount = new PageRetriever.GetEntryCount() {
            @Override
            public void get(ReceiveData handler) {
                APICaller.get("threads/thread/" + threadId + "/posts/count", handler);
            }
        };
        PageRetriever.GetEntries getEntries = new PageRetriever.GetEntries() {
            @Override
            public void get(int pageNb, int pageSize, ReceiveArray handler) {
                APICaller.get(MODULE_PATH + "thread/" + threadId + "/posts?pageNb=" + pageNb + "&pageSize=" + pageSize, handler);
            }
        };
        pr = new PageRetriever(7, scrollView, (ViewGroup) findViewById(R.id.postsLayout),
                getCount, getEntries, addToView);
    }

    public void btnClick(View v) {
        pr.getNewEntries(null);
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
}
