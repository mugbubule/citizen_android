package jonas.emile.agora;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.ReceiveData;

import org.json.JSONArray;
import org.json.JSONException;

import static jonas.emile.agora.AgoraModule.MODULE_PATH;

public class CategoryActivity extends AppCompatActivity {

    private static final int pageSize = 20;
    private String categoryId;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        init();
    }

    private void init() {
        categoryId = getIntent().getStringExtra("id");
        categoryName = getIntent().getStringExtra("name");
        if (categoryName != null) {
            ((TextView)findViewById(R.id.txtDiscussions)).setText(getResources().getString(R.string.threadsCat, categoryName));
        } else {
            ((TextView)findViewById(R.id.txtDiscussions)).setText(getResources().getString(R.string.threads));
        }
        getThreads(0);
    }

    private void getThreads(final int pageNb) { // TODO: populate
        APICaller.get(MODULE_PATH + "category/" + categoryId + "?pageNb=" + pageNb + "&pageSize=" + pageSize,
                new ReceiveData() {
                    @Override
                    public void onReceiveData(JSONArray data) {
                        if (pageNb == 0)
                            ((ViewGroup)findViewById(R.id.threadsLayout)).removeAllViews();
                        addThreads(data);
                    }
                });
    }

    private void addThreads(JSONArray jsonThreads) {
        ViewGroup layout = (ViewGroup) findViewById(R.id.threadsLayout);
        for (int i = 0; i < jsonThreads.length(); ++i) {
            try {
                Button btn = new Button(this);
                btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                btn.setText(jsonThreads.getJSONObject(i).getString("topic"));
                btn.setTag(jsonThreads.getJSONObject(i).getString("uuid"));
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectThread((String) view.getTag(), (String) ((TextView)view).getText());
                    }
                });
                layout.addView(btn);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void selectThread(String threadId, String topic) {
        Intent intent = new Intent(CategoryActivity.this, ThreadActivity.class);
        intent.putExtra("id", threadId);
        intent.putExtra("topic", topic);
        startActivity(intent);
    }
}