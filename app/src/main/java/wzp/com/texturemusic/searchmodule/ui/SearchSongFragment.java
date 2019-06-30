package wzp.com.texturemusic.searchmodule.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.WYApiUtil;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.core.customui.OnLoadMoreForLinearLayoutManager;
import wzp.com.texturemusic.searchmodule.SearchActivity;
import wzp.com.texturemusic.searchmodule.adapter.SearchSingleSongAdapter;
import wzp.com.texturemusic.searchmodule.listener.OnSearchListener;
import wzp.com.texturemusic.util.KeyBoardUtil;
import wzp.com.texturemusic.util.SPSetingUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/9/17.
 */

public class SearchSongFragment extends BaseFragment implements OnSearchListener {
    @BindView(R.id.fr_search_recycleview)
    RecyclerView mRecycleview;
    @BindView(R.id.fr_search_refresh)
    SwipeRefreshLayout mRefreshLayout;
    Unbinder unbinder;
    private String lastSearchStr = "";//标识上一次搜索的数据
    private SearchSingleSongAdapter adapter;
    private int limit = 20, offset = 0;
    private boolean lazyLoading = false;
    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SearchSingleSongAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        MusicBean bean = adapter.getDataList().get(itemBean.getPosition());
                        View view = itemBean.getView();
                        if (view.getId() == R.id.item_song_operation) {
                            getMusicDetail(bean.getMusicId());
                        } else {
                            ((SearchActivity) getActivity()).playMusic(bean);
                        }
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_search, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                loadSearchDataWithRefreshUI(lastSearchStr, offset, limit, true);
            }
        });
        mRecycleview.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycleview.setLayoutManager(layoutManager);
        mRecycleview.addOnScrollListener(new OnLoadMoreForLinearLayoutManager(layoutManager) {
            @Override
            public void onLoadMore() {
                offset++;
                loadSearchDataWithRefreshUI(lastSearchStr, offset * limit, limit, false);
            }
        });
        SlideInUpAnimator animator = new SlideInUpAnimator();
        mRecycleview.setItemAnimator(animator);
        mRecycleview.getItemAnimator().setAddDuration(600);
        mRecycleview.getItemAnimator().setChangeDuration(600);
    }

    @Override
    public void lazyLoading() {
        if (!lazyLoading) {
            //已经加载过同样的数据
            lazyLoading = true;
            offset = 0;
            loadSearchDataWithRefreshUI(lastSearchStr, offset, limit, true);
        }
    }

    /**
     * 准备开始搜索数据
     */
    @Override
    public void onSearch(String s) {
        if (TextUtils.isEmpty(s)) {
            //空数据
            lastSearchStr = "";
        } else {
            if (!s.equals(lastSearchStr)) {
                //搜索数据 开始搜索
                lastSearchStr = s;
                lazyLoading = false;
                if (getUserVisibleHint() && isInitView) {
                    loadSearchDataWithRefreshUI(s, offset, limit, true);
                }
            }
        }
    }

    /**
     * 加载数据并刷新
     * 加载数据的类型 直接加载 或者 加载更多
     */
    @SuppressLint("CheckResult")
    private void loadSearchDataWithRefreshUI(String searchStr, int offset, int limit, final boolean isRefresh) {
        WYApiUtil.getInstance().buildSearchService()
                .getSearchData(searchStr, 1, offset, true, limit)
                .map(new Function<String, List<MusicBean>>() {
                    @Override
                    public List<MusicBean> apply(@NonNull String s) throws Exception {
                        return jsonToMusicBean(s);
                    }
                }).compose(this.<List<MusicBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MusicBean>>() {
                    @Override
                    public void accept(List<MusicBean> list) throws Exception {
                        if (mRefreshLayout.isRefreshing()) {
                            mRefreshLayout.setRefreshing(false);
                        }
                        if (isRefresh) {
                            int size = adapter.getDataList().size();
                            if (size == 0) {
                                adapter.addDataList(list);
                                adapter.notifyItemRangeInserted(0, list.size());
                            } else {
                                adapter.clearDataList();
                                adapter.addDataList(list);
                                adapter.notifyItemRangeRemoved(0, size);
                            }
                        } else {
                            int size = adapter.getDataList().size();
                            adapter.addDataList(list);
                            adapter.notifyItemRangeInserted(size, list.size());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showNormalMsg("出错啦");
                    }
                });
    }

    private List<MusicBean> jsonToMusicBean(String json) {
        int bit = SPSetingUtil.getIntValue(AppConstant.SP_KEY_PLAY_QUALITY, AppConstant.MUSIC_BITRATE_NORMAL);
        List<MusicBean> list = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(json);
        if (jsonObject.getIntValue("code") == 200) {
            JSONObject result = jsonObject.getJSONObject("result");
            JSONArray songs = result.getJSONArray("songs");
            if (songs != null && songs.size() > 0) {
                int size = songs.size();
                JSONObject itemData;
                JSONArray artistJson;
                JSONObject albumJson;
                Long mvId;
                for (int i = 0; i < size; i++) {
                    itemData = songs.getJSONObject(i);
                    MusicBean bean = new MusicBean();
                    bean.setMusicBitrate(bit);
                    bean.setLocalMusic(false);
                    bean.setMusicId(String.valueOf(itemData.getLongValue("id")));
                    bean.setMusicName(itemData.getString("name"));
                    bean.setAllTime(itemData.getLongValue("duration"));
                    mvId = itemData.getLong("mvid");
                    if (mvId != 0L) {
                        bean.setHasMV(true);
                        MvBean mvBean = new MvBean();
                        mvBean.setMvId(mvId);
                    } else {
                        bean.setHasMV(false);
                    }
                    artistJson = itemData.getJSONArray("artists");
                    if (artistJson != null && artistJson.size() > 0) {
                        bean.setArtistName(artistJson.getJSONObject(0).getString("name"));
                        bean.setArtistId(String.valueOf(artistJson.getJSONObject(0).getLongValue("id")));
                        bean.setCoverImgUrl(artistJson.getJSONObject(0).getString("img1v1Url"));
                    }
                    albumJson = itemData.getJSONObject("album");
                    if (albumJson != null) {
                        bean.setAlbumId(String.valueOf(albumJson.getLongValue("id")));
                        bean.setAlbumName(albumJson.getString("name"));
                    }
                    list.add(bean);
                }
            }

        }
        return list;
    }


    private void getMusicDetail(String musicId) {
        WYApiUtil.getInstance().buildSongService()
                .getSongDetatil(musicId, "[" + musicId + "]")
                .map(new Function<String, MusicBean>() {
                    @Override
                    public MusicBean apply(String s) throws Exception {
                        MusicBean bean = new MusicBean();
                        int bit = SPSetingUtil.getIntValue(AppConstant.SP_KEY_PLAY_QUALITY, AppConstant.MUSIC_BITRATE_NORMAL);
                        JSONObject rootObject = JSON.parseObject(s);
                        if (rootObject.getIntValue("code") == 200) {
                            JSONArray jsonArray = rootObject.getJSONArray("songs");
                            if (jsonArray != null && jsonArray.size() > 0) {
                                JSONObject itemObject = jsonArray.getJSONObject(0);
                                bean.setMusicName(itemObject.getString("name"));
                                bean.setMusicId(String.valueOf(itemObject.getLongValue("id")));
                                JSONArray artistArray = itemObject.getJSONArray("artists");
                                if (artistArray != null && artistArray.size() > 0) {
                                    bean.setArtistImgUrl(artistArray.getJSONObject(0).getString("picUrl"));
                                    bean.setArtistName(artistArray.getJSONObject(0).getString("name"));
                                    bean.setArtistId(String.valueOf(artistArray.getJSONObject(0).getLongValue("id")));
                                }
                                JSONObject albumObject = itemObject.getJSONObject("album");
                                if (albumObject != null) {
                                    bean.setAlbumImgUrl(albumObject.getString("picUrl"));
                                    bean.setAlbumId(String.valueOf(albumObject.getLongValue("id")));
                                    bean.setAlbumName(albumObject.getString("name"));
                                }
                                bean.setCoverImgUrl(bean.getAlbumImgUrl());
                                bean.setLocalMusic(false);
                                bean.setAllTime(itemObject.getLongValue("duration"));
                                bean.setCommentId(itemObject.getString("commentThreadId"));
                                long mvId = itemObject.getLongValue("mvId");
                                if (mvId == 0) {
                                    bean.setHasMV(false);
                                } else {
                                    bean.setHasMV(true);
                                    MvBean mvBean = new MvBean();
                                    mvBean.setMvId(mvId);
                                }
                                bean.setMusicBitrate(bit);
                            }
                        }
                        return bean;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MusicBean>() {
                    @Override
                    public void accept(MusicBean bean) throws Exception {
                        KeyBoardUtil.hideSoftInput(getActivity());
                        ((SearchActivity) getActivity()).showMusicInfoPop(bean, null);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showNormalMsg(throwable.getMessage());
                    }
                });


    }

}
