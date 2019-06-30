package wzp.com.texturemusic.core.customui;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * author:Go_oG
 * date: on 2018/5/11
 * packageName: wzp.com.texturemusic.core.customview
 * 优化RecyclerView相关
 */
public class BaseRecyclerView extends RecyclerView {
    //竖直滑动时的抛掷速度因子
    private double flingScale = 1;

    public BaseRecyclerView(Context context) {
        super(context);
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initCacheConfig();
    }


    /**
     * 当内存足够时可以适当提高缓存大小
     */
    private void initCacheConfig(){
        setItemViewCacheSize(30);
        setDrawingCacheEnabled(true);
        setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    public void setFlingScale(double flingScale) {
        this.flingScale = flingScale;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityY *= flingScale;
        return super.fling(velocityX, velocityY);
    }
}
