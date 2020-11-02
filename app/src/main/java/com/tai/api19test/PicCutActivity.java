package com.tai.api19test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PicCutActivity extends AppCompatActivity {
    private Context context = PicCutActivity.this;
    private String TAG = "PicCutActivityTAG";
    private Uri uri;// 选中的图片
    private Bitmap bitmap;// 裁剪后的图片
    private File cameraFile;// 拍照后的图片
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.CHINA);

    private Button camera;
    private ImageView showPic;
    private Button choosePic;
    private ImageView showAfterCut;
    private TextView picInfo;
    private Button save;
    private Button compress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_cut);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 0);
        }

        initView();
        myListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case 222:// 拍照
                    Log.d(TAG, "拍照");
                    Bundle bundle0 = data.getExtras();
                    if (bundle0 != null) {
                        Bitmap bitmap = (Bitmap) bundle0.get("data");
                        if (bitmap != null) {
                            showPic.setImageBitmap(bitmap);
                            uri = Tools.bitmap2Uri(context, bitmap);
                        }
                    }
                    break;
                case 266:// 选择图片
                    uri = data.getData();
                    if (uri != null) {
                        showPic.setImageURI(uri);
                        Log.d(TAG, "path: " + uri.getPath());
                    }
                    break;
                case 262:// 裁剪图片
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        bitmap = bundle.getParcelable("data");
                        if (bitmap != null) {
                            showAfterCut.setImageBitmap(bitmap);
                            Log.d(TAG, "bitmap: " + bitmap);
                            save.setEnabled(true);
                            compress.setEnabled(true);
                        }
                    }
                    break;
            }
        } else {
            Log.d(TAG, "返回数据为空");
        }
    }

    @Override
    public void onBackPressed() {
        bitmap.recycle();
        super.onBackPressed();
    }

    private void initView() {
        camera = findViewById(R.id.camera);
        showPic = findViewById(R.id.showPic);
        choosePic = findViewById(R.id.choosePic);
        showAfterCut = findViewById(R.id.showAfterCut);
        picInfo = findViewById(R.id.picInfo);
        save = findViewById(R.id.save);
        compress = findViewById(R.id.compress);
    }

    @SuppressLint("SetTextI18n")
    private void myListener() {
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePath = Environment.getDataDirectory().getAbsolutePath();
                String fileName = format.format(System.currentTimeMillis()) + ".jpg";
                cameraFile = new File(filePath, fileName);
                Log.d(TAG, "e path: " + filePath);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFile);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                }
                startActivityForResult(intent, 222);
            }
        });

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
            @Override
            public void onClick(View v) {
                File imageFile = saveImage();
                if (imageFile != null)
                    picInfo.setText("文件名：" + imageFile.getName() + "    尺寸：" + imageFile.length() + "B");
            }
        });

        compress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File imageFile = saveImage();
                if (imageFile != null) {
                    long size = imageFile.length();
                    if (size > 2048/*2kB*/) {
                        int scale = (int) (204800 / size);
                        Log.d(TAG, "scale: " + scale);
                        if (scale <= 0)
                            scale = 1;
                        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, scale, fos);
                            fos.flush();
                        } catch (IOException e) {
                            Log.d(TAG, "保存失败");
                        }
                        picInfo.setText("文件名：" + imageFile.getName() + "    尺寸：" + imageFile.length() + "B");
                    }
                }
            }
        });
    }

    private File saveImage() {
        if (getExternalCacheDir() == null) {
            Log.d(TAG, "保存失败");
            return null;
        }
        String filePath = getExternalCacheDir().getAbsolutePath();
        String fileName = format.format(System.currentTimeMillis()) + ".jpeg";
        File destImage = new File(filePath, fileName);
        try (FileOutputStream fos = new FileOutputStream(destImage)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            Log.d(TAG, "保存失败");
        }
        return destImage;
    }
}
