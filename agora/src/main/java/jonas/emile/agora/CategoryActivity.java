package jonas.emile.agora;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.navispeed.greg.common.Consumer;
import com.navispeed.greg.common.paging.PRAutoFetchingActivity;
import com.navispeed.greg.common.paging.PageRetriever;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import jonas.emile.agora.services.PostService;
import jonas.emile.agora.services.ThreadService;
import jp.wasabeef.blurry.Blurry;

public class CategoryActivity extends PRAutoFetchingActivity {

    private static final int PAGE_SIZE = 20;

    private String categoryId;
    ThreadService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        init();
        findViewById(R.id.background_category).post(new Runnable() {
            // Post in the parent's message queue to make sure the parent
            // lays out its children before you call getHitRect()
            @Override
            public void run() {
                /*Blurry.with(WelcomeActivity.this)
                        .radius(25)
                        .sampling(1)
                        .color(Color.argb(80, 0, 0, 0))
                        .async()
                        .capture(findViewById(R.id.imageView2))
                        .into((ImageView) findViewById(R.id.imageView2));*/
                Blurry.with(CategoryActivity.this)
                        .radius(25)
                        .sampling(1)
                        .color(Color.argb(80, 0, 0, 0))
                        .async()
                        .animate(2000)
                        .onto((ViewGroup) findViewById(R.id.background_category));
            }
        });
    }

    private void init() {
        categoryId = getIntent().getStringExtra("id");
        String categoryName = getIntent().getStringExtra("name");
        if (categoryName != null) {
            ((TextView) findViewById(R.id.txtDiscussions)).setText(categoryName);
        } else {
            ((TextView) findViewById(R.id.txtDiscussions)).setText(getResources().getString(R.string.threads));
        }
    }

    protected PageRetriever initPr() {
        PageRetriever pr;
        final ScrollView scrollView = (ScrollView) findViewById(R.id.threadsScrollView);
        service = new ThreadService(this, categoryId);
        pr = new PageRetriever(this, PAGE_SIZE,
                scrollView, (ViewGroup) findViewById(R.id.threadsLayout),
                service, this::addThread);
        return pr;
    }

    protected int getAutoFetchFreq() {
        return 2000;
    }

    private void addThread(ViewGroup layout, JSONObject jsonThread, boolean addAtEnd) throws JSONException {

        RelativeLayout rl = new RelativeLayout(this);
        rl.setBackground(getDrawable(R.drawable.background_white_rounded_shadow));
        rl.setTag(jsonThread);
        rl.setOnClickListener(view -> {
            try {
                selectThread(((JSONObject) view.getTag()).getString("uuid"), ((JSONObject) view.getTag()).getString("topic"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        LinearLayout.LayoutParams rlPL = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rlPL.setMargins(16, 16, 16, 16);
        Point dimensions = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(dimensions);
        rlPL.height = dimensions.y / 5;
        rl.setLayoutParams(rlPL);
        if (addAtEnd) {
            layout.addView(rl);
        } else {
            layout.addView(rl, 0);
        }

        TextView txtTopic = new TextView(this);
        txtTopic.setTextSize(24);
        txtTopic.setTextColor(Color.BLACK);
        txtTopic.setText(jsonThread.getString("topic"));
        RelativeLayout.LayoutParams txtTopicLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        txtTopicLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        txtTopicLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        txtTopicLP.setMargins(24, 16, 16, 16);
        txtTopic.setLayoutParams(txtTopicLP);
        int txtTopicId = new Random().nextInt();
        txtTopic.setId(txtTopicId);
        rl.addView(txtTopic);

        TextView txtDate = new TextView(this);
        txtDate.setTextColor(Color.BLACK);
        txtDate.setText(DateTime.parse(jsonThread.getString("created"), DateTimeFormat.forPattern("YYYY-MM-DD HH:mm:ss")).toString("YYYY-MM-DD"));
        RelativeLayout.LayoutParams txtDateLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        txtDateLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        txtDateLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        txtDateLP.setMargins(16, 16, 16, 16);
        txtDate.setLayoutParams(txtDateLP);
        rl.addView(txtDate);

        LinearLayout postsLayout = new LinearLayout(this);
        postsLayout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams postsLayoutLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        postsLayoutLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        postsLayoutLP.addRule(RelativeLayout.BELOW, txtTopicId);
        postsLayoutLP.setMargins(32, 0, 32, 16);
        postsLayout.setLayoutParams(postsLayoutLP);
        rl.addView(postsLayout);

        int nameSize = dimensions.x / 5;
        Context c = this;
        PostService postService = new PostService(this, jsonThread.getString("uuid"));
        postService.getEntries(0, 10).accept(posts -> {
            for (int i = 0; i < posts.length(); ++i) {

                LinearLayout postLayout = new LinearLayout(c);
                postLayout.setOrientation(LinearLayout.HORIZONTAL);
                postsLayout.addView(postLayout);

                try {
                    TextView txtAuthor = new TextView(c);
                    LinearLayout.LayoutParams txtAuthorLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    txtAuthorLP.width = nameSize;
                    txtAuthor.setLayoutParams(txtAuthorLP);
                    txtAuthor.setText(((JSONObject) posts.get(i)).getString("author") + ":");
                    postLayout.addView(txtAuthor);

                    TextView txtMessage = new TextView(c);
                    LinearLayout.LayoutParams txtMessageLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    txtMessage.setLayoutParams(txtMessageLP);
                    txtMessage.setText(((JSONObject) posts.get(i)).getString("message"));
                    postLayout.addView(txtMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
        });
    }

    private void selectThread(String threadId, String topic) {
        Intent intent = new Intent(CategoryActivity.this, ThreadActivity.class);
        intent.putExtra("id", threadId);
        intent.putExtra("topic", topic);
        startActivity(intent);
    }

    public void addThreadClick(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.newThreadPrompt));
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(getString(android.R.string.ok), null);
        builder.setNegativeButton(getString(android.R.string.cancel), ((dialogInterface, i) -> dialogInterface.cancel()));
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String txt = input.getText().toString();
                if (txt.isEmpty()) {
                    input.setError(getString(R.string.newThreadErrEmpty));
                } else {
                    dialog.dismiss();
                    createThread(txt);
                }
            });
        });
        dialog.show();
    }

    private void createThread(String topic) {
        Context c = this;
        Button newThreadBtn = (Button) findViewById(R.id.btnNewThread);
        newThreadBtn.setEnabled(false);
        service.createThread(topic).accept(
                consumable -> pr.getNewEntries(retrievalSuccessful -> newThreadBtn.setEnabled(true)),
                error -> {
                    newThreadBtn.setEnabled(true);
                    Toast toast = Toast.makeText(c, getText(R.string.newThreadCreationErr), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                });
    }
}