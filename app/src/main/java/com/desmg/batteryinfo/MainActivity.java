package com.desmg.batteryinfo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Date;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilterBatteryChanged;
    Context ctx;
    NotificationManagerCompat notificationManagerCompat;
    NotificationCompat.Builder builder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView copy1 = findViewById(R.id.copyright1);
        TextView copy2 = findViewById(R.id.copyright2);

        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Etc/GMT-8"));
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        copy1.setText("Copyright" + " " + "\u00A9" + " " + mYear);
        copy2.setText("DESMG All Rights Reserved.");

        ctx = getApplicationContext();
        this.registerNotification();
        this.registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        broadcastReceiver = new ReceiverHandler();
        intentFilterBatteryChanged = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        ctx.registerReceiver(broadcastReceiver, intentFilterBatteryChanged);
    }

    private void registerNotification() {
        notificationManagerCompat = NotificationManagerCompat.from(ctx);

        final NotificationChannel channel = new NotificationChannel("com.desmg.BatteryInfo", "BatteryInfo", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("BatteryInfo");
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);// 测试发现MIUI10在手动开放权限后将会显示锁屏通知但默认权限为禁止
        channel.setSound(null, null);
        channel.setShowBadge(false);// 将不会显示角标但权限依然开放
        channel.enableLights(false);// 默认权限为禁止
        channel.enableVibration(false);// 将不会震动标但权限依然开放

        final NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        builder = new NotificationCompat.Builder(ctx, "com.desmg.BatteryInfo");
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setOnlyAlertOnce(true);// 正常
        builder.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE); // setBadgeIconType 去掉角标
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT); // 默认优先级
    }

    public class ReceiverHandler extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //region TextView
            TextView tv1 = findViewById(R.id.tv1);
            TextView tv2 = findViewById(R.id.tv2);
            TextView tv3 = findViewById(R.id.tv3);
            TextView tv4 = findViewById(R.id.tv4);
            TextView tv5 = findViewById(R.id.tv5);
            TextView tv6 = findViewById(R.id.tv6);
            TextView tv7 = findViewById(R.id.tv7);
            TextView tv8 = findViewById(R.id.tv8);
            TextView tv9 = findViewById(R.id.tv9);
            TextView tv10 = findViewById(R.id.tv10);
            TextView tv11 = findViewById(R.id.tv11);
            //endregion TextView

            switch (intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    tv1.setText("状态：状态未知");
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    switch (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
                        case BatteryManager.BATTERY_PLUGGED_AC:
                            tv1.setText("状态：正在通过AC充电");
                            break;
                        case BatteryManager.BATTERY_PLUGGED_USB:
                            tv1.setText("状态：正在通过USB充电");
                            break;
                        case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                            tv1.setText("状态：正在通过Wireless充电");
                            break;
                        default:
                            tv1.setText("状态：获取充电类型失败");
                            break;
                    }
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    tv1.setText("状态：已断开充电器");
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    tv1.setText("状态：未在充电");
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    tv1.setText("状态：已充满");
                    break;
                default:
                    tv1.setText("状态：完全获取失败");
                    break;
            }

            int batteryTemperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            if (batteryTemperature != -1) {
                tv2.setText("温度：" + batteryTemperature / 10 + "摄氏度");
            }
            int chargeV = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            if (chargeV != -1) {
                tv3.setText("电压：" + chargeV + "mV");
            }

            BatteryManager mbm = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
            if (mbm != null) {
                int nowA = mbm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                if (nowA != Integer.MIN_VALUE) {
                    tv4.setText("瞬时电流：" + nowA / -1000 + "mA");
                } else {
                    tv4.setText("瞬时电流：获取失败");
                }
                int avgA = mbm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
                if (avgA != Integer.MIN_VALUE) {
                    tv5.setText("平均电流：" + avgA / -1000 + "mA");
                } else {
                    tv5.setText("平均电流：获取失败");
                }
                int bpct = mbm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                if (bpct != Integer.MIN_VALUE) {
                    tv6.setText("剩余水平：" + bpct + "%");
                } else {
                    tv6.setText("剩余水平：获取失败");
                }
                int bcc = mbm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                if (bcc != Integer.MIN_VALUE) {
                    tv7.setText("剩余电量：" + bcc / 1000 + "mAh");
                } else {
                    tv7.setText("剩余电量：获取失败");
                }
                int bec = mbm.getIntProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
                if (bec != Integer.MIN_VALUE) {
                    tv8.setText("瞬时电量：" + bec + "nWh");
                } else {
                    tv8.setText("瞬时电量：获取失败");
                }
                long willFullTime = mbm.computeChargeTimeRemaining();
                if (willFullTime != Long.MIN_VALUE) {
                    tv9.setText("预计充满时间：" + willFullTime / 1000 / 60 + "分钟");
                } else {
                    tv9.setText("预计充满时间：获取失败");
                }
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
            tv10.setText(healthText);
            String tech = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            tv11.setText("技术：" + tech);

            this.sendNotification(
                    tv1.getText(),
                    tv2.getText() + "\r\n" + tv3.getText() + "\r\n" + tv4.getText() + "\r\n" + tv6.getText() + "\r\n" + tv7.getText() + "\r\n" + tv9.getText()
            );

            Handler h = new Handler(Looper.myLooper());
            h.postDelayed(
                    () -> {
                        ctx.unregisterReceiver(broadcastReceiver);
                        ctx.registerReceiver(broadcastReceiver, intentFilterBatteryChanged);
                    },
                    1000);
        }

        private void sendNotification(@Nullable CharSequence csTitle, @Nullable CharSequence csText) {
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setSummaryText("电池信息");
            bigTextStyle.setBigContentTitle(csTitle);
            bigTextStyle.bigText(csText);
            builder.setStyle(bigTextStyle);
            // 如果使用经典样式通知可能无法正常展开列表 需要使用原生样式
            if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManagerCompat.notify(0, builder.build());
            }
        }
    }
}