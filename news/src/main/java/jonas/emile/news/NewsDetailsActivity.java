package jonas.emile.news;

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

import jonas.emile.news.models.News;
import jonas.emile.news.services.NewsService;

public class NewsDetailsActivity extends AppCompatActivity {

    String newsUuid;
    NewsService service;
    News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        newsUuid = getIntent().getStringExtra("newsUuid");
        service = new NewsService(this);
        fetchDetails();
    }

    private void fetchDetails() {
        Context c = this;
        service.getDetails(newsUuid).accept(json -> {
            news = new News(json);
            displayDetails();
        }, error -> {
            Toast toast = Toast.makeText(c, getString(R.string.fetch_error), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
    }

    private void displayDetails() {
        ((TextView) findViewById(R.id.txtTitle)).setText(news.title);
        ((TextView) findViewById(R.id.txtSubtitle)).setText(news.subtitle);
        ((TextView) findViewById(R.id.txtContent)).setText(Html.fromHtml((news.content), Html.FROM_HTML_MODE_COMPACT));
        new DownloadImageTask(((ImageView) findViewById(R.id.img))).execute("http://cdn.skim.gs/image/upload/c_fill,dpr_1.0,f_auto,fl_lossy,q_auto,w_940/v1456335851/msi/Yorkshire_Terrier_xkjh7m.jpg");

    }
}
