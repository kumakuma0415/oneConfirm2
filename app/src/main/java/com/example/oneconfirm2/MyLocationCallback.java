package com.example.oneconfirm2;

import android.location.Location;
import android.widget.TextView;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

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
    }
}
