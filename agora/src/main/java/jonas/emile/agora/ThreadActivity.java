package jonas.emile.agora;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.navispeed.greg.common.paging.PRAutoFetchingActivity;
import com.navispeed.greg.common.paging.PageRetriever;

import org.json.JSONException;
import org.json.JSONObject;

import jonas.emile.agora.services.PostService;

import static android.graphics.Color.BLACK;

public class ThreadActivity extends PRAutoFetchingActivity {

    private static final int PAGE_SIZE = 20;

    private PostService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        init();
    }

    @Override
    protected int getAutoFetchFreq() {
        return 1000;
    }

    @Override
    protected PageRetriever initPr() {
        PageRetriever pr;
        String threadId = getIntent().getStringExtra("id");
        final ScrollView scrollView = (ScrollView) findViewById(R.id.postsScrollView);
        service = new PostService(this, threadId);
        pr = new PageRetriever(this, PAGE_SIZE, scrollView, (ViewGroup) findViewById(R.id.postsLayout),
                service, this::addPost);
        return pr;
    }

    private void init() {

        String threadTopic = getIntent().getStringExtra("topic");
        ((TextView) findViewById(R.id.txtPosts)).setText(getResources().getString(R.string.posts, threadTopic));
        }

    public void btnClick(View btn) {
        EditText txtView = (EditText) (findViewById(R.id.editText));
        String msg = txtView.getText().toString();
        btn.setEnabled(false);
        service.sendMessage(msg).accept(consumable -> pr.getNewEntries(retrievalSuccessful -> {
            txtView.setText("");
            btn.setEnabled(true);
        }), error -> {
            pr.showNetworkErrorMessage();
            btn.setEnabled(true);
        });
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
