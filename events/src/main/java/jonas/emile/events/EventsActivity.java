package jonas.emile.events;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.navispeed.greg.common.utils.DownloadImageTask;
import jonas.emile.events.models.Event;
import jonas.emile.events.services.EventsService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

/* Created by jonas_e on 18/11/2017. */

public class EventsActivity extends AppCompatActivity {

    LinearLayout eventListLayout;
    EventsService service = new EventsService(this);
    List<Event> eventList = new ArrayList<>();
    Event lastEventFetched = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_layout);

        eventListLayout = (LinearLayout) findViewById(R.id.layout);
        fetchEvents();
    }

    private void fetchEvents() {
        lastEventFetched = null;
        service.getAll().accept(array -> {
            eventList = new ArrayList<>();
            try {
                for (int i = 0; i < array.length(); ++i) {
                    addEvent(new Event((JSONObject) array.get(i)));
                }
            } catch (JSONException e) {
                showMessage(R.string.json_error);
            }
        }, error ->
                showMessage(R.string.fetch_error));
    }

    int first = 0; // TODO remove
    private void addEvent(Event event) {

        eventList.add(event);

        DateTime dateTime = DateTime.parse(event.datetime, DateTimeFormat.forPattern("YYYY-MM-DD HH:mm:ss"));
        if (lastEventFetched == null || DateTime.parse(lastEventFetched.datetime, DateTimeFormat.forPattern("YYYY-MM-DD HH:mm:ss")).getMonthOfYear() != dateTime.getMonthOfYear()) {
            TextView txtMonth = new TextView(this);
            txtMonth.setTextColor(Color.BLACK);
            txtMonth.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            txtMonth.setGravity(Gravity.CENTER);
            eventListLayout.addView(txtMonth);
            lastEventFetched = event;
        }

        RelativeLayout singleEventLayout = new RelativeLayout(this);
        singleEventLayout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 8, 0, 8);
        singleEventLayout.setLayoutParams(layoutParams);
        eventListLayout.addView(singleEventLayout);

        ImageView img = new ImageView(this);
        img.setTag(event);
        img.setAdjustViewBounds(true);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setOnClickListener(view -> eventClick((Event) view.getTag()));
        img.setColorFilter(Color.rgb(150, 150, 150), android.graphics.PorterDuff.Mode.MULTIPLY);
        RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        if (point.y > point.x)
            imgParams.height = (int)((double)point.y / 4.5);
        else
            imgParams.width = (int)((double)point.x / 4.5);
        img.setLayoutParams(imgParams);
        singleEventLayout.addView(img);

        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        textParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        textLayout.setLayoutParams(textParams);
        singleEventLayout.addView(textLayout);

        TextView textTitle = new TextView(this);
        textTitle.setText(event.name);
        textTitle.setTextColor(Color.WHITE);
        textTitle.setGravity(Gravity.CENTER);
        textTitle.setAllCaps(true);
        textTitle.setTextSize(20);
        textTitle.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textLayout.addView(textTitle);

        TextView separator = new TextView(this);
        separator.setText("---");
        separator.setTextColor(Color.rgb(20, 110, 180));
        separator.setGravity(Gravity.CENTER);
        separator.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textLayout.addView(separator);

        TextView textSub = new TextView(this);
        textSub.setText(new DateFormatSymbols().getWeekdays()[dateTime.getDayOfWeek()] + " " + dateTime.getDayOfMonth() + " " + new DateFormatSymbols().getMonths()[dateTime.getMonthOfYear()]);
        textSub.setTextColor(Color.WHITE);
        textSub.setGravity(Gravity.CENTER);
        textSub.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textLayout.addView(textSub);

        if (first % 3 == 0) {
            new DownloadImageTask(img, () -> {}, 20).execute("http://4.bp.blogspot.com/-c8Tnsoq1IjE/UDsk0cpg2GI/AAAAAAAAISA/r36wSAKwexU/s1600/Night+City+Glow+Wallpapers+1.jpg"); // TODO change
        } else if (first % 3 == 1) {
            new DownloadImageTask(img, () -> {}, 20).execute("http://www.highreshdwallpapers.com/wp-content/uploads/2011/09/Epic-High-Definition-City-Wallpaper.jpg"); // TODO change
        } else {
            new DownloadImageTask(img, () -> {}, 20).execute("http://2.bp.blogspot.com/-z6zZ2ZeUWG0/UDsmg9pHH5I/AAAAAAAAITI/xInXSA8LElk/s1600/Night+City+Glow+Wallpapers.jpg"); // TODO change
        }
        first++;
    }

    private void eventClick(Event event) {
        /*Intent intent = new Intent(EventActivity.this, EventDetailsActivity.class);
        intent.putExtra("eventUuid", event.uuid);
        startActivity(intent);*/
    }

    private void showMessage(int stringId) {
        Toast toast = Toast.makeText(this, getString(stringId), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
