package jonas.emile.events.models;

import com.navispeed.greg.common.utils.SimpleModel;

import org.json.JSONObject;

public class Event extends SimpleModel {

    public String uuid;
    public String name;
    public String description;
    public String datetime;
    public String img_uuid;
    public String created;
    public String updated;

    public Event(JSONObject object) {
        super(object);
    }
}
