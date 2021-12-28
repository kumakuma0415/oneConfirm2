package com.example.oneconfirm2;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.ServiceCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class PushNotificationService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //onStartCommandが実行されているかLogで確認する
        Log.d("LocalService", "onStartCommand Received start id" + startId + ": " + intent);

        //サービスの開始をトーストで表示
        Toast toast = makeText(this, "自宅までの距離を観測します", LENGTH_LONG);
        toast.show();

        //通知をタッチすると、アプリが起動する。そして表記したActivityに画面が出てくる。
        intent = new Intent(this, ChecklistActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, intent, 0);

        //通知に関する設定全般
        String CHANNEL_ID = "0";
        NotificationCompat.Builder nfBuilder = new NotificationCompat.Builder(
                this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_checklist_24)
                .setContentTitle("鍵を締めましたか？ 部屋の電気を消しましたか？…")
                .setContentText("こちらをタップして確認")
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        //チャンネルを作成。通知がonかoffか判断するためのもの。
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "ListView notification", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("This channel is for ListView notification.");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        int NOTIFICATION_ID = 0;

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);


        //タイマーを新規生成
        Timer timer1 = new Timer();
        //ハンドラーを新規生成
        Handler handler1 = new Handler();
        //フォーマットを新規生成
        DecimalFormat decimalFormat1 = new DecimalFormat("0.000");
        //*
        //タイマーに直接スケジュール(1秒後に1秒間隔の処理を開始)を追加して実行
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                //システム時間を取得
                double systemTime1 = System.currentTimeMillis();
                //"0.000秒"形式に成型してログに出力
                Log.d("SytemTime = ", decimalFormat1.format(systemTime1 / 3000) + "秒");
                //直接だとエラーになるのでハンドラーを経由して画面表示を変更する
                handler1.post(new Runnable() {
                    @Override
                    public void run() {

                        //保存した値を引き出し、２つの距離に代入する
                        SharedPreferences pref = getSharedPreferences("text_status", Context.MODE_PRIVATE);

                        String set_distance = pref.getString("set_distance", "1");
                        Double double_set_distance = Double.parseDouble(set_distance);
                        String num_distance_to_home = pref.getString("num_distance_to_home", "0");
                        Double double_num_distance = Double.parseDouble(num_distance_to_home);

                        //距離算出はサービス内で行うこと
                        Log.d("double_num_distance", ":" + double_num_distance);

                        //通知が何度も行われるための対策。エラーの原因
                        //onStartCommand以外になにか式を書いて、通知を止めさせる
                        //通知をタップして、サービスを終了させる
                        String count0 = pref.getString("COUNT", "0");
                        Integer count = Integer.valueOf(count0);

                        //通知の条件式と通知の命令
                        if (double_num_distance >= double_set_distance) {
                            notificationManagerCompat.notify(NOTIFICATION_ID, nfBuilder.build());

                            count += 1;

                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("COUNT", String.valueOf(count));
                            editor.commit();
                        }
                    }
                });
            }
        }, 1000, 1000);

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}