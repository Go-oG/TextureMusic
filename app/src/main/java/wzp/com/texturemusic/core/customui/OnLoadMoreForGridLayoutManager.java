package wzp.com.texturemusic.core.customui;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author:Go_oG
 * date: on 2018/5/10
 * packageName: wzp.com.texturemusic.playlistmodule
 * 实现针对布局管理器为GridLayoutManager时加载更多的实现
 */
public abstract class OnLoadMoreForGridLayoutManager extends RecyclerView.OnScrollListener {
    private GridLayoutManager layoutManager;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    public OnLoadMoreForGridLayoutManager(GridLayoutManager manager) {
        super();
        layoutManager = manager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy > 0) {
            visibleItemCount = layoutManager.getChildCount();
            totalItemCount = layoutManager.getItemCount();
            pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                loadMore();
            }
        }
    }

    public void loadMore() {
    }

    ;
}
