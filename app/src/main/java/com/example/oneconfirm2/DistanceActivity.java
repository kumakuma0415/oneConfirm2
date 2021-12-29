package com.example.oneconfirm2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;


public class DistanceActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    TextView home_latitude;
    TextView write_latitude;
    TextView home_longitude;
    TextView write_longitude;
    TextView set_distance;
    EditText assumed_distance;

    TextView now_latitude;
    TextView now_longitude;
    TextView num_distance_to_home;


    //位置情報受取をするコールバッククラス
    public class MyLocationCallback extends LocationCallback {

        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {

            // 現在値を取得
            Location location = locationResult.getLastLocation();

            // 画面に現在地の緯度経度を表示
            now_latitude = findViewById(R.id.now_latitude);
            now_longitude = findViewById(R.id.now_longitude);
            String str_now_latitude = String.valueOf(location.getLatitude());
            String str_now_longitude = String.valueOf(location.getLongitude());

            now_latitude.setText(str_now_latitude);
            now_longitude.setText(str_now_longitude);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);

        // LocationClientクラスのインスタンスを生成
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 位置情報取得開始
        startUpdateLocation();


        //自宅の緯度経度を取得するボタン(ボタン１)
        Button register_myHome = findViewById(R.id.register_my_home);
        RegisterMyHome listener1 = new RegisterMyHome();
        register_myHome.setOnClickListener(listener1);
        home_latitude = findViewById(R.id.home_latitude);
        home_longitude = findViewById(R.id.home_longitude);

        //テキストビューを取得
        num_distance_to_home = findViewById(R.id.num_distance_to_home);

        //タイマーを新規生成
        Timer timer1 = new Timer();
        //ハンドラーを新規生成
        Handler handler1 = new Handler();
        //フォーマットを新規生成
        DecimalFormat decimalFormat1 = new DecimalFormat("0.000");

        //タイマーに直接スケジュール(1秒後に1秒間隔の処理を開始)を追加して実行
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                //システム時間を取得
                double systemTime1 = System.currentTimeMillis();
                //"0.000秒"形式に成型してログに出力
                Log.d("SystemTime = ", decimalFormat1.format(systemTime1 / 1000) + "秒");
                //直接だとエラーになるのでハンドラーを経由して画面表示を変更する
                handler1.post(() -> {
                    //現在地の緯度経度を取得
                    String now_latitude2 = now_latitude.getText().toString();
                    String now_longitude2 = now_longitude.getText().toString();
                    double now_latitude3 = Double.parseDouble(now_latitude2);
                    double now_longitude3 = Double.parseDouble(now_longitude2);

                    //画面から自宅の緯度経度を取得
                    String home_latitude2 = home_latitude.getText().toString();
                    String home_longitude2 = home_longitude.getText().toString();
                    double home_latitude3 = Double.parseDouble(home_latitude2);
                    double home_longitude3 = Double.parseDouble(home_longitude2);

                    //以上の数値を用いて。距離算出
                    float[] distance =
                            getDistance(now_latitude3, now_longitude3, home_latitude3, home_longitude3);

                    //距離をUIに表示
                    String dis = String.valueOf(distance[0]);
                    num_distance_to_home = findViewById(R.id.num_distance_to_home);
                    num_distance_to_home.setText(dis);

                    Log.d("distance", ":" + distance[0]);

                    //距離を保存する
                    SharedPreferences pref = getSharedPreferences("text_status", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("REAL_DISTANCE", num_distance_to_home.getText().toString());
                    editor.putString("SET_DISTANCE", set_distance.getText().toString());
                    editor.apply();
                });
            }
        }, 10000, 10000);
        num_distance_to_home = findViewById(R.id.num_distance_to_home);
        //*/


        //プッシュ通知するかどうかを決める基準の距離を登録するボタン（ボタン３）
        Button register_distance = findViewById(R.id.distance_to_home);
        RegisterDistance listener3 = new RegisterDistance();
        register_distance.setOnClickListener(listener3);
        set_distance = findViewById(R.id.set_distance);

        //戻るボタン　ConfigActivityに画面遷移
        Button d_btn_return = findViewById(R.id.distance_btn_return);
        d_btn_return.setOnClickListener((View v) -> startActivity(new Intent(this, MainActivity.class)));

    }
    //onCreate終了


    //以下はボタンの関数の詳細
    //自宅の緯度経度を取得するボタン(ボタン１)
    private class RegisterMyHome implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //自宅の緯度を取得して、表示
            home_latitude = findViewById(R.id.home_latitude);
            write_latitude = findViewById(R.id.now_latitude);
            String text1 = write_latitude.getText().toString();
            home_latitude.setText(text1);

            //自宅の経度を取得して、表示
            home_longitude = findViewById(R.id.home_longitude);
            write_longitude = findViewById(R.id.now_longitude);
            String text2 = write_longitude.getText().toString();
            home_longitude.setText(text2);
        }
    }



    //2点間の距離（メートル）、方位角（始点、終点）を取得
    //配列で返す[距離、始点から見た方位角、終点から見た方位角]
    public float[] getDistance(double x, double y, double x2, double y2) {
        // 結果を格納するための配列を生成
        float[] results = new float[3];
        // 距離計算
        Location.distanceBetween(x, y, x2, y2, results);

        return results;
    }

    //「 プッシュ通知するかどうかを決める基準の距離 」を登録するボタン（ボタン３）
    private class RegisterDistance implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            set_distance = findViewById(R.id.set_distance);
            assumed_distance = findViewById(R.id.assumed_distance);
            String text3 = assumed_distance.getText().toString();
            set_distance.setText(text3);
        }
    }

    //緯度経度、距離を数値として保存する
    @Override
    protected void onPause(){
        SharedPreferences pref = getSharedPreferences("text_status", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if(home_latitude != null) {
            editor.putString("home_latitude", home_latitude.getText().toString());
        }
        if(home_longitude != null) {
            editor.putString("home_longitude", home_longitude.getText().toString());
        }
        if(num_distance_to_home != null) {
            editor.putString("num_distance_to_home", num_distance_to_home.getText().toString());
        }
        if(set_distance != null) {
            editor.putString("set_distance", set_distance.getText().toString());
        }
        editor.apply();
        super.onPause();
    }

    //保存した数値をUIに表示
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref = getSharedPreferences("text_status",Context.MODE_PRIVATE);
        home_latitude.setText(pref.getString("home_latitude","0"));
        home_longitude.setText(pref.getString("home_longitude","0"));
        set_distance.setText(pref.getString("set_distance","0"));
        num_distance_to_home.setText(pref.getString("num_distance_to_home","0"));
    }


    //以下は緯度経度を取得する下ごしらえ

    //位置情報取得開始メソッド
    private void startUpdateLocation() {
        // 位置情報取得権限の確認
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 権限がない場合、許可ダイアログ表示
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 2000);
            return;
        }

        // 位置情報の取得方法を設定
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);       // 位置情報更新間隔の希望
        locationRequest.setFastestInterval(5000); // 位置情報更新間隔の最速値
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // この位置情報要求の優先度

        fusedLocationClient.requestLocationUpdates(locationRequest, new MyLocationCallback(), null);
    }

    /**
     * 許可ダイアログの結果受取
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 2000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 位置情報取得開始
            startUpdateLocation();
        }
    }
}