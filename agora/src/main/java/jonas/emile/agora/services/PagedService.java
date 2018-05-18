package jonas.emile.agora.services;

import com.android.volley.Response;
import com.navispeed.greg.common.Consumer;

import org.json.JSONArray;

import java.util.function.BiConsumer;

public interface PagedService {

    BiConsumer<Consumer<String>, Response.ErrorListener> getEntryCount();
    BiConsumer<Consumer<JSONArray>, Response.ErrorListener> getEntries(int pageNb, int pageSize);
}
