package com.tai.api19test.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class MyImageView extends AppCompatImageView {
    String TAG = "MyImageViewTAG";

    public MyImageView(@NonNull Context context) {
        super(context);
    }

    public MyImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        changeSize:
        if (drawable != null) {
            int wMode = MeasureSpec.getMode(widthMeasureSpec);
            int wSize = MeasureSpec.getSize(widthMeasureSpec);
            int hMode = MeasureSpec.getMode(heightMeasureSpec);
            int hSize = MeasureSpec.getSize(heightMeasureSpec);
            int wPic  = drawable.getIntrinsicWidth();
            int hPic  = drawable.getIntrinsicHeight();
            if (wMode == MeasureSpec.EXACTLY) {
                if (hMode == MeasureSpec.EXACTLY)
                    break changeSize;
                hSize = (int) ((double) hPic / wPic * wSize);
                /*Log.d(TAG, "确定的宽");
                Log.d(TAG, "wPic=" + wPic + "  hPic=" + hPic);
                Log.d(TAG, "wSize=" + wSize + "  hSize=" + hSize);*/
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(hSize, MeasureSpec.EXACTLY);
            } else if (hMode == MeasureSpec.EXACTLY) {
                wSize = (int) ((double) wPic / hPic * hSize);
                /*Log.d(TAG, "确定的高");
                Log.d(TAG, "wPic=" + wPic + "  hPic=" + hPic);
                Log.d(TAG, "wSize=" + wSize + "  hSize=" + hSize);*/
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(wSize, MeasureSpec.EXACTLY);
            } else {
                /*Log.d(TAG, "未确定宽高");
                Log.d(TAG, "宽：");
                displayMode(wMode);
                Log.d(TAG, "高：");
                displayMode(hMode);
                Log.d(TAG, "wPic=" + wPic + "  hPic=" + hPic);*/
                if (wMode == MeasureSpec.AT_MOST) {
                    hSize = (int) ((double) hPic / wPic * wSize);
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(hSize, MeasureSpec.EXACTLY);
                } else if (hMode == MeasureSpec.AT_MOST) {
                    wSize = (int) ((double) wPic / hPic * hSize);
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(wSize, MeasureSpec.EXACTLY);
                }
                //Log.d(TAG, "wSize=" + wSize + "  hSize=" + hSize);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void displayMode(int mode) {
        switch (mode) {
            case MeasureSpec.EXACTLY:
                Log.d(TAG, "精确模式");
                break;
            case MeasureSpec.UNSPECIFIED:
                Log.d(TAG, "无限制模式");
                break;
            case MeasureSpec.AT_MOST:
                Log.d(TAG, "有最大值模式");
                break;
            default:
                Log.d(TAG, "err: 未知模式");
        }
    }
}
