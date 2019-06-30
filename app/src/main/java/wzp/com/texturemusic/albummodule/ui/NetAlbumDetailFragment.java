package wzp.com.texturemusic.albummodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.albummodule.NetAlbumDetailActivity;
import wzp.com.texturemusic.albummodule.adapter.AlbumItemAdapter;
import wzp.com.texturemusic.api.AlbumApiManager;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.mvmodule.MvDetailActivity;
import wzp.com.texturemusic.util.SPSetingUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:网络专辑详情页的Fragment
 * on 2018/1/26.
 */

public class NetAlbumDetailFragment extends BaseFragment {
    @BindView(R.id.m_recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.m_swiperefresh)
    SwipeRefreshLayout mSwiperefresh;
    Unbinder unbinder;
    private AlbumBean albumBean;
    private AlbumItemAdapter adapter;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumBean = getArguments().getParcelable("album");
        adapter = new AlbumItemAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        MusicBean bean = adapter.getDataList().get(itemBean.getPosition());
                        View view = itemBean.getView();
                        if (view.getId() == R.id.item_operation_img) {
                            ((NetAlbumDetailActivity) getActivity()).showMusicInfoPop(bean, null);
                        } else if (view.getId() == R.id.item_show_mv_img && bean.getHasMV()) {
                            //MV
                            long mvId = bean.getMvBean().getMvId();
                            Intent intent = new Intent(mContext, MvDetailActivity.class);
                            intent.putExtra(AppConstant.UI_INTENT_KEY_MV, mvId);
                            startActivity(intent);
                        } else {
                            //播放音乐
                            ((NetAlbumDetailActivity) getActivity()).playMusic(bean);
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
            mContentView = inflater.inflate(R.layout.fragment_net_album_detail, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        SlideInDownAnimator animator = new SlideInDownAnimator(new OvershootInterpolator(0f));
        mRecyclerview.setItemAnimator(animator);
        mRecyclerview.getItemAnimator().setAddDuration(600);
        mRecyclerview.getItemAnimator().setChangeDuration(600);
        mSwiperefresh.setEnabled(false);
    }

    @Override
    public void lazyLoading() {
        if (!hasLoadData && albumBean != null) {
            getAlbumDetail(Long.parseLong(albumBean.getAlbumId()));
        }

    }

    @SuppressLint("CheckResult")
    private void getAlbumDetail(long albumID) {
        if (albumID == 0) {
            return;
        }
        AlbumApiManager
                .getAlbumDetail(albumID, albumID, 0, 100)
                .map(s -> jsonToAlbumDetail(s)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(albumBean -> {
                    hasLoadData = true;
                    if (!TextUtils.isEmpty(albumBean.getAlbumId())) {
                        adapter.clearDataList();
                        adapter.addDataList(albumBean.getMusicList());
                        adapter.notifyItemRangeChanged(0, albumBean.getMusicList().size());
                        ((NetAlbumDetailActivity) getActivity()).
                                mTablayout.getTabAt(1).setText("音乐(" + albumBean.getMusicList().size() + ")");
                    } else {
                        ToastUtil.showNormalMsg("数据错误");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showNormalMsg("数据错误");
                    }
                });
    }

    private AlbumBean jsonToAlbumDetail(String json) {
        int bit = SPSetingUtil.getIntValue(AppConstant.SP_KEY_PLAY_QUALITY, AppConstant.MUSIC_BITRATE_NORMAL);
        AlbumBean bean = new AlbumBean();
        JSONObject resultObject = JSONObject.parseObject(json);
        if (resultObject.getIntValue("code") == 200) {
            bean = new AlbumBean();
            JSONObject albumObject = resultObject.getJSONObject("album");
            JSONArray songsObject = albumObject.getJSONArray("songs");
            JSONObject infoObject = albumObject.getJSONObject("info");
            JSONObject artistObject = albumObject.getJSONObject("artist");
            bean.setCommentId(albumObject.getString("commentThreadId"));
            bean.setAlbumId(String.valueOf(albumObject.getLongValue("id")));
            String albumName = albumObject.getString("name");
            bean.setAlbumName(albumName);
            bean.setLikedCount(infoObject.getIntValue("likedCount"));
            bean.setShareCount(infoObject.getIntValue("shareCount"));
            bean.setCommentCount(infoObject.getIntValue("commentCount"));
            bean.setDescription(albumObject.getString("description"));
            bean.setAlbumImgUrl(albumObject.getString("picUrl"));
            bean.setAlbumId(String.valueOf(albumObject.getLongValue("id")));
            if (songsObject != null && songsObject.size() > 0) {
                List<MusicBean> musicBeanList = new ArrayList<>();
                int size = songsObject.size();
                JSONArray aliasArray;
                JSONObject itemObject, hMusicObject;
                for (int i = 0; i < size; i++) {
                    itemObject = songsObject.getJSONObject(i);
                    MusicBean musicBean = new MusicBean();
                    musicBean.setLocalMusic(false);
                    musicBean.setMusicBitrate(bit);
                    musicBean.setMusicId(String.valueOf(itemObject.getLongValue("id")));
                    musicBean.setMusicName(itemObject.getString("name"));
                    musicBean.setCommentId(itemObject.getString("commentThreadId"));
                    musicBean.setHasMV(itemObject.getLongValue("mvid") != 0);
                    musicBean.setCoverImgUrl(albumBean.getAlbumImgUrl());
                    long mvId = itemObject.getLongValue("mvid");
                    if (mvId != 0) {
                        musicBean.setHasMV(true);
                        MvBean mvBean = new MvBean();
                        mvBean.setMvId(mvId);
                        musicBean.setMvBean(mvBean);
                    } else {
                        musicBean.setHasMV(false);
                    }
                    musicBean.setAllTime(itemObject.getLongValue("duration"));
                    musicBean.setAlbumName(albumName);
                    musicBean.setAlbumId(bean.getAlbumId());
                    musicBean.setArtistName(albumObject.getJSONObject("artist").getString("name"));
                    musicBean.setArtistId(String.valueOf(artistObject.getLongValue("id")));
                    musicBean.setArtistImgUrl(artistObject.getString("picUrl"));
                    aliasArray = itemObject.getJSONArray("alias");
                    if (aliasArray != null) {
                        String alStr = "";
                        for (int j = 0; j < aliasArray.size(); j++) {
                            alStr += aliasArray.getString(j);
                        }
                        musicBean.setAlias(alStr);
                    }
                    hMusicObject = itemObject.getJSONObject("hMusic");
                    musicBean.setSQMusic(hMusicObject != null);
                    musicBeanList.add(musicBean);
                }
                bean.setMusicList(musicBeanList);
            }
            bean.setHasLoadData(true);
            bean.setLocalAlbum(false);
        }
        return bean;
    }

    public List<MusicBean> getMusicList() {
        if (adapter != null) {
            return adapter.getDataList();
        }
        return null;
    }

}
