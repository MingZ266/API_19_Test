package com.tai.api19test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

public class DiverseNotifyActivity extends AppCompatActivity {
    private Context context = this;
    private String channelName = "多样通知";

    private Button allText;
    private Button withPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diverse_notify);

        initView();
        myListener();
    }

    private void initView() {
        allText = findViewById(R.id.allText);
        withPic = findViewById(R.id.withPic);
    }

    private void myListener() {
        allText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int notifyId = 1002;
                String channelId = String.valueOf(notifyId);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notify_all_text);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
                    manager.createNotificationChannel(channel);
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_file)
                        .setCustomContentView(views);
                manager.notify(notifyId, builder.build());
            }
        });

        withPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int notifyId = 1003;
                String channelId = String.valueOf(notifyId);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notify_with_pic);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
                    manager.createNotificationChannel(channel);
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_file)
                        .setCustomContentView(views);
                manager.notify(notifyId, builder.build());
            }
        });
    }
}