package com.navispeed.greg.common;

import android.app.Activity;

/**
 * Created by greg on 04/06/2017.
 */

public interface Module {

    Class<? extends Activity> getMainActivity();

    String getName();
}
