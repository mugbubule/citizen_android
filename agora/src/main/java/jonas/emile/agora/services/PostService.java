package jonas.emile.agora.services;

import android.content.Context;

import com.android.volley.Response;
import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.Consumer;

import org.json.JSONArray;

import java.util.function.BiConsumer;

public class PostService implements PagedService {
    private Context c;
    private String threadId;

    public static final Response.ErrorListener IGNORE = (ignore) -> {};

    public PostService(Context c, String threadId) {
        this.c = c;
        this.threadId = threadId;
    }

    public BiConsumer<Consumer<String>, Response.ErrorListener> getEntryCount() {
        return (Consumer<String> consumer, Response.ErrorListener onError) ->
                APICaller.get(c, "/threads/thread/" + threadId + "/posts/count", consumer, onError, true, String.class);
    }

    public BiConsumer<Consumer<JSONArray>, Response.ErrorListener> getEntries(int pageNb, int pageSize) {
        return (Consumer<JSONArray> consumer, Response.ErrorListener onError) ->
                APICaller.get(c, "/threads/thread/" + threadId + "/posts?pageNb=" + pageNb + "&pageSize=" + pageSize, consumer, onError, true, JSONArray.class);
    }
}
