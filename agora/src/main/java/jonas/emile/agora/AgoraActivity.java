package jonas.emile.agora;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.ReceiveData;

import org.json.JSONArray;
import org.json.JSONException;

import static jonas.emile.agora.AgoraModule.MODULE_PATH;

public class AgoraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agora);
        getCategories();
    }

    private void getCategories() {
        APICaller.get(MODULE_PATH + "categories/all", new ReceiveData() {
            @Override
            public void onReceiveData(JSONArray data) {
                fillCatsLayout(data);
            }
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
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            selectCategory((String)view.getTag(), (String)((Button)view).getText());
                        }
                    });
                    layout.addView(btn);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void selectCategory(String id, String name) {
        Intent intent = new Intent(AgoraActivity.this, CategoryActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        startActivity(intent);
    }

}
