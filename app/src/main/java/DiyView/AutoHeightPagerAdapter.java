package DiyView;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AutoHeightPagerAdapter extends PagerAdapter {
    private List<View> viewList;

    public AutoHeightPagerAdapter(List<View> viewList) {
        this.viewList = viewList;
    }

    public View getIndexView(int index) {
        return viewList.get(index);
    }

    public List<View> getViewList() {
        return new ArrayList<>(viewList);
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
