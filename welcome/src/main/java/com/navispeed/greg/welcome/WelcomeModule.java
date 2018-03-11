package com.navispeed.greg.welcome;

import android.app.Activity;

import com.navispeed.greg.common.Module;

/**
 * Created by greg on 04/06/2017.
 */

public class WelcomeModule implements Module {
    private static WelcomeModule ourInstance = new WelcomeModule();

    public static WelcomeModule getInstance() {
        return ourInstance;
    }

    private WelcomeModule() {}

    @Override
    public Class<? extends Activity> getMainActivity() {
        return WelcomeActivity.class;
    }

    @Override
    public String getName() {
        return "Welcome";
    }
}
