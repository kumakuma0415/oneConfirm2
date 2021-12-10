package com.example.oneconfirm2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainConfig extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_config);

        Button co_btn_dis = findViewById(R.id.co_dis);

        Button co_btn_return = findViewById(R.id.co_return);

        co_btn_dis.setOnClickListener((View v) -> {
            startActivity(new Intent(this, DistanceActivity.class));
        });


        co_btn_return.setOnClickListener((View v) -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }
}