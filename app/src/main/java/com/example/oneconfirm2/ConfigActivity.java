package com.example.oneconfirm2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.text.BreakIterator;

public class ConfigActivity extends AppCompatActivity {

    Switch c_switch;
    boolean isChecked2;
    private static final String SWITCH1_PREFKEY = "switch1";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Button co_btn_dis = findViewById(R.id.co_dis);
        co_btn_dis.setOnClickListener((View v) -> {
            startActivity(new Intent(this, DistanceActivity.class));
        });

        Button co_btn_return = findViewById(R.id.co_return);
        co_btn_return.setOnClickListener((View v) -> {
            startActivity(new Intent(this, MainActivity.class));
        });



        /**
         * Set an Action for Notification is tapped. In this case, when the notification is
         * tapped, the application will be launched.
         */

        Intent intent = new Intent(this, ChecklistActivity.class);
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

        c_switch = findViewById(R.id.c_switch);

        /*
        c_switch.setOnClickListener((View v) -> {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(NOTIFICATION_ID, nfBuilder.build());
        });
         */

        //DistanceActivity distanceActivity = new DistanceActivity();

        Button btn_pra_alert = findViewById(R.id.btn_pra_alert);
        btn_pra_alert.setOnClickListener((View v) -> {

            //DistanceActivity distanceActivity = new DistanceActivity();

            /*
            //TextView num_distance_to_home2 = distanceActivity.num_distance_to_home;
            TextView num_distance_to_home2 = findViewById(R.id.num_distance_to_home);
            String num_distance_to_home3 = num_distance_to_home2.getText().toString();
            //Log.v("distanceActivity", String.valueOf(distanceActivity.num_distance_to_home));
            double num_distance_to_home4 = Double.parseDouble(num_distance_to_home3);

            //TextView set_distance2 = distanceActivity.set_distance;
            TextView set_distance2 = findViewById(R.id.set_distance);
            String set_distance3 = set_distance2.getText().toString();
            double set_distance4 = Double.parseDouble(set_distance3);
            */

            //if(num_distance_to_home4 >= set_distance4) {
            if(7 >= 5) {
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
                notificationManagerCompat.notify(NOTIFICATION_ID, nfBuilder.build());
              //  }
            }
        });

        /*
        DistanceActivity distanceActivity = new DistanceActivity();
        String num_distance_to_home2 = distanceActivity.num_distance_to_home.getText().toString();
        double num_distance_to_home3 = Double.parseDouble(num_distance_to_home2);
        String set_distance2 = distanceActivity.set_distance.getText().toString();
        double set_distance3 = Double.parseDouble(set_distance2);

        if(num_distance_to_home3 >= set_distance3) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(NOTIFICATION_ID, nfBuilder.build());
        }
        */

        c_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    //通知を止める処理
                }else {
                    //通知を再開
                }

                SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ConfigActivity.this);
                SharedPreferences.Editor editor = preference.edit();
                editor.putBoolean(SWITCH1_PREFKEY, isChecked);
                editor.apply();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        final Switch switchButton =  findViewById(R.id.c_switch);
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(ConfigActivity.this);
        boolean isChecked = preference.getBoolean(SWITCH1_PREFKEY, false);
        switchButton.setChecked(isChecked);
    }

}