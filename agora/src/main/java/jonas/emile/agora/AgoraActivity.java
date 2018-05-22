package jonas.emile.agora;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import jonas.emile.agora.services.CategoryService;

public class AgoraActivity extends AppCompatActivity {

    boolean noCategory = false;
    CategoryService service = new CategoryService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agora);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getCategories();
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
                    Button btn = new Button(this);
                    btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    btn.setText(catsJson.getJSONObject(i).getString("name"));
                    btn.setTag(catsJson.getJSONObject(i).getString("uuid"));
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
