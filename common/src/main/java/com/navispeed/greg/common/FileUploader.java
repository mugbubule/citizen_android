package com.navispeed.greg.common;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

/* Created by jonas_e on 25/10/2018. */
public class FileUploader  extends AsyncTask<File, Void, String> {
    private Context c;
    private ReceiveData handler;

    public FileUploader(Context c) {
        this.c = c;
    }

    @Override
    protected String doInBackground(File... file) {
        try {
            URL url = new URL("https://citizen.navispeed.eu/api/common/upload/file");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            //connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + StoredData.getInstance().getAccessToken());
            connection.setRequestProperty("Content-Type", "image/jpeg");
            connection.getOutputStream().write(IOUtils.toByteArray(file[0].toURI()));
//            connection.setChunkedStreamingMode();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.getResponseCode();
            InputStream in = new BufferedInputStream(connection.getInputStream());
            String inString = IOUtils.toString(in, StandardCharsets.UTF_8.name());
            connection.disconnect();
            return inString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public FileUploader setHandler(ReceiveData handler) {
        this.handler = handler;
        return this;
    }

    protected void onPostExecute(String result) {
        if (result != null) //pitète afficher une erreur de network?
            handler.onReceiveData(result);
    }
}
