package jonas.emile.agora;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import jonas.emile.agora.services.CategoryService;
import jp.wasabeef.blurry.Blurry;

public class AgoraActivity extends AppCompatActivity {

    boolean noCategory = false;
    CategoryService service = new CategoryService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agora);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getCategories();

        findViewById(R.id.background_agora).post(new Runnable() {
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
                Blurry.with(AgoraActivity.this)
                        .radius(25)
                        .sampling(1)
                        .color(Color.argb(80, 0, 0, 0))
                        .async()
                        .animate(1000)
                        .onto((ViewGroup) findViewById(R.id.background_agora));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (noCategory) {
            finish();
        }
    }

    private void getCategories() {
        Context c = this;
        service.getAll().accept(this::fillCatsLayout, error -> {
            Toast toast = Toast.makeText(c, getText(R.string.categoryFetchError), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
    }

    private void fillCatsLayout(JSONArray catsJson) {
        if (catsJson.length() == 0) {
            selectCategory(null, null);
        } else if (catsJson.length() == 1) {
            try {
                selectCategory(catsJson.getJSONObject(0).getString("uuid"),
                        catsJson.getJSONObject(0).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ViewGroup layout = (ViewGroup) findViewById(R.id.catsLayout);
            layout.removeAllViews();
            for (int i = 0; i < catsJson.length(); ++i) {
                try {
                    if (i != 0) {
                        View hr = new View(this);
                        hr.setLayoutParams(new LinearLayout.LayoutParams(150, 4));
                        hr.setPadding(0, 10, 0, 10);
                        hr.setBackgroundColor(Color.parseColor("#e0e0e0"));
                        layout.addView(hr);
                    }
                    Button btn = new Button(this);
                    btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    btn.setText(catsJson.getJSONObject(i).getString("name"));
                    btn.setTag(catsJson.getJSONObject(i).getString("uuid"));
                    btn.setBackgroundColor(0);
                    btn.setTextSize(24);
                    btn.setTextColor(Color.parseColor("#e0e0e0"));
                    btn.setOnClickListener(view -> selectCategory((String)view.getTag(), (String)((Button)view).getText()));
                    layout.addView(btn);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void selectCategory(String id, String name) {
        if (id == null) {
            noCategory = true;
        }
        Intent intent = new Intent(AgoraActivity.this, CategoryActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        startActivity(intent);
    }

}
