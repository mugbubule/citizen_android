package com.navispeed.greg.common;
/* Created by jonas_e on 01/12/2017. */

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class APICaller extends AsyncTask<String, Void, String> {

    private static String API_URL = "https://citizen.navispeed.eu/api";

    private ReceiveData handler;

    private static class StringRequestWithAuth extends StringRequest {

        JSONObject body = new JSONObject();
        boolean withAuth;

        public StringRequestWithAuth(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            this(method, url, listener, errorListener, false);
        }

        public StringRequestWithAuth(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener, boolean withAuth) {
            super(method, url, listener, errorListener);
            this.withAuth = withAuth;
        }

        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> params = new HashMap<>();
            if (getMethod() != Method.GET)
                params.put("Content-Type", "application/json");
            if (withAuth)
                params.put("Authorization", "Bearer " + StoredData.getInstance().getAccessToken());
            return params;
        }

        @Override
        public byte[] getBody() {
            return body.toString().getBytes();
        }

        @Override
        public String getBodyContentType() {
            return "application/json";
        }

        public void setBody(JSONObject body) {
            this.body = body;
        }
    }

    @Deprecated
    public static void get(String endpoint, ReceiveData handler) {
        new APICaller().setHandler(handler).get(endpoint);
    }

    public static <T> void get(Context c, String endpoint, Consumer<T> onSuccess, Response.ErrorListener onError, boolean withAuth, Class<T> as) {
        RequestQueue queue = Volley.newRequestQueue(c);

        StringRequest stringRequest = new StringRequestWithAuth(Request.Method.GET, getAbsoluteUrl(endpoint),
                (String r) -> {
                    try {
                        onSuccess.apply(as.getDeclaredConstructor(String.class).newInstance(r));
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        Log.e("APICaller.get", "LogicalError :" + e.getMessage());
                    }
                }, onError, withAuth);
        queue.add(stringRequest);
    }

    public static <T> void post(Context c, String endpoint, JSONObject body, Consumer<T> onSuccess, Response.ErrorListener onError, boolean withAuth, Class<T> as) {
        withBody(Request.Method.POST, c, endpoint, body, onSuccess, onError, withAuth, as);
    }

    public static <T> void put(Context c, String endpoint, JSONObject body, Consumer<T> onSuccess, Response.ErrorListener onError, boolean withAuth, Class<T> as) {
        withBody(Request.Method.PUT, c, endpoint, body, onSuccess, onError, withAuth, as);
    }

    public static <T> void delete(Context c, String endpoint, JSONObject body, Consumer<T> onSuccess, Response.ErrorListener onError, boolean withAuth, Class<T> as) {
        withBody(Request.Method.PUT, c, endpoint, body, onSuccess, onError, withAuth, as);
    }

    private static <T> void withBody(int m, Context c, String endpoint, JSONObject body, Consumer<T> onSuccess, Response.ErrorListener onError, boolean withAuth, Class<T> as) {
        RequestQueue queue = Volley.newRequestQueue(c);

        StringRequestWithAuth stringRequest = new StringRequestWithAuth(m, getAbsoluteUrl(endpoint),
                (String r) -> {
                    try {
                        onSuccess.apply(as.getDeclaredConstructor(String.class).newInstance(r));
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        Log.e("APICaller.post", "LogicalError :" + e.getMessage());
                    }
                }, onError, withAuth);
        stringRequest.setBody(body);
        queue.add(stringRequest);
    }

    @Deprecated
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

    @Deprecated
    public APICaller setHandler(ReceiveData handler) {
        this.handler = handler;
        return this;
    }

    @Deprecated
    protected void onPostExecute(String result) {
        if (result != null) //pitète afficher une erreur de network?
            handler.onReceiveData(result);
    }

    @NonNull
    private static String getAbsoluteUrl(String endpoint) {
        if (endpoint.isEmpty()) {
            return API_URL;
        }
        if (endpoint.matches("http.+")) {
            return endpoint;
        }

        return API_URL + (endpoint.startsWith("/") ? endpoint : "/" + endpoint);
    }
}
