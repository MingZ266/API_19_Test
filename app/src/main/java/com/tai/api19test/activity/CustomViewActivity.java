package com.tai.api19test.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tai.api19test.R;
import com.tai.api19test.util.Tools;
import com.tai.api19test.view.CropPictureView;
import com.tai.api19test.view.MyImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CustomViewActivity extends AppCompatActivity {
    private final AppCompatActivity activity = this;
    private static final String TAG = "CustomViewTAG";
    private final int choosePicRequestCode = 266;
    private boolean turnDo = true;
    private Bitmap cutBitmap;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());

    private CropPictureView cropPicture;
    private Button choosePic;
    private Button sure;
    private MyImageView showCropPic;
    private Button save;
    private Button look;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view);

        initView();
        myListener();
    }

    private void initView() {
        cropPicture = findViewById(R.id.cropPicture);
        choosePic = findViewById(R.id.choosePic);
        sure = findViewById(R.id.sure);
        showCropPic = findViewById(R.id.showCropPic);
        save = findViewById(R.id.save);
        look = findViewById(R.id.look);
    }

    private void myListener() {
        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSystemPicAlbum = new Intent(Intent.ACTION_PICK);
                toSystemPicAlbum.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(toSystemPicAlbum, choosePicRequestCode);
            }
        });

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cutBitmap = cropPicture.getCropPicBitmap();
                showCropPic.setImageBitmap(cutBitmap);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cutBitmap != null) {
                    Log.d(TAG, "Bitmap图片尺寸：" + cutBitmap.getByteCount() + "或" + cutBitmap.getAllocationByteCount());
                    String filename = dateFormat.format(System.currentTimeMillis());
                    File cutPicPNGFile = Tools.generateFileAtFiles(activity, null, filename + ".png", true);
                    File cutPicCodePNGFile = Tools.generateFileAtFiles(activity, null, filename + ".png.txt", true);
                    File cutPicJPEGFile = Tools.generateFileAtFiles(activity, null, filename + ".jpeg", true);
                    File cutPicCodeJPEGFile = Tools.generateFileAtFiles(activity, null, filename + ".jpeg.txt", true);
                    try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                        if (cutBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)) {
                            byte[] picBytes = bos.toByteArray();
                            Tools.saveFile(cutPicCodePNGFile, Base64.encodeToString(picBytes, Base64.NO_WRAP));
                            try (FileOutputStream fos = new FileOutputStream(cutPicPNGFile)) {
                                fos.write(picBytes);
                                Toast.makeText(activity, "已存储PNG", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(activity, "存储PNG失败", Toast.LENGTH_SHORT).show();
                        }
                        bos.reset();
                        if (cutBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
                            byte[] picBytes = bos.toByteArray();
                            Tools.saveFile(cutPicCodeJPEGFile, Base64.encodeToString(picBytes, Base64.NO_WRAP));
                            try (FileOutputStream fos = new FileOutputStream(cutPicJPEGFile)) {
                                fos.write(picBytes);
                                Toast.makeText(activity, "已存储JPEG", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(activity, "存储JPEG失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.d(TAG, "存储文件失败");
                    }
                }
            }
        });

        look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final File file = getExternalFilesDir("");
                if (file != null) {
                    final String[] filenames = file.list();
                    if (filenames != null) {
                        new Thread() {
                            @Override
                            public void run() {
                                Bitmap bitmap;
                                for (String filename : filenames) {
                                    if (filename.endsWith(".jpeg.txt")) {
                                        String code = Tools.readFile(new File(file.getAbsolutePath(), filename));
                                        if (code != null) {
                                            byte[] bytes = Base64.decode(code, Base64.NO_WRAP);
                                            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            setPic(bitmap);
                                            try {
                                                sleep(2000);
                                            } catch (InterruptedException e) {
                                                // ignore
                                            }
                                        }
                                    }
                                }
                                setPic(null);
                            }

                            private void setPic(final Bitmap bitmap) {
                                showCropPic.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showCropPic.setImageBitmap(bitmap);
                                    }
                                });
                            }
                        }.start();
                        return;
                    }
                }
                Toast.makeText(activity, "查看图片失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == choosePicRequestCode) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    if (turnDo) {
                        turnDo = false;
                        try {
                            cropPicture.setSrcPic(uri);
                        } catch (FileNotFoundException e) {
                            Log.d(TAG, "Uri转图片失败");
                        }
                    } else {
                        turnDo = true;
                        cropPicture.setSrcPic(Tools.uri2Bitmap(activity, uri));
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (cutBitmap != null) {
            cutBitmap.recycle();
        }
        super.onBackPressed();
    }
}