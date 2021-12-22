package com.example.oneconfirm2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class PushNotificationService extends Service {
    public PushNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {

        /**
         * Set an Action for Notification is tapped. In this case, when the notification is
         * tapped, the application will be launched.
         */

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, intent, 0);


        /**
         * Configure Notification contents how it show at notification bar.
         */
        String CHANNEL_ID = "0";
        NotificationCompat.Builder nfBuilder = new NotificationCompat.Builder(
                this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_checklist_24)
                .setContentTitle("鍵を締めましたか？ 部屋の電気を消しましたか？…")
                .setContentText("こちらをタップして確認")
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        /**
         * Create a channel for the notification and set importance for it.
         * User can configure whether the notification turns on or off by channels.
         */
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "ListView notification", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("This channel is for ListView notification.");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        int NOTIFICATION_ID = 0;

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, nfBuilder.build());

    }
}