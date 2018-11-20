package com.navispeed.greg.androidmodularize.services;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.navispeed.greg.androidmodularize.models.Notification;
import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.Consumer;

import org.json.JSONArray;
import org.json.JSONObject;

import jonas.emile.news.NewsActivity;
import jonas.emile.poll.activity.PollListActivity;

/* Created by jonas_e on 14/11/2018. */
public class NotificationRouter extends AppCompatActivity {
    @Override
    protected void onStart() {
        super.onStart();
        Class target = null;
        switch (getIntent().getStringExtra("route")) {
            case "news":
                target = NewsActivity.class;
            case "consultation":
                target = PollListActivity.class;
        }
        Intent intent = new Intent(this, target);
        Response.ErrorListener onError = error -> Log.i("NotificationRouter", Integer.toString(error.networkResponse.statusCode));
        APICaller.put(this, "/notification/" + getIntent().getStringExtra("UUID"), new JSONObject(), consumable -> {}, onError, false, Void.class);
        startActivity(intent);
    }
}
