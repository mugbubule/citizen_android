package com.navispeed.greg.common;
/* Created by jonas_e on 20/02/2018. */

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class StoredData {
    private static StoredData instance = null;
    private SharedPreferences sharedPreferences;
    private String accessToken;
    private String refreshToken;
    private Set<String> notifications;

    private StoredData() {
    }

    public void init(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        accessToken = sharedPreferences.getString("accessToken", "placeholder if not initizalized");
        refreshToken = sharedPreferences.getString("refreshToken", "placeholder if not initizalized");
        notifications = sharedPreferences.getStringSet("notifications", new HashSet<String>());
    }

    public void close() {
        sharedPreferences.edit().putString("accessToken", accessToken).apply();
        sharedPreferences.edit().putString("refreshToken", refreshToken).apply();
        sharedPreferences.edit().putStringSet("notifications", notifications).apply();
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
