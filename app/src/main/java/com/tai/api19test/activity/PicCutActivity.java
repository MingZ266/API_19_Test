package com.tai.api19test.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.tai.api19test.R;
import com.tai.api19test.util.Tools;
import com.tai.api19test.view.MyImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PicCutActivity extends AppCompatActivity {
    private final Context context = PicCutActivity.this;
    private final String TAG = "PicCutActivityTAG";
    private Uri uri;// 选中的图片
    private Bitmap bitmap;// 裁剪后的图片
    private File cameraFile;// 拍照后的图片
    private String srcImageCode;
    private Bitmap codeBitmap;
    private final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.CHINA);

    private Button camera;
    private MyImageView showPic;
    private Button choosePic;
    private MyImageView showAfterCut;
    private TextView picInfo;
    private Button save;
    private Button compress;
    private Button chooseSrcPic;
    private MyImageView showSrcPic;
    private Button codeToPic;
    private MyImageView showCodePic;
    private MyImageView cutPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_cut);

        initView();
        myListener();
    }

    private void initView() {
        camera = findViewById(R.id.camera);
        showPic = findViewById(R.id.showPic);
        choosePic = findViewById(R.id.choosePic);
        showAfterCut = findViewById(R.id.showAfterCut);
        picInfo = findViewById(R.id.picInfo);
        save = findViewById(R.id.save);
        compress = findViewById(R.id.compress);
        chooseSrcPic = findViewById(R.id.chooseSrcPic);
        showSrcPic = findViewById(R.id.showSrcPic);
        codeToPic = findViewById(R.id.codeToPic);
        showCodePic = findViewById(R.id.showCodePic);
        cutPic = findViewById(R.id.cutPic);
    }

    @SuppressLint("SetTextI18n")
    private void myListener() {
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File external = getExternalFilesDir("");
                if (external != null) {
                    String filePath = external.getAbsolutePath();
                    String fileName = format.format(System.currentTimeMillis()) + ".jpg";
                    cameraFile = new File(filePath, fileName);
                    Log.d(TAG, "e path: " + filePath);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context, getPackageName() + ".fileProvider", cameraFile));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                    }
                    startActivityForResult(intent, 222);
                } else {
                    Log.d(TAG, "无外部存储");
                }
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

        chooseSrcPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSystemPicAlbum = new Intent(Intent.ACTION_PICK);
                toSystemPicAlbum.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(toSystemPicAlbum, 260);
            }
        });

        codeToPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (srcImageCode != null) {
                    try {
                        byte[] pic = Base64.decode(srcImageCode, Base64.NO_WRAP);
                        codeBitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
                        showCodePic.setImageBitmap(codeBitmap);
                    } catch (Exception e) {
                        Log.d(TAG, "解码失败：" + e.getClass().getSimpleName() + ": " + e.getMessage());
                    }
                }
            }
        });

        showCodePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (codeBitmap != null) {
                    int srcWeightHalf = codeBitmap.getWidth() / 2;
                    int srcHeightHalf = codeBitmap.getHeight() / 2;
                    Bitmap cutBitmap = Bitmap.createBitmap(codeBitmap, srcWeightHalf / 2, srcHeightHalf / 2,
                            srcWeightHalf, srcHeightHalf);
                    cutPic.setImageBitmap(cutBitmap);
                }
            }
        });
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
                case 260:// 选择将被编码的图片
                    // 被转换为Base64编码的图片
                    final Uri srcUri = data.getData();
                    if (srcUri != null) {
                        showSrcPic.setImageURI(srcUri);
                            new Thread() {// 图片过大会阻塞线程，故使用子线程以避免ANR
                                @Override
                                public void run() {
                                    try {
                                        Bitmap srcBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), srcUri);
                                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                        srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                                        srcImageCode = Base64.encodeToString(bos.toByteArray(), Base64.NO_WRAP);
                                        Log.d(TAG, "imageCode：" + srcImageCode);
                                    } catch (IOException e) {
                                        Log.d(TAG, "使用Uri构造Bitmap失败");
                                    }
                                }
                            }.start();
                    }
                    break;
            }
        } else {
            if (requestCode == 222) {
                Bitmap bitmap = BitmapFactory.decodeFile(cameraFile.getAbsolutePath());
                showPic.setImageBitmap(bitmap);
            } else
                Log.d(TAG, "返回数据为空");
        }
    }

    @Override
    public void onBackPressed() {
        if (bitmap != null) {
            bitmap.recycle();
        }
        if (codeBitmap != null) {
            codeBitmap.recycle();
        }
        super.onBackPressed();
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
