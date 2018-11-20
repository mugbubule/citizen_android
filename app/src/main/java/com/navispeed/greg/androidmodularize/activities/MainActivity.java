package com.navispeed.greg.androidmodularize.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.navispeed.greg.androidmodularize.R;
import com.navispeed.greg.androidmodularize.controllers.MainController;
import com.navispeed.greg.androidmodularize.helpers.ModuleRegister;
import com.navispeed.greg.common.Module;
import com.navispeed.greg.common.StoredData;

import org.reflections.Store;

import java.util.List;

import jonas.emile.login.LoginActivity;
import jp.wasabeef.blurry.Blurry;

public class MainActivity extends AppCompatActivity {
    private boolean isVisible = false;
    MainController controller;



    @Override
     public void onStart() {
        super.onStart();
        LinearLayout layout = (LinearLayout) findViewById(R.id.toto);
        ModuleRegister instance = ModuleRegister.getInstance();

        if (StoredData.getInstance().getLogged() && !isVisible) {
            List<Module> moduleList = instance.getModuleList();
            for (int i = 0; i < moduleList.size(); i++) {
                final Module module = moduleList.get(i);
                View hr = new View(this);
                hr.setLayoutParams(new LinearLayout.LayoutParams(150, 4));
                hr.setPadding(0, 10, 0, 10);
                hr.setBackgroundColor(Color.parseColor("#e0e0e0")); //e0e0e0
                Button btnTag = new Button(this);
                btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                btnTag.setBackgroundColor(0);
                btnTag.setTextSize(24);
                btnTag.setTextColor(Color.parseColor("#e0e0e0"));
                btnTag.setText(module.getName());
                btnTag.setId(1 + i);
                layout.addView(btnTag);
                if (i < moduleList.size() - 1) {
                    layout.addView(hr);
                }
                btnTag.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, module.getMainActivity());
                    MainActivity.this.startActivity(intent);
                });
            }
            isVisible = true;
        }
        if (!StoredData.getInstance().getLogged()) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.controller = new MainController();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);
        ModuleRegister instance = ModuleRegister.getInstance();
        StoredData.getInstance().init(getSharedPreferences("citizen_data", MODE_PRIVATE));

        LinearLayout layout = (LinearLayout) findViewById(R.id.toto);
        layout.setOrientation(LinearLayout.VERTICAL);  //Can also be done in xml by android:orientation="vertical"
        layout.setGravity(Gravity.CENTER);

        TextView cityName = new TextView(this);
        cityName.setText(R.string.city_name);
        cityName.setText(R.string.city_name);
        cityName.setTextSize(42);
        cityName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        cityName.setTextColor(Color.parseColor("#e0e0e0"));
        cityName.setPadding(0,0,0,60);
        layout.addView(cityName);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1000);
        layout.setAnimation(fadeIn);

        findViewById(R.id.background_landing).post(() -> Blurry.with(MainActivity.this)
                .radius(25)
                .sampling(1)
                .color(Color.argb(80, 0, 0, 0))
                .async()
                .animate(1000)
                .onto(findViewById(R.id.background_landing)));

        this.controller.init(this);
        StoredData.getInstance().setLogged(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        StoredData.getInstance().close();
    }
}
