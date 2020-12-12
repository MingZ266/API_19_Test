package com.tai.api19test.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Tools {
    public static final String selfStartCfg = "SelfStart.cfg";

    private static final String TAG = "ToolsTAG";
    private static final Gson gson = new Gson();

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
        private final DisplayMetrics dm;

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

    private static <E> List<E> getListFromJson(String json, Class<E> eClass) {
        try {
            List<E> list = new LinkedList<>();
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(gson.fromJson(String.valueOf(jsonArray.getJSONObject(i)), eClass));
            }
            return list;
        } catch (JSONException e) {
            Log.d(TAG, "解析为List结构失败，原因是：" + e.getClass().getSimpleName() + ":" + e.getMessage());
            return null;
        }
    }

    //========================文件==========================
    /**
     * 简单配置的存储.
     * 存储路径为 /data/user/0/包名/shared_prefs/ 下
     *
     * @param context 需要存储配置的Activity上下文
     * @param fileName 配置的文件名
     * @param keyAndValue 以key，value，key，value，...这种方式的存储值序列。
     *                    若为值数目奇数个，将舍掉最后一个
     */
    public static void saveSharedPreferences(Context context, String fileName, String... keyAndValue) {
        if (keyAndValue.length % 2 == 1) {
            keyAndValue = Arrays.copyOf(keyAndValue, keyAndValue.length - 1);
        }
        SharedPreferences read = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = read.edit();
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

    /**
     * 对较大的数据进行文件存储.
     *
     * @param file 用来存储数据的文件，原文件内容将被覆盖。
     *             若文件不存在，将会创建。
     * @param data 将被存储的数据
     * @return 若文件存储成功，则返回存储数据的文件引用；否则返回null。
     */
    public static File saveFile(File file, String data) {
        if (file == null || data == null) {
            Log.d(TAG, "保存文件失败：参数含空");
            return null;
        }
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs())
                return null;
        }
        try {
            if (file.exists() || file.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(data.getBytes());
                    return file;
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.d(TAG, "保存文件发生异常：" + e.getMessage());
            return null;
        }
    }

    /**
     * 从文件中读取数据.
     *
     * @param file 指定的要读取数据的文件。
     * @return 若读取成功，则返回读取的结果；否则返回null。
     */
    public static String readFile(File file) {
        if (file == null) {
            Log.d(TAG, "读取文件失败：参数为空");
            return null;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            int len;
            byte[] cache = new byte[1024];
            StringBuilder stringBuilder = new StringBuilder();
            while ((len = fis.read(cache)) != -1) {
                stringBuilder.append(new String(cache, 0, len));
            }
            return String.valueOf(stringBuilder);
        } catch (IOException e) {
            Log.d(TAG, "读取文件发生异常：" + e.getMessage());
            return null;
        }
    }

    /**
     * 将对象保存到文件.
     *
     * @param file 用来存储数据的文件，原文件内容将被覆盖。
     *             若文件不存在，将会创建。
     * @param object 保存的对象
     * @param <T> 任意对象类型
     * @return 若文件存储成功，则返回存储数据的文件引用；否则返回null。
     */
    public static <T> File saveObjectAtFile(File file, T object) {
        return saveFile(file, gson.toJson(object));
    }

    /**
     * 从文件读取对象.
     *
     * @param file 指定的要读取对象的文件
     * @param tClass 读取的对象类型
     * @param <T> 任意对象类型
     * @return 若读取成功，则返回读取的对象；否则返回null。
     */
    public static <T> T readObjectFromFile(File file, Class<T> tClass) {
        String readData = readFile(file);
        if (readData != null)
            return gson.fromJson(readData, tClass);
        else
            return null;
    }

    /**
     * 从文件中读取List对象.
     *
     * @param file 指定的要读取对象的文件
     * @param eClass 读取的List集合元素类型
     * @param <E> 任意对象类型
     * @return 若读取成功，则返回读取的List集合；否则返回null
     */
    public static <E> List<E> readListFromFile(File file, Class<E> eClass) {
        String data = readFile(file);
        if (data == null) {
            return null;
        }
        return getListFromJson(data, eClass);
    }

    /**
     * 通过指定的各个属性生成cache目录下的文件引用.
     *
     * @param directory 存储的文件目录
     * @param filename 存储的文件名
     * @param atExternal true表示保存在外部存储设备；false表示保存在内部存储
     * @return 返回生成的文件引用
     */
    public static File generateFileAtCache(Context context, String directory, String filename, boolean atExternal) {
        String path;
        if (atExternal)
            path = getExternalCachePath(context);
        else
            path = getInternalCachePath(context);
        if (!TextUtils.isEmpty(directory)) {
            if (directory.lastIndexOf('/') == directory.length() - 1)// 若末尾有'/'，则去除
                directory = directory.substring(0, directory.length() - 1);
            if (directory.indexOf('/') == 0)
                path += directory;
            else
                path += ("/" + directory);
        }
        return new File(path, filename);
    }

    /**
     * 通过指定的各个属性生成files目录下的文件引用.
     *
     * @param directory 存储的文件目录
     * @param filename 存储的文件名
     * @param atExternal true表示保存在外部存储设备；false表示保存在内部存储
     * @return 返回生成的文件引用
     */
    public static File generateFileAtFiles(Context context, String directory, String filename, boolean atExternal) {
        String path;
        if (atExternal)
            path = getExternalFilesPath(context);
        else
            path = getInternalFilesPath(context);
        if (!TextUtils.isEmpty(directory)) {
            if (directory.lastIndexOf('/') == directory.length() - 1)// 若末尾有'/'，则去除
                directory = directory.substring(0, directory.length() - 1);
            if (directory.indexOf('/') == 0)
                path += directory;
            else
                path += ("/" + directory);
        }
        return new File(path, filename);
    }

    //// 内部存储(internal)，路径一般为：
    ////         /data/user/0/包名/...
    /**
     * 获得内部存储中的cache目录路径.
     *
     * @return 返回内部存储中的cache目录路径
     */
    public static String getInternalCachePath(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    /**
     * 获得内部存储中的files目录路径.
     *
     * @return 返回内部存储中的files目录路径.
     */
    public static String getInternalFilesPath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    //// 外部存储(external)，路径一般为：
    ////         /storage/emulated/0/Android/data/包名/...
    /**
     * 获得外部存储中的cache目录路径.
     *
     * @return 返回外部存储中的cache目录路径；
     *         若获取为空，则返回内部存储中的cache目录路径
     */
    public static String getExternalCachePath(Context context) {
        File cache = context.getExternalCacheDir();
        if (cache != null)
            return context.getExternalCacheDir().getAbsolutePath();
        else
            return getInternalCachePath(context);
    }

    /**
     * 获得外部存储中的files目录路径.
     *
     * @return 返回外部存储中的files目录路径；
     *         若获取为空，则返回外部存储中的files目录路径
     */
    public static String getExternalFilesPath(Context context) {
        File file = context.getExternalFilesDir("");
        if (file != null)
            return context.getExternalFilesDir("").getAbsolutePath();
        else
            return getInternalFilesPath(context);
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
