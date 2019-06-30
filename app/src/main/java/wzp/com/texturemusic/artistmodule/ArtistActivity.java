package wzp.com.texturemusic.artistmodule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.ArtistApiManager;
import wzp.com.texturemusic.artistmodule.adapter.ArtistAdapter;
import wzp.com.texturemusic.artistmodule.ui.ArtistDetailActivity;
import wzp.com.texturemusic.artistmodule.ui.ArtistRankActivity;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.customui.BaseGridLayoutManager;
import wzp.com.texturemusic.core.customui.OnLoadMoreForGridLayoutManager;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.customview.RecycleItemStytle;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description: 热门歌手Activity
 * on 2017/9/16.
 */
public class ArtistActivity extends BaseActivityWrapper {
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;
    @BindView(R.id.ac_artist_recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.m_swiperefresh)
    SwipeRefreshLayout mSwiperefresh;
    @BindView(R.id.toolbar_right_img1)
    ImageView toolbarRightImg;
    @BindView(R.id.common_toolabr)
    RelativeLayout commonToolabr;
    private ArtistAdapter adapter;
    private int offset = 0;
    private int limit = 20;
    private boolean hasMoreData = true;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        ButterKnife.bind(this);
        toolbarTitleTv.setText("热门歌手");
        toolbarRightImg.setVisibility(View.VISIBLE);
        toolbarRightImg.setImageResource(R.drawable.ic_artist_rank);
        adapter = new ArtistAdapter(mContext);
        adapter.getItemClickSubject()
                .compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(itemBean -> {
                    Intent intent = new Intent(mContext, ArtistDetailActivity.class);
                    ArtistBean bean = adapter.getDataList().get(itemBean.getPosition());
                    intent.putExtra(AppConstant.UI_INTENT_KEY_ARTIST, bean);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(ArtistActivity.this,
                            itemBean.getView().findViewById(R.id.item_img), getString(R.string.shareartistdetail));
                    startActivity(intent, optionsCompat.toBundle());
                });
        mSwiperefresh.setOnRefreshListener(() -> {
            offset = 0;
            loadData(offset, limit, false);
        });
        BaseGridLayoutManager layoutManager;
        layoutManager = new BaseGridLayoutManager(mContext, 2);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setAdapter(adapter);
        mRecycleView.addOnScrollListener(new OnLoadMoreForGridLayoutManager(layoutManager) {
            @Override
            public void loadMore() {
                super.loadMore();
                if (hasMoreData) {
                    offset++;
                    loadData(offset * limit, limit, true);
                } else {
                    ToastUtil.showNormalMsg("没有更多数据了");
                }
            }
        });

        SlideInUpAnimator upAnimator = new SlideInUpAnimator();
        mRecycleView.setItemAnimator(upAnimator);
        mRecycleView.addItemDecoration(new RecycleItemStytle(mContext, GridLayoutManager.VERTICAL, BaseUtil.dp2px(4),
                getResources().getColor(R.color.color_driver)));
        loadData(offset, limit, false);
    }

    @SuppressLint("CheckResult")
    private void loadData(int offset, int limit, final boolean isLoadMore) {
        ArtistApiManager.getHotArtists(offset, limit)
                .map(s -> jsonToEntiy(s)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(listBeen -> {
                    if (mSwiperefresh.isRefreshing()) {
                        mSwiperefresh.setRefreshing(false);
                    }
                    hasLoadData = true;
                    int size = adapter.getDataList().size();
                    if (!isLoadMore) {
                        if (size == 0) {
                            adapter.addDataList(listBeen);
                            adapter.notifyItemRangeInserted(0, listBeen.size());
                        } else {
                            adapter.clearDataList();
                            adapter.addDataList(listBeen);
                            adapter.notifyItemRangeRemoved(0, size);
                        }
                    } else {
                        adapter.addDataList(listBeen);
                        adapter.notifyItemRangeInserted(size, listBeen.size());
                    }
                }, throwable -> {
                    if (mSwiperefresh.isRefreshing()) {
                        mSwiperefresh.setRefreshing(false);
                    }
                });

    }

    private List<ArtistBean> jsonToEntiy(String json) {
        List<ArtistBean> list = new ArrayList<>();
        JSONObject rootObject = JSON.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            hasMoreData = rootObject.getBoolean("more");
            JSONArray artistArray = rootObject.getJSONArray("artists");
            int size = artistArray.size();
            if (size > 0) {
                JSONObject itemObject;
                for (int i = 0; i < size; i++) {
                    itemObject = artistArray.getJSONObject(i);
                    ArtistBean bean = new ArtistBean();
                    String name = itemObject.getString("name");
                    JSONArray array = itemObject.getJSONArray("alias");
                    if (array != null && array.size() > 0) {
                        name = name + "  别名：" + array.getString(0);
                    }
                    bean.setArtistName(name);
                    String info = "专辑：" + itemObject.getIntValue("albumSize") + "  音乐：" + itemObject.getIntValue("musicSize");
                    bean.setDecriptions(info);
                    bean.setArtistId(String.valueOf(itemObject.getLongValue("id")));
                    bean.setArtistImgUrl(itemObject.getString("picUrl"));
                    bean.setLocalArtist(false);
                    list.add(bean);
                }
            }
        }
        return list;
    }


    @Override
    protected void onNetworkChange(boolean isConnected, boolean isVpn) {
        super.onNetworkChange(isConnected, isVpn);
        if (isConnected && !hasLoadData) {
            loadData(offset, limit, false);
        }
    }

    @OnClick({R.id.toolar_return_img, R.id.toolbar_right_img1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolar_return_img:
                finish();
                break;
            case R.id.toolbar_right_img1:
                //歌手排行榜
                Intent intent = new Intent(mContext, ArtistRankActivity.class);
                startActivity(intent);
                break;
        }
    }
}
