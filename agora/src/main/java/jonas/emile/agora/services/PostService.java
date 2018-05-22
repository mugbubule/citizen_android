package jonas.emile.agora.services;

import android.content.Context;

import com.android.volley.Response;
import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.Consumer;
import com.navispeed.greg.common.paging.PagedService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.function.BiConsumer;

public class PostService implements PagedService {
    private Context c;
    private String threadId;

    public PostService(Context c, String threadId) {
        this.c = c;
        this.threadId = threadId;
    }

    @Override
    public BiConsumer<Consumer<Integer>, Response.ErrorListener> getEntryCount() {
        return (Consumer<Integer> consumer, Response.ErrorListener onError) ->
                APICaller.get(c, "/threads/thread/" + threadId + "/posts/count", consumer, onError, true, Integer.class);
    }

    @Override
    public BiConsumer<Consumer<JSONArray>, Response.ErrorListener> getEntries(int pageNb, int pageSize) {
        return (Consumer<JSONArray> consumer, Response.ErrorListener onError) ->
                APICaller.get(c, "/threads/thread/" + threadId + "/posts?pageNb=" + pageNb + "&pageSize=" + pageSize, consumer, onError, true, JSONArray.class);
    }

    public BiConsumer<Consumer<String>, Response.ErrorListener> sendMessage(String message) { //TODO user name
        return (Consumer<String> consumer, Response.ErrorListener onError) ->
                APICaller.post(c, "/threads/posts?tid=" + threadId + "&author=user&message=" + message, new JSONObject(), consumer, onError, true, String.class);
    }
}
