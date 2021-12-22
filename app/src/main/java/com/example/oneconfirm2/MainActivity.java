package com.example.oneconfirm2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button m_btn_check0 = findViewById(R.id.m_btn_check0);
        m_btn_check0.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, PushNotificationService.class);
            startActivity(intent);
        });

        //「 チェックリスト 」に画面遷移するボタン
        Button m_btn_check = findViewById(R.id.m_btn_check);
        m_btn_check.setOnClickListener((View v) -> {
            startActivity(new Intent(this, ChecklistActivity.class));
        });

        //「 設定 」に画面遷移するボタン
        Button m_btn_config = findViewById(R.id.m_btn_config);
        m_btn_config.setOnClickListener((View v) -> {
            startActivity(new Intent(this, ConfigActivity.class));
        });
    }
}