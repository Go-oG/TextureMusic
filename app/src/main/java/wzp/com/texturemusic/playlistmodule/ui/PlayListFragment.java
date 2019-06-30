package wzp.com.texturemusic.playlistmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.PlaylistApiManager;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.PlayListBean;
import wzp.com.texturemusic.common.bean.PlaylistCoverBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.customui.BaseGridLayoutManager;
import wzp.com.texturemusic.core.customui.BaseRecyclerView;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.playlistmodule.FinePlayListActivity;
import wzp.com.texturemusic.core.customui.OnLoadMoreForGridLayoutManager;
import wzp.com.texturemusic.playlistmodule.PlaylistDetailActivity;
import wzp.com.texturemusic.playlistmodule.adapter.IndexPlaylistAdapter;
import wzp.com.texturemusic.playlistmodule.bean.PlayListHeadBean;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.DataCacheUtil;
import wzp.com.texturemusic.util.NetWorkUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:用于在IndexActivity界面中的歌单列表
 * on 2017/9/17.
 * offset= -1*limit
 */

public class PlayListFragment extends BaseFragment {
    //主动更新间隔 2小时
    private static final long UPDATE_TIME = 2 * 3600000;
    @BindView(R.id.fr_index_recycleview)
    BaseRecyclerView mRecycleview;
    Unbinder unbinder;
    @BindView(R.id.fr_index_swiperefresh)
    SwipeRefreshLayout mSwiperefresh;
    @BindView(R.id.fab_btn)
    FloatingActionButton mFAB;
    private int offset = 0, limit = 40;
    private String type = "全部";//歌单的类别
    private RecyclerView.RecycledViewPool viewPool;
    private int scrollY = 0;
    private long lastUpdateTime = 0;
    ////////////////新增内容
    private IndexPlaylistAdapter mAdapter;
    private BaseGridLayoutManager mGridLayoutManager;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewPool = new RecyclerView.RecycledViewPool();
        initAdapter();
    }

    @SuppressLint("CheckResult")
    private void initAdapter() {
        mAdapter = new IndexPlaylistAdapter(mContext);
        mAdapter.setClickListener(new IndexPlaylistAdapter.ItemClickListener() {
            @Override
            public void click(boolean isHead, int position, String tvContent) {
                if (isHead) {
                    Intent intent = new Intent(mContext, FinePlayListActivity.class);
                    startActivity(intent);
                } else {
                    if (position == 0) {
                        //点击的头部
                        new MaterialDialog.Builder(mContext)
                                .title("选择分类")
                                .theme(Theme.LIGHT)
                                .items(R.array.songlistname)
                                .itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                        type = (String) text;
                                        offset = 0;
                                        mAdapter.setButtonText(type);
                                        mAdapter.notifyItemChanged(1);
                                        loadData(type, offset, limit, false);
                                    }
                                })
                                .show();
                    } else {
                        type = tvContent;
                        offset=0;
                        mAdapter.setButtonText(type);
                        mAdapter.notifyItemChanged(1);
                        loadData(type, offset, limit, false);
                    }
                }
            }
        });
        mAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        Intent intent = new Intent(mContext, PlaylistDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST, mAdapter.getDataList().get(position).getPlaylistId());
                        intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST_IMG, mAdapter.getDataList().get(position).getCoverImgUr());
                        startActivity(intent);
                    }
                });

        mGridLayoutManager = new BaseGridLayoutManager(mContext, 2);
        mGridLayoutManager.setRecycleChildrenOnDetach(true);
        mGridLayoutManager.setExtraLayoutSpace(200);
        mGridLayoutManager.setSlideSpeedRatio(3);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0 || position == 1) {
                    return 2;
                }
                return 1;
            }
        });

    }

    @Override
    public void lazyLoading() {
        long currentTime = System.currentTimeMillis();
        if (!hasLoadData) {
            if (NetWorkUtil.netWorkIsConnection()) {
                lastUpdateTime = currentTime;
                loadCoverDataFromNet();
            } else {
                loadDataFromLocal();
                Toast.makeText(mContext, "当前网络不可用", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (currentTime - lastUpdateTime >= UPDATE_TIME) {
                lastUpdateTime = currentTime;
                loadCoverDataFromNet();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        offset = 0;
        type = "全部";
        unbinder.unbind();
    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_playlist, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecycleview.setFlingScale(1.5);
        mRecycleview.setRecycledViewPool(viewPool);
        mRecycleview.setHasFixedSize(true);
        mRecycleview.setLayoutManager(mGridLayoutManager);
        mRecycleview.setAdapter(mAdapter);
        mRecycleview.addOnScrollListener(new OnLoadMoreForGridLayoutManager(mGridLayoutManager) {
            @Override
            public void loadMore() {
                offset++;
                loadData(type, offset * limit, limit, true);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollY += dy;
                if (dy <= 0) {
                    //往上划
                    mFAB.setVisibility(View.VISIBLE);
                } else {
                    //往下滑
                    if (scrollY > BaseUtil.dp2px(400)) {
                        mFAB.setVisibility(View.GONE);
                    }
                }
            }


        });
        mSwiperefresh.setEnabled(true);
        int mainColor = getMainColor();
        mSwiperefresh.setColorSchemeColors(mainColor);
        mSwiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                type = "全部";
                loadData(type, offset, limit, false);
            }
        });
    }

    /**
     * 当用户第一次进入界面时加载的数据
     * 包括 精品歌单、和全部类型的歌单
     */
    @SuppressLint("CheckResult")
    private void loadCoverDataFromNet() {
        if (mAdapter.getDataList().isEmpty()) {
            showLoadingView(true);
        }
        Observable.zip(loadHeadData(), loadContentData(), new BiFunction<String, String, PlaylistCoverBean>() {
            @Override
            public PlaylistCoverBean apply(String s, String s2) throws Exception {
                PlaylistCoverBean coverBean = new PlaylistCoverBean();
                JSONObject jsonObject = JSON.parseObject(s);
                if (jsonObject.getIntValue("code") == 200) {
                    JSONArray playlists = jsonObject.getJSONArray("playlists");
                    if (playlists.size() > 0) {
                        PlayListHeadBean headBean = new PlayListHeadBean();
                        JSONObject data = playlists.getJSONObject(0);
                        headBean.setPlaylistName(data.getString("name"));
                        headBean.setCoverImgUrl(data.getString("coverImgUrl"));
                        headBean.setPlaylistDesc(data.getString("copywriter"));
                        coverBean.setHeadBean(headBean);
                    }
                }
                coverBean.setListBeans(jsonToEntiy(s2));
                return coverBean;
            }
        }).compose(this.<PlaylistCoverBean>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PlaylistCoverBean>() {
                    @Override
                    public void accept(PlaylistCoverBean playlistCoverBean) throws Exception {
                        showContentView();
                        hasLoadData = true;
                        DataCacheUtil.savePlaylistCoverData(JSONObject.toJSONString(playlistCoverBean));
                        PlayListHeadBean headBean = playlistCoverBean.getHeadBean();
                        if (headBean != null) {
                            mAdapter.setHeadPlaylist(headBean);
                        }
                        List<PlayListBean> listBeans = playlistCoverBean.getListBeans();
                        if (listBeans != null) {
                            mAdapter.addDataList(listBeans);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        loadDataFromLocal();
                    }
                });
    }

    private Observable<String> loadHeadData() {

        return PlaylistApiManager.getHighQualityPlayList(null, 0, 1);
    }

    private Observable<String> loadContentData() {
        return PlaylistApiManager.getDeffrentTypePlayList(type, "hot", offset, limit);
    }

    /**
     * 加载更多多数据
     * hot 热门(一般选择热门)  new 最新
     */
    @SuppressLint("CheckResult")
    private void loadData(String type, int offset, int limit, final boolean isLoadMore) {
        PlaylistApiManager
                .getDeffrentTypePlayList(type, "hot", offset, limit)
                .map(new Function<String, List<PlayListBean>>() {
                    @Override
                    public List<PlayListBean> apply(@NonNull String s) throws Exception {
                        return jsonToEntiy(s);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<PlayListBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<List<PlayListBean>>() {
                    @Override
                    public void accept(List<PlayListBean> listBeen) throws Exception {
                        if (mSwiperefresh != null && mSwiperefresh.isRefreshing()) {
                            mSwiperefresh.setRefreshing(false);
                        }
                        if (isLoadMore) {
                            int startPosition = mAdapter.getDataList().size() + 2;
                            mAdapter.addDataList(listBeen);
                            mAdapter.notifyItemRangeInserted(startPosition, listBeen.size());
                        } else {
                            mAdapter.clearDataList();
                            mAdapter.addDataList(listBeen);
                            mAdapter.notifyDataSetChanged();
                        }
                        showContentView();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mSwiperefresh.isRefreshing()) {
                            mSwiperefresh.setRefreshing(false);
                        }
                        loadDataFromLocal();
                    }
                });
    }

    private List<PlayListBean> jsonToEntiy(String json) {
        List<PlayListBean> list = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(json);
        if (jsonObject.getIntValue("code") == 200) {
            JSONArray playlists = jsonObject.getJSONArray("playlists");
            int size = playlists.size();
            if (size > 0) {
                JSONObject itemData;
                for (int i = 0; i < size; i++) {
                    itemData = playlists.getJSONObject(i);
                    PlayListBean bean = new PlayListBean();
                    bean.setPlaylistName(itemData.getString("name"));
                    bean.setPlaylistId(itemData.getLong("id"));
                    bean.setCoverImgUr(itemData.getString("coverImgUrl"));
                    bean.setCreaterName(itemData.getJSONObject("creator").getString("nickname"));
                    bean.setCreaterId(itemData.getJSONObject("creator").getLong("userId"));
                    bean.setPlayCount(itemData.getInteger("playCount"));
                    list.add(bean);
                }
            }
        }
        return list;
    }

    private void loadDataFromLocal() {
        PlaylistCoverBean coverBean = JSONObject.parseObject(DataCacheUtil.getPlaylistCoverData(), PlaylistCoverBean.class);
        if (coverBean == null) {
            ToastUtil.showNormalMsg("暂无网络");
            showErrorView();
            return;
        }
        PlayListHeadBean headBean = coverBean.getHeadBean();
        if (headBean != null) {
            mAdapter.setHeadPlaylist(headBean);
            mAdapter.notifyItemChanged(0);
        }
        List<PlayListBean> listBeans = coverBean.getListBeans();
        if (listBeans != null) {
            mAdapter.addDataList(listBeans);
            mAdapter.notifyDataSetChanged();
            showContentView();
        } else {
            ToastUtil.showNormalMsg("暂无网络");
            showErrorView();
        }
    }


    @OnClick(R.id.fab_btn)
    public void onViewClicked() {
        if (scrollY != 0) {
            mRecycleview.smoothScrollToPosition(0);
        }
    }

}
