package DiyView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class AutoHeightViewPager extends ViewPager {

    public AutoHeightViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.d("TAG", "执行onMeasure");
        AutoHeightPagerAdapter adapter = (AutoHeightPagerAdapter) getAdapter();
        if (adapter == null) {
            Log.w("", "AutoHeightViewPager中获得的adapter为空！");
            super.onMeasure(widthMeasureSpec, 0);
            return;
        }
        View nowView = adapter.getIndexView(getCurrentItem());
        if (nowView == null)
            throw new NullPointerException("AutoHeightViewPager中获得的nowView为空！");
        //Log.d("TAG", "获得的高度是：" + nowView.getMeasuredHeight() + "px");
        /*if (getCurrentItem() == 0) {
            Log.d("TAG", "Text区的高度是：" +
                    (
                            nowView.findViewById(R.id.Text_1).getMeasuredHeight() +
                            nowView.findViewById(R.id.Text_2).getMeasuredHeight() +
                            nowView.findViewById(R.id.Text_3).getMeasuredHeight()
                    )
                    + "px");
        }*/
        int result = measureHeight(heightMeasureSpec, nowView);
        //Log.d("TAG", "返回的高度是：" + nowView.getMeasuredHeight() + "px");
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(result, MeasureSpec.EXACTLY));
        //Log.d("TAG", "----------------------");
    }

    private int measureHeight(int measureSpec, View view) {
        int specMode = MeasureSpec.getMode(measureSpec);// 模式
        int specSize = MeasureSpec.getSize(measureSpec);// 尺寸
        if (specMode == MeasureSpec.EXACTLY) {
            //Log.d("TAG", "EXACTLY");
            return specSize;// 精确模式直接返回尺寸
        }
        else {
            view.measure(0, 0);
            //Log.d("TAG", "View高度：" + view.getMeasuredHeight() + "px");
            if (specMode == MeasureSpec.UNSPECIFIED) {
                //Log.d("TAG", "UNSPECIFIED");
                return view.getMeasuredHeight();// 无限制模式返回view高度
            }
            else {
                //Log.d("TAG", "AT_MOST");
                return Math.min(view.getMeasuredHeight(), specSize);// 父控件最大空间模式返回view高度和最大尺寸之间的最小值
            }
        }
    }
}
