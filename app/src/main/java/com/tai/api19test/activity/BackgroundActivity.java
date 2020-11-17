package com.tai.api19test.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tai.api19test.service.BackgroundService;
import com.tai.api19test.R;
import com.tai.api19test.util.Tools;

public class BackgroundActivity extends AppCompatActivity {
    private String TAG = "BackgroundActivityTAG";
    private Context context = BackgroundActivity.this;

    private EditText inputTime;
    private Button getTime;
    private Button startService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);

        initView();
        myListener();
    }

    private void initView() {
        inputTime = findViewById(R.id.inputTime);
        getTime = findViewById(R.id.getTime);
        startService = findViewById(R.id.startService);
    }

    private void myListener() {
        getTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStr = String.valueOf(inputTime.getText());
                if (timeStr.equals(""))
                    Toast.makeText(context, "输入为空", Toast.LENGTH_SHORT).show();
                else {
                    int time = (int) (Double.parseDouble(timeStr) * 60000/*1分钟*/);
                    if (time < 10000/*10秒钟*/)
                        Toast.makeText(context, "时间不能少于10秒钟", Toast.LENGTH_SHORT).show();
                    else {
                        Intent startIntent = new Intent(context, MainActivity.class);
                        PendingIntent start = PendingIntent.getActivity(context, 222, startIntent, PendingIntent.FLAG_ONE_SHOT);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, start); Toast.makeText(context, "将在" + (time / 1000) +"秒后启动主页面", Toast.LENGTH_SHORT).show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Intent toTime = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
                                startActivity(toTime);
                                finishAffinity();// 关闭当前Activity所属栈中所有的Activity
                            }
                        }
                    }
                }
            }
        });

        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnSelfStart();
                Intent service = new Intent(context, BackgroundService.class);
                service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(service);
            }
        });
    }

    private void turnOnSelfStart() {
        SharedPreferences read = context.getSharedPreferences(Tools.selfStartCfg, MODE_PRIVATE);
        boolean notRemind = Boolean.parseBoolean(read.getString("notRemind", "false"));
        if (notRemind)
            return;
        View view = View.inflate(context, R.layout.dialog_tip_self_starting, null);
        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CircleCornerDialog)
                .setView(view)
                .create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        final CheckBox doNotRemindAgain = view.findViewById(R.id.doNotRemindAgain);

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.saveSharedPreferences(context, Tools.selfStartCfg, "notRemind", String.valueOf(doNotRemindAgain.isChecked()));
                dialog.cancel();
            }
        });

        view.findViewById(R.id.goToSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.saveSharedPreferences(context, Tools.selfStartCfg, "notRemind", String.valueOf(doNotRemindAgain.isChecked()));

                Intent intent;
                try {
                    // 打开该App(指定包名应用)的详细应用信息管理
                    intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // 未找到Activity则直接打开应用管理
                    intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                    startActivity(intent);
                } catch (Exception e) {
                    // 出现其它异常则直接打开设置界面
                    Log.d(TAG, "----------------------------------------------------------");
                    Log.e(TAG, "异常为：" + e.getClass().getSimpleName() + "，详细信息为：");
                    for (StackTraceElement err : e.getStackTrace()) {
                        Log.e(TAG, err.toString());
                    }
                    Log.d(TAG, "----------------------------------------------------------");
                    intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                }

                dialog.cancel();
            }
        });
    }
}
