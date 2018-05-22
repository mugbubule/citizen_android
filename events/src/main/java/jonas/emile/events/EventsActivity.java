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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import jonas.emile.events.models.Event;
import jonas.emile.events.services.EventsService;

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
        }, error -> showMessage(R.string.fetch_error));
    }

    boolean first = true; // TODO remove
    private void addEvent(Event event) {

        eventList.add(event);

        DateTime dateTime = DateTime.parse(event.datetime, DateTimeFormat.forPattern("YYYY-MM-DD HH:mm:ss"));
        if (lastEventFetched == null || DateTime.parse(lastEventFetched.datetime, DateTimeFormat.forPattern("YYYY-MM-DD HH:mm:ss")).getMonthOfYear() != dateTime.getMonthOfYear()) {
            TextView txtMonth = new TextView(this);
            txtMonth.setTextColor(Color.WHITE);
            txtMonth.setText(new DateFormatSymbols().getMonths()[dateTime.getMonthOfYear()] + " " + dateTime.getYear());
            txtMonth.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
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
        textSub.setText(new DateFormatSymbols().getWeekdays()[dateTime.getDayOfWeek()] + " " + dateTime.getDayOfMonth());
        textSub.setTextColor(Color.WHITE);
        textSub.setGravity(Gravity.CENTER);
        textSub.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textLayout.addView(textSub);

        if (first) {
            new DownloadImageTask(img, () -> {}, 20).execute("https://cn.bing.com/images/search?view=detailV2&ccid=vjXbt5il&id=4FEB2FE7D9B056705AB8850BE099DE358D0F3EDC&thid=OIP.vjXbt5ilwGztEii_4l2OeQHaFj&mediaurl=http%3a%2f%2fcdn.architecturendesign.net%2fwp-content%2fuploads%2f2014%2f08%2f225.jpg&exph=900&expw=1200&q=Beautiful+building&simid=608028532314672905&selectedIndex=1"); // TODO change
            first = false;
        } else {
            new DownloadImageTask(img, () -> {}, 20).execute("https://cn.bing.com/images/search?view=detailV2&ccid=Erm%2bsS6w&id=BECB915300E728C883305ADFB0E0E056B6141843&thid=OIP.Erm-sS6wQBc1Qto9MZGidwHaE8&mediaurl=http%3a%2f%2fanimals.loepr.com%2fwp-content%2fuploads%2f2013%2f09%2fCute-Animals-Photographs-2.jpg&exph=683&expw=1024&q=Beautiful+Animals&simid=608017412636803590&selectedIndex=5"); // TODO change
        }
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
