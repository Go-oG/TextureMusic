package wzp.com.texturemusic.mvmodule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.CommentApiManager;
import wzp.com.texturemusic.api.MvApiManager;
import wzp.com.texturemusic.bean.CommentBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.KeyValueBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.bean.ShareBean;
import wzp.com.texturemusic.bean.SubCommentBean;
import wzp.com.texturemusic.bean.UserBean;
import wzp.com.texturemusic.common.popwindow.MvQualityPopwindow;
import wzp.com.texturemusic.common.ui.CommentActivity;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.manger.CacheManager;
import wzp.com.texturemusic.core.ui.BaseActivity;
import wzp.com.texturemusic.customview.VideoControllerView;
import wzp.com.texturemusic.interf.OnDialogListener;
import wzp.com.texturemusic.interf.OnRecycleItemClickListener;
import wzp.com.texturemusic.mvmodule.adapter.MvDetailCommentAdapter;
import wzp.com.texturemusic.mvmodule.adapter.MvDetailInfoAdapter;
import wzp.com.texturemusic.mvmodule.adapter.MvDetailSimilarAdapter;
import wzp.com.texturemusic.mvmodule.adapter.MvDetailSingleAdapter;
import wzp.com.texturemusic.usermodule.UserDetailActivity;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.DownloadUtil;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.StringUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:Mv详情界面
 * on 2017/10/27.
 */

public class MvDetailActivity extends BaseActivity {
    private static final String TAG = "MvDetailActivity";
    TextureView mTextureview;
    @BindView(R.id.m_recycleview)
    RecyclerView mRecycleview;
    @BindView(R.id.m_videoView)
    VideoControllerView mVideoView;
    @BindView(R.id.m_tips_tv)
    TextView mTipsTv;
    @BindView(R.id.m_video_frame)
    FrameLayout mVideoFrame;
    private volatile SimpleExoPlayer mExoPlayer;
    private DataSource.Factory dataSourceFactory;
    //用于测量网络带宽可以不要
    private DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    private long mvId = 0L;
    private MvBean mvBean;
    private Disposable progressDisposable;//用于更新进度条
    ///////////////adapter/////////////
    private MvDetailInfoAdapter infoAdapter;
    private MvDetailSimilarAdapter similarAdapter;
    private MvDetailCommentAdapter commentAdapter;
    private MvDetailSingleAdapter simSingleAdapter;
    private MvDetailSingleAdapter comSingleAdapter;
    private long startTime = 0L;
    private MvQualityPopwindow popwindow;
    private int screenLight;
    private boolean isAutoLight = false;//是否开启了屏幕自动亮度
    private AudioManager mAudioManager;

    private ContentObserver rotationObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (selfChange) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }
        }
    };

    private class VideoCallback extends AbstractVideoListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_ENDED) {
                //播放完成
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    //当前是横屏
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mVideoFrame.getLayoutParams();
                    layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    layoutParams.height = BaseUtil.dp2px(196);
                    mVideoFrame.setLayoutParams(layoutParams);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                mVideoView.setPlayImgSrc(R.drawable.exo_controls_play);
                mVideoView.setViewIsExpand(false);
                mVideoView.hintOrShowView(true, VideoControllerView.PLAYSTATUS_PAUSE, false);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initService(false);
        addActivityToCustomStack(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mv_detail);
        setStatusBarHeight(0);
        ButterKnife.bind(this);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //获取当前屏幕亮度的模式
        isAutoLight = BaseUtil.isAutoBrightness(this);
        screenLight = BaseUtil.getScreenBrightness(this);
        mTextureview = mVideoView.getTextureView();
        mVideoView.setOperationImg(R.drawable.ic_download);
        mvId = getIntent().getLongExtra(AppConstant.UI_INTENT_KEY_MV, 0L);
        creatExoPlayer();
        initAdapter();
        initListener();
        getContentResolver().registerContentObserver(Settings.System.getUriFor(
                Settings.System.ACCELEROMETER_ROTATION), true, rotationObserver);
        Intent intent = new Intent();
        intent.setAction(AppConstant.RECIVER_ACTION_PAUSE);
        sendBroadcast(intent);
        if (mvId != 0L) {
            getMvDetail(mvId);
        } else {
            ToastUtil.showCustomDialog(mContext, "暂无该Mv的信息", new OnDialogListener() {
                @Override
                public void onResult(boolean success) {
                    if (success) {
                        finish();
                    }
                }
            });
        }
    }

    @SuppressLint("CheckResult")
    private void initAdapter() {
        VirtualLayoutManager layoutManager = new VirtualLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        infoAdapter = new MvDetailInfoAdapter(mContext);
        infoAdapter.setListener(new OnRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        //点赞
                        break;
                    case 1:
                        //收藏
                        break;
                    case 2:
                        //评论
                        String commentId = mvBean.getMvCommentId();
                        if (TextUtils.isEmpty(commentId)) {
                            commentId = "R_MV_5_" + mvBean.getMvId();
                        }
                        SubCommentBean sbean = new SubCommentBean();
                        sbean.setCommentId(commentId);
                        sbean.setCoverImgUrl(mvBean.getCoverImgUrl());
                        sbean.setTitle(mvBean.getMvName());
                        sbean.setSubTitle(mvBean.getArtistName());
                        sbean.setCommentType(AppConstant.COMMENT_TYPE_MV);
                        sbean.setMvBean(mvBean);
                        Intent intent = new Intent(mContext, CommentActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_COMMENT, sbean);
                        startActivity(intent);
                        break;
                    case 3:
                        //分享
                        showShareWindown(mvBean);
                        break;
                }
            }
        });

        similarAdapter = new MvDetailSimilarAdapter(this);
        similarAdapter.getItemClickSubject().compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        mvId = similarAdapter.getDataList().get(position).getMvId();
                        getMvDetail(mvId);
                    }
                });
        commentAdapter = new MvDetailCommentAdapter(this);
        commentAdapter.getItemClickSubject().compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        UserBean userBean = commentAdapter.getDataList().get(position).getUserBean();
                        Intent intent = new Intent(mContext, UserDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_USER, userBean);
                        startActivity(intent);
                    }
                });
        simSingleAdapter = new MvDetailSingleAdapter(mContext, "相似推荐");
        comSingleAdapter = new MvDetailSingleAdapter(mContext, "精彩评论");
        List<DelegateAdapter.Adapter> adapterList = new ArrayList<>();
        adapterList.add(infoAdapter);
        adapterList.add(simSingleAdapter);
        adapterList.add(similarAdapter);
        adapterList.add(comSingleAdapter);
        adapterList.add(commentAdapter);
        DelegateAdapter adapter = new DelegateAdapter(layoutManager);
        adapter.setAdapters(adapterList);
        mRecycleview.setAdapter(adapter);
        mRecycleview.setLayoutManager(layoutManager);
    }

    private void initListener() {
        mVideoView.setControlListener(new VideoControllerView.AbstractVideoControlAdapter() {
            @Override
            public void onReturn(boolean expand) {
                super.onReturn(expand);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    //当前是横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    finish();
                }
            }

            @Override
            public void onVideoPlay(boolean expand) {
                controlPlay();
            }

            @Override
            public void onShare() {
                super.onShare();
                showShareWindown(mvBean);
            }

            @Override
            public void onOperation(boolean expand) {
                //下载
                DownloadUtil.downloadMv(mContext, mvBean);
                ToastUtil.showNormalMsg("开始下载Mv");
            }

            @Override
            public void onResolution() {
                showTypePopupMenu();
            }

            @Override
            public void onLeftRight(float distance, boolean isScroll) {
                adjuestProgress(distance, isScroll);
            }

            @Override
            public void onRightLeft(float distance, boolean isScroll) {
                adjuestProgress(distance, isScroll);
            }

            @Override
            public void onLeftVerticalScroll(boolean isUp, float distance, float distancePercent, boolean isContinueDirection) {
                adjustVolume(distance);
            }

            @Override
            public void onRightVerticalScroll(boolean isUp, float distance, float distancePercent, boolean isContinueDirection) {
                float percent = distance / (float) mVideoView.getMeasuredHeight();
                int currentLight = BaseUtil.getScreenBrightness(mContext);
                float lightOffsetAccurate = 255 * percent * 5;
                int lightOffset = (int) lightOffsetAccurate;
                if (Math.abs(lightOffsetAccurate) > 1f) {
                    if (distance > 0) {
                        lightOffset = 30;
                    } else {
                        lightOffset = -30;
                    }
                }
                currentLight += lightOffset;
                if (currentLight < 0) {
                    currentLight = 0;
                }
                if (currentLight > 255) {
                    currentLight = 255;
                }
                BaseUtil.setBrightness(MvDetailActivity.this, currentLight);
            }

            @Override
            public void onExpand() {
                super.onExpand();
                rotateScreen();
            }

            @Override
            public void onUnExpand() {
                super.onUnExpand();
                rotateScreen();
            }

            @Override
            public void onDownUp() {
                mTipsTv.setVisibility(View.GONE);
            }

            @Override
            public void onProgressChange(boolean expand, boolean isTouchStop) {
                super.onProgressChange(expand, isTouchStop);
                if (!isTouchStop) {
                    return;
                }
                creatExoPlayer();
                int state = mExoPlayer.getPlaybackState();
                if (state == Player.STATE_READY) {
                    int progress = mVideoView.getSeekBarCompat().getProgress();
                    int all = mVideoView.getSeekBarCompat().getMax();
                    long percent = (long) (((float) progress / (float) all) * mExoPlayer.getDuration());
                    mExoPlayer.seekTo(percent);
                } else if (state == Player.STATE_BUFFERING) {
                    ToastUtil.showNormalMsg("正在缓冲中,请稍等片刻");
                } else if (state == Player.STATE_IDLE) {
                    if (mvBean != null) {
                        updateUI(mvBean);
                    } else {
                        ToastUtil.showNormalMsg("当前暂无数据,请选择播放数据");
                    }
                } else if (state == Player.STATE_ENDED) {
                    ToastUtil.showCustomDialog(mContext, "当前MV已经播放完成，是否从新播放", new OnDialogListener() {
                        @Override
                        public void onResult(boolean success) {
                            if (success) {
                                updateUI(mvBean);
                            }
                        }
                    });
                }
            }

            @Override
            public void onViewVisibilityChange(boolean visibility) {
                if (!visibility) {
                    if (popwindow != null && popwindow.isShowing()) {
                        popwindow.dismiss();
                        popwindow = null;
                    }
                }
            }


        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mExoPlayer != null && (!mExoPlayer.getPlayWhenReady())) {
            mExoPlayer.setPlayWhenReady(true);
        }
        initPercentThreed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mExoPlayer != null && mExoPlayer.getPlayWhenReady()) {
            mExoPlayer.setPlayWhenReady(false);
        }
        if (progressDisposable != null && !progressDisposable.isDisposed()) {
            progressDisposable.dispose();
            progressDisposable = null;
        }
    }

    @Override
    protected void onDestroy() {
        //恢复相关的屏幕设置
        BaseUtil.setBrightness(this, screenLight);
        super.onDestroy();
        getContentResolver().unregisterContentObserver(rotationObserver);
        rotationObserver = null;
        if (mExoPlayer != null) {
            mExoPlayer.clearVideoTextureView(mTextureview);
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
        if (mAudioManager != null) {
            mAudioManager = null;
        }
        CacheManager.getInstance().stopCacheCurrentData();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mVideoFrame.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            mVideoFrame.setLayoutParams(layoutParams);
        } else {
            //竖屏
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mVideoFrame.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = BaseUtil.dp2px(196);
            mVideoFrame.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void onNetworkChange(boolean isConnected, boolean isVpn) {
        super.onNetworkChange(isConnected, isVpn);
        if (isConnected && !hasLoadData && mvBean != null) {
            getMvDetail(mvBean.getMvId());
        }
    }

    private void creatExoPlayer() {
        if (mExoPlayer == null) {
            synchronized (SimpleExoPlayer.class) {
                if (mExoPlayer == null) {
                    VideoCallback mVideoCallback = new VideoCallback();
                    TrackSelection.Factory mTrackFactory = new AdaptiveTrackSelection.Factory();//自适应
                    DefaultTrackSelector   mTrackSelector = new DefaultTrackSelector(mTrackFactory);
                    dataSourceFactory = new DefaultDataSourceFactory(mContext, null, new OkHttpDataSourceFactory(new OkHttpClient(), "mvplayer"));
                    mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, mTrackSelector);
                    mExoPlayer.setVideoTextureView(mTextureview);
                    mExoPlayer.addVideoListener(mVideoCallback);
                    mExoPlayer.addListener(mVideoCallback);
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private void getMvDetail(long mvId) {
        if (mvId == 0) {
            return;
        }
        Observable.zip(
                MvApiManager.getMvDetail(mvId),
                MvApiManager.getSimilarMv(mvId),
                CommentApiManager
                        .getComments("R_MV_5_" + mvId, 0, 50, false),
                new Function3<String, String, String, MvBean>() {
                    @Override
                    public MvBean apply(String s, String s2, String s3) throws Exception {
                        MvBean mvBean = jsonToMvBean(s);
                        mvBean.setSimilarList(jsonToSimilarMv(s2));
                        mvBean.setCommentList(jsonToComment(s3));
                        return mvBean;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<MvBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<MvBean>() {
                    @Override
                    public void accept(MvBean mvBean) throws Exception {
                        hasLoadData = true;
                        updateUI(mvBean);
                    }
                });
    }

    private MvBean jsonToMvBean(String json) {
        MvBean bean = new MvBean();
        bean.setLocalMv(false);
        JSONObject rootObject = JSON.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            JSONObject data = rootObject.getJSONObject("data");
            bean.setMvId(data.getLongValue("id"));
            bean.setMvName(data.getString("name"));
            bean.setArtistName(data.getString("artistName"));
            bean.setArtistId(data.getLongValue("artistId"));
            bean.setShortDes(data.getString("briefDesc"));
            bean.setDescription(data.getString("desc"));
            bean.setCoverImgUrl(data.getString("cover"));
            bean.setPlayCount(data.getIntValue("playCount"));
            bean.setSubCount(data.getIntValue("subCount"));
            bean.setLikeCount(data.getInteger("likeCount"));
            bean.setShareCount(data.getIntValue("shareCount"));
            bean.setCommentCount(data.getIntValue("commentCount"));
            bean.setDurationTime(data.getLongValue("duration"));
            bean.setPublishTime(data.getString("publishTime"));
            List<KeyValueBean> playPaths = new ArrayList<>();
            JSONObject pathArray = data.getJSONObject("brs");
            if (pathArray.containsKey("240")) {
                KeyValueBean valueBean240 = new KeyValueBean();
                valueBean240.setKey("240");
                valueBean240.setValue(pathArray.getString("240"));
                playPaths.add(valueBean240);
            }
            if (pathArray.containsKey("480")) {
                KeyValueBean valueBean480 = new KeyValueBean();
                valueBean480.setKey("480");
                valueBean480.setValue(pathArray.getString("480"));
                playPaths.add(valueBean480);
            }
            if (pathArray.containsKey("720")) {
                KeyValueBean valueBean720 = new KeyValueBean();
                valueBean720.setKey("720");
                valueBean720.setValue(pathArray.getString("720"));
                playPaths.add(valueBean720);
            }
            if (pathArray.containsKey("1080")) {
                KeyValueBean valueBean1080 = new KeyValueBean();
                valueBean1080.setKey("1080");
                valueBean1080.setValue(pathArray.getString("1080"));
                playPaths.add(valueBean1080);
            }
            bean.setPlayPathList(playPaths);
            bean.setMvCommentId(data.getString("commentThreadId"));
        }
        return bean;
    }

    private List<MvBean> jsonToSimilarMv(String json) {
        List<MvBean> list = new ArrayList<>();
        JSONObject rootObject = JSONObject.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            JSONArray mvsArray = rootObject.getJSONArray("mvs");
            int size = mvsArray.size();
            if (size > 0) {
                JSONObject itemObject;
                for (int i = 0; i < size; i++) {
                    itemObject = mvsArray.getJSONObject(i);
                    MvBean mvBean = new MvBean();
                    mvBean.setMvId(itemObject.getLongValue("id"));
                    mvBean.setMvName(itemObject.getString("name"));
                    mvBean.setCoverImgUrl(itemObject.getString("cover"));
                    mvBean.setPlayCount(itemObject.getIntValue("playCount"));
                    mvBean.setDescription(itemObject.getString("briefDesc"));
                    mvBean.setArtistId(itemObject.getLongValue("artistId"));
                    mvBean.setArtistName(itemObject.getString("artistName"));
                    mvBean.setDurationTime(itemObject.getLongValue("duration"));
                    list.add(mvBean);
                }
            }
        }
        return list;
    }

    private List<CommentBean> jsonToComment(String json) {
        List<CommentBean> list = new ArrayList<>();
        JSONObject rootObject = JSONObject.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            boolean hasMoreDate = rootObject.getBoolean("more");
            JSONArray hotCommens = rootObject.getJSONArray("hotComments");
            JSONArray commentsObject = rootObject.getJSONArray("comments");
            int hotSize = hotCommens.size();
            if (hotSize > 0) {
                List<CommentBean> hotCommentList = new ArrayList<>();
                for (int i = 0; i < hotSize; i++) {
                    JSONObject itemObject = hotCommens.getJSONObject(i);
                    String userNAme = itemObject.getJSONObject("user").getString("nickname");
                    String userIMgurl = itemObject.getJSONObject("user").getString("avatarUrl");
                    long userID = itemObject.getJSONObject("user").getLongValue("userId");
                    long commentTime = itemObject.getLongValue("time");
                    int likeCount = itemObject.getIntValue("likedCount");
                    long commentId = itemObject.getLongValue("commentId");
                    String commentContent = itemObject.getString("content");
                    CommentBean bean = new CommentBean();
                    bean.setDescription(commentContent);
                    bean.setLikeCount(likeCount);
                    bean.setCommentId(String.valueOf(commentId));
                    UserBean userBean = new UserBean();
                    userBean.setUserId(userID);
                    userBean.setUserCoverImgUrl(userIMgurl);
                    userBean.setNickName(userNAme);
                    bean.setUserBean(userBean);
                    bean.setCreateTime(commentTime + "");
                    JSONArray replayArray = itemObject.getJSONArray("beReplied");//有可能为空
                    int replaySize = replayArray.size();
                    if (replaySize > 0) {
                        List<CommentBean> replayList = new ArrayList<>();
                        for (int j = 0; j < replaySize; j++) {
                        }
                        bean.setReplayList(replayList);
                    }
                    hotCommentList.add(bean);
                }
                list.addAll(hotCommentList);
            }

            int normalSize = commentsObject.size();
            if (hotSize > 0) {
                List<CommentBean> normalCommentList = new ArrayList<>();
                for (int i = 0; i < normalSize; i++) {
                    JSONObject itemObject = commentsObject.getJSONObject(i);
                    String userNAme = itemObject.getJSONObject("user").getString("nickname");
                    String userIMgurl = itemObject.getJSONObject("user").getString("avatarUrl");
                    long userID = itemObject.getJSONObject("user").getLongValue("userId");
                    long commentTime = itemObject.getLongValue("time");
                    int likeCount = itemObject.getIntValue("likedCount");
                    long commentId = itemObject.getLongValue("commentId");
                    String commentContent = itemObject.getString("content");
                    CommentBean bean = new CommentBean();
                    bean.setDescription(commentContent);
                    bean.setLikeCount(likeCount);
                    bean.setCommentId(String.valueOf(commentId));
                    UserBean userBean = new UserBean();
                    userBean.setUserId(userID);
                    userBean.setUserCoverImgUrl(userIMgurl);
                    userBean.setNickName(userNAme);
                    bean.setUserBean(userBean);
                    bean.setCreateTime(commentTime + "");
                    JSONArray replayArray = itemObject.getJSONArray("beReplied");//有可能为空
                    int replaySize = replayArray.size();
                    if (replaySize > 0) {
                        List<CommentBean> replayList = new ArrayList<>();
                        for (int j = 0; j < replaySize; j++) {
                        }
                        bean.setReplayList(replayList);
                    }
                    normalCommentList.add(bean);
                }
                list.addAll(normalCommentList);
            }
        }
        return list;
    }

    private void updateUI(MvBean bean) {
        this.mvBean = bean;
        mVideoView.setNavTitle(bean.getMvName());
        mVideoView.setTimeTv("0:00", FormatData.timeValueToString(bean.getDurationTime()));
        mVideoView.setProgress(0, bean.getDurationTime().intValue());
        infoAdapter.setMvBean(bean);
        infoAdapter.notifyDataSetChanged();
        similarAdapter.clearDataList();
        similarAdapter.addDataList(bean.getSimilarList());
        similarAdapter.notifyDataSetChanged();
        if (bean.getCommentList().isEmpty()) {
            comSingleAdapter.title = "暂无评论";
            comSingleAdapter.notifyDataSetChanged();
            commentAdapter.clearDataList();
            commentAdapter.notifyDataSetChanged();
        } else {
            commentAdapter.clearDataList();
            commentAdapter.addDataList(bean.getCommentList());
            commentAdapter.notifyDataSetChanged();
        }
        if (bean.getPlayPathList().isEmpty()) {
            return;
        }
        KeyValueBean maxBean = getMaxResolution(bean.getPlayPathList());
        if (maxBean != null) {
            String path = maxBean.getValue();
            String val = maxBean.getKey();
            if (val.equals("240")) {
                mVideoView.setResolutionTv("240P");
            }
            if (val.equals("480")) {
                mVideoView.setResolutionTv("480P");
            }
            if (val.equals("720")) {
                mVideoView.setResolutionTv("720P");
            }
            if (val.equals("1080")) {
                mVideoView.setResolutionTv("1080p");
            }
            path = CacheManager.getInstance().getCacheUrl(path, null);
            if (StringUtil.isEmpty(path)) {
                ToastUtil.showNormalMsg("暂时无法播放该MV");
                return;
            }
            Uri uri = Uri.parse(path);
            ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri);
            if (mExoPlayer != null) {
                mExoPlayer.prepare(mediaSource);
                mExoPlayer.setPlayWhenReady(true);
                initPercentThreed();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rotateScreen();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 旋转屏幕
     */
    private void rotateScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //设置为竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mVideoView.setViewIsExpand(false);
        } else {
            //设置为横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mVideoView.setViewIsExpand(true);
        }
    }

    private void controlPlay() {
        if (mExoPlayer != null) {
            int state = mExoPlayer.getPlaybackState();
            if (state == Player.STATE_READY) {
                if (mExoPlayer.getPlayWhenReady()) {
                    mExoPlayer.setPlayWhenReady(false);
                    mVideoView.setPlayImgSrc(R.drawable.exo_controls_play);
                } else {
                    mExoPlayer.setPlayWhenReady(true);
                    mVideoView.setPlayImgSrc(R.drawable.exo_controls_pause);
                }
            } else if (state == Player.STATE_BUFFERING) {
                //缓冲中
                ToastUtil.showNormalMsg("当前正在缓冲中");
            } else if (state == Player.STATE_ENDED) {
                //播放结束
                mVideoView.setPlayImgSrc(R.drawable.exo_controls_play);
                ToastUtil.showCustomDialog(mContext, "当前MV已经播放完成，是否从新播放", new OnDialogListener() {
                    @Override
                    public void onResult(boolean success) {
                        if (success) {
                            updateUI(mvBean);
                        }
                    }
                });
            }
        }
    }

    private void showShareWindown(MvBean bean) {
        ShareBean shareBean = new ShareBean();
        shareBean.setTitle("MV：" + bean.getMvName());
        shareBean.setWebUrl("http://music.163.com/#/mv?id=" + bean.getMvId());
        shareBean.setText(bean.getDescription());
        shareBean.setImgUrl(bean.getCoverImgUrl());
        shareMusic(mTextureview, shareBean);
    }

    private void showTypePopupMenu() {
        popwindow = new MvQualityPopwindow(mContext);
        popwindow.setItemClickListener(new OnRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mExoPlayer != null) {
                    String path = mvBean.getPlayPathList().get(position).getValue();
                    path = CacheManager.getInstance().getCacheUrl(path, null);
                    Uri uri = Uri.parse(path);
                    ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(uri);
                    if (mExoPlayer.getPlayWhenReady() && mExoPlayer.getPlaybackState() == Player.STATE_READY) {
                        long currentPosition = mExoPlayer.getCurrentPosition();
                        mExoPlayer.prepare(mediaSource);
                        mExoPlayer.setPlayWhenReady(true);
                        mExoPlayer.seekTo(currentPosition);
                    } else {
                        mExoPlayer.prepare(mediaSource);
                        mExoPlayer.setPlayWhenReady(true);
                    }
                }
                String val = mvBean.getPlayPathList().get(position).getKey();
                if (val.equals("240")) {
                    mVideoView.setResolutionTv("240P");
                }
                if (val.equals("480")) {
                    mVideoView.setResolutionTv("480P");
                }
                if (val.equals("720")) {
                    mVideoView.setResolutionTv("720P");
                }
                if (val.equals("1080")) {
                    mVideoView.setResolutionTv("1080p");
                }
                popwindow.dismiss();
            }
        });
        List<String> list = new ArrayList<>();
        if (mvBean != null) {
            for (KeyValueBean bean : mvBean.getPlayPathList()) {
                if (bean.getKey().equals("240")) {
                    list.add("240P");
                }
                if (bean.getKey().equals("480")) {
                    list.add("480P");
                }
                if (bean.getKey().equals("720")) {
                    list.add("720P");
                }
                if (bean.getKey().equals("1080")) {
                    list.add("1080P");
                }
            }
            popwindow.setData(list);
        }
        int height = -((BaseUtil.dp2px(28) * list.size()) + mVideoView.getResolutionTv().getHeight());
        float x = ((float) (BaseUtil.dp2px(74) - mVideoView.getResolutionTv().getWidth())) / 2.0f;
        popwindow.showAsDropDown(mVideoView.getResolutionTv(), (int) -x, height, Gravity.TOP);
    }

    /**
     * 初始化进度条线程
     */
    private void initPercentThreed() {
        if (progressDisposable == null || progressDisposable.isDisposed()) {
            progressDisposable = Observable.interval(200, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            mVideoView.setTimeTv(FormatData.timeValueToString(mExoPlayer.getCurrentPosition()),
                                    FormatData.timeValueToString(mvBean.getDurationTime().intValue()));
                            mVideoView.setProgress((int) mExoPlayer.getCurrentPosition(),
                                    mvBean.getDurationTime().intValue());
                            if (mExoPlayer.getPlaybackState() == Player.STATE_READY &&
                                    mExoPlayer.getPlayWhenReady()) {
                                //正在播放
                                long currentTime = System.currentTimeMillis();
                                if (startTime == 0L) {
                                    startTime = currentTime;
                                } else {
                                    if (currentTime - startTime > 6 * 1000) {
                                        startTime = currentTime;
                                    } else {
                                        mVideoView.setPlayImgSrc(R.drawable.exo_controls_pause);
                                    }
                                }
                            } else {
                                mVideoView.setPlayImgSrc(R.drawable.exo_controls_play);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
        }
    }

    private KeyValueBean getMaxResolution(List<KeyValueBean> resList) {
        KeyValueBean keyValueBean = null;
        List<String> strings = new ArrayList<>();
        for (KeyValueBean bean : resList) {
            strings.add(bean.getKey());
        }
        int position;
        if (strings.contains("1080")) {
            position = strings.indexOf("1080");
            if (resList.size() > 0 && (position >= 0 && position < resList.size())) {
                keyValueBean = resList.get(position);
            }
            return keyValueBean;
        }
        if (strings.contains("720")) {
            position = strings.indexOf("720");
            if (resList.size() > 0 && (position >= 0 && position < resList.size())) {
                keyValueBean = resList.get(position);
            }
            return keyValueBean;
        }
        if (strings.contains("480")) {
            position = strings.indexOf("480");
            if (resList.size() > 0 && (position >= 0 && position < resList.size())) {
                keyValueBean = resList.get(position);
            }
            return keyValueBean;
        }
        if (strings.contains("240")) {
            position = strings.indexOf("240");
            if (resList.size() > 0 && (position >= 0 && position < resList.size())) {
                keyValueBean = resList.get(position);
            }
            return keyValueBean;
        }
        return null;
    }

    private void adjustVolume(float distance) {
        if (mAudioManager == null) {
            return;
        }
        //调节音量
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float percent = distance / (float) mVideoView.getMeasuredHeight();
        float volumeOffsetAccurate = max * percent * 4;
        int volumeOffset = (int) volumeOffsetAccurate;
        if (volumeOffset == 0 && Math.abs(volumeOffsetAccurate) > 0.2f) {
            if (distance > 0) {
                volumeOffset = 1;
            } else {
                volumeOffset = -1;
            }
        }
        currentVolume += volumeOffset;
        if (currentVolume < 0) {
            currentVolume = 0;
        }
        if (currentVolume > max) {
            currentVolume = max;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);

    }

    private void adjuestProgress(float distance, boolean isScroll) {
        if (mExoPlayer == null || mExoPlayer.getPlaybackState() != Player.STATE_READY) {
            if (mTipsTv.getVisibility() != View.VISIBLE) {
                mTipsTv.setVisibility(View.VISIBLE);
            }
            mTipsTv.setText("资源还没有准备好,请稍等片刻");
            return;
        }
        long duration = mExoPlayer.getDuration();
        long position = mExoPlayer.getCurrentPosition();
        float percent = Math.abs(distance) / (float) mVideoView.getMeasuredWidth();
        //毫秒
        float durationOffset = (percent / 0.12f) * 8000;
        long willPosition;
        if (distance > 0) {
            willPosition = (long) (position + durationOffset);
        } else {
            willPosition = (long) (position - durationOffset);
        }
        if (willPosition > duration) {
            willPosition = duration;
        }
        if (willPosition < 0) {
            willPosition = 0;
        }
        if (isScroll) {
            if (mTipsTv.getVisibility() != View.VISIBLE) {
                mTipsTv.setVisibility(View.VISIBLE);
            }
            mTipsTv.setText(FormatData.timeValueToString(willPosition) + "/" + FormatData.timeValueToString(duration));
        } else {
            mTipsTv.setVisibility(View.GONE);
            mExoPlayer.seekTo(willPosition);
        }

    }

}
