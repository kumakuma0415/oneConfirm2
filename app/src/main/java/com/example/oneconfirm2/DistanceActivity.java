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

    private FusedLocationProviderClient fusedLocationProviderClient;
    TextView home_latitude;
    TextView home_longitude;
    TextView set_distance;
    EditText assumed_distance;

    Double now_latitude3;
    Double now_longitude3;
    Double home_latitude3;
    Double home_longitude3;
    TextView distance_to_home;

    static LocationResult locationResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);

        // LocationClientクラスのインスタンスを生成
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 位置情報取得開始
        startUpdateLocation();

        //エラーの原因
        //Location location = locationResult.getLastLocation();

        Button register_myHome1 = findViewById(R.id.register_my_home);
        register_myHome1.setOnClickListener(v -> {
            home_latitude = findViewById(R.id.home_latitude);
            TextView write_latitude = findViewById(R.id.now_latitude);
            String text1 = write_latitude.getText().toString();
            home_latitude.setText(text1);
            //追加部分
            home_latitude3 = locationResult.getLastLocation().getLatitude();

            home_longitude = findViewById(R.id.home_longitude);
            TextView write_longitude = findViewById(R.id.now_longitude);
            String text2 = write_longitude.getText().toString();
            home_longitude.setText(text2);
            //追加部分
            home_longitude3 = locationResult.getLastLocation().getLongitude();
        });
        home_latitude = findViewById(R.id.home_latitude);
        home_longitude = findViewById(R.id.home_longitude);
        //追加部分　エラーの原因
        //home_latitude3 = locationResult.getLastLocation().getLatitude();
        //home_longitude3 = locationResult.getLastLocation().getLongitude();


        Button register_distance = findViewById(R.id.register_distance);
        register_distance.setOnClickListener(v -> {
            set_distance = findViewById(R.id.set_distance);
            assumed_distance = findViewById(R.id.assumed_distance);
            String text3 = assumed_distance.getText().toString();
            set_distance.setText(text3);
        });
        set_distance = findViewById(R.id.set_distance);


        Button d_btn_return = findViewById(R.id.d_btn_return);
        d_btn_return.setOnClickListener((View v) -> startActivity(new Intent(this, ConfigActivity.class)));

        //エラーの原因
        //now_latitude3 = locationResult.getLastLocation().getLatitude();
        //now_latitude3 = locationResult.getLastLocation().getLongitude();

        /*
        float[] distance =
               getDistance(now_latitude3, now_longitude3, home_latitude3, home_longitude3);

        //ボタンを押すと、距離を算出してくれる仕組み
        //distance[0]は2地点間の距離
        Button register_distance2 = findViewById(R.id.register_distance2);
        register_distance2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dis = String.valueOf(distance[0]);
                distance_to_home = findViewById(R.id.distance_toHome);
                distance_to_home.setText(dis);
          }
        });
        distance_to_home = findViewById(R.id.distance_toHome);
         */
    }

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
    }


    /**
     * 位置情報取得開始メソッド
     */
    public void startUpdateLocation() {
        // 位置情報取得権限の確認
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
     *
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 位置情報取得開始
            startUpdateLocation();
        }
    }


    public FusedLocationProviderClient fusedLocationClient;


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
            TextView view1 = findViewById(R.id.now_latitude);
            TextView view2 = findViewById(R.id.now_longitude);
            view1.setText(String.valueOf(location.getLatitude()));
            view2.setText(String.valueOf(location.getLongitude()));

            Double num_now_latitude = location.getLatitude();
            Double num_now_longitude = location.getLongitude();
        }
    }


}

