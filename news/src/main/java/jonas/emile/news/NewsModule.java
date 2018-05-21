package jonas.emile.news;

import android.app.Activity;

import com.navispeed.greg.common.Module;

/* Created by jonas_e on 18/11/2017. */

public class NewsModule implements Module {

    private static final NewsModule ourInstance = new NewsModule();

    public static NewsModule getInstance() {
        return ourInstance;
    }

    private NewsModule() {
    }

    @Override
    public Class<? extends Activity> getMainActivity() { return NewsActivity.class; }

    @Override
    public String getName() {
        return "Nouvelles";
    }
}
