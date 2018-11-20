package jonas.emile.reports;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.android.volley.Response;
import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.Consumer;
import com.navispeed.greg.common.FileUploader;
import com.navispeed.greg.common.ReceiveArray;
import com.navispeed.greg.common.ReceiveData;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.function.BiConsumer;

import jonas.emile.reports.services.ReportsServices;
import jp.wasabeef.blurry.Blurry;

/* Created by jonas_e on 18/11/2017. */

public class ReportsActivity extends AppCompatActivity {
    final int REQUEST_PERMISSION_CAMERA = 1;
    ReportsServices reportsServices = new ReportsServices(this);
    String description = null;
    String title = null;
    File pic = null;
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
//        apiCaller.execute("https://citizen.navispeed.eu/api/reports/", "GET");

        findViewById(R.id.galerie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
            }
        });

        findViewById(R.id.photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(reportsActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CAMERA);
            }
        });

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description = ((TextInputEditText) findViewById(R.id.description)).getText().toString();
                title = ((AutoCompleteTextView) findViewById(R.id.title)).getText().toString();
                if (pic == null) {
                    BiConsumer<Consumer<String>, Response.ErrorListener> test = reportsServices.sendReport(description, title);
                    test.accept(array -> {

                    }, message ->
                            Log.i("<<<<<<<<<<<<<<", Integer.toString(message.networkResponse.statusCode)));
                } else {
                    FileUploader fileUploader = new FileUploader(reportsActivity);
                    ReceiveData handler = new ReceiveData() {
                        @Override
                        public void onReceiveData(String data) {
                            Log.i("hey my body count", data.toString());
                            System.out.print(data);
                            BiConsumer<Consumer<String>, Response.ErrorListener> test = reportsServices.sendReport(description, title, UUID.fromString(data.toString()));
                            test.accept(array -> {

                            }, message ->
                                    Log.i("<<<<<<<<<<<<<<", Integer.toString(message.networkResponse.statusCode)));
                            //data.get(0);
                            //content.setText(data.toString());
                        }
                    };
                    fileUploader.setHandler(handler);
                    fileUploader.execute(pic);
                }
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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pic = null;
                    try {
                        pic = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    Uri file = FileProvider.getUriForFile(this,
                            "com.navispeed.greg.androidmodularize.fileprovider",
                            pic);
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                    startActivityForResult(takePictureIntent, 0);
                }
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Citizen_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView image = findViewById(R.id.imageconfirm);

        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = Uri.fromFile(pic);
                    image.setImageURI(selectedImage);
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    image.setImageURI(selectedImage);
                }
                break;
        }
    }
}

