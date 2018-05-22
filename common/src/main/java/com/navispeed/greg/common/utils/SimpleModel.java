package com.navispeed.greg.common.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class SimpleModel {

    private static final Map<String, JSONToField> map;

    static {
        Map<String, JSONToField> tmpMap = new TreeMap<>();
        tmpMap.put(String.class.getName(), (field, target, src) -> field.set(target, src.getString(field.getName())));
        tmpMap.put(int.class.getName(), (field, target, src) -> field.setInt(target, src.getInt(field.getName())));
        tmpMap.put(boolean.class.getName(), (field, target, src) -> field.setBoolean(target, src.getBoolean(field.getName())));
        tmpMap.put(double.class.getName(), (field, target, src) -> field.setDouble(target, src.getDouble(field.getName())));
        tmpMap.put("com.android.tools.ir.runtime.IncrementalChange", (field, target, src) -> {});
        map = Collections.unmodifiableMap(tmpMap);
    }

    public SimpleModel() {
    }

    public SimpleModel(JSONObject json) {
        for (Field field : this.getClass().getFields()) {
            try {
                if (!field.getName().equals("serialVersionUID")) {
                    map.get(field.getType().getName()).apply(field, this, json);
                }
            } catch (JSONException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private interface JSONToField<T> {
        void apply(Field field, SimpleModel target, JSONObject src) throws JSONException, IllegalAccessException;
    }
}
