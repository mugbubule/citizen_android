package com.navispeed.greg.common;
/* Created by jonas_e on 01/12/2017. */

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class APICaller extends AsyncTask<String, Void, String> {

    public static final Response.ErrorListener IGNORE = (ignore) -> {
    };
    public static final Response.ErrorListener NOT_CONNECTED = (ignore) -> {
    };

    private static String API_URL = "https://citizen.navispeed.eu/api";

    private ReceiveData handler;

    private static RetryPolicy retryPolicy = new RetryPolicy() {
        int retry = 0;
        int maxRetry = 1;

        @Override
        public int getCurrentTimeout() {
            return 3000;
        }

        @Override
        public int getCurrentRetryCount() {
            return retry;
        }

        @Override
        public void retry(VolleyError error) throws VolleyError {
            if (retry < maxRetry) {
                throw error;
            }
            retry += 1;
            if (error.networkResponse.statusCode == 401) {
                new RefreshToken().execute();
            }
        }
    };

    public static void refreshTokenOnInit() {
        new RefreshToken().execute();
    }

    private static class RefreshToken extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                URL url = new URL("https://oauth.citizen.navispeed.eu/oauth/token");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Authorization", "Basic Y2l0aXplbjpzZWNyZXQ=");
                connection.setRequestMethod("POST");
                String parameters = "refresh_token=" + StoredData.getInstance().getRefreshToken() + "&grant_type=refresh_token";
                connection.setFixedLengthStreamingMode(
                        parameters.getBytes().length);
                PrintWriter out = new PrintWriter(connection.getOutputStream());
                out.print(parameters);
                out.close();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                int code = connection.getResponseCode();
                if (code == 200) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    String inString = IOUtils.toString(in, StandardCharsets.UTF_8.name());
                    JSONObject jObject = new JSONObject(inString);
                    StoredData.getInstance().setAccessToken(jObject.getString("access_token"));
                    StoredData.getInstance().setRefreshToken(jObject.getString("refresh_token"));
                    return true;
                }
                connection.disconnect();
                return false;
            } catch (JSONException | IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private static class StringRequestWithAuth extends StringRequest {

        JSONObject body = new JSONObject();
        boolean withAuth;

        public StringRequestWithAuth(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            this(method, url, listener, errorListener, false);
        }

        StringRequestWithAuth(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener, boolean withAuth) {
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

        void setBody(JSONObject body) {
            this.body = body;
        }
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
        //stringRequest.setRetryPolicy(retryPolicy);
        queue.add(stringRequest);
    }

    public static <T> void post(Context c, String endpoint, JSONObject body, Consumer<T> onSuccess, Response.ErrorListener onError, boolean withAuth, Class<T> as) {
        Log.i("APICaller", String.format("Post %s withBody %s", endpoint, body.toString()));
        withBody(Request.Method.POST, c, endpoint, body, onSuccess, onError, withAuth, as);
    }

    public static <T> void put(Context c, String endpoint, JSONObject body, Consumer<T> onSuccess, Response.ErrorListener onError, boolean withAuth, Class<T> as) {
        Log.i("APICaller", String.format("Put %s withBody %s", endpoint, body.toString()));
        withBody(Request.Method.PUT, c, endpoint, body, onSuccess, onError, withAuth, as);
    }

    public static <T> void delete(Context c, String endpoint, JSONObject body, Consumer<T> onSuccess, Response.ErrorListener onError, boolean withAuth, Class<T> as) {
        Log.i("APICaller", String.format("Delete %s withBody %s", endpoint, body.toString()));
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
    public static void get(String endpoint, ReceiveData handler) {
        new APICaller().setHandler(handler).get(endpoint);
    }

    @Deprecated
    public void get(String endpoint) {
        execute(API_URL + (endpoint.startsWith("/") ? endpoint : "/" + endpoint), "GET");
    }

    @Deprecated
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
