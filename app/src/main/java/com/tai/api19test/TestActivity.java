package com.tai.api19test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity {
    private Context context = TestActivity.this;
    private LinearLayout baseViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initView();
        myProcess();
    }

    private void initView() {
        baseViewGroup = findViewById(R.id.baseViewGroup);
    }

    private void myProcess() {
        View addView = View.inflate(context, R.layout.test_add_view, null);
        baseViewGroup.addView(addView);
        TextView text = baseViewGroup.findViewById(R.id.text);
        text.setText("绑定成功");
    }
}