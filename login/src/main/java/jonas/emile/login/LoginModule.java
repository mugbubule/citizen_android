package jonas.emile.login;
/* Created by jonas_e on 21/11/2017. */

import android.app.Activity;

import com.navispeed.greg.common.Module;

public class LoginModule implements Module {
    private static LoginModule ourInstance = new LoginModule();

    public static LoginModule getInstance() {
        return ourInstance;
    }

    private LoginModule() {}

    @Override
    public Class<? extends Activity> getMainActivity() {
        return LoginActivity.class;
    }

    @Override
    public String getName() {
        return "Login";
    }
}