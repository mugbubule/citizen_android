package jonas.emile.events;

import android.app.Activity;

import com.navispeed.greg.common.Module;

/* Created by jonas_e on 18/11/2017. */

public class EventsModule implements Module {

    private static final EventsModule ourInstance = new EventsModule();

    public static EventsModule getInstance() {
        return ourInstance;
    }

    private EventsModule() {
    }

    @Override
    public Class<? extends Activity> getMainActivity() { return EventsActivity.class; }

    @Override
    public String getName() {
        return "Events";
    }
}
