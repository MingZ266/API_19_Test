package com.tai.api19test.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Tools {
    public static String selfStartCfg = "SelfStart.cfg";

    private static String TAG = "ToolsTAG";

    /**
     * 测量View的宽高.
     *
     * 获得View的宽高前需发送一个View来进行测量;
     * 测量已展示出来的View可能导致View的展示效果改变;
     * 在view没有指定的父控件时，match_parent效果同warp_content.
     */
    public static class AboutView {
        private View view;

        public void setView(View view) {
            if (view == null)
                throw new NullPointerException("传给Tools.AboutView的view为空");
            this.view = view;
            this.view.measure(0, 0);
        }

        public int getViewW() {
            if (view == null)
                throw new NullPointerException("Tools.AboutView中的view为空");
            return view.getMeasuredWidth();
        }

        public int getViewH() {
            if (view == null)
                throw new NullPointerException("Tools.AboutView中的view为空");
            return view.getMeasuredHeight();
        }
    }

    /**
     * 对表示屏幕大小的部分单位进行单位转换.
     *
     * 可转换单位：
     *      px -> dp    dp -> px
     *      px -> sp    sp -> px
     */
    public static class UnitChange {
        private DisplayMetrics dm;

        public UnitChange(Context context) {
            dm = context.getResources().getDisplayMetrics();
        }

        public int fromPxToDp(float pxValue) {
            return (int) (pxValue / dm.density + 0.5f);
        }

        public int fromPxToSp(float pxValue) {
            return (int) (pxValue / dm.scaledDensity + 0.5f);
        }

        public int fromDpToPx(float dpValue) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, dm);
        }

        public int fromSpToPx(float spValue) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, dm);
        }
    }

    public static void saveStartTimeLog(Context context) {
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日 HH:mm:ss", Locale.CHINA);
        //Log.d(TAG, format.format(System.currentTimeMillis()));
        File cache = context.getExternalCacheDir();
        if (cache != null) {
            File file = new File(cache.getAbsolutePath(), "StartTime.log");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file, true);
                fos.write((format.format(System.currentTimeMillis()) + '\n').getBytes());
            } catch (Exception e) {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ex) {
                        // ignore
                    }
                }
            }
        }
    }

    /**
     * 进行简单配置的存储.
     * 存储路径为 data/包名/shared_prefs/ 下
     *
     * @param context 需要存储配置的Activity上下文
     * @param fileName 文件名
     * @param keyAndValue 以key，value，key，value，...这种方式的存储值
     *                    若为奇数个，将舍掉最后一个
     */
    public static void saveSharedPreferences(Context context, String fileName, String... keyAndValue) {
        if (keyAndValue.length % 2 == 1) {
            String[] newStrings = new String[keyAndValue.length - 1];
            System.arraycopy(keyAndValue, 0, newStrings, 0, newStrings.length);
            keyAndValue = newStrings;
        }
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        for (int i = 0; i < keyAndValue.length; i += 2) {
            editor.putString(keyAndValue[i], keyAndValue[i + 1]);
        }
        editor.apply();
    }

    /**
     * 清空配置文件.
     */
    public static void clearSharedPreferences(Context context, String fileName) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    //---------------------图片、Uri、Bitmap----------------------
    /**
     * 将图片的Uri转为Bitmap对象.
     *
     * @param imageUri 图片的uri
     * @return 转换后的Bitmap对象
     */
    public static Bitmap uri2Bitmap(Context context, Uri imageUri) {
        if (imageUri == null)
            return null;
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 将图片的Bitmap对象转为uri.
     * 通过向相册插入bitmap图像实现，因此建议uri使用后删除图片
     *
     * @param bitmap 图片的Bitmap对象
     * @return 指向插入相册的图片的uri
     */
    public static Uri bitmap2Uri(Context context, Bitmap bitmap) {
        if (bitmap == null)
            return null;
        return Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null));
    }

    /**
     * 获得uri指向的图片存储路径.
     *
     * @param imageUri 图片的uri
     * @return 返回图片的存储路径
     */
    public static String getImagePathFromUri(Context context, Uri imageUri) {
        String imagePath;
        try (Cursor cursor = context.getContentResolver().query(imageUri, new String[]{MediaStore.Images.Media.DATA}, null, null, null)) {
          if (cursor == null)
              imagePath = imageUri.getPath();
          else {
              int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
              cursor.moveToFirst();
              imagePath = context.getString(index);
          }
        }
        return imagePath;
    }

    /**
     * 删除uri指向的图片.
     *
     * @param imageUri 图片的uri
     */
    @SuppressWarnings("all")// 忽略未使用delete的返回值
    public static void delImageFromUri(Context context, Uri imageUri) {
        if (imageUri == null)
            return;
        File image = new File(getImagePathFromUri(context, imageUri));
         image.delete();
    }
}
