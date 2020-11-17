package com.tai.api19test.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;
import com.tai.api19test.R;

public class QRCodeActivity extends AppCompatActivity {
    private final String TAG = "QRCodeActivityTAG";
    private Button start;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        initView();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRCodeActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 266);
            }
        });
    }

    private void initView() {
        start = findViewById(R.id.start);
        result = findViewById(R.id.result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 266) {
            Log.d(TAG, "扫描QR码");
            if (data != null) {
                String str = String.valueOf(data.getStringExtra("SCAN_RESULT"));
                Log.d(TAG, "结果：" + str);
                Bundle c = data.getExtras();
                for (String key : c.keySet()) {
                    Log.d(TAG, key);
                }
                result.setText(str);
            }
        }
    }
}