package jonas.emile.poll;

import android.app.Activity;
import com.navispeed.greg.common.Module;
import jonas.emile.poll.activity.PollListActivity;

/* Created by jonas_e on 18/11/2017. */

public class PollModule implements Module {

    private static final PollModule ourInstance = new PollModule();

    public static PollModule getInstance() {
        return ourInstance;
    }

    private PollModule() {
    }

    @Override
    public Class<? extends Activity> getMainActivity() { return PollListActivity.class; }

    @Override
    public String getName() {
        return "Polls";
    }
}
