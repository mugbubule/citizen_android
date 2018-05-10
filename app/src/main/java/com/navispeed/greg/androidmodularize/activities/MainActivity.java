package com.navispeed.greg.androidmodularize.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.navispeed.greg.androidmodularize.R;
import com.navispeed.greg.androidmodularize.helpers.ModuleRegister;
import com.navispeed.greg.common.Module;
import com.navispeed.greg.common.StoredData;
import com.navispeed.greg.welcome.WelcomeActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ModuleRegister instance = ModuleRegister.getInstance();
        StoredData.getInstance().init(getSharedPreferences("citizen_data", MODE_PRIVATE));

        LinearLayout layout = (LinearLayout) findViewById(R.id.toto);
        layout.setOrientation(LinearLayout.VERTICAL);  //Can also be done in xml by android:orientation="vertical"

        List<Module> moduleList = instance.getModuleList();
        for (int i = 0; i < moduleList.size(); i++) {
            final Module module = moduleList.get(i);
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            Button btnTag = new Button(this);
            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnTag.setText("Button " + module.getName());
            btnTag.setId(1 + i);
            row.addView(btnTag);
            layout.addView(row);
            btnTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("toto", WelcomeActivity.class.getName() + " : " + module.getMainActivity().getName());
                    Intent intent = new Intent(MainActivity.this, module.getMainActivity());
                    MainActivity.this.startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        StoredData.getInstance().close();
    }
}
