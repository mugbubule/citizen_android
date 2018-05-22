package jonas.emile.agora.services;

import android.content.Context;

import com.android.volley.Response;
import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.Consumer;

import org.json.JSONArray;

import java.util.function.BiConsumer;

public class CategoryService {

    private Context c;

    public CategoryService(Context c) {
        this.c = c;
    }

    public BiConsumer<Consumer<JSONArray>, Response.ErrorListener> getAll() {
        return (Consumer<JSONArray> consumer, Response.ErrorListener onError) ->
                APICaller.get(c, "/threads/categories/all", consumer, onError, true, JSONArray.class);
    }
}
