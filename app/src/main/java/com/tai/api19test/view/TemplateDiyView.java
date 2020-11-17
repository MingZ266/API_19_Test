package com.tai.api19test.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.tai.api19test.R;

/**
 * 自定义View的模版.
 */
public class TemplateDiyView extends View {
    private DisplayMetrics dm;
    private Paint paint;// 画笔
    private Path path;// 路径1
    private Path path2;// 路径2
    private Region region;// 区域
    private Rect rect;// 矩形（整型）
    private RectF rectF;// 矩形（浮点型）
    private float[] radii;// 圆角矩形四个角各角的椭圆半长轴长、半短轴长，共8个数据

    public TemplateDiyView(Context context) {
        this(context, null);
    }

    public TemplateDiyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TemplateDiyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dm = context.getResources().getDisplayMetrics();
        paint = new Paint();
        path = new Path();
        path2 = new Path();
        region = new Region();
        rect = new Rect();
        rectF = new RectF();
        radii = new float[8];
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TemplateDiyView);
        try {

        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wSize = MeasureSpec.getSize(widthMeasureSpec), wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec), hMode = MeasureSpec.getMode(heightMeasureSpec);
        if (wMode != MeasureSpec.EXACTLY)
            wSize = fromDpToPx(200);
        if (hMode != MeasureSpec.EXACTLY)
            hSize = fromDpToPx(20);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(wSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(hSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private int fromDpToPx(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, dm);
    }

    // 刷新View使用requestLayout()
}
