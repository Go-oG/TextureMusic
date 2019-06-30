package wzp.com.texturemusic.usermodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.WYApiForUser;
import wzp.com.texturemusic.api.WYApiUtil;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.UserBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.core.customui.OnLoadMoreForLinearLayoutManager;
import wzp.com.texturemusic.playlistmodule.PlaylistDetailActivity;
import wzp.com.texturemusic.usermodule.UserDetailActivity;
import wzp.com.texturemusic.usermodule.adapter.UserDetailMusicAdapter;
import wzp.com.texturemusic.usermodule.bean.UserInfoBean;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/11/27.
 */

public class UserDetailMusicFragment extends BaseFragment {
    private static final String MAP_KEY_DJ = "info_dj";
    private static final String MAP_KEY_PLAYLIST = "info_playlist";
    @BindView(R.id.m_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private UserBean bean;
    private int offset = 0;
    private int limit = 30;
    private boolean hasMorePlaylist = true;
    private DelegateAdapter adapter;
    private VirtualLayoutManager layoutManager;
    private UserDetailMusicAdapter playlistAdapter, djAdapter;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bean = getArguments().getParcelable("bundle");
        layoutManager = new VirtualLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        layoutManager.setRecycleChildrenOnDetach(true);
        List<DelegateAdapter.Adapter>  adapterList = new ArrayList<>();
        djAdapter = new UserDetailMusicAdapter(mContext, 0);
        djAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        int position = itemBean.getPosition();
                        if (view.getId() == R.id.item_item) {
                            ((UserDetailActivity) getActivity()).playMusic(djAdapter.getDataList().get(position).getMusicBean());
                        } else {
                            //弹窗
                            ((UserDetailActivity) getActivity())
                                    .showMusicInfoPop(djAdapter.getDataList().get(position).getMusicBean(), null);
                        }
                    }
                });

        playlistAdapter = new UserDetailMusicAdapter(mContext, 1);

        playlistAdapter.getItemClickSubject().compose(this.<ItemBean>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        int position = itemBean.getPosition();
                        if (view.getId() == R.id.item_item) {
                            Intent intent = new Intent(mContext, PlaylistDetailActivity.class);
                            intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST, playlistAdapter.getDataList().get(position)
                                    .getId());
                            intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST_IMG, playlistAdapter.getDataList().get(position)
                                    .getImgUrl());
                            startActivity(intent);
                        }
                    }
                });
        adapterList.add(djAdapter);
        adapterList.add(playlistAdapter);
        adapter = new DelegateAdapter(layoutManager);
        adapter.setAdapters(adapterList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_user_detail, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecycleview.setAdapter(adapter);
        mRecycleview.setLayoutManager(layoutManager);
        mRecycleview.addOnScrollListener(new OnLoadMoreForLinearLayoutManager(layoutManager) {
            @Override
            public void onLoadMore() {
                if (hasMorePlaylist) {
                    offset++;
                    getData(bean.getUserId(), offset * limit, limit, true);
                } else {
                    ToastUtil.showNormalMsg("没有更多数据");
                }

            }
        });
    }

    @Override
    public void lazyLoading() {
        if (!hasLoadData) {
            if (bean.getUserId() != 0) {
                getData(bean.getUserId(), offset, limit, false);
            }
        }

    }

    @SuppressLint("CheckResult")
    private void getData(Long userId, int offset, int limit, final boolean isLoadMore) {
        WYApiForUser apiForUser = WYApiUtil.getInstance().buildUserService();
        Observable.zip(apiForUser.getUserDjRadio(userId, offset, limit),
                apiForUser.getUserPlayList(userId, offset, limit),
                new BiFunction<String, String, Map<String, List<UserInfoBean>>>() {
                    @Override
                    public Map<String, List<UserInfoBean>> apply(String s, String s2) throws Exception {
                        Map<String, List<UserInfoBean>> map = new ArrayMap<>();
                        map.put(MAP_KEY_DJ, djJson(s));
                        map.put(MAP_KEY_PLAYLIST, playlistJson(s2));
                        return map;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Map<String, List<UserInfoBean>>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<Map<String, List<UserInfoBean>>>() {
                    @Override
                    public void accept(Map<String, List<UserInfoBean>> map) throws Exception {
                        hasLoadData = true;
                        if (djAdapter.getDataList().isEmpty()) {
                            djAdapter.addDataList(map.get(MAP_KEY_DJ));
                            djAdapter.notifyDataSetChanged();
                        }
                        if (!isLoadMore) {
                            playlistAdapter.clearDataList();
                        }
                        playlistAdapter.addDataList(map.get(MAP_KEY_PLAYLIST));
                        playlistAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        hasLoadData = false;
                    }
                });
    }

    private List<UserInfoBean> djJson(String json) {
        List<UserInfoBean> list = new ArrayList<>();
        JSONObject rootObject = JSONObject.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            JSONArray programArray = rootObject.getJSONArray("programs");
            int size = programArray.size();
            if (size > 0) {
                JSONObject itemObject;
                for (int i = 0; i < size; i++) {
                    itemObject = programArray.getJSONObject(i);
                    UserInfoBean bean = new UserInfoBean();
                    JSONObject songObject = itemObject.getJSONObject("mainSong");
                    MusicBean musicBean = new MusicBean();
                    musicBean.setMusicName(songObject.getString("name"));
                    musicBean.setMusicId(String.valueOf(songObject.getLongValue("id")));
                    musicBean.setCommentId(songObject.getString("commentThreadId"));
                    musicBean.setLocalMusic(false);
                    musicBean.setAllTime(songObject.getLongValue("duration"));
                    musicBean.setCoverImgUrl(itemObject.getString("coverUrl"));
                    musicBean.setAlbumName(songObject.getJSONObject("album").getString("name"));
                    musicBean.setArtistName(itemObject.getJSONObject("dj").getString("nickname"));
                    musicBean.setMusicBitrate(AppConstant.MUSIC_BITRATE_NORMAL);
                    bean.setMusicBean(musicBean);
                    bean.setVal(String.valueOf(itemObject.getIntValue("listenerCount")));
                    bean.setVal1(String.valueOf(itemObject.getLongValue("createTime")));
                    list.add(bean);
                }
            }
        }
        return list;
    }

    private List<UserInfoBean> playlistJson(String json) {
        List<UserInfoBean> list = new ArrayList<>();
        JSONObject rootObject = JSONObject.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            hasMorePlaylist = rootObject.getBooleanValue("more");
            JSONArray playlistArray = rootObject.getJSONArray("playlist");
            if (playlistArray != null && playlistArray.size() > 0) {
                int size = playlistArray.size();
                JSONObject itemObject;
                for (int i = 0; i < size; i++) {
                    UserInfoBean bean = new UserInfoBean();
                    itemObject = playlistArray.getJSONObject(i);
                    bean.setImgUrl(itemObject.getString("coverImgUrl"));
                    bean.setName(itemObject.getString("name"));
                    bean.setId(itemObject.getLongValue("id"));
                    bean.setVal(itemObject.getString("commentThreadId"));
                    bean.setVal1(String.valueOf(itemObject.getIntValue("playCount")));
                    bean.setVal2(String.valueOf(itemObject.getIntValue("subscribedCount")));
                    bean.setVal3(String.valueOf(itemObject.getIntValue("trackCount")));
                    list.add(bean);
                }
            }
        }
        return list;
    }

}
