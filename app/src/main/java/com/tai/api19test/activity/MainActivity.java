package com.tai.api19test.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.github.aelstad.keccakj.fips202.SHA3_256;

import org.apache.commons.mycodec.binary.Hex;
import org.apache.commons.mycodec.digest.DigestUtils;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.tai.api19test.R;
import com.tai.api19test.view.AutoHeightPagerAdapter;
import com.tai.api19test.view.AutoHeightViewPager;
import com.tai.api19test.util.Tools;

public class MainActivity extends AppCompatActivity {
    private final Context context = MainActivity.this;
    private static boolean saveTime = true;
    private AutoHeightPagerAdapter adapter;
    private Tools.UnitChange uc;

    private AutoHeightViewPager btnPager;
    private LinearLayout pointLinear;
    private Button restartBtn;
    private TextView testText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (saveTime)
            Tools.saveStartTimeLog(context);

        /*Log.d("TAG", ":" + getSHA265(""));
        Log.d("TAG", "----------------------------");
        Log.d("TAG", "XQ: " + getSHA265("XQ"));
        Log.d("TAG", "zhangaoran: " + getSHA265("zhangaoran"));
        Log.d("TAG", "----------------------------");
        Log.d("TAG", "XQ: " + getSHA265("XQ"));
        Log.d("TAG", "zhangaoran: " + getSHA265("zhangaoran"));
        Log.d("TAG", "----------------------------");*/

        //Log.d("TAG", "加密：" + getSHA3_256("123456"));

        initView();
        myProcess();
        myListener();

        testMethod();
    }

    private String getSHA265(String psw) {
        /*byte[] temp = DigestUtils.sha256(psw);
        return new String(Hex.encodeHex(temp));*/
        return DigestUtils.sha256Hex(psw);
    }

    private String getSHA3_256(String psw) {
        //MessageDigest md = new SHA3_256();
        //AbstractSpongeStreamCipher spongeStreamCipher = new Shake256StreamCipher();
        SHA3_256 tool = new SHA3_256();
        byte[] out = tool.digest(psw.getBytes());
        Log.d("TAG", "应为：" + new String(Hex.encodeHex(out)));
        return new BigInteger(1, out).toString(16);
    }

    private void initView() {
        btnPager = findViewById(R.id.btnPager);
        pointLinear = findViewById(R.id.pointLinear);
        restartBtn = findViewById(R.id.restartBtn);
        testText = findViewById(R.id.testText);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void myProcess() {
        uc = new Tools.UnitChange(context);
        // 添加btnPager
        List<View> viewList = new ArrayList<>();
        viewList.add(View.inflate(context, R.layout.btn_pager_one, null));
        viewList.add(View.inflate(context, R.layout.btn_pager_one, null));
        viewList.add(View.inflate(context, R.layout.btn_pager_one, null));
        viewList.add(View.inflate(context, R.layout.btn_pager_one, null));
        viewList.add(View.inflate(context, R.layout.btn_pager_one, null));
        viewList.add(View.inflate(context, R.layout.btn_pager_one, null));

        adapter = new AutoHeightPagerAdapter(viewList);
        btnPager.setAdapter(adapter);

        // 添加圆点指示器
        View point;
        LinearLayout.LayoutParams params;
        for (int i = 0; i < 6; i++) {
            point = new View(context);
            point.setBackground(getResources().getDrawable(R.drawable.point));
            if (i == 0) {
                params = new LinearLayout.LayoutParams(uc.fromDpToPx(8), uc.fromDpToPx(8));
                point.setEnabled(false);
            }
            else {
                params = new LinearLayout.LayoutParams(uc.fromDpToPx(5), uc.fromDpToPx(5));
                params.leftMargin = uc.fromDpToPx(10);
            }
            point.setLayoutParams(params);
            pointLinear.addView(point);
        }
    }

    private void myListener() {
        btnPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int lastPager = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                btnPager.requestLayout();
                // 更改圆点尺寸和颜色
                View nowPoint = pointLinear.getChildAt(position);
                View lastPoint = pointLinear.getChildAt(lastPager);
                LinearLayout.LayoutParams nowParams = new LinearLayout.LayoutParams(uc.fromDpToPx(8), uc.fromDpToPx(8));
                LinearLayout.LayoutParams lastParams = new LinearLayout.LayoutParams(uc.fromDpToPx(5), uc.fromDpToPx(5));
                if (position != 0)
                    nowParams.leftMargin = uc.fromDpToPx(10);
                if (lastPager != 0)
                    lastParams.leftMargin = uc.fromDpToPx(10);
                nowPoint.setLayoutParams(nowParams);
                lastPoint.setLayoutParams(lastParams);
                nowPoint.setEnabled(false);
                lastPoint.setEnabled(true);
                lastPager = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTime = false;
                Intent restart = getIntent();
                restart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                restart.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(restart);
            }
        });

        // btn_pager_one
        skip(adapter.getIndexView(0).findViewById(R.id.toFile), FileActivity.class);
        skip(adapter.getIndexView(0).findViewById(R.id.toTestList), TestListActivity.class);
        skip(adapter.getIndexView(0).findViewById(R.id.toCheckBox), CheckBoxActivity.class);
        skip(adapter.getIndexView(0).findViewById(R.id.toTimeDeal), TimeDealActivity.class);
        skip(adapter.getIndexView(0).findViewById(R.id.toTest), TestActivity.class);
        skip(adapter.getIndexView(0).findViewById(R.id.toBackground), BackgroundActivity.class);
        skip(adapter.getIndexView(0).findViewById(R.id.toDiverseNotify), DiverseNotifyActivity.class);
        skip(adapter.getIndexView(0).findViewById(R.id.toPicCut), PicCutActivity.class);
        skip(adapter.getIndexView(0).findViewById(R.id.toNetRequest), NetRequestActivity.class);
        skip(adapter.getIndexView(0).findViewById(R.id.toScrollTest), ScrollTestActivity.class);
        skip(adapter.getIndexView(0).findViewById(R.id.toQRCode), QRCodeActivity.class);
    }

    private void skip(View skipBtn, final Class<? extends AppCompatActivity> skipActivityClass) {
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, skipActivityClass));
            }
        });
    }

    private void testMethod() {
        String TAG = "testMethodTAG";
        Class<?> clazz = getWindow().getDecorView().getClass();
        Log.d(TAG, clazz.getSimpleName());
        //Log.d(TAG, Arrays.toString(clazz.getDeclaredFields()));
        /*for (Field f : clazz.getDeclaredFields()) {
            Log.d(TAG, f.toString());
        }*/
        try {
            Field barColor = clazz.getDeclaredField("mSemiTransparentStatusBarColor");
            barColor.setAccessible(true);
            Log.d(TAG, "颜色: #" + Integer.toHexString((int) barColor.get(getWindow().getDecorView())));
            barColor.set(getWindow().getDecorView(), Color.BLUE);
            Log.d(TAG, "修改后颜色: #" + Integer.toHexString((int) barColor.get(getWindow().getDecorView())));
        } catch (NoSuchFieldException e) {
            Log.d(TAG, "反射失败");
        } catch (IllegalAccessException e) {
            Log.d(TAG, "访问值失败");
        }
    }
}