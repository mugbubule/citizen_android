package jonas.emile.agora;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import jonas.emile.agora.services.PostService;
import jonas.emile.agora.utils.PageRetriever;

import static android.graphics.Color.BLACK;

public class ThreadActivity extends AppCompatActivity {

    private PageRetriever pr;
    private PostService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        init();
        startAutoFetch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pr.stopAutoFetch();
    }

    private void init() {

        String threadId = getIntent().getStringExtra("id");
        String threadTopic = getIntent().getStringExtra("topic");
        ((TextView) findViewById(R.id.txtPosts)).setText(getResources().getString(R.string.posts, threadTopic));

        final ScrollView scrollView = (ScrollView) findViewById(R.id.postsScrollView);
        service = new PostService(this, threadId);
        pr = new PageRetriever(this, 7, scrollView, (ViewGroup) findViewById(R.id.postsLayout),
                service, this::addPost);
    }

    private void startAutoFetch() {
        pr.startAutoFetch(1000);
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
