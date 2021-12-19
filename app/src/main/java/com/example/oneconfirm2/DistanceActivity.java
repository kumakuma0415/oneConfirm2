package com.example.oneconfirm2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);

        // LocationClientクラスのインスタンスを生成
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 位置情報取得開始
        startUpdateLocation();


        Button register_myHome = findViewById(R.id.register_my_home);
        register_myHome.setOnClickListener(v -> {

            home_latitude = findViewById(R.id.home_latitude);
            write_latitude = findViewById(R.id.now_latitude);
            String text1 = write_latitude.getText().toString();
            home_latitude.setText(text1);

            home_longitude = findViewById(R.id.home_longitude);
            write_longitude = findViewById(R.id.now_longitude);
            String text2 = write_longitude.getText().toString();
            home_longitude.setText(text2);
        });
        home_latitude = findViewById(R.id.home_latitude);
        home_longitude = findViewById(R.id.home_longitude);


        //ボタンを押すと、距離を算出してくれる仕組み
        //distance[0]は2地点間の距離
        Button btn_distance_to_home = findViewById(R.id.btn_distance_to_home);
        btn_distance_to_home.setOnClickListener(v -> {

            String now_latitude2 = now_latitude.getText().toString();
            String now_longitude2 = now_longitude.getText().toString();
            double now_latitude3 = Double.parseDouble(now_latitude2);
            double now_longitude3 = Double.parseDouble(now_longitude2);

            String home_latitude2 = home_latitude.getText().toString();
            String home_longitude2 = home_longitude.getText().toString();
            double home_latitude3 = Double.parseDouble(home_latitude2);
            double home_longitude3 = Double.parseDouble(home_longitude2);

            float[] distance =
                    getDistance(now_latitude3, now_longitude3, home_latitude3, home_longitude3);

            String dis = String.valueOf(distance[0]);
            num_distance_to_home = findViewById(R.id.num_distance_to_home);
            num_distance_to_home.setText(dis);
        });
        num_distance_to_home = findViewById(R.id.num_distance_to_home);


        Button register_distance = findViewById(R.id.distance_to_home);
        register_distance.setOnClickListener(v -> {
            set_distance = findViewById(R.id.set_distance);
            assumed_distance = findViewById(R.id.assumed_distance);
            String text3 = assumed_distance.getText().toString();
            set_distance.setText(text3);
        });
        set_distance = findViewById(R.id.set_distance);

        //compare_distance();

        /*
        Button practice_btn = findViewById(R.id.practice_btn);
        practice_btn.setOnClickListener(v -> {
            num_distance_to_home2 = num_distance_to_home.getText().toString();
            num_distance_to_home3 = Double.parseDouble(num_distance_to_home2);
            String set_distance2 = set_distance.getText().toString();
            double set_distance3 = Double.parseDouble(set_distance2);

            if (num_distance_to_home3 >= set_distance3) {
                TextView distance_practice = findViewById(R.id.distance_practice);
                distance_practice.setText("できました！");
            }
        });
        */

        Button d_btn_return = findViewById(R.id.d_btn_return);
        d_btn_return.setOnClickListener((View v) -> startActivity(new Intent(this, ConfigActivity.class)));

    }

    /*
    private void compare_distance() {
        String num_distance_to_home2 = num_distance_to_home.getText().toString();
        double num_distance_to_home3 = Double.parseDouble(num_distance_to_home2);
        String set_distance2 = set_distance.getText().toString();
        double set_distance3 = Double.parseDouble(set_distance2);

        if (num_distance_to_home3 >= set_distance3) {
            TextView distance_practice = findViewById(R.id.distance_practice);
            distance_practice.setText("できました！");
        }
    }
    */

    /*
     * 2点間の距離（メートル）、方位角（始点、終点）を取得
     * ※配列で返す[距離、始点から見た方位角、終点から見た方位角]
     */

    public float[] getDistance(double x, double y, double x2, double y2) {
        // 結果を格納するための配列を生成
        float[] results = new float[3];

        // 距離計算
        Location.distanceBetween(x, y, x2, y2, results);

        return results;
    }


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
        editor.commit();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref = getSharedPreferences("text_status",Context.MODE_PRIVATE);

        home_latitude.setText(pref.getString("home_latitude","未設定"));
        home_longitude.setText(pref.getString("home_longitude","未設定"));
        set_distance.setText(pref.getString("set_distance","未設定"));
        num_distance_to_home.setText(pref.getString("num_distance_to_home","未設定"));
    }


    /**
     * 位置情報取得開始メソッド
     */

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 2000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 位置情報取得開始
            startUpdateLocation();
        }
    }


    /**
     * 位置情報受取コールバッククラス
     */
    public class MyLocationCallback extends LocationCallback {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            // 現在値を取得
            Location location = locationResult.getLastLocation();

            // 画面に表示
            now_latitude = findViewById(R.id.now_latitude);
            now_longitude = findViewById(R.id.now_longitude);
            String str_now_latitude = String.valueOf(location.getLatitude());
            String str_now_longitude = String.valueOf(location.getLongitude());

            now_latitude.setText(str_now_latitude);
            now_longitude.setText(str_now_longitude);

            //String str_now_latitude = now_latitude.setText().toString;
            //Double num_now_latitude = Double.parseDouble(str_now_latitude);
        }

    }
}