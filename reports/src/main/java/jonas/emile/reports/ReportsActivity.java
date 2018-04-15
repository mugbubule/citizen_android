package jonas.emile.reports;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.ReceiveArray;
import com.navispeed.greg.common.ReceiveData;

import org.json.JSONArray;

/* Created by jonas_e on 18/11/2017. */

public class ReportsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reports_menu);
        //final TextView content = (TextView) findViewById(R.id.textView);
        //content.setText("xddddd".toLowerCase());
        APICaller apiCaller = new APICaller();
        ReceiveData handler = new ReceiveArray() {
            @Override
            public void onReceiveData(JSONArray data) {
                System.out.print(data);
                //content.setText(data.toString());
            }
        };
        apiCaller.setHandler(handler);
        apiCaller.execute("https://citizen.navispeed.eu/api/reports/", "GET");

        /*GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(ReportsActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });*/
    }
}
