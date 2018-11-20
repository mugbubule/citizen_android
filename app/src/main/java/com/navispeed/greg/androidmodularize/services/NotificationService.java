package com.navispeed.greg.androidmodularize.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.util.Pair;
import com.google.gson.Gson;
import com.navispeed.greg.androidmodularize.R;
import com.navispeed.greg.androidmodularize.models.Notification;
import com.navispeed.greg.common.APICaller;
import com.navispeed.greg.common.StoredData;
import jonas.emile.news.NewsActivity;
import jonas.emile.poll.activity.PollListActivity;


import org.json.JSONArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.navispeed.greg.common.APICaller.IGNORE;

public class NotificationService extends IntentService {
    Map<UUID, Notification> notifications = new HashMap<>();
    NotificationManager notificationManager;

    public static interface NotificationEvent {
        NotificationCompat.Builder consume(Context context, android.support.v4.app.NotificationCompat.Builder nBuilder, String[] params);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        createNotificationChannel();
        while (true) {
            try {
                Log.i("NotificationService", "Réveil");
                fetch();
                for (Notification notification: notifications.values()) {
                    if (!notification.isViewed()) {
                        process(notification);
                    }
                }
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void fetch() {
        APICaller.get(this, "/notification/unread", (res) -> {
            Log.i("NotificationService", String.format("Receive %d", res.length()));
            Notification notification[] = new Gson().fromJson(res.toString(), Notification[].class);
            for (Notification notif: notification) {
                notifications.put(notif.getUuid(), notif);
            }
        }, IGNORE, true, JSONArray.class);
    }

    private void process(Notification notification) {

        /*Class target = null;
        switch (notification.getUrl().split("/")[1]) {
            case "news":
                target = NewsActivity.class;
            case "consultation":
                target = PollListActivity.class;
        }*/
        Intent intent = new Intent(this, NotificationRouter.class);
        intent.putExtra("UUID", notification.getUserUUID());
        intent.putExtra("route", notification.getUrl().split("/")[1]);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.orleans)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getContent())
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(0, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "citizen", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("citizen");
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
