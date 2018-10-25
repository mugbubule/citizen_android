package jonas.emile.reports.services;

import android.content.Context;

import com.android.volley.Response;
import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.Consumer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;
import java.util.function.BiConsumer;

/* Created by jonas_e on 22/10/2018. */
public class ReportsServices {

    private Context c;

    public ReportsServices(Context c) {
        this.c = c;
    }

    public BiConsumer<Consumer<String>, Response.ErrorListener> sendReport(String title, String description) {
        JSONObject content = new JSONObject();
        try {
            content.put("title", title);
            content.put("description", description);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return (Consumer<String> consumer, Response.ErrorListener onError) ->
                APICaller.post(c, "/reports", content, consumer, onError, true, String.class);
    }

    public BiConsumer<Consumer<String>, Response.ErrorListener> sendReport(String title, String description, UUID imgUUID) {
        JSONObject content = new JSONObject();
        try {
            content.put("title", title);
            content.put("description", description);
            content.put("img_uuid", imgUUID.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return (Consumer<String> consumer, Response.ErrorListener onError) ->
                APICaller.post(c, "/reports", content, consumer, onError, true, String.class);
    }
}
