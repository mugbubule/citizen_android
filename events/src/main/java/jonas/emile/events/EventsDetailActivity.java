package jonas.emile.events;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.navispeed.greg.common.utils.DownloadImageTask;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.text.DateFormatSymbols;

import jonas.emile.events.models.Event;
import jonas.emile.events.services.EventsService;

/* Created by jonas_e on 31/10/2018. */
public class EventsDetailActivity extends AppCompatActivity {
    String eventUuid;
    EventsService service;
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        eventUuid = getIntent().getStringExtra("eventUuid");
        service = new EventsService(this);
        fetchDetails();
    }

    private void fetchDetails() {
        Context c = this;
        service.getDetails(eventUuid).accept(json -> {
            event = new Event(json);
            displayDetails();
        }, error -> {
            Toast toast = Toast.makeText(c, error.getLocalizedMessage(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
    }

    static int i = 0;
    private void displayDetails() {
        ((TextView) findViewById(R.id.txtTitle)).setText(event.name);
        DateTime dateTime = DateTime.parse(event.datetime, DateTimeFormat.forPattern("YYYY-MM-DD HH:mm:ss"));
        StringBuilder date = new StringBuilder();
        date.append(new DateFormatSymbols().getWeekdays()[dateTime.getDayOfWeek()]).append(' ')
                .append(dateTime.getDayOfMonth()).append(' ')
                .append(new DateFormatSymbols().getMonths()[dateTime.getMonthOfYear()]).append(' ')
                .append(dateTime.getYear());
        ((TextView) findViewById(R.id.txtSubtitle)).setText(date.toString());
        ((TextView) findViewById(R.id.txtContent)).setText(Html.fromHtml((event.description), Html.FROM_HTML_MODE_COMPACT));
        if (i == 0)
            new DownloadImageTask(findViewById(R.id.img)).execute("http://cdn.skim.gs/image/upload/c_fill,dpr_1.0,f_auto,fl_lossy,q_auto,w_940/v1456335851/msi/Yorkshire_Terrier_xkjh7m.jpg");
        else
            new DownloadImageTask(findViewById(R.id.img)).execute("https://www.animalaid.org.uk/wp-content/uploads/2016/08/lamb-iStock-copy-767x655.jpg");
        i++;
        findViewById(R.id.img).post(() -> {
            /*Blurry.with(NewsDetailsActivity.this)
                    .radius(25)
                    .sampling(1)
                    .color(Color.argb(80, 0, 0, 0))
                    .async()
                    .capture(findViewById(R.id.img))
                    .into((ImageView) findViewById(R.id.img));*/
            /*Blurry.with(NewsDetailsActivity.this)
                    .radius(25)
                    .sampling(1)
                    .color(Color.argb(80, 0, 0, 0))
                    .async()
                    .animate(1000)
                    .onto((ViewGroup) findViewById(R.id.background_news_details));*/
        });
    }
}
