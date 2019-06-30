package wzp.com.texturemusic.playlistmodule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.NoNetworkException;
import wzp.com.texturemusic.api.PlaylistApiManager;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.bean.PlayListBean;
import wzp.com.texturemusic.bean.ShareBean;
import wzp.com.texturemusic.bean.SubCommentBean;
import wzp.com.texturemusic.bean.UserBean;
import wzp.com.texturemusic.common.ui.CommentActivity;
import wzp.com.texturemusic.common.ui.MultipleChoiceActivity;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.config.AppFileConstant;
import wzp.com.texturemusic.core.customui.BaseLinearLayoutManager;
import wzp.com.texturemusic.core.customui.BaseRecyclerView;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.dbmodule.DbUtil;
import wzp.com.texturemusic.dbmodule.util.DbCollectUtil;
import wzp.com.texturemusic.interf.OnOperationListener;
import wzp.com.texturemusic.mvmodule.MvDetailActivity;
import wzp.com.texturemusic.playlistmodule.adapter.PlaylistDetailAdapter;
import wzp.com.texturemusic.playlistmodule.bean.PlaylistAdapterBean;
import wzp.com.texturemusic.usermodule.UserDetailActivity;
import wzp.com.texturemusic.util.DownloadUtil;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;
import wzp.com.texturemusic.util.SPSetingUtil;
import wzp.com.texturemusic.util.ToastUtil;

import static wzp.com.texturemusic.util.StringUtil.checkStringNull;

/**
 * Created by Wang on 2018/2/21.
 * 歌单详情第二个界面
 */
public class PlaylistDetailActivity extends BaseActivityWrapper {
    @BindView(R.id.m_recyclerview)
    BaseRecyclerView mRecyclerview;
    @BindView(R.id.m_toolbar_tv)
    TextView mToolbarTv;
    @BindView(R.id.m_Toolbar_linear)
    LinearLayout mToolbarLinear;
    private Long playlistId;
    private PlayListBean currentPlayList;
    private int coverImgColor;
    private PopupWindow popupWindow;
    //Recycleview的滚动距离
    private int DY = 0;
    private int TOOLBAR_HEIGHT = 0;
    ////////////////////
    private PlaylistDetailAdapter adapterV2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBottomView(true);
        setContentView(R.layout.activity_playlist_detail);
        ButterKnife.bind(this);
        setStatusBarHeight(0);
        playlistId = getIntent().getLongExtra(AppConstant.UI_INTENT_KEY_PLAYLIST, 0L);
        String imgUrl = getIntent().getStringExtra(AppConstant.UI_INTENT_KEY_PLAYLIST_IMG);
        PlayListBean bean = new PlayListBean();
        bean.setPlaylistId(playlistId);
        bean.setCoverImgUr(imgUrl);
        bean.setCollectCount(0);
        bean.setShareCount(0);
        bean.setPlayCount(0);
        bean.setCommentCount(0);
        bean.setCreater(new UserBean());
        PlaylistAdapterBean adapterBean = new PlaylistAdapterBean();
        adapterBean.setType(0);
        adapterBean.setPlayList(bean);
        adapterV2 = new PlaylistDetailAdapter(mContext);
        adapterV2.getDataList().add(adapterBean);
        adapterV2.setAnimatorListener(new OnAnimatorFinishListener() {
            @Override
            public void onFinishi(int color) {
                coverImgColor = color;
                mToolbarLinear.setBackgroundColor(color);
                if (mToolbarLinear.getBackground() != null) {
                    mToolbarLinear.getBackground().setAlpha(0);
                }
            }
        });
        ////////////////////////////
        initListener();
        mToolbarLinear.setBackgroundColor(0x00ffffff);
        if (mToolbarLinear.getBackground() != null) {
            mToolbarLinear.getBackground().setAlpha(0);
        }
        BaseLinearLayoutManager layoutManager = new BaseLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        layoutManager.setRecycleChildrenOnDetach(true);
        layoutManager.setExtraLayoutSpace(200);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        mRecyclerview.setRecycledViewPool(viewPool);
        mRecyclerview.setFlingScale(1.20);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.setAdapter(adapterV2);
        PlaylistDetailAnimator playlistDetailAnimator = new PlaylistDetailAnimator(new OvershootInterpolator());
        mRecyclerview.setItemAnimator(playlistDetailAnimator);
        mRecyclerview.getItemAnimator().setAddDuration(600);
        mRecyclerview.getItemAnimator().setChangeDuration(600);
        if (playlistId != 0L) {
            getPlaylistDetail(playlistId);
        } else {
            ToastUtil.showNormalMsg("该歌单不存在");
            finish();
        }
    }

    @SuppressLint("CheckResult")
    private void initListener() {
        adapterV2.getItemClickSubject().compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        int position = itemBean.getPosition();
                        MusicBean musicBean = adapterV2.getDataList().get(position).getMusicBean();
                        if (view.getId() == R.id.item_operation_img) {
                            showMusicInfoPop(musicBean, null);
                            return;
                        }
                        if (view.getId() == R.id.item_show_mv_img) {
                            Boolean hasMv = musicBean.getHasMV();
                            if (hasMv != null && hasMv) {
                                //跳转到Mv中去
                                Long mvId = musicBean.getMvBean().getMvId();
                                Intent intent = new Intent(mContext, MvDetailActivity.class);
                                intent.putExtra(AppConstant.UI_INTENT_KEY_MV, mvId);
                                startActivity(intent);
                                return;
                            }
                        }
                        playMusic(musicBean);
                    }
                });

        adapterV2.setOperationListener(new OnOperationListener() {
            @Override
            public void onPlayAll() {
                List<MusicBean> list = currentPlayList.getMusicBeanList();
                playMusicList(list, true);
            }

            @Override
            public void onMultipleChoice() {
                ArrayList<MusicBean> list = (ArrayList<MusicBean>) currentPlayList.getMusicBeanList();
                Intent intent = new Intent(mContext, MultipleChoiceActivity.class);
                intent.putParcelableArrayListExtra(MultipleChoiceActivity.INTENT_DATA_LIST, list);
                startActivity(intent);
            }
        });

        adapterV2.setInfoCallback(new PlaylistDetailAdapter.OnInfoCallback() {
            @Override
            public void onViewClick(View view) {
                Intent intent;
                switch (view.getId()) {
                    case R.id.m_headimg:
                        //显示歌单的详细介绍
                        if (currentPlayList != null) {
                            showPlaylistDetailInfo(currentPlayList);
                        } else {
                            ToastUtil.showNormalMsg("请等待数据加载完成");
                        }
                        break;
                    case R.id.m_creater_linear:
                        UserBean userBean = new UserBean();
                        userBean.setUserId(currentPlayList.getCreaterId());
                        userBean.setUserCoverImgUrl(currentPlayList.getCreater().getUserCoverImgUrl());
                        intent = new Intent(mContext, UserDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_USER, userBean);
                        startActivity(intent);
                        break;
                    case R.id.m_share_linear:
                        ShareBean shareBean = new ShareBean();
                        shareBean.setWebUrl("http://music.163.com/#/playlist?id=" + currentPlayList.getPlaylistId());
                        shareBean.setTitle(currentPlayList.getPlaylistName());
                        shareBean.setImgUrl(currentPlayList.getCoverImgUr());
                        shareBean.setText(currentPlayList.getDescription());
                        shareMusic(shareBean);
                        break;
                    case R.id.m_download_linear:
                        downloadPlaylist();
                        break;
                    case R.id.m_comment_linear:
                        SubCommentBean bean = new SubCommentBean();
                        bean.setCommentId(currentPlayList.getCommentId());
                        bean.setCoverImgUrl(currentPlayList.getCoverImgUr());
                        bean.setTitle(currentPlayList.getPlaylistName());
                        bean.setSubTitle(currentPlayList.getCreater().getNickName());
                        bean.setCommentType(AppConstant.COMMENT_TYPE_PLAYLIST);
                        bean.setPlayList(currentPlayList);
                        intent = new Intent(mContext, CommentActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_COMMENT, bean);
                        startActivity(intent);
                        break;
                }
            }
        });
        mRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                DY += dy;
                TOOLBAR_HEIGHT = mToolbarLinear.getHeight();
                if (DY <= 0) {
                    if (mToolbarLinear.getBackground() != null) {
                        mToolbarLinear.getBackground().setAlpha(0);
                    }
                }
                if (DY > 0 && DY < TOOLBAR_HEIGHT * 2.5F) {
                    float alpha255 = (1 - (DY) / (TOOLBAR_HEIGHT * 2.5F)) * 260;
                    if (mToolbarLinear.getBackground() != null) {
                        int alpha = 255 - (int) alpha255;
                        mToolbarLinear.getBackground().setAlpha(alpha);
                    }
                    if (DY >= TOOLBAR_HEIGHT * 1.5F) {
                        mToolbarTv.setText(currentPlayList.getPlaylistName());
                    } else if (DY < TOOLBAR_HEIGHT * 1.5F) {
                        mToolbarTv.setText("歌单");
                    }
                } else if (DY >= (TOOLBAR_HEIGHT * 2.5F)) {
                    if (mToolbarLinear.getBackground() != null) {
                        mToolbarLinear.getBackground().setAlpha(255);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("CheckResult")
    private void getPlaylistDetail(Long playlistId) {
        if (playlistId == null || playlistId == 0) {
            hasLoadData = true;
            return;
        }
        PlaylistApiManager.getPlayListDetailV3(String.valueOf(playlistId))
                .map(new Function<String, PlayListBean>() {
                    @Override
                    public PlayListBean apply(@NonNull String s) throws Exception {
                        PlayListBean bean = jsonToPlaylistBeanV3(s);
                        bean.setLiked(DbCollectUtil.queryLiked(bean));
                        return bean;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PlayListBean>() {
                    @Override
                    public void accept(PlayListBean playListBean) throws Exception {
                        hasLoadData = true;
                        currentPlayList = playListBean;
                        updateUI(playListBean);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof NoNetworkException) {
                            ToastUtil.showErrorMsg("当前无网络");
                        } else if (throwable instanceof SocketTimeoutException) {
                            ToastUtil.showErrorMsg("网络超时");
                        }
                        showErrorView();
                    }
                });
    }

    private PlayListBean jsonToPlaylistBeanV3(String json) {
        int musicBit = SPSetingUtil.getIntValue(AppConstant.SP_KEY_PLAY_QUALITY, AppConstant.MUSIC_BITRATE_NORMAL);
        PlayListBean bean = new PlayListBean();
        List<MusicBean> musicBeanList = new ArrayList<>();
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONObject result;
        if (jsonObject.getIntValue("code") == 200) {
            JSONArray pribilegeArray = jsonObject.getJSONArray("privileges");
            result = jsonObject.getJSONObject("playlist");
            bean.setMusicCount(result.getIntValue("trackCount"));
            bean.setCoverImgUr(result.getString("coverImgUrl"));
            bean.setPlayCount(result.getInteger("playCount"));
            bean.setShareCount(result.getIntValue("shareCount"));
            bean.setCommentCount(result.getInteger("commentCount"));
            bean.setCollectCount(result.getInteger("subscribedCount"));
            bean.setCreaterId(result.getLong("userId"));
            bean.setPlaylistId(result.getLong("id"));
            bean.setDescription(result.getString("description"));
            bean.setPlaylistName(result.getString("name"));
            bean.setCommentId(result.getString("commentThreadId"));
            UserBean userBean = new UserBean();
            JSONObject createrJson = result.getJSONObject("creator");
            userBean.setGender(createrJson.getIntValue("gender"));
            userBean.setUserCoverImgUrl(createrJson.getString("avatarUrl"));
            userBean.setSignnature(createrJson.getString("signature"));
            userBean.setNickName(createrJson.getString("nickname"));
            userBean.setProvince(String.valueOf(createrJson.getLongValue("province")));
            userBean.setCity(String.valueOf(createrJson.getLongValue("city")));
            bean.setCreater(userBean);
            JSONArray tags = result.getJSONArray("tags");
            StringBuilder playlistTag = new StringBuilder();
            if (tags != null && tags.size() > 0) {
                for (int i = 0; i < tags.size(); i++) {
                    playlistTag.append(tags.get(i).toString() + " ");
                }
            }
            bean.setSubDescription(playlistTag.toString());
            JSONArray jsonArray = result.getJSONArray("tracks");
            int length = jsonArray.size();
            JSONObject tracks;
            int bit = SPSetingUtil.getIntValue(AppConstant.SP_KEY_DOWNLOAD_QUALITY, AppConstant.MUSIC_BITRATE_NORMAL);
            JSONArray aliasArraay;
            String musicId, coverImgUrl;
            Long mvId;
            for (int i = 0; i < length; i++) {
                tracks = jsonArray.getJSONObject(i);
                MusicBean netMusicBean = new MusicBean();
                netMusicBean.setMusicBitrate(musicBit);
                musicId = String.valueOf(tracks.getLongValue("id"));
                coverImgUrl = tracks.getJSONObject("al").getString("picUrl");
                mvId = tracks.getLongValue("mv");
                netMusicBean.setAlbumName(tracks.getJSONObject("al").getString("name").trim());
                netMusicBean.setMusicBitrate(bit);
                netMusicBean.setAllTime(tracks.getLongValue("dt"));
                netMusicBean.setAlbumId(tracks.getJSONObject("al").getString("id"));
                netMusicBean.setArtistId(String.valueOf(tracks.getJSONArray("ar").getJSONObject(0).getLongValue("id")));
                netMusicBean.setArtistName(tracks.getJSONArray("ar").getJSONObject(0).getString("name").trim());
                netMusicBean.setArtistImgUrl(coverImgUrl);
                netMusicBean.setCommentId("R_SO_4_" + musicId); //单独一个音乐的ID
                netMusicBean.setCoverImgUrl(coverImgUrl);
                netMusicBean.setAlbumImgUrl(coverImgUrl);
                netMusicBean.setMusicName(tracks.getString("name"));
                netMusicBean.setMusicId(String.valueOf(tracks.getLongValue("id")));
                netMusicBean.setLocalMusic(false);
                aliasArraay = tracks.getJSONArray("alia");
                if (aliasArraay == null) {
                    netMusicBean.setAlias("");
                } else {
                    int k = aliasArraay.size();
                    StringBuilder builder = new StringBuilder();
                    for (int q = 0; q < k; q++) {
                        builder.append(aliasArraay.getString(q));
                    }
                    netMusicBean.setAlias(builder.toString().trim());
                }
                if (pribilegeArray.getJSONObject(i).getIntValue("maxbr") > 320000) {
                    netMusicBean.setSQMusic(true);
                } else {
                    netMusicBean.setSQMusic(false);
                }
                if (mvId != 0) {
                    MvBean mvBean = new MvBean();
                    mvBean.setMvId(mvId);
                    netMusicBean.setMvBean(mvBean);
                    netMusicBean.setHasMV(true);
                } else {
                    netMusicBean.setHasMV(false);
                }
                musicBeanList.add(netMusicBean);
            }
        }
        bean.setMusicBeanList(musicBeanList);
        return bean;
    }

    /**
     * 下载歌单
     */
    private void downloadPlaylist() {
        final List<MusicBean> list = currentPlayList.getMusicBeanList();
        if (!list.isEmpty()) {
            DbUtil.insertDownloadListHistory(String.valueOf(playlistId), 0);
            DownloadUtil.downloadPlaylist(mContext, list);
        } else {
            ToastUtil.showNormalMsg("下载内容不能为空");
        }
    }

    /**
     * 显示歌单的详情信息
     * 通过popupwindow显示
     */
    private void showPlaylistDetailInfo(PlayListBean bean) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pop_playlist_detail, null);
        ImageView popCloseImg = view.findViewById(R.id.pop_colose_img);
        ImageView popCenterImg = view.findViewById(R.id.pop_center_img);
        TextView popPlaylistDescTv = view.findViewById(R.id.pop_playlist_desc_tv);
        TextView popPlaylistInfoTv = view.findViewById(R.id.pop_playlist_info_tv);
        TextView popPlaylistTagTv = view.findViewById(R.id.pop_playlist_tag_tv);
        Button popPlaylistSaveCoverImg = view.findViewById(R.id.pop_playlist_save_cover_img);
        FrameLayout popPlaylistBkLinear = view.findViewById(R.id.pop_playlist_bk_linear);
        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(R.style.pop_playlist_anim);
        popCloseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        popPlaylistSaveCoverImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCoverImg(currentPlayList.getCoverImgUr());
            }
        });

        ImageUtil.loadImage(mContext, bean.getCoverImgUr() + AppConstant.WY_IMG_400_400, popCenterImg, R.drawable.ic_large_album);

        popPlaylistBkLinear.setBackgroundColor(coverImgColor);
        popPlaylistDescTv.setText(bean.getPlaylistName());
        popPlaylistInfoTv.setText(bean.getDescription());
        popPlaylistTagTv.setText("标签: " + bean.getSubDescription());
        popupWindow.showAtLocation(mToolbarLinear, Gravity.CENTER, 0, 0);
    }

    @SuppressLint("CheckResult")
    private void saveCoverImg(final String imgUrl) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                Bitmap bitmap = ImageUtil.getBitmap(mContext, imgUrl, 1080, 1920);
                if (bitmap != null) {
                    File dirFile = new File(AppFileConstant.COVER_IMG);
                    if (!dirFile.exists()) {
                        dirFile.mkdirs();
                    }
                    File file = new File(AppFileConstant.COVER_IMG, currentPlayList.getPlaylistName() + ".jpg");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                    outputStream.close();
                }
                e.onNext(true);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        ToastUtil.showNormalMsg("保存成功");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showNormalMsg("保存失败");
                    }
                });

    }

    @OnClick(R.id.toolbar_return_img)
    public void onViewClicked() {
        finish();
    }

    @Override
    protected void onNetworkChange(boolean isConnected, boolean isVpn) {
        super.onNetworkChange(isConnected, isVpn);
        if (isConnected && !hasLoadData) {
            getPlaylistDetail(playlistId);
        }
    }

    /**
     * 用于更新Recycleview界面
     */
    private void updateUI(PlayListBean listBean) {
        List<PlaylistAdapterBean> list = new ArrayList<>();
        PlaylistAdapterBean b1 = new PlaylistAdapterBean();
        b1.setType(0);
        b1.setPlayList(listBean);
        list.add(b1);
        PlaylistAdapterBean b2 = new PlaylistAdapterBean();
        b2.setType(1);
        b2.setMusicSize(listBean.getMusicBeanList().size());
        list.add(b2);
        for (MusicBean musicBean : listBean.getMusicBeanList()) {
            PlaylistAdapterBean bean = new PlaylistAdapterBean();
            bean.setType(2);
            bean.setMusicBean(musicBean);
            list.add(bean);
        }
        adapterV2.clearDataList();
        adapterV2.addDataList(list);
        adapterV2.notifyItemRangeInserted(1, list.size() - 1);
        RecyclerView.ViewHolder holder = mRecyclerview.findViewHolderForAdapterPosition(0);
        if (holder != null && holder instanceof PlaylistDetailAdapter.ViewholderInfoV2) {
            PlaylistDetailAdapter.ViewholderInfoV2 viewHolder = (PlaylistDetailAdapter.ViewholderInfoV2) holder;
            if (listBean.getLiked() != null && listBean.getLiked()) {
                viewHolder.mLikedimg.setImageResource(R.drawable.ic_collection);
            } else {
                viewHolder.mLikedimg.setImageResource(R.drawable.ic_unlike);
            }
            viewHolder.mPlaylistNameTv.setText(checkStringNull(listBean.getPlaylistName()));
            viewHolder.mCreaterNameTv.setText(checkStringNull(listBean.getCreater() == null ? "" : listBean.getCreater().getNickName()));
            viewHolder.mCollectionTv.setText(checkStringNull(FormatData.longValueToString(listBean.getCollectCount())));
            viewHolder.mSharecountTv.setText(checkStringNull(FormatData.longValueToString(listBean.getShareCount())));
            viewHolder.mCommentCountTv.setText(checkStringNull(FormatData.longValueToString(listBean.getCommentCount())));
            viewHolder.playcountTv.setText(checkStringNull(FormatData.longValueToString(listBean.getPlayCount())));
            ImageUtil.loadImage(mContext,
                    listBean.getCreater().getUserCoverImgUrl() + AppConstant.WY_IMG_100_100,
                    viewHolder.mCreaterHeadImg,
                    R.drawable.ic_user_head, R.drawable.ic_user_head);
        }
    }

}
