package com.tai.api19test.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.tai.api19test.R;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service {
    private String TAG = "BackgroundServiceTAG";
    private int NOTIFICATION_ID = 266;
    private Notification notification = null;// 标记是否调用了onDestroy()方法
    private NotificationCompat.Builder builder;
    private Context context = BackgroundService.this;
    private SimpleDateFormat format = new SimpleDateFormat("HH时mm分ss秒", Locale.CHINA);
    private long startTime;
    private static boolean isRunning = false;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();// 空方法
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        startTheForegroundService();// 必需在coreTask()之前执行，避免isRunning被改变
        if (!isRunning)
            coreTask();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();// 空方法
        startService(new Intent(context, BackgroundService.class));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void coreTask() {
        isRunning = true;
        new Timer().schedule(new TimerTask() {
            private int HH = 0, mm = 0, ss = 0;

            @Override
            public void run() {
                String message = "已运行：" + HH + "时" + mm + "分" + ss++ + "秒";
                if (ss >= 60) {
                    ss = 0;
                    mm++;
                    if (mm >= 60) {
                        mm = 0;
                        HH++;
                        if (HH >= 24) {
                            this.cancel();
                            isRunning = false;
                            message = "已运行1天";
                        }
                    }
                }
                Log.d(TAG, message + "  实际运行" + getRunTime());
                refreshNotification(message + "  实际运行" + getRunTime());
            }
        }, 0, 1000);
    }

    private void startTheForegroundService() {
        if (isRunning && notification != null)
            return;
        String channelId = String.valueOf(NOTIFICATION_ID);
        // Android 8.0 及以上需添加通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // a.获取系统通知服务
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // b.设定通知渠道
            NotificationChannel channel = new NotificationChannel(channelId, "渠道", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("常驻服务所需");
            manager.createNotificationChannel(channel);
        }
        // c.设定通知布局、属性
        startTime = System.currentTimeMillis();
        PendingIntent manageNotifications = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", getPackageName(), null));
            manageNotifications = PendingIntent.getActivity(context, 0, intent, 0);
        }
        builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_file)
                .setContentTitle(format.format(startTime) + "创建")
                .setContentText("这是一段文本信息")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .setContentIntent(manageNotifications)
                .setOnlyAlertOnce(true)
                .setOngoing(true);
        notification = builder.build();
        startForeground(NOTIFICATION_ID, notification);
        /*// d.获得通知管理器
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        // e.发出通知
        managerCompat.notify(NOTIFICATION_ID, builder.build());*/
    }

    private String getRunTime() {
        return format.format(System.currentTimeMillis() - startTime - 28800000/*时区差8小时*/);
    }

    private void refreshNotification(String newMessage) {
        builder.setContentText(newMessage);
        startForeground(NOTIFICATION_ID, builder.build());
    }
}
