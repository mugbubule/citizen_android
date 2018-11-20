package com.navispeed.greg.common;
/* Created by jonas_e on 20/02/2018. */

import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class StoredData {
    private static StoredData instance = null;
    private SharedPreferences sharedPreferences;
    private String accessToken;
    private String refreshToken;
    private Set<String> notifications;
    private Set<String> votedPolls = new HashSet<>();
    private boolean logged;

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public boolean getLogged() {
        return this.logged;
    }

    private StoredData() {
    }

    public Set<String> getVotedPolls() {
        return votedPolls;
    }

    public void addVotedPolls(String uuid) {
        votedPolls.add(uuid);
    }

/*    public String votedPollsToString() {
        StringBuilder store = new StringBuilder();
        Object[] data = votedPolls.toArray();
        for (int i = 0; i < votedPolls.size(); i++) {
            store.append(data[i].toString());
            store.append(",");
        }
        return store.toString();
    }*/

    public void init(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        accessToken = sharedPreferences.getString("accessToken", "placeholder if not initizalized");
        refreshToken = sharedPreferences.getString("refreshToken", "placeholder if not initizalized");
        APICaller.refreshTokenOnInit();
        notifications = sharedPreferences.getStringSet("notifications", new HashSet<String>());
        votedPolls = sharedPreferences.getStringSet("votedPolls", votedPolls);
    }

    public void close() {
        sharedPreferences.edit().putString("accessToken", accessToken).apply();
        sharedPreferences.edit().putString("refreshToken", refreshToken).apply();
        sharedPreferences.edit().putStringSet("notifications", notifications).apply();
        sharedPreferences.edit().putStringSet("votedPolls", votedPolls).apply();
    }

    public static synchronized StoredData getInstance() {
        if(instance == null){
            instance = new StoredData();
        }
        return instance;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Set<String> getNotifications() {
        if (notifications == null)
            notifications = new HashSet<>();
        return notifications;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setNotifications(Set<String> notifications) {
        this.notifications = notifications;
    }
}
