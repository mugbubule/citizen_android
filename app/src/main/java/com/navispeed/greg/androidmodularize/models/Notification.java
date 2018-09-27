package com.navispeed.greg.androidmodularize.models;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.UUID;

public class Notification {
    private final static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private UUID uuid;
    private UUID userUUID;
    private String title;
    private String content;
    private String created;
    private Boolean viewed;
    private String url;

    public Notification(UUID uuid, UUID userUUID, String title, String content, String created, Boolean viewed, String url) {
        this.uuid = uuid;
        this.userUUID = userUUID;
        this.title = title;
        this.content = content;
        this.created = created;
        this.viewed = viewed;
        this.url = url;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getUserUUID() {
        return userUUID;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public DateTime getCreated() {
        return formatter.parseDateTime(this.created);
    }

    public Boolean getViewed() {
        return viewed;
    }

    public String getUrl() {
        return url != null ? url : "";
    }

    @Override
    public String toString() {
        return "Notification{" +
                "uuid=" + uuid +
                ", userUUID=" + userUUID +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", created='" + created + '\'' +
                ", viewed=" + viewed +
                ", url='" + url + '\'' +
                '}';
    }
}
