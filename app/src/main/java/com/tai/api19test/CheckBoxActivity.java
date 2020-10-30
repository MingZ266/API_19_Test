package com.tai.api19test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class CheckBoxActivity extends AppCompatActivity {
    private StringBuffer str;

    private CheckBox red, yellow, blue;
    private Button OK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_box);

        initView();
        myListener();
    }

    private void initView() {
        red = findViewById(R.id.red);
        yellow = findViewById(R.id.yellow);
        blue = findViewById(R.id.blue);
        OK = findViewById(R.id.OK);
    }

    private void myListener() {
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = new StringBuffer();
                if (red.isChecked())
                    str.append(" 红 ");
                if (yellow.isChecked())
                    str.append(" 黄 ");
                if (blue.isChecked())
                    str.append(" 蓝 ");
                Toast.makeText(CheckBoxActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
    }
}