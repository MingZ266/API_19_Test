package com.tai.api19test.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.tai.api19test.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeDealActivity extends AppCompatActivity {
    String TAG = "TimeDealActivityTAG";

    private TextView timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_deal);

        initView();
        myProcess();
        myListener();
    }

    private void initView() {
        timeText = findViewById(R.id.timeText);
    }

    @SuppressLint("SetTextI18n")
    private void myProcess() {
        StringBuffer text = new StringBuffer();
        String dateStr = "7:36";
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        TimeZone tz = TimeZone.getTimeZone("GMT+0");
        format.setTimeZone(tz);
        format1.setTimeZone(tz);
        try {
            Date date = format.parse(dateStr);
            if (date != null)
                text.append(format1.format(date));
            else
                timeText.setText("date为空");
            text.append('\n');
            text.append(format1.format(System.currentTimeMillis()));
            text.append('\n');
            text.append(System.currentTimeMillis());
            text.append('\n');
            text.append(format1.format(System.currentTimeMillis() % 86400000));
        } catch (ParseException e) {
            Log.d(TAG, "日期格式化异常：" + dateStr);
        }
        timeText.setText(text);
    }

    private void myListener() {}
}