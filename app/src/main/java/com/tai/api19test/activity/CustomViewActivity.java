package com.tai.api19test.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tai.api19test.R;
import com.tai.api19test.util.Tools;
import com.tai.api19test.view.CropPictureView;
import com.tai.api19test.view.MyImageView;

import java.io.FileNotFoundException;

public class CustomViewActivity extends AppCompatActivity {
    private final AppCompatActivity activity = this;
    private static final String TAG = "CustomViewTAG";
    private final int choosePicRequestCode = 266;

    private CropPictureView cropPicture;
    private Button choosePic;
    private Button sure;
    private MyImageView showCropPic;

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
                showCropPic.setImageBitmap(cropPicture.getCropPicBitmap());
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
                    /*try {
                        cropPicture.setSrcPic(uri);
                    } catch (FileNotFoundException e) {
                        Log.d(TAG, "Uri转图片失败");
                    }*/
                    cropPicture.setSrcPic(Tools.uri2Bitmap(activity, uri));
                }
            }
        }
    }
}