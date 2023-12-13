/**
 * Copyright (C) 2023 DESMG
 * All Rights Reserved.
 */

package com.desmg.utility;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BatteryInfoService extends Service {
    private final BroadcastReceiver batteryReceiver = new BatteryInfo();
    private final IntentFilter intentFilterBatteryChanged = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    private Context ctx;
    private Disposable disposable = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ctx = getApplicationContext();
        try {
            disposable = Observable.interval(1, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread()).subscribe(count -> new BatteryInfoTicker().run());
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
        try {
            ctx.unregisterReceiver(batteryReceiver);
        } catch (Exception e1) {
            try {
                ctx.unregisterReceiver(batteryReceiver);
            } catch (Exception e2) {
                try {
                    ctx.unregisterReceiver(batteryReceiver);
                } catch (Exception e3) {
                    Log.e("ERROR", Objects.requireNonNull(e1.getMessage()));
                    Log.e("ERROR", Objects.requireNonNull(e2.getMessage()));
                    Log.e("ERROR", Objects.requireNonNull(e3.getMessage()));
                }
            }
        }
    }

    private class BatteryInfo extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!Objects.equals(intent.getAction(), Intent.ACTION_BATTERY_CHANGED)) {
                return;
            }

            StringBuilder sbTitle = new StringBuilder();
            StringBuilder sb = new StringBuilder();

            //region BatteryInfo
            switch (intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    sbTitle.append("状态：状态未知");
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    switch (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
                        case BatteryManager.BATTERY_PLUGGED_AC:
                            sbTitle.append("状态：正在通过AC充电");
                            break;
                        case BatteryManager.BATTERY_PLUGGED_USB:
                            sbTitle.append("状态：正在通过USB充电");
                            break;
                        case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                            sbTitle.append("状态：正在通过Wireless充电");
                            break;
                        default:
                            sbTitle.append("状态：获取充电类型失败");
                            break;
                    }
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    sbTitle.append("状态：已断开充电器");
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    sbTitle.append("状态：未在充电");
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    sbTitle.append("状态：已充满");
                    break;
                default:
                    sbTitle.append("状态：完全获取失败");
                    break;
            }

            int batteryTemperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            if (batteryTemperature != -1) {
                sb.append("温度：").append(batteryTemperature / 10).append("摄氏度");
            }
            sb.append("\n");
            int chargeV = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            if (chargeV != -1) {
                sb.append("电压：").append(chargeV).append("mV");
            }
            sb.append("\n");

            BatteryManager mbm = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            if (mbm != null) {
                int nowA = mbm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                if (nowA != Integer.MIN_VALUE) {
                    sb.append("瞬时电流：").append(nowA / -1000).append("mA");
                } else {
                    sb.append("瞬时电流：获取失败");
                }
                sb.append("\n");
                int avgA = mbm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
                if (avgA != Integer.MIN_VALUE) {
                    sb.append("平均电流：").append(avgA / -1000).append("mA");
                } else {
                    sb.append("平均电流：获取失败");
                }
                sb.append("\n");
                int bpct = mbm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                if (bpct != Integer.MIN_VALUE) {
                    sb.append("剩余水平：").append(bpct).append("%");
                } else {
                    sb.append("剩余水平：获取失败");
                }
                sb.append("\n");
                int bcc = mbm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                if (bcc != Integer.MIN_VALUE) {
                    sb.append("剩余电量：").append(bcc / 1000 > 10 ? bcc / 1000 : bcc).append("mAh"); // 这里做MIUI兼容，如有问题请发issue
                } else {
                    sb.append("剩余电量：获取失败");
                }
                sb.append("\n");
                int bec = mbm.getIntProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
                if (bec != Integer.MIN_VALUE) {
                    sb.append("瞬时电量：").append(bec).append("nWh");
                } else {
                    sb.append("瞬时电量：获取失败");
                }
                sb.append("\n");
                long willFullTime = mbm.computeChargeTimeRemaining();
                if (willFullTime != Long.MIN_VALUE) {
                    sb.append("预计充满时间：").append(willFullTime / 1000 / 60).append("分钟");
                } else {
                    sb.append("预计充满时间：获取失败");
                }
                sb.append("\n");
            }
            String healthText;
            switch (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                    healthText = "健康：未知";
                    break;
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    healthText = "健康：良好";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    healthText = "健康：过热";
                    break;
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    healthText = "健康：死亡";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    healthText = "健康：过压";
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    healthText = "健康：未指定失败原因";
                    break;
                case BatteryManager.BATTERY_HEALTH_COLD:
                    healthText = "健康：过冷";
                    break;
                default:
                    healthText = "健康：获取失败";
                    break;
            }
            sb.append(healthText);
            sb.append("\n");
            String tech = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            sb.append("技术：").append(tech);
            //endregion

            this.sendNotification(sbTitle.toString(), sb.toString());
        }

        private void sendNotification(CharSequence csTitle, CharSequence csText) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, "com.desmg.utility.BatteryInfo");
            builder.setShowWhen(false);
            builder.setSmallIcon(R.drawable.ic_launcher_foreground);
            builder.setOnlyAlertOnce(true);// 仅一次
            builder.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE); // 去掉角标
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT); // 默认优先级

            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setSummaryText("电池信息");
            bigTextStyle.setBigContentTitle(csTitle);
            bigTextStyle.bigText(csText);

            builder.setStyle(bigTextStyle);

            // 如果使用经典样式通知可能无法正常展开列表 需要使用原生样式
            if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(ctx);
                notificationManagerCompat.notify(0, builder.build());
            }
        }
    }

    private class BatteryInfoTicker {
        public void run() {
            try {
                ctx.unregisterReceiver(batteryReceiver);
            } catch (Exception e1) {
                try {
                    ctx.unregisterReceiver(batteryReceiver);
                } catch (Exception e2) {
                    try {
                        ctx.unregisterReceiver(batteryReceiver);
                    } catch (Exception e3) {
                        Log.e("ERROR", Objects.requireNonNull(e1.getMessage()));
                        Log.e("ERROR", Objects.requireNonNull(e2.getMessage()));
                        Log.e("ERROR", Objects.requireNonNull(e3.getMessage()));
                    }
                }
            }
            try {
                ctx.registerReceiver(batteryReceiver, intentFilterBatteryChanged);
            } catch (Exception e1) {
                try {
                    ctx.registerReceiver(batteryReceiver, intentFilterBatteryChanged);
                } catch (Exception e2) {
                    try {
                        ctx.registerReceiver(batteryReceiver, intentFilterBatteryChanged);
                    } catch (Exception e3) {
                        Log.e("ERROR", Objects.requireNonNull(e1.getMessage()));
                        Log.e("ERROR", Objects.requireNonNull(e2.getMessage()));
                        Log.e("ERROR", Objects.requireNonNull(e3.getMessage()));
                    }
                }
            }
        }
    }
}
