package jonas.emile.events;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.ImageAdapter;
import com.navispeed.greg.common.ReceiveData;

import org.json.JSONArray;

/* Created by jonas_e on 18/11/2017. */

public class EventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        //final TextView content = (TextView) findViewById(R.id.textView);
        //content.setText("xddddd".toLowerCase());
        APICaller apiCaller = new APICaller();
        ReceiveData handler = new ReceiveData() {
            @Override
            public void onReceiveData(JSONArray data) {
                //content.setText(data.toString());
            }
        };
        apiCaller.setHandler(handler);
        apiCaller.execute("https://citizen.navispeed.eu/api/events/all", "GET");

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(EventsActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}