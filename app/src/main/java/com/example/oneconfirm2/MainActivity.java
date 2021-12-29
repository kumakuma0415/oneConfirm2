package com.example.oneconfirm2;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //サービス開始ボタン
        CompoundButton m_btn_service = findViewById(R.id.main_btn_service);
        Intent intent = new Intent(MainActivity.this, PushNotificationService.class);
        m_btn_service.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //サービスを開始
                startService(intent);
            } else {
                //サービスを止める処理を書き直す
                stopService(intent);
            }
        });

        //「 チェックリスト 」に画面遷移するボタン
        Button m_btn_check = findViewById(R.id.main_btn_check);
        m_btn_check.setOnClickListener((View v) -> startActivity(new Intent(this, ChecklistActivity.class)));

        //「 設定 」に画面遷移するボタン
        Button m_btn_config = findViewById(R.id.main_btn_config);
        m_btn_config.setOnClickListener((View v) -> startActivity(new Intent(this, DistanceActivity.class)));
    }



}