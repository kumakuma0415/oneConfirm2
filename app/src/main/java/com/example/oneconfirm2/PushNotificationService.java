package com.example.oneconfirm2;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class PushNotificationService extends Service {

    FusedLocationProviderClient fusedLocationClient;
    static String TAG ;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    Handler mServiceHandler;
    NotificationManager mNotificationManager;
    String androidId;
    private Timer timer1 = null;
    private Timer timer2 = null;
    //ハンドラーを新規生成
    Handler handler1 = new Handler();


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TestService", "onCreate");

    }

    @SuppressLint("HardwareIds")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    //「 サービスを開始する 」のボタンを押したときの処理
    public int onStartCommand(Intent intent, int flags, int startId) {

        TAG = PushNotificationService.class.getSimpleName();//このアプリのパッケージ名？
        Log.i(TAG, "onCreate");

        androidId= Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mLocationRequest = LocationRequest.create();

        //出来る限り高精度の位置情報を表示する 電力をかなり消費
        mLocationRequest.setInterval(10000);//おおよそ10秒毎
        mLocationRequest.setFastestInterval(5000);//最短で5秒毎
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //タイマーを新規生成
                timer1 = new Timer(true);
                //フォーマットを新規生成
                DecimalFormat decimalFormat1 = new DecimalFormat("0.000");

                //タイマーに直接スケジュール(3秒後に3秒間隔の処理を開始)を追加して実行
                timer1.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //システム時間を取得
                        double systemTime1 = System.currentTimeMillis();
                        //"0.000秒"形式に成型してログに出力
                        Log.d("SystemTime = ", decimalFormat1.format(systemTime1 / 3000) + "秒");
                        //直接だとエラーになるのでハンドラーを経由して画面表示を変更する
                        handler1.post(() -> {
                            double str_now_latitude = locationResult.getLastLocation().getLatitude();
                            double str_now_longitude = locationResult.getLastLocation().getLongitude();

                            SharedPreferences pref = getSharedPreferences("text_status",Context.MODE_PRIVATE);
                            String home_latitude = pref.getString("home_latitude","0");
                            String home_longitude = pref.getString("home_longitude","0");

                            double home_latitude3 = Double.parseDouble(home_latitude);
                            double home_longitude3 = Double.parseDouble(home_longitude);

                            //距離算出をして、変数に代入
                            float[] distance =
                                    getDistance(str_now_latitude, str_now_longitude, home_latitude3, home_longitude3);

                            Log.d("DISTANCE", distance[0] + "m");

                            //距離を保存する
                            SharedPreferences pref2 = getSharedPreferences("text_status", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref2.edit();
                            editor.putString("REAL_DISTANCE", String.valueOf(distance[0]));
                            editor.apply();
                        });
                        //3秒間隔で以上の処理を行う
                    }
                }, 3000, 3000);

            }
        };

        //onStartCommandが実行されているかLogで確認する
        Log.d("LocalService", "onStartCommand Received start id" + startId + ": " + intent);

        //サービスの開始をトーストで表示
        Toast toast = makeText(this, "自宅までの距離を観測します。", LENGTH_LONG);
        toast.show();

        //通知をタッチすると、アプリが起動する。そして表記したActivityに画面が出てくる。
        intent = new Intent(this, ChecklistActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this,
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
        timer2 = new Timer(true);
        //ハンドラーを新規生成
        Handler handler1 = new Handler();
        //フォーマットを新規生成
        DecimalFormat decimalFormat1 = new DecimalFormat("0.000");
        //*
        //タイマーに直接スケジュール(3秒後に3秒間隔の処理を開始)を追加して実行
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                //システム時間を取得
                double systemTime1 = System.currentTimeMillis();
                //"0.000秒"形式に成型してログに出力
                Log.d("SystemTime = ", decimalFormat1.format(systemTime1 / 3000) + "秒");
                //直接だとエラーになるのでハンドラーを経由して画面表示を変更する
                handler1.post(() -> {

                    //保存した値を引き出し、２つの距離に代入する
                    SharedPreferences pref = getSharedPreferences("text_status", Context.MODE_PRIVATE);

                    String set_distance = pref.getString("set_distance", "1");
                    double double_set_distance = Double.parseDouble(set_distance);
                    String num_distance_to_home = pref.getString("REAL_DISTANCE", "0");
                    double double_num_distance = Double.parseDouble(num_distance_to_home);

                    //距離算出はサービス内で行うこと
                    Log.d("double_num_distance", ":" + double_num_distance);

                    //通知の条件式と通知の命令
                    if (double_num_distance >= double_set_distance) {
                        notificationManagerCompat.notify(NOTIFICATION_ID, nfBuilder.build());
                        timer2.cancel();
                    }
                });
                //3秒間隔で以上の処理を行う
            }
        }, 3000, 3000);

        return super.onStartCommand(intent, flags, startId);
    }

    //「 サービスを中断する 」を押したときの処理
    @Override
    public void onDestroy() {
        Log.i("TestService", "onDestroy");

        if(timer1 != null) {
            timer1.cancel();
            timer1 = null;
        }
        if(timer2 != null) {
            timer2.cancel();
            timer2 = null;
        }
        //サービスの開始をトーストで表示
        Toast toast = makeText(this, "観測を終了します。", LENGTH_LONG);
        toast.show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("TestService", "onBind");
        return null;
    }

    //2点間の距離（メートル）、方位角（始点、終点）を取得
    //※配列で返す[距離、始点から見た方位角、終点から見た方位角]
    public float[] getDistance(double x, double y, double x2, double y2) {
        // 結果を格納するための配列を生成
        float[] results = new float[3];
        // 距離計算
        Location.distanceBetween(x, y, x2, y2, results);

        return results;
    }

}