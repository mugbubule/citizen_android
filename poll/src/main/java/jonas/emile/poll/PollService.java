package jonas.emile.poll;

import android.annotation.SuppressLint;
import android.content.Context;
import com.android.volley.Response;
import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.Consumer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;
import java.util.function.BiConsumer;

public class PollService {
    private Context c;

    @SuppressLint("NewApi")
    public interface apiConsummer<R> extends BiConsumer<Consumer<R>, Response.ErrorListener> {
    }

    public static final Response.ErrorListener IGNORE = (ignore) -> {};

    public PollService(Context c) {
        this.c = c;
    }

    public apiConsummer<JSONArray> getAll() {
        return (Consumer<JSONArray> consumer, Response.ErrorListener onError) -> APICaller.get(c, "/poll/", consumer, onError, true, JSONArray.class);
    }

    public apiConsummer<JSONObject> getOne(UUID uuid) {
        return (Consumer<JSONObject> consumer, Response.ErrorListener onError) -> APICaller.get(c, String.format("/poll/%s", uuid), consumer, onError, true, JSONObject.class);
    }

    public apiConsummer<JSONArray> getAvailablesChoices(UUID pollUuid) {
        return (Consumer<JSONArray> consumer, Response.ErrorListener onError) -> APICaller.get(c, String.format("/poll/poll/%s/choices", pollUuid), consumer, onError, true, JSONArray.class);
    }
}
