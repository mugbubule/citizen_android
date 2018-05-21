package jonas.emile.agora.services;

import android.content.Context;

import com.android.volley.Response;
import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.Consumer;
import com.navispeed.greg.common.utils.PagedService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.function.BiConsumer;

public class ThreadService implements PagedService {
    private Context c;
    private String categoryId;

    public ThreadService(Context c, String categoryId) {
        this.c = c;
        this.categoryId = categoryId;
    }

    @Override
    public BiConsumer<Consumer<Integer>, Response.ErrorListener> getEntryCount() {
        if (categoryId == null) {
            return (Consumer<Integer> consumer, Response.ErrorListener onError) ->
                    APICaller.get(c, "/threads/count", consumer, onError, true, Integer.class);
        } else {
            return (Consumer<Integer> consumer, Response.ErrorListener onError) ->
                    APICaller.get(c, "/threads/category/" + categoryId + "/count", consumer, onError, true, Integer.class);
        }
    }

    @Override
    public BiConsumer<Consumer<JSONArray>, Response.ErrorListener> getEntries(int pageNb, int pageSize) {
        if (categoryId == null) {
            return (Consumer<JSONArray> consumer, Response.ErrorListener onError) ->
                    APICaller.get(c, "/threads?pageNb=" + pageNb + "&pageSize=" + pageSize, consumer, onError, true, JSONArray.class);
        } else {
            return (Consumer<JSONArray> consumer, Response.ErrorListener onError) ->
                    APICaller.get(c, "/threads/category/" + categoryId + "?pageNb=" + pageNb + "&pageSize=" + pageSize, consumer, onError, true, JSONArray.class);
        }
    }

    public BiConsumer<Consumer<String>, Response.ErrorListener> createThread(String topic) {
        return (Consumer<String> consumer, Response.ErrorListener onError) ->
                APICaller.post(c, "/threads?topic=" + topic + (categoryId == null ? "" : "&category=" + categoryId), new JSONObject(), consumer, onError, true, String.class);
    }
}
