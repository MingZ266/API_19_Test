package com.tai.api19test.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import com.tai.api19test.R;
import com.tai.api19test.util.Local;
import com.tai.api19test.util.Tools;

public class NetRequestActivity extends AppCompatActivity {
    private final String TAG = "NetRequestActivityTAG";
    private final String cfg = "NetRequest";
    private final Context context = NetRequestActivity.this;
    private String requestResult = "";
    private long timeOne;
    private EditText inputAddress;
    private EditText inputPath;
    private Button sendRequest;
    private TextView showResult;
    private Button sendKey;
    private Button getData;
    private Button decryptData;
    private Button quickly;
    private TextView showTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_request);

        initView();
        SharedPreferences read = getSharedPreferences(cfg, MODE_PRIVATE);
        inputAddress.setText(read.getString("address", ""));
        myListener();
    }

    private void initView() {
        inputAddress = findViewById(R.id.inputAddress);
        inputPath = findViewById(R.id.inputPath);
        sendRequest = findViewById(R.id.sendRequest);
        showResult = findViewById(R.id.showResult);
        sendKey = findViewById(R.id.sendKey);
        getData = findViewById(R.id.getData);
        decryptData = findViewById(R.id.decryptData);
        quickly = findViewById(R.id.quickly);
        showTime = findViewById(R.id.showTime);
    }

    private void myListener() {
        final Callback callback = new Callback() {
            private String getResult;

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //Log.d(TAG, "请求失败");
                showResult.setText("请求失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "请求成功");
                ResponseBody body = response.body();
                if (body == null) {
                    //Log.d(TAG, "返回结果为空");
                    getResult = "返回结果为空";
                } else {
                    //Log.d(TAG, "返回结果：" + body.string());
                    getResult = body.string();
                }
                requestResult = getResult;
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        showResult.setText(getResult);
                        showTime.setText("耗时：" + (System.currentTimeMillis() - timeOne) + "ms");
                    }
                });
            }
        };

        sendRequest.setOnClickListener(new View.OnClickListener() {
            //private SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", Locale.CHINA);
            //private String nowTime = "null";

            @Override
            public void onClick(View v) {
                //String nowTime = format.format(System.currentTimeMillis());
                String address = String.valueOf(inputAddress.getText());
                String path = String.valueOf(inputPath.getText());
                if (address.equals("") || path.equals("")) {
                    Toast.makeText(context, "域名或访问路径不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (path.indexOf('/') > 0) {
                    path = path.substring(1);
                }
                timeOne = System.currentTimeMillis();
                Tools.saveSharedPreferences(context, cfg, "address", address);
                address = "https://" + address + ".ngrok.io/" + path;

                OkHttpClient client = new OkHttpClient();
                /*RequestBody requestBody = new FormBody.Builder()
                        .add("a", "6")
                        .add("b", "鸣筝")
                        .build();*/
                Request request = new Request.Builder()
                        .url(address)
                        //.post(requestBody)
                        .build();
                client.newCall(request).enqueue(callback);
            }
        });

        sendKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = String.valueOf(inputAddress.getText());
                if (address.equals("")) {
                    Toast.makeText(context, "域名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                timeOne = System.currentTimeMillis();
                Tools.saveSharedPreferences(context, cfg, "address", address);
                address = "https://" + address + ".ngrok.io/getEncryptAESKey";
                String publicKeyStr = String.valueOf(Local.getLocalPublicKeyStr());
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("publicKeyStr", publicKeyStr)
                        .build();
                Request request = new Request.Builder()
                        .url(address)
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(callback);
            }
        });

        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Local.decryptAESKey(requestResult);
                String address = String.valueOf(inputAddress.getText());
                if (address.equals("")) {
                    Toast.makeText(context, "域名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                timeOne = System.currentTimeMillis();
                Tools.saveSharedPreferences(context, cfg, "address", address);
                address = "https://" + address + ".ngrok.io/getEncryptData";
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(address)
                        .build();
                client.newCall(request).enqueue(callback);
            }
        });

        decryptData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = Local.aesDecryptData(requestResult);
                showResult.setText(result);
            }
        });

        quickly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = String.valueOf(inputAddress.getText());
                if (address.equals("")) {
                    Toast.makeText(context, "域名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                timeOne = System.currentTimeMillis();
                Tools.saveSharedPreferences(context, cfg, "address", address);
                final String srcAddress = "https://" + address + ".ngrok.io/";
                address = srcAddress + "getEncryptAESKey";
                String publicKeyStr = String.valueOf(Local.getLocalPublicKeyStr());
                Callback callback1 = new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.d(TAG, "发送公钥失败");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        ResponseBody body = response.body();
                        if (body == null) {
                            Log.d(TAG, "发送公钥失败");
                            return;
                        }
                        Local.decryptAESKey(body.string());
                        String address = srcAddress + "getEncryptData";
                        Callback callback2 = new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Log.d(TAG, "获取加密后的数据失败");
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                ResponseBody body = response.body();
                                if (body == null) {
                                    Log.d(TAG, "获取加密后的数据失败");
                                    return;
                                }
                                final String result = Local.aesDecryptData(body.string());
                                runOnUiThread(new Runnable() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void run() {
                                        showResult.setText(result);
                                        showTime.setText("耗时：" + (System.currentTimeMillis() - timeOne) + "ms");
                                    }
                                });
                            }
                        };
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(address)
                                .build();
                        client.newCall(request).enqueue(callback2);
                    }
                };
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("publicKeyStr", publicKeyStr)
                        .build();
                Request request = new Request.Builder()
                        .url(address)
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(callback1);
            }
        });
    }
}