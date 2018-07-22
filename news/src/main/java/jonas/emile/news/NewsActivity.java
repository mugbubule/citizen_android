package jonas.emile.news;

import android.content.Intent;
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
import jonas.emile.news.models.News;
import jonas.emile.news.services.NewsService;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/* Created by jonas_e on 18/11/2017. */

public class NewsActivity extends AppCompatActivity {

    LinearLayout newsListLayout;
    NewsService service = new NewsService(this);
    List<News> newsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_layout);

        newsListLayout = (LinearLayout) findViewById(R.id.layout);
        fetchNews();
    }

    private void fetchNews() {
        service.getAll().accept(array -> {
            newsList = new ArrayList<>();
            try {
                for (int i = 0; i < array.length(); ++i) {
                    addNews(new News((JSONObject) array.get(i)));
                }
            } catch (JSONException e) {
                showMessage(R.string.json_error);
            }
        }, error -> showMessage(R.string.fetch_error));
    }

    int first = 0; // TODO remove
    private void addNews(News news) {

        newsList.add(news);

        RelativeLayout singleNewsLayout = new RelativeLayout(this);
        singleNewsLayout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 8, 0, 8);
        singleNewsLayout.setLayoutParams(layoutParams);
        newsListLayout.addView(singleNewsLayout);

        ImageView img = new ImageView(this);
        img.setTag(news);
        img.setAdjustViewBounds(true);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setOnClickListener(view -> newsClick((News) view.getTag()));
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
        singleNewsLayout.addView(img);

        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        textParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        textLayout.setLayoutParams(textParams);
        singleNewsLayout.addView(textLayout);

        TextView textTitle = new TextView(this);
        textTitle.setText(news.title);
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
        textSub.setText(news.subtitle);
        textSub.setTextColor(Color.WHITE);
        textSub.setGravity(Gravity.CENTER);
        textSub.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textLayout.addView(textSub);

        if (first == 0) {
            new DownloadImageTask(img, this::blur, 20).execute("https://jpeg.org/images/jpeg2000-home.jpg"); // TODO change
        } else if (first == 1) {
            new DownloadImageTask(img, this::blur, 20).execute("http://cdn.skim.gs/image/upload/c_fill,dpr_1.0,f_auto,fl_lossy,q_auto,w_940/v1456335851/msi/Yorkshire_Terrier_xkjh7m.jpg"); // TODO change
        } else {
            new DownloadImageTask(img, this::blur, 20).execute("https://animalsadda.com/wp-content/uploads/2013/07/Asian-Palm-Civet-4.jpg"); // TODO change
        }
        first++;
    }

    private void blur() {
        // Post in the parent's message queue to make sure the parent
// lays out its children before you call getHitRect()
//        findViewById(R.id.layout).post(() -> {
//            Blurry.with(this)
//                    .radius(25)
//                    .sampling(1)
//                    .color(Color.argb(80, 0, 0, 0))
//                    .async()
//                    .animate(1000)
//                    .onto((ViewGroup) findViewById(R.id.layout));
//        });

    }

    private void newsClick(News news) {
        Intent intent = new Intent(NewsActivity.this, NewsDetailsActivity.class);
        intent.putExtra("newsUuid", news.uuid);
        startActivity(intent);
    }

    private void showMessage(int stringId) {
        Toast toast = Toast.makeText(this, getString(stringId), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
