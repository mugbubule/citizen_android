package com.navispeed.greg.androidmodularize.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import com.navispeed.greg.androidmodularize.R;
import com.navispeed.greg.androidmodularize.controllers.MainController;
import com.navispeed.greg.androidmodularize.helpers.ModuleRegister;
import com.navispeed.greg.common.Module;
import com.navispeed.greg.common.StoredData;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    MainController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.controller = new MainController();
        setContentView(R.layout.activity_main);
        ModuleRegister instance = ModuleRegister.getInstance();
        StoredData.getInstance().init(getSharedPreferences("citizen_data", MODE_PRIVATE));

        LinearLayout layout = (LinearLayout) findViewById(R.id.toto);
        layout.setOrientation(LinearLayout.VERTICAL);  //Can also be done in xml by android:orientation="vertical"
        layout.setGravity(Gravity.CENTER);



        List<Module> moduleList = instance.getModuleList();
        for (int i = 0; i < moduleList.size(); i++) {
            final Module module = moduleList.get(i);
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            LinearLayout hrRow = new LinearLayout(this);
            hrRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            View hr = new View(this);
            hr.setLayoutParams(new LinearLayout.LayoutParams(150, 4));
            hr.setPadding(0, 10, 0,10);
            hrRow.setGravity(Gravity.CENTER_HORIZONTAL);
            hr.setBackgroundColor(Color.parseColor("#e0e0e0")); //e0e0e0
            Button btnTag = new Button(this);
            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnTag.setBackgroundColor(0);
            btnTag.setTextSize(24);
            btnTag.setTextColor(Color.parseColor("#e0e0e0"));
            btnTag.setText(module.getName());
            btnTag.setId(1 + i);
            row.addView(btnTag);
            layout.addView(row);
            if (i < moduleList.size() - 1) {
                hrRow.addView(hr);
                layout.addView(hrRow);
            }
            btnTag.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, module.getMainActivity());
                MainActivity.this.startActivity(intent);
            });
        }
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(5000);
        layout.setAnimation(fadeIn);

        this.controller.init(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        StoredData.getInstance().close();
    }
}
