package com.navispeed.greg.common;
/* Created by jonas_e on 01/12/2017. */

import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class APICaller extends AsyncTask<String, Void, String> {

    private static String API_URL = "https://citizen.navispeed.eu/api";

    private ReceiveData handler;

    public static void get(String endpoint, ReceiveData handler) {
        new APICaller().setHandler(handler).get(endpoint);
    }

    public void get(String endpoint) {
        execute(API_URL + (endpoint.startsWith("/") ? endpoint : "/" + endpoint), "GET");
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            final int PARAM_URL = 0;
            final int PARAM_METHOD = 1;

            URL url = new URL(params[PARAM_URL]);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            //connection.setDoOutput(true);
            connection.setRequestMethod(params[PARAM_METHOD]);
            connection.setRequestProperty("Authorization", "Bearer " + StoredData.getInstance().getAccessToken());
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.getResponseCode();
 //           connection.connect();
            InputStream in = new BufferedInputStream(connection.getInputStream());
            String inString = IOUtils.toString(in, StandardCharsets.UTF_8.name());
            connection.disconnect();
            return inString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public APICaller setHandler(ReceiveData handler) {
        this.handler = handler;
        return this;
    }

    protected void onPostExecute(String result) {
        if (result != null) //pitète afficher une erreur de network?
            handler.onReceiveData(result);
    }
}
