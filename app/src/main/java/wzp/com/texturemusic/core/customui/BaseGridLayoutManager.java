package wzp.com.texturemusic.core.customui;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;

/**
 * author:Go_oG
 * date: on 2018/5/11
 * packageName: wzp.com.texturemusic.core.customui
 */
public class BaseGridLayoutManager extends GridLayoutManager {
    //让RecyclerView能够预先加载部分数据
    private int extraLayoutSpace = 300;
    //RecyclerView的滑动速度因子，通过改动该参数实现Recyclerview不同的滑动速度
    //只针对于竖直方向
    private double slideSpeedRatio = 0.82;

    public void setSlideSpeedRatio(double slideSpeedRatio) {
        this.slideSpeedRatio = slideSpeedRatio;
    }

    public void setExtraLayoutSpace(int extraLayoutSpace) {
        this.extraLayoutSpace = extraLayoutSpace;
    }

    public BaseGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public BaseGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public BaseGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        super.getExtraLayoutSpace(state);
        return extraLayoutSpace;
    }
}
