package com.navispeed.greg.goodbye;

import android.app.Activity;

import com.navispeed.greg.common.Module;

/**
 * Created by greg on 04/06/2017.
 */

public class GoodbyeModule implements Module {

    private static GoodbyeModule ourInstance = new GoodbyeModule();

    public static GoodbyeModule getInstance() {
        return ourInstance;
    }

    private GoodbyeModule() {}

    @Override
    public Class<? extends Activity> getMainActivity() {
        return GoodbyeActivity.class;
    }

    @Override
    public String getName() {
        return "GoodBye";
    }


}
