package jonas.emile.events.services;

import android.content.Context;

import com.android.volley.Response;
import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.Consumer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.function.BiConsumer;

public class EventsService {

    private Context c;

    public EventsService(Context c) {
        this.c = c;
    }

    public BiConsumer<Consumer<JSONArray>, Response.ErrorListener> getAll() {
        return (Consumer<JSONArray> consumer, Response.ErrorListener onError) ->
                APICaller.get(c, "/events/all", consumer, onError, true, JSONArray.class);
    }

    public BiConsumer<Consumer<JSONObject>, Response.ErrorListener> getDetails(String uuid) {
        return (Consumer<JSONObject> consumer, Response.ErrorListener onError) ->
                APICaller.get(c, "/events/" + uuid, consumer, onError, false, JSONObject.class);
    }
}
