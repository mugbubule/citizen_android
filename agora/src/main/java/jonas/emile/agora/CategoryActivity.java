package jonas.emile.agora;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.navispeed.greg.common.utils.PRAutoFetchingActivity;
import com.navispeed.greg.common.utils.PageRetriever;

import org.json.JSONException;
import org.json.JSONObject;

import jonas.emile.agora.services.ThreadService;

public class CategoryActivity extends PRAutoFetchingActivity {

    private static final int PAGE_SIZE = 20;

    private String categoryId;
    ThreadService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        init();
    }

    private void init() {
        categoryId = getIntent().getStringExtra("id");
        String categoryName = getIntent().getStringExtra("name");
        if (categoryName != null) {
            ((TextView) findViewById(R.id.txtDiscussions)).setText(getResources().getString(R.string.threadsCat, categoryName));
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
        Button btn = new Button(this);
        btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn.setText(jsonThread.getString("topic"));
        btn.setTag(jsonThread.getString("uuid"));
        btn.setOnClickListener(view -> selectThread((String) view.getTag(), (String) ((TextView) view).getText()));
        if (addAtEnd) {
            layout.addView(btn);
        } else {
            layout.addView(btn, 0);
        }
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
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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