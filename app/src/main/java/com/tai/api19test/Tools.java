package com.tai.api19test;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

class Tools {
    static String selfStartCfg = "SelfStart.cfg";

    private static String TAG = "ToolsTAG";

    /**
     * 测量View的宽高.
     *
     * 获得View的宽高前需发送一个View来进行测量;
     * 测量已展示出来的View可能导致View的展示效果改变;
     * 在view没有指定的父控件时，match_parent效果同warp_content.
     */
    static class AboutView {
        private View view;

        void setView(View view) {
            if (view == null)
                throw new NullPointerException("传给Tools.AboutView的view为空");
            this.view = view;
            this.view.measure(0, 0);
        }

        int getViewW() {
            if (view == null)
                throw new NullPointerException("Tools.AboutView中的view为空");
            return view.getMeasuredWidth();
        }

        int getViewH() {
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
    static class UnitChange {
        private DisplayMetrics dm;

        UnitChange(Context context) {
            dm = context.getResources().getDisplayMetrics();
        }

        int fromPxToDp(float pxValue) {
            return (int) (pxValue / dm.density + 0.5f);
        }

        int fromPxToSp(float pxValue) {
            return (int) (pxValue / dm.scaledDensity + 0.5f);
        }

        int fromDpToPx(float dpValue) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, dm);
        }

        int fromSpToPx(float spValue) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, dm);
        }
    }

    static void saveStartTimeLog(Context context) {
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
}
