package jonas.emile.agora;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.ReceiveData;

import org.json.JSONArray;
import org.json.JSONException;

import static android.graphics.Color.BLACK;
import static jonas.emile.agora.AgoraModule.MODULE_PATH;

public class ThreadActivity extends AppCompatActivity {

    private static final int pageSize = 20;
    private String threadId;
    private String threadTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        init();
    }

    private void init() {
        threadId = getIntent().getStringExtra("id");
        threadTopic = getIntent().getStringExtra("topic");
        ((TextView) findViewById(R.id.txtPosts)).setText(getResources().getString(R.string.posts, threadTopic));
        getPosts(0); // TODO: should actually be last page
    }

    private void getPosts(final int pageNb) { // TODO: populate
        APICaller.get(MODULE_PATH + "thread/" + threadId + "/posts?pageNb=" + pageNb + "&pageSize=" + pageSize,
                new ReceiveData() {
                    @Override
                    public void onReceiveData(JSONArray data) {
                        if (pageNb == 0)
                            ((ViewGroup) findViewById(R.id.postsLayout)).removeAllViews();
                        addPosts(data);
                    }
                });
    }

    private void addPosts(JSONArray jsonPosts) {
        ViewGroup layout = (ViewGroup) findViewById(R.id.postsLayout);
        for (int i = jsonPosts.length() - 1; i >= 0; --i) {
            try {
                LinearLayout hv = new LinearLayout(this);
                hv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                hv.setOrientation(LinearLayout.HORIZONTAL);
                layout.addView(hv);
                // author
                TextView authorText = new TextView(this);
                LinearLayout.LayoutParams authorTextLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                authorTextLayout.setMargins(8, 0, 24, 0);
                authorText.setLayoutParams(authorTextLayout);
                authorText.setText(jsonPosts.getJSONObject(i).getString("author"));
                hv.addView(authorText);
                // post text
                TextView postText = new TextView(this);
                LinearLayout.LayoutParams postTextLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                postText.setLayoutParams(postTextLayout);
                postText.setText(jsonPosts.getJSONObject(i).getString("message"));
                postText.setTextColor(BLACK);
                hv.addView(postText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
