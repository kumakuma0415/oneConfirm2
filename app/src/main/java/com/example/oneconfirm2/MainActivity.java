package com.example.oneconfirm2;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //サービス開始ボタン
        //*
        CompoundButton m_btn_service = findViewById(R.id.m_btn_service);
        Intent intent = new Intent(MainActivity.this, PushNotificationService.class);
        m_btn_service.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //サービスを開始
                    startService(intent);
                } else {
                    //サービスの開始をトーストで表示できるように書き直す
                    //Toast toast = makeText(this, "自宅までの距離を観測します", LENGTH_LONG);
                    //toast.show();

                    //サービスを止める処理を書き直す
                    stopService(intent);
                }
            }
        });
        //*/


        //「 チェックリスト 」に画面遷移するボタン
        Button m_btn_check = findViewById(R.id.m_btn_check);
        m_btn_check.setOnClickListener((View v) -> {
            startActivity(new Intent(this, ChecklistActivity.class));
        });

        //「 設定 」に画面遷移するボタン
        Button m_btn_config = findViewById(R.id.m_btn_config);
        m_btn_config.setOnClickListener((View v) -> {
            startActivity(new Intent(this, DistanceActivity.class));
        });
    }



}