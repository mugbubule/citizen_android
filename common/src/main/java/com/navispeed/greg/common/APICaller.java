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

public class APICaller extends AsyncTask<String, Void, JSONArray> {

    private static String API_URL = "https://citizen.navispeed.eu/api";

    private ReceiveData handler;

    public void get(String endpoint) {
        execute(API_URL + (endpoint.startsWith("/") ? endpoint : "/" + endpoint), "GET");
    }

    private static int PARAM_URL = 0;
    private static int PARAM_METHOD = 1;
    @Override
    protected JSONArray doInBackground(String... params) {
        try {
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
            JSONArray jObject = new JSONArray(inString);
            connection.disconnect();
            return jObject;
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setHandler(ReceiveData handler) {
        this.handler = handler;
    }

    protected void onPostExecute(JSONArray result) {
        if (result != null) //pitète afficher une erreur de network?
            handler.onReceiveData(result);
    }
}
