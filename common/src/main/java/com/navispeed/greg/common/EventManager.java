package com.navispeed.greg.common;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class EventManager {

    public enum Event {
        ON_LOGIN,
        CALL_LOGIN
    }

    private static EventManager instance = new EventManager();
    private Map<Event, Consumer<Context>> events = new HashMap<>();

    private EventManager() {

    }

    public static EventManager getInstance() {
        return instance;
    }

    public void add(Event e, Consumer<Context> fct) {
        this.events.put(e, fct);
    }

    @SuppressLint("NewApi")
    public void call(Event e, Context ctx) {
        this.events.getOrDefault(e, (Context c) -> {}).apply(ctx);
    }

}
