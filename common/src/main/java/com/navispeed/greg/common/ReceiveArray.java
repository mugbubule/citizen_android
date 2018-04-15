package com.navispeed.greg.common;

import org.json.JSONArray;
import org.json.JSONException;

public abstract class ReceiveArray implements ReceiveData {
    @Override
    public void onReceiveData(String data) {
        try {
            JSONArray array = new JSONArray(data);
            onReceiveData(array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public abstract void onReceiveData(JSONArray data);
}
