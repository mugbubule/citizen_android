package com.navispeed.greg.androidmodularize.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

    public static interface NotificationEvent {
        NotificationCompat.Builder consume(Context context, android.support.v4.app.NotificationCompat.Builder nBuilder, String[] params);
    }

    private static HashMap<String, Pair<String, NotificationEvent>> notificationEventMap = new HashMap<>();
    private HashMap<Integer, Notification> notificationSent = new HashMap<>();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NotificationService() {
        super("NotificationService");
        register("Test", "/api", (a, b, c) -> {
            Intent intent = new Intent(this, NewsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            return b.setContentIntent(pendingIntent);
        });
    }

    public static void register(String title, String endpointPattern, NotificationEvent onReceive) {
        notificationEventMap.put(endpointPattern, new Pair<>(title, onReceive));
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (true) {
            try {
                Log.i("NotificationService", "Réveil");
                fetch();
                process(new Notification(UUID.randomUUID(), UUID.randomUUID(), "Nouvelle actualité", "", "2018-05-22 22:00:00", false, "/api"));
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void fetch() {
        APICaller.get(this, "/api/notification/unread", (res) -> {
            Log.i("NotificationService", String.format("Receive %d", res.length()));
            final Set<String> oldNotification = StoredData.getInstance().getNotifications();
            final Notification[] notifications = new Gson().fromJson(res.toString(), Notification[].class);
            final Stream<Notification> notificationStream = Arrays.stream(notifications).filter(n -> !oldNotification.contains(n.getUuid().toString()));

            notificationStream.forEach(this::process);

            StoredData.getInstance().setNotifications(Arrays.stream(notifications).map(n -> n.getUuid().toString()).collect(Collectors.toSet()));
        }, IGNORE, true, JSONArray.class);
    }

    private void process(Notification notification) {
        android.support.v4.app.NotificationCompat.Builder nouvelle = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.orleans)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getContent())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        final Optional<Map.Entry<String, Pair<String, NotificationEvent>>> first = notificationEventMap.entrySet().stream().filter(c -> notification.getUrl().matches(c.getKey())).findFirst();
        if (!first.isPresent()) {
            return;
        }
        nouvelle = first.get().getValue().second.consume(this, nouvelle, new String[]{});
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(this.notificationSent.size() + 1, nouvelle.build());
    }
}
