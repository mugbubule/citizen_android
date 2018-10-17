package jonas.emile.reports;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.ReceiveArray;
import com.navispeed.greg.common.ReceiveData;

import org.json.JSONArray;

import jp.wasabeef.blurry.Blurry;

/* Created by jonas_e on 18/11/2017. */

public class ReportsActivity extends AppCompatActivity {
    final int REQUEST_PERMISSION_CAMERA = 1;
    AppCompatActivity reportsActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        findViewById(R.id.background).post(new Runnable() {
            @Override
            public void run() {
                Blurry.with(ReportsActivity.this)
                        .radius(25)
                        .sampling(1)
                        .color(Color.argb(80, 0, 0, 0))
                        .async()
                        .animate(1000)
                        .onto((ViewGroup) findViewById(R.id.background));
            }
        });
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

        findViewById(R.id.galerie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
            }
        });

        findViewById(R.id.photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(reportsActivity, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
            }

        });







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
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);//zero can be replaced with any action code
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        ImageView image = findViewById(R.id.imageconfirm);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    image.setImageURI(selectedImage);
                }
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    image.setImageURI(selectedImage);
                }
                break;
        }
    }
}
