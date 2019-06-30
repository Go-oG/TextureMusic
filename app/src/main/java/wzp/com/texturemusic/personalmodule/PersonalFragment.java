package wzp.com.texturemusic.personalmodule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.fastjson.JSON;
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
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function6;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.albummodule.NetAlbumDetailActivity;
import wzp.com.texturemusic.albummodule.ui.NewAlbumActivity;
import wzp.com.texturemusic.api.AlbumApiManager;
import wzp.com.texturemusic.api.ArtistApiManager;
import wzp.com.texturemusic.api.DJApiManager;
import wzp.com.texturemusic.api.MvApiManager;
import wzp.com.texturemusic.api.PlaylistApiManager;
import wzp.com.texturemusic.api.WYApiUtil;
import wzp.com.texturemusic.artistmodule.ArtistActivity;
import wzp.com.texturemusic.artistmodule.ui.ArtistDetailActivity;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.common.adapter.RecomnentSingleAdapater;
import wzp.com.texturemusic.common.bean.PersonalCoverBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.indexmodule.IndexActivity;
import wzp.com.texturemusic.interf.OnRecycleItemClickListener;
import wzp.com.texturemusic.mvmodule.MvActivity;
import wzp.com.texturemusic.mvmodule.MvDetailActivity;
import wzp.com.texturemusic.personalmodule.adapter.IndexPersonAlbumAdapter;
import wzp.com.texturemusic.personalmodule.adapter.IndexPersonArtistAdapter;
import wzp.com.texturemusic.personalmodule.adapter.IndexPersonDjAdapter;
import wzp.com.texturemusic.personalmodule.adapter.IndexPersonMvAdapter;
import wzp.com.texturemusic.personalmodule.adapter.IndexPersonNewSongAdapter;
import wzp.com.texturemusic.personalmodule.adapter.IndexPersonPlaylistAdapter;
import wzp.com.texturemusic.personalmodule.bean.IndexPersonEntiy;
import wzp.com.texturemusic.playlistmodule.PlaylistDetailActivity;
import wzp.com.texturemusic.songmodule.NewestMusicActivity;
import wzp.com.texturemusic.util.DataCacheUtil;
import wzp.com.texturemusic.util.NetWorkUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:个性推荐
 * on 2017/9/17.
 */
public class PersonalFragment extends BaseFragment {
    private static final String DATA_TYPE_PLAYLIST = "playlist";
    private static final String DATA_TYPE_NEWSONGS = "newsongs";
    private static final String DATA_TYPE_MV = "mvdata";
    private static final String DATA_TYPE_DJ = "djdata";
    private static final String DATA_TYPE_ALBUM = "albumdata";
    private static final String DATA_TYPE_ARTIST = "artistdata";
    //主动更新间隔 2小时
    private static final long UPDATE_TIME = 2 * 60 * 60 * 1000;
    @BindView(R.id.fr_index_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    @BindView(R.id.fr_index_swiperefresh)
    SwipeRefreshLayout mSwiperefresh;
    private RecyclerView.RecycledViewPool viewPool;
    private IndexPersonPlaylistAdapter playlistAdapter;
    private IndexPersonNewSongAdapter newSongAdapter;
    private IndexPersonMvAdapter mvAdapter;
    private IndexPersonDjAdapter djAdapter;
    private IndexPersonArtistAdapter artistAdapter;
    private IndexPersonAlbumAdapter albumAdapter;
    private VirtualLayoutManager layoutManager;
    private DelegateAdapter adapter;
    private long lastUpdateTime = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAdapter();
    }

    @SuppressLint("CheckResult")
    private void initAdapter() {
        final List<DelegateAdapter.Adapter> adapterList = new ArrayList<>();
        layoutManager = new VirtualLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        layoutManager.setRecycleChildrenOnDetach(true);
        adapter = new DelegateAdapter(layoutManager);

        RecomnentSingleAdapater tjPlaylist = new RecomnentSingleAdapater(mContext, "推荐歌单");
        tjPlaylist.setClickListener(new OnRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ((IndexActivity) getActivity()).mViewpager.setCurrentItem(1);
            }
        });

        RecomnentSingleAdapater zxMusic = new RecomnentSingleAdapater(mContext, "最新音乐");
        zxMusic.setClickListener(new OnRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, NewestMusicActivity.class);
                startActivity(intent);
            }
        });

        RecomnentSingleAdapater tjMv = new RecomnentSingleAdapater(mContext, "推荐mv");
        tjMv.setClickListener(new OnRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, MvActivity.class);
                startActivity(intent);
            }
        });

        RecomnentSingleAdapater zbDj = new RecomnentSingleAdapater(mContext, "主播电台");
        zbDj.setClickListener(new OnRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ((IndexActivity) getActivity()).mViewpager.setCurrentItem(2);
            }
        });

        RecomnentSingleAdapater tjAlbum = new RecomnentSingleAdapater(mContext, "推荐专辑");
        tjAlbum.setClickListener(new OnRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, NewAlbumActivity.class);
                startActivity(intent);
            }
        });

        RecomnentSingleAdapater tjArtist = new RecomnentSingleAdapater(mContext, "热门歌手");
        tjArtist.setClickListener(new OnRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, ArtistActivity.class);
                startActivity(intent);
            }
        });

        playlistAdapter = new IndexPersonPlaylistAdapter(mContext);
        playlistAdapter.getItemClickSubject()
                .compose(this.<ItemBean>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        Intent intent = new Intent(mContext, PlaylistDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST, playlistAdapter.getDataList().get(position).getId());
                        intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST_IMG, playlistAdapter.getDataList().get(position).getCoverImgUrl());
                        startActivity(intent);
                    }
                });

        newSongAdapter = new IndexPersonNewSongAdapter(mContext);

        newSongAdapter.getItemClickSubject()
                .compose(this.<ItemBean>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        IndexPersonEntiy personEntiy = newSongAdapter.getDataList().get(position);
                        MusicBean bean = new MusicBean();
                        bean.setMusicId(String.valueOf(personEntiy.getId()));
                        bean.setCoverImgUrl(personEntiy.getCoverImgUrl());
                        bean.setMusicName(personEntiy.getName());
                        bean.setArtistName(personEntiy.getSubName());
                        bean.setLocalMusic(false);
                        bean.setMusicBitrate(AppConstant.MUSIC_BITRATE_NORMAL);
                        bean.setAlbumName(personEntiy.getVal());
                        bean.setAlbumId(personEntiy.getVal2());
                        if (!TextUtils.isEmpty(personEntiy.getVal3())) {
                            bean.setAllTime(Long.parseLong(personEntiy.getVal3()));
                        } else {
                            bean.setAllTime(Long.valueOf(0));
                        }
                        bean.setArtistId(personEntiy.getVal4());
                        long mvid = Long.parseLong(personEntiy.getVal5());
                        if (mvid != 0) {
                            bean.setHasMV(true);
                            MvBean mvBean = new MvBean();
                            mvBean.setMvId(mvid);
                            bean.setMvBean(mvBean);
                        } else {
                            bean.setHasMV(false);
                        }
                        ((IndexActivity) getActivity()).playMusic(bean);
                    }
                });

        mvAdapter = new IndexPersonMvAdapter(mContext);

        mvAdapter.getItemClickSubject()
                .compose(this.<ItemBean>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        Intent intent = new Intent(mContext, MvDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_MV, mvAdapter.getDataList().get(position).getId());
                        startActivity(intent);
                    }
                });

        djAdapter = new IndexPersonDjAdapter(mContext);

        djAdapter.getItemClickSubject()
                .compose(this.<ItemBean>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        MusicBean bean = djAdapter.getDataList().get(position).getMusicBean();
                        if (bean != null) {
                            playMusic(bean);
                        } else {
                            toast("暂无法播放该歌曲");
                        }
                    }
                });
        albumAdapter = new IndexPersonAlbumAdapter(mContext);

        albumAdapter.getItemClickSubject()
                .compose(this.<ItemBean>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        View view = itemBean.getView();
                        Intent intent = new Intent(mContext, NetAlbumDetailActivity.class);
                        AlbumBean bean = new AlbumBean();
                        bean.setAlbumId(String.valueOf(albumAdapter.getDataList().get(position).getId()));
                        bean.setLocalAlbum(false);
                        bean.setAlbumName(albumAdapter.getDataList().get(position).getName());
                        bean.setAlbumImgUrl(albumAdapter.getDataList().get(position).getCoverImgUrl());
                        intent.putExtra(AppConstant.UI_INTENT_KEY_ALBUM, bean);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view.findViewById(R.id.comment_index_img), getString(R.string.sharealbumdetail));
                        startActivity(intent, optionsCompat.toBundle());
                    }
                });

        artistAdapter = new IndexPersonArtistAdapter(mContext);

        artistAdapter.getItemClickSubject()
                .compose(this.<ItemBean>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        View view = itemBean.getView();
                        Intent intent = new Intent(mContext, ArtistDetailActivity.class);
                        ArtistBean bean = new ArtistBean();
                        bean.setLocalArtist(false);
                        bean.setArtistId(String.valueOf(artistAdapter.getDataList().get(position).getId()));
                        bean.setArtistName(artistAdapter.getDataList().get(position).getName());
                        bean.setArtistImgUrl(artistAdapter.getDataList().get(position).getCoverImgUrl());
                        intent.putExtra(AppConstant.UI_INTENT_KEY_ARTIST, bean);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view.findViewById(R.id.comment_index_img), getString(R.string.shareartistdetail));
                        startActivity(intent, optionsCompat.toBundle());
                    }
                });
        adapterList.add(tjPlaylist);
        adapterList.add(playlistAdapter);

        adapterList.add(zxMusic);
        adapterList.add(newSongAdapter);

        adapterList.add(tjAlbum);
        adapterList.add(albumAdapter);

        adapterList.add(tjMv);
        adapterList.add(mvAdapter);

        adapterList.add(zbDj);
        adapterList.add(djAdapter);

        adapterList.add(tjArtist);
        adapterList.add(artistAdapter);
        adapter.setAdapters(adapterList);
        viewPool = new RecyclerView.RecycledViewPool();
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
        mRecycleview.setItemViewCacheSize(30);
        mRecycleview.setDrawingCacheEnabled(true);
        mRecycleview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecycleview.setLayoutManager(layoutManager);
        mRecycleview.setAdapter(adapter);
        mRecycleview.setRecycledViewPool(viewPool);
        mSwiperefresh.setEnabled(false);
        mSwiperefresh.setColorSchemeColors(getMainColor());
        mSwiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(true);
            }
        });
    }


    @Override
    public void lazyLoading() {
        long currentTime = System.currentTimeMillis();
        if (!hasLoadData) {
            lastUpdateTime = currentTime;
            loadData(false);
        } else {
            if (currentTime - lastUpdateTime >= UPDATE_TIME) {
                lastUpdateTime = currentTime;
                loadData(false);
            }
        }
    }

    private void loadData(boolean isSwipe) {
        if (NetWorkUtil.netWorkIsConnection()) {
            loadDataFromNet();
        } else {
            if (mSwiperefresh.isRefreshing()) {
                mSwiperefresh.setRefreshing(false);
            }
            loadDataFromLocal();
        }
    }

    @SuppressLint("CheckResult")
    private void loadDataFromNet() {
        if (albumAdapter.getDataList().isEmpty()) {
            mRecycleview.setVisibility(View.GONE);
            showLoadingView(false);
        }
        Observable.zip(
                PlaylistApiManager.getRecommentSongList(),
                WYApiUtil.getInstance().buildSongService().getRecommentNewSongs(),
                MvApiManager.getRecommentMv(),
                DJApiManager.getRecommentDjprogram(),
                AlbumApiManager.getRecommentNewAlbum(),
                ArtistApiManager.getHotArtists(0, 6),
                new Function6<String, String, String, String, String, String, Map<String, List<IndexPersonEntiy>>>() {
                    @Override
                    public Map<String, List<IndexPersonEntiy>> apply(@NonNull String playlistjson, @NonNull String songJson,
                                                                     @NonNull String mvJson, @NonNull String djJson,
                                                                     @NonNull String albumJson, @NonNull String artistJson) throws Exception {
                        Map<String, List<IndexPersonEntiy>> listMap = new HashMap<>();
                        listMap.put(DATA_TYPE_PLAYLIST, jsonToPlayList(playlistjson));
                        listMap.put(DATA_TYPE_NEWSONGS, jsonToNewSongs(songJson));
                        listMap.put(DATA_TYPE_MV, jsonToMv(mvJson));
                        listMap.put(DATA_TYPE_DJ, jsonToDj(djJson));
                        listMap.put(DATA_TYPE_ALBUM, jsonToAlbum(albumJson));
                        listMap.put(DATA_TYPE_ARTIST, jsonToArtist(artistJson));
                        return listMap;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Map<String, List<IndexPersonEntiy>>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<Map<String, List<IndexPersonEntiy>>>() {
                    @Override
                    public void accept(Map<String, List<IndexPersonEntiy>> listMap) throws Exception {
                        showContentView();
                        PersonalCoverBean bean = new PersonalCoverBean();
                        bean.setMap(listMap);
                        DataCacheUtil.savePersonalCoverData(JSON.toJSONString(bean));
                        hasLoadData = true;
                        playlistAdapter.clearDataList();
                        newSongAdapter.clearDataList();
                        mvAdapter.clearDataList();
                        djAdapter.clearDataList();
                        albumAdapter.clearDataList();
                        artistAdapter.clearDataList();
                        playlistAdapter.addDataList(listMap.get(DATA_TYPE_PLAYLIST));
                        newSongAdapter.addDataList(listMap.get(DATA_TYPE_NEWSONGS));
                        mvAdapter.addDataList(listMap.get(DATA_TYPE_MV));
                        djAdapter.addDataList(listMap.get(DATA_TYPE_DJ));
                        albumAdapter.addDataList(listMap.get(DATA_TYPE_ALBUM));
                        artistAdapter.addDataList(listMap.get(DATA_TYPE_ARTIST));
                        playlistAdapter.notifyDataSetChanged();
                        newSongAdapter.notifyDataSetChanged();
                        mvAdapter.notifyDataSetChanged();
                        djAdapter.notifyDataSetChanged();
                        albumAdapter.notifyDataSetChanged();
                        artistAdapter.notifyDataSetChanged();
                        mRecycleview.setVisibility(View.VISIBLE);
                        if (mSwiperefresh.isRefreshing()) {
                            mSwiperefresh.setRefreshing(false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        cancleLoadingView(true);
                        if (mSwiperefresh != null && mSwiperefresh.isRefreshing()) {
                            mSwiperefresh.setRefreshing(false);
                        }
                        loadDataFromLocal();
                        hasLoadData = false;
                    }
                });
    }

    private void loadDataFromLocal() {
        String json = DataCacheUtil.getPersonalCoverData();
        if (!TextUtils.isEmpty(json)) {
            showContentView();
            PersonalCoverBean bean = JSON.parseObject(json, PersonalCoverBean.class);
            playlistAdapter.clearDataList();
            newSongAdapter.clearDataList();
            mvAdapter.clearDataList();
            djAdapter.clearDataList();
            albumAdapter.clearDataList();
            artistAdapter.clearDataList();
            playlistAdapter.addDataList(bean.getMap().get(DATA_TYPE_PLAYLIST));
            newSongAdapter.addDataList(bean.getMap().get(DATA_TYPE_NEWSONGS));
            mvAdapter.addDataList(bean.getMap().get(DATA_TYPE_MV));
            djAdapter.addDataList(bean.getMap().get(DATA_TYPE_DJ));
            albumAdapter.addDataList(bean.getMap().get(DATA_TYPE_ALBUM));
            artistAdapter.addDataList(bean.getMap().get(DATA_TYPE_ARTIST));
            playlistAdapter.notifyDataSetChanged();
            newSongAdapter.notifyDataSetChanged();
            mvAdapter.notifyDataSetChanged();
            djAdapter.notifyDataSetChanged();
            albumAdapter.notifyDataSetChanged();
            artistAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showNormalMsg("暂无网络");
            showErrorView();
        }
    }

    private List<IndexPersonEntiy> jsonToPlayList(String json) {
        List<IndexPersonEntiy> list = new ArrayList<>();
        JSONObject root;
        root = JSON.parseObject(json);
        if (root != null) {
            if (root.getIntValue("code") == 200) {
                JSONArray arrays = root.getJSONArray("result");
                int lengh = arrays.size();
                JSONObject reObject;
                for (int i = 0; i < lengh; i++) {
                    reObject = arrays.getJSONObject(i);
                    IndexPersonEntiy personEntiy = new IndexPersonEntiy();
                    personEntiy.setId(reObject.getLongValue("id"));
                    personEntiy.setCoverImgUrl(reObject.getString("picUrl"));
                    personEntiy.setPlayCount(reObject.getLongValue("playCount"));
                    personEntiy.setName(reObject.getString("name"));
                    personEntiy.setSubName(reObject.getString("copywriter"));
                    list.add(personEntiy);
                }
            }
        }

        if (list.size() > 6) {
            return list.subList(0, 6);
        } else {
            return list;
        }

    }

    private List<IndexPersonEntiy> jsonToNewSongs(String json) {
        List<IndexPersonEntiy> list = new ArrayList<>();
        JSONObject root;
        root = JSON.parseObject(json);
        if (root != null) {
            if (root.getIntValue("code") == 200) {
                JSONArray arrays = root.getJSONArray("result");
                int lengh = arrays.size();
                JSONObject reObject;
                for (int i = 0; i < lengh; i++) {
                    reObject = arrays.getJSONObject(i);
                    JSONObject songObject = reObject.getJSONObject("song");
                    IndexPersonEntiy personEntiy = new IndexPersonEntiy();
                    personEntiy.setId(reObject.getLongValue("id"));
                    personEntiy.setCoverImgUrl(songObject.getJSONObject("album").getString("picUrl"));
                    personEntiy.setName(reObject.getString("name"));
                    personEntiy.setSubName(songObject.getJSONArray("artists").getJSONObject(0).getString("name"));

                    personEntiy.setVal(songObject.getJSONObject("album").getString("name"));
                    personEntiy.setVal2(String.valueOf(songObject.getJSONObject("album").getLongValue("id")));
                    personEntiy.setVal3(String.valueOf(songObject.getLongValue("duration")));
                    personEntiy.setVal4(String.valueOf(songObject.getJSONArray("artists").getJSONObject(0).getLongValue("id")));
                    personEntiy.setVal5(String.valueOf(songObject.getLongValue("mvid")));

                    list.add(personEntiy);
                }
            }
        }
        if (list.size() > 6) {
            return list.subList(0, 6);
        } else {
            return list;
        }
    }

    private List<IndexPersonEntiy> jsonToMv(String json) {
        List<IndexPersonEntiy> list = new ArrayList<>();
        JSONObject root;
        root = JSON.parseObject(json);
        if (root != null) {
            if (root.getIntValue("code") == 200) {
                JSONArray arrays = root.getJSONArray("result");
                int lengh = arrays.size();
                JSONObject reObject;
                for (int i = 0; i < lengh; i++) {
                    reObject = arrays.getJSONObject(i);
                    IndexPersonEntiy personEntiy = new IndexPersonEntiy();
                    personEntiy.setId(reObject.getLongValue("id"));
                    personEntiy.setCoverImgUrl(reObject.getString("picUrl"));
                    personEntiy.setPlayCount(reObject.getLongValue("playCount"));
                    personEntiy.setName(reObject.getString("name"));
                    personEntiy.setSubName(reObject.getString("artistNaeme"));
                    list.add(personEntiy);
                }
            }

        }
        return list;
    }

    private List<IndexPersonEntiy> jsonToDj(String json) {
        List<IndexPersonEntiy> list = new ArrayList<>();
        JSONObject root;
        root = JSON.parseObject(json);
        if (root != null) {
            if (root.getIntValue("code") == 200) {
                JSONArray arrays = root.getJSONArray("result");
                int lengh = arrays.size();
                JSONObject reObject;
                for (int i = 0; i < lengh; i++) {
                    reObject = arrays.getJSONObject(i);
                    IndexPersonEntiy personEntiy = new IndexPersonEntiy();
                    personEntiy.setId(reObject.getLongValue("id"));
                    personEntiy.setCoverImgUrl(reObject.getString("picUrl"));
                    personEntiy.setName(reObject.getString("name"));
                    JSONObject programObject = reObject.getJSONObject("program");
                    if (programObject != null) {
                        MusicBean musicBean = new MusicBean();
                        musicBean.setMusicId(String.valueOf(programObject.getLongValue("mainTrackId")));
                        musicBean.setSubCommentId(String.valueOf(programObject.getLongValue("id")));
                        musicBean.setDjProgramId(programObject.getLongValue("id"));
                        musicBean.setMusicName(programObject.getString("name"));
                        musicBean.setAllTime(programObject.getLongValue("duration"));
                        musicBean.setCoverImgUrl(programObject.getString("coverUrl"));
                        musicBean.setMusicOrigin(AppConstant.MUSIC_ORIGIN_DJ);
                        musicBean.setLocalMusic(false);
                        musicBean.setLocalArtist(false);
                        musicBean.setLocalAlbum(false);
                        musicBean.setCommentId(programObject.getString("commentThreadId"));
                        musicBean.setCommentCount(programObject.getIntValue("commentCount"));
                        musicBean.setHasMV(false);
                        JSONObject artistObject = programObject.getJSONObject("dj");
                        if (artistObject != null) {
                            musicBean.setArtistName(artistObject.getString("nickname"));
                            musicBean.setArtistId(String.valueOf(artistObject.getLongValue("userId")));
                            musicBean.setArtistImgUrl(artistObject.getString("avatarUrl"));
                        } else {
                            musicBean.setArtistName("");
                            musicBean.setArtistId(null);
                            musicBean.setArtistImgUrl("");
                        }
                        JSONObject albumObject = programObject.getJSONObject("radio");
                        if (albumObject != null) {
                            musicBean.setAlbumId(String.valueOf(albumObject.getLongValue("id")));
                            musicBean.setAlbumName(albumObject.getString("name"));
                            musicBean.setAlbumImgUrl(albumObject.getString("picUrl"));
                        } else {
                            musicBean.setAlbumId(null);
                            musicBean.setAlbumName("");
                            musicBean.setAlbumImgUrl("");
                        }
                        personEntiy.setMusicBean(musicBean);
                    }
                    list.add(personEntiy);
                }
            }
        }
        return list;
    }

    private List<IndexPersonEntiy> jsonToAlbum(String json) {
        List<IndexPersonEntiy> list = new ArrayList<>();
        JSONObject root;
        root = JSON.parseObject(json);
        if (root != null) {
            if (root.getIntValue("code") == 200) {
                JSONArray arrays = root.getJSONArray("result");
                int lengh = arrays.size();
                JSONObject reObject;
                for (int i = 0; i < lengh; i++) {
                    reObject = arrays.getJSONObject(i);
                    IndexPersonEntiy personEntiy = new IndexPersonEntiy();
                    personEntiy.setId(reObject.getLongValue("id"));
                    personEntiy.setCoverImgUrl(reObject.getString("picUrl"));
                    personEntiy.setName(reObject.getString("name"));
                    personEntiy.setSubName(reObject.getString("artistName"));
                    list.add(personEntiy);
                }
            }

        }
        return list;
    }

    private List<IndexPersonEntiy> jsonToArtist(String json) {
        List<IndexPersonEntiy> list = new ArrayList<>();
        JSONObject root;
        root = JSON.parseObject(json);
        if (root != null) {
            if (root.getIntValue("code") == 200) {
                JSONArray arrays = root.getJSONArray("artists");
                int lengh = arrays.size();
                JSONObject reObject;
                for (int i = 0; i < lengh; i++) {
                    reObject = arrays.getJSONObject(i);
                    IndexPersonEntiy personEntiy = new IndexPersonEntiy();
                    personEntiy.setId(reObject.getLongValue("id"));
                    personEntiy.setCoverImgUrl(reObject.getString("picUrl"));
                    personEntiy.setName(reObject.getString("name"));
                    list.add(personEntiy);
                }
            }
        }
        return list;
    }

}
