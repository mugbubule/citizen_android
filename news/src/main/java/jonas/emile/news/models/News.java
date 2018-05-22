package jonas.emile.news.models;

import com.navispeed.greg.common.utils.SimpleModel;

import org.json.JSONObject;

public class News extends SimpleModel {

    public String uuid;
    public String title;
    public String content;
    public String subtitle;
    public String img_uuid;
    public String created;
    public String updated;

    public News(JSONObject object) {
        super(object);
    }
}
