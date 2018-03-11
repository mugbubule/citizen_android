package jonas.emile.reports;

import android.app.Activity;

import com.navispeed.greg.common.Module;

/* Created by jonas_e on 18/11/2017. */

public class ReportsModule implements Module {

    private static final ReportsModule ourInstance = new ReportsModule();

    public static ReportsModule getInstance() {
        return ourInstance;
    }

    private ReportsModule() {
    }

    @Override
    public Class<? extends Activity> getMainActivity() { return ReportsActivity.class; }

    @Override
    public String getName() {
        return "Reports";
    }
}
