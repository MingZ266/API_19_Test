package com.tai.api19test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PicCutActivity extends AppCompatActivity {
    private Context context = PicCutActivity.this;
    private String TAG = "PicCutActivityTAG";
    private Uri uri;// 选中的图片
    private Bitmap bitmap;// 裁剪后的图片
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.CHINA);

    private ImageView showPic;
    private Button choosePic;
    private ImageView showAfterCut;
    private TextView picInfo;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_cut);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        initView();
        myListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 266) {// 选择图片
            if (data == null)
                Log.d(TAG, "请求为空");
            else {
                //Log.d(TAG, "Uri: " + data.getData());
                uri = data.getData();
                if (uri != null) {
                    showPic.setImageURI(uri);
                    Log.d(TAG, "path: " + uri.getPath());
                }
            }
        }
        if (requestCode == 262) {// 裁剪图片
            if (data != null) {
                //showAfterCut.setImageURI(data.getData());
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    bitmap = bundle.getParcelable("data");
                    if (bitmap != null) {
                        showAfterCut.setImageBitmap(bitmap);
                        Log.d(TAG, "bitmap: " + bitmap);
                        save.setEnabled(true);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        bitmap.recycle();
        super.onBackPressed();
    }

    private void initView() {
        showPic = findViewById(R.id.showPic);
        choosePic = findViewById(R.id.choosePic);
        showAfterCut = findViewById(R.id.showAfterCut);
        picInfo = findViewById(R.id.picInfo);
        save = findViewById(R.id.save);
    }

    private void myListener() {
        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSystemPicAlbum = new Intent(Intent.ACTION_PICK);
                toSystemPicAlbum.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(toSystemPicAlbum, 266);
            }
        });

        showPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCut = new Intent("com.android.camera.action.CROP");
                toCut.setDataAndType(uri, "image/*");
                // 设置启用裁剪
                toCut.putExtra("crop", "true");
                // aspectX aspectY 宽高的比例
                toCut.putExtra("aspectX", 1);
                toCut.putExtra("aspectY", 1);
                // outputX outputY 裁剪图片的宽高  !建议不要超过350，有crash风险
                toCut.putExtra("outputX", 200);
                toCut.putExtra("outputY", 200);
                // 设置裁剪数据保留在Bitmap中
                toCut.putExtra("return-data", true);
                // 设置非圆形裁剪
                toCut.putExtra("circleCrop", false);
                // "MediaStore.EXTRA_OUTPUT", uri    将裁剪数据保存在uri中
                try {
                    startActivityForResult(toCut, 262);
                } catch (ActivityNotFoundException e) {
                    Log.d(TAG, "裁剪失败");
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (getExternalCacheDir() == null) {
                    Log.d(TAG, "保存失败");
                    return;
                }
                String filePath = getExternalCacheDir().getAbsolutePath();
                Log.d(TAG, "path: " + filePath);
                String fileName = format.format(System.currentTimeMillis()) + ".jpeg";
                File destImage = new File(filePath, fileName);
                try (FileOutputStream fos = new FileOutputStream(destImage)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                } catch (IOException e) {
                    Log.d(TAG, "保存失败");
                }
                picInfo.setText("文件名：" + fileName + "    尺寸：" + destImage.length() + "B");
            }
        });
    }
}
