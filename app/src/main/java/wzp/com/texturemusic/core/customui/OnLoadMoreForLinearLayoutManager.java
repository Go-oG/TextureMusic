package wzp.com.texturemusic.core.customui;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by wang on 2017/5/1.
 * RecycleView 加载更多监听实现
 * 实现对于布局管理器为LinearLayoutManager时加载更多的实现
 */

public abstract class OnLoadMoreForLinearLayoutManager extends RecyclerView.OnScrollListener {
    //声明一个LinearLayoutManager
    private LinearLayoutManager mLinearLayoutManager;
    //已经加载出来的Item的数量
    private int totalItemCount;
    //主要用来存储上一个totalItemCount
    private int previousTotal = 0;
    //在屏幕上可见的item数量
    private int visibleItemCount;
    //在屏幕可见的Item中的第一个
    private int firstVisibleItem;
    //是否正在上拉数据
    private boolean loading = true;



    public OnLoadMoreForLinearLayoutManager(LinearLayoutManager layoutManager) {
        mLinearLayoutManager = layoutManager;
    }

    public OnLoadMoreForLinearLayoutManager() {
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (mLinearLayoutManager == null) {
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                mLinearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            } else {
                throw new NullPointerException("mLinearLayoutManager 不能为空");
            }
        }
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        if (loading) {
            if (totalItemCount > previousTotal) {
                //数据已经加载结束
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        //这里需要好好理解
        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem) {
            onLoadMore();
            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        switch (newState) {
            //当屏幕滚动且用户使用的触碰或手指还在屏幕上，停止加载图片
            case RecyclerView.SCROLL_STATE_DRAGGING:
                //由于用户的操作，屏幕产生惯性滑动，停止加载图片
            case RecyclerView.SCROLL_STATE_SETTLING:
                onGlidePause();
                break;
            case RecyclerView.SCROLL_STATE_IDLE:
                onGlideResume();
                break;
        }
    }



    /**
     * 提供一个抽象方法，在Activity中监听到这个EndLessOnScrollListener
     * 并且实现这个方法
     */
    public void onLoadMore() {
    }

    public void onGlidePause() {
    }

    public void onGlideResume() {
    }


}
