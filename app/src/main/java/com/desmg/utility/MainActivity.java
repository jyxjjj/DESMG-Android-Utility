/**
 * Copyright (C) 2023 DESMG
 * All Rights Reserved.
 */
package com.desmg.utility;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.registerNotification();
        this.requestPermission();
        Context ctx = getApplicationContext();
        Intent batteryInfoIntent = new Intent(ctx, BatteryInfoService.class);
        ctx.startService(batteryInfoIntent);
        Intent smsIntent = new Intent(ctx, Sms2TelegramService.class);
        ctx.startService(smsIntent);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, 0);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 0);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 0);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_NUMBERS}, 0);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 0);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode < 3) {
            int reqCode = requestCode + 1;
            Context ctx = getApplicationContext();
            if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, reqCode);
            }
            if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, reqCode);
            }
            if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, reqCode);
            }
            if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, reqCode);
            }
            if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_NUMBERS}, reqCode);
            }
            if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, reqCode);
            }
            if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, reqCode);
            }
            if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, reqCode);
            }
            if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, reqCode);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        TextView appname = findViewById(R.id.appname);
        appname.setText(String.format("%s %s", getString(R.string.app_name), BuildConfig.VERSION_NAME));
        TextView copy1 = findViewById(R.id.copyright1);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Etc/GMT-8"));
        String mYear = String.valueOf(calendar.get(Calendar.YEAR));
        copy1.setText(String.format("Copyright © %s", mYear));
        TextView copy2 = findViewById(R.id.copyright2);
        copy2.setText("DESMG All Rights Reserved.");
    }

    private void registerNotification() {
        final NotificationManager notificationManager = getSystemService(NotificationManager.class);

        final NotificationChannel channel = new NotificationChannel("com.desmg.utility.BatteryInfo", "BatteryInfo", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("BatteryInfo");
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);// 测试发现MIUI10在手动开放权限后将会显示锁屏通知但默认权限为禁止
        channel.setSound(null, null);
        channel.setShowBadge(false);// 将不会显示角标但权限依然开放
        channel.enableLights(false);// 默认权限为禁止
        channel.enableVibration(false);// 将不会震动标但权限依然开放

        notificationManager.createNotificationChannel(channel);
    }
}
