package jonas.emile.poll.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.UUID;

public class Poll implements Serializable {

    private final static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");


    private UUID uuid;
    private String proposition;
    private String details;
    private String created;
    private String end;
    private String updated;
    private String published;

    public Poll(UUID uuid, String proposition, String details, String created, String end, String updated, String published) {
        this.uuid = uuid;
        this.proposition = proposition;
        this.details = details;
        this.created = created;
        this.end = end;
        this.updated = updated;
        this.published = published;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getProposition() {
        return proposition;
    }

    public String getDetails() {
        return details;
    }

    public DateTime getCreated() {
        return formatter.parseDateTime(created);
    }

    public String getPublished() { return published; }

    public DateTime getEnd() {
        return formatter.parseDateTime(end);
    }

    public DateTime getUpdated() {
        return formatter.parseDateTime(updated);
    }

    @Override
    public String toString() {
        return "Poll{" +
                "uuid=" + uuid +
                ", proposition='" + proposition + '\'' +
                ", details='" + details + '\'' +
                ", created='" + getCreated() + '\'' +
                ", end='" + getEnd() + '\'' +
                ", updated='" + getUpdated() + '\'' +
                ", published='" +getPublished() + '\'' +
                '}';
    }


}
