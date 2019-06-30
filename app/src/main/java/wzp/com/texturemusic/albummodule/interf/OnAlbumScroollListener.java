package wzp.com.texturemusic.albummodule.interf;

import androidx.recyclerview.widget.RecyclerView;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CenterScrollListener;


/**
 * Created by Go_oG
 * Description:专门用于滚动recycleview的监听器
 * on 2017/11/10.
 */

public abstract class OnAlbumScroollListener extends CenterScrollListener {
    private CarouselLayoutManager layoutManager;
    //已经加载出来的Item的数量
    private int totalItemCount;

    //主要用来存储上一个totalItemCount
    private int previousTotal = 0;

    //是否正在上拉数据
    private boolean loading = true;

    public OnAlbumScroollListener(CarouselLayoutManager la) {
        layoutManager = la;
    }

    @Override

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int centerItemPosition = layoutManager.getCenterItemPosition();

        totalItemCount = layoutManager.getItemCount();
        if (loading) {
            if (totalItemCount > previousTotal) {
                //数据已经加载结束
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && centerItemPosition >= (totalItemCount - 2)) {
            onLoadMore();
            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            onScrolledCenterPosition(layoutManager.getCenterItemPosition());
        }
    }

    /**
     * 提供一个抽象方法，在Activity中监听到这个EndLessOnScrollListener
     * 并且实现这个方法
     */
    public abstract void onLoadMore();

    public abstract void onScrolledCenterPosition(int position);


}
