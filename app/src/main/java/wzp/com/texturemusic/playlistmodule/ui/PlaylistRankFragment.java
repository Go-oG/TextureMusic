package wzp.com.texturemusic.playlistmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.PlaylistApiManager;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.common.adapter.PaddingViewAdapater;
import wzp.com.texturemusic.common.adapter.RecomnentSingleAdapater;
import wzp.com.texturemusic.common.bean.RankCoverBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.playlistmodule.PlaylistDetailActivity;
import wzp.com.texturemusic.playlistmodule.adapter.RankingFragGFAdapater;
import wzp.com.texturemusic.playlistmodule.adapter.RankingFragQQAdapter;
import wzp.com.texturemusic.playlistmodule.bean.PlaylistRankBean;
import wzp.com.texturemusic.util.DataCacheUtil;
import wzp.com.texturemusic.util.ToastUtil;


/**
 * Created by Go_oG
 * Description: 排行榜
 * on 2017/9/17.
 */

public class PlaylistRankFragment extends BaseFragment {
    private static final String MAP_QQ = "map_key_QQ";
    private static final String MAP_GF = "map_key_gf";
    //主动更新间隔 3小时 (单位毫秒)
    private static final long UPDATE_TIME = 3 * 3600000;
    @BindView(R.id.fr_index_swiperefresh)
    SwipeRefreshLayout mSwiperefresh;
    @BindView(R.id.fr_index_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private RecyclerView.RecycledViewPool mViewPool;
    private VirtualLayoutManager layoutManager;
    private DelegateAdapter delegateAdapter;
    private RankingFragGFAdapater gfAdapater;
    private RankingFragQQAdapter qqAdapter;
    //上次更新时间戳
    private long lastUpdateTime = 0;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutManager = new VirtualLayoutManager(mContext);
        List<DelegateAdapter.Adapter> adapterList = new ArrayList<>();
        gfAdapater = new RankingFragGFAdapater(mContext);
        qqAdapter = new RankingFragQQAdapter(mContext);
        PaddingViewAdapater paddingViewAdapater = new PaddingViewAdapater(mContext);
        RecomnentSingleAdapater gfSingle = new RecomnentSingleAdapater(mContext, "官方榜");
        RecomnentSingleAdapater qqSingle = new RecomnentSingleAdapater(mContext, "全球榜");
        gfSingle.showRightImg(false);
        qqSingle.showRightImg(false);
        gfAdapater.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        int position = itemBean.getPosition();
                        Intent intent = new Intent(mContext, PlaylistDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST, gfAdapater.getDataList().get(position).getPlaylistId());
                        intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST_IMG, gfAdapater.getDataList().get(position).getCoverImgUrl());
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view.findViewById(R.id.fr_ranking_gf_rec_img), getString(R.string.shareplaylistdetail));
                        //startActivity(intent, optionsCompat.toBundle());
                        startActivity(intent);
                    }
                });
        qqAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        int position = itemBean.getPosition();
                        Intent intent = new Intent(mContext, PlaylistDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST, qqAdapter.getDataList().get(position).getPlaylistId());
                        intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST_IMG, qqAdapter.getDataList().get(position).getCoverImgUrl());
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view, getString(R.string.shareplaylistdetail));
                        //  startActivity(intent, optionsCompat.toBundle());
                        startActivity(intent);
                    }
                });
        adapterList.add(gfSingle);
        adapterList.add(gfAdapater);
        adapterList.add(qqSingle);
        adapterList.add(qqAdapter);
        adapterList.add(paddingViewAdapater);
        delegateAdapter = new DelegateAdapter(layoutManager);
        delegateAdapter.setAdapters(adapterList);
        mViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_index, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecycleview.setItemViewCacheSize(25);
        mRecycleview.setDrawingCacheEnabled(true);
        mRecycleview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecycleview.setAdapter(delegateAdapter);
        mRecycleview.setLayoutManager(layoutManager);
        mRecycleview.setRecycledViewPool(mViewPool);
        mSwiperefresh.setEnabled(false);
    }

    @Override
    public void lazyLoading() {
        long currentTime = System.currentTimeMillis();
        if (!hasLoadData) {
            lastUpdateTime = currentTime;
            loadRankDataFromNet();
        } else {
            if (currentTime - lastUpdateTime >= UPDATE_TIME) {
                lastUpdateTime = currentTime;
                loadRankDataFromNet();
            }
        }
    }

    @SuppressLint("CheckResult")
    private void loadRankDataFromNet() {
        if (gfAdapater.getDataList().isEmpty()) {
            showLoadingView(true);
        }
        PlaylistApiManager
                .getPlaylistRank()
                .map(new Function<String, List<PlaylistRankBean>>() {
                    @Override
                    public List<PlaylistRankBean> apply(String s) throws Exception {
                        return jsonToEntiy(s);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<PlaylistRankBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<List<PlaylistRankBean>>() {
                    @Override
                    public void accept(List<PlaylistRankBean> list) throws Exception {
                        if (!list.isEmpty() && list.size() > 4) {
                            hasLoadData = true;
                            List<PlaylistRankBean> gfList = list.subList(0, 4);
                            List<PlaylistRankBean> qqList = list.subList(4, list.size() - 1);
                            Map<String, List<PlaylistRankBean>> map = new HashMap<>();
                            map.put(MAP_GF, gfList);
                            map.put(MAP_QQ, qqList);
                            RankCoverBean coverBean = new RankCoverBean();
                            coverBean.setMap(map);
                            DataCacheUtil.saveRankCoverData(JSONObject.toJSONString(coverBean));
                            gfAdapater.clearDataList();
                            gfAdapater.addDataList(gfList);
                            gfAdapater.notifyDataSetChanged();
                            qqAdapter.clearDataList();
                            qqAdapter.addDataList(qqList);
                            qqAdapter.notifyDataSetChanged();
                        }
                        if (mSwiperefresh.isRefreshing()) {
                            mSwiperefresh.setRefreshing(false);
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

    private void loadDataFromLocal() {
        RankCoverBean coverBean = JSONObject.parseObject(DataCacheUtil.getRankCoverData(), RankCoverBean.class);
        if (coverBean != null && coverBean.getMap() != null) {
            Map<String, List<PlaylistRankBean>> map = coverBean.getMap();
            List<PlaylistRankBean> gfList = map.get(MAP_GF);
            List<PlaylistRankBean> qqList = map.get(MAP_QQ);
            gfAdapater.clearDataList();
            gfAdapater.addDataList(gfList);
            gfAdapater.notifyDataSetChanged();
            qqAdapter.clearDataList();
            qqAdapter.addDataList(qqList);
            qqAdapter.notifyDataSetChanged();
            showContentView();
        } else {
            ToastUtil.showNormalMsg("暂无网络");
            showErrorView();
        }
    }

    private List<PlaylistRankBean> jsonToEntiy(String json) {
        List<PlaylistRankBean> list = new ArrayList<>();
        JSONObject rootObject = JSONObject.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            JSONArray listArray = rootObject.getJSONArray("list");
            int size = listArray.size();
            if (size > 0) {
                JSONObject itemObject;
                JSONArray trackArraay;
                for (int i = 0; i < size; i++) {
                    itemObject = listArray.getJSONObject(i);
                    PlaylistRankBean bean = new PlaylistRankBean();
                    bean.setPlaylistId(itemObject.getLongValue("id"));
                    bean.setTips(itemObject.getString("updateFrequency"));
                    bean.setPlaylistName(itemObject.getString("name"));
                    bean.setCoverImgUrl(itemObject.getString("coverImgUrl"));
                    trackArraay = itemObject.getJSONArray("tracks");
                    int tsize = trackArraay.size();
                    List<MusicBean> musicBeanList = new ArrayList<>();
                    for (int j = 0; j < tsize; j++) {
                        JSONObject item = trackArraay.getJSONObject(j);
                        MusicBean musicBean = new MusicBean();
                        musicBean.setMusicName(item.getString("first"));
                        musicBean.setArtistName(item.getString("second"));
                        musicBeanList.add(musicBean);
                    }
                    bean.setMusicList(musicBeanList);
                    list.add(bean);
                }
            }
        }
        return list;
    }

}
