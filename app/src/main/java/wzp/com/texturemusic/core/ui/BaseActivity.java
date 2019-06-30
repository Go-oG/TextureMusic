package wzp.com.texturemusic.core.ui;

import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.PopupWindow;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wzp.com.texturemusic.BuildConfig;
import wzp.com.texturemusic.MyApplication;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.albummodule.NetAlbumDetailActivity;
import wzp.com.texturemusic.artistmodule.ui.ArtistDetailActivity;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.RadioBean;
import wzp.com.texturemusic.bean.ShareBean;
import wzp.com.texturemusic.bean.SubCommentBean;
import wzp.com.texturemusic.bean.UserBean;
import wzp.com.texturemusic.common.dialog.EditIDvDialog;
import wzp.com.texturemusic.common.dialog.LoadingDialog;
import wzp.com.texturemusic.common.popwindow.MusicInfoPopwindow;
import wzp.com.texturemusic.common.popwindow.MusicOperationPopwindow;
import wzp.com.texturemusic.common.popwindow.PlayQueuePopwindow;
import wzp.com.texturemusic.common.popwindow.SharePopwindow;
import wzp.com.texturemusic.common.ui.CommentActivity;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.eventlistener.EngineEvent;
import wzp.com.texturemusic.core.service.MediaService;
import wzp.com.texturemusic.dbmodule.DbUtil;
import wzp.com.texturemusic.djmodule.ui.DjDetailActivity;
import wzp.com.texturemusic.interf.OnDeleteListener;
import wzp.com.texturemusic.interf.OnDialogListener;
import wzp.com.texturemusic.interf.OnMusicPopItemListener;
import wzp.com.texturemusic.interf.OnPopItemClick;
import wzp.com.texturemusic.interf.OnRecycleItemClickListener;
import wzp.com.texturemusic.mvmodule.MvDetailActivity;
import wzp.com.texturemusic.usermodule.UserDetailActivity;
import wzp.com.texturemusic.util.DownloadUtil;
import wzp.com.texturemusic.util.LogUtil;
import wzp.com.texturemusic.util.MusicUtil;
import wzp.com.texturemusic.util.ROMUtil;
import wzp.com.texturemusic.util.SPSetingUtil;
import wzp.com.texturemusic.util.ShareUtil;
import wzp.com.texturemusic.util.StringUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by wang on 2016/11/6.
 * 核心基类
 */
public abstract class BaseActivity extends RxAppCompatActivity {
    protected String TAG = this.getClass().getSimpleName();
    protected Context mContext;
    private MediaSessionCompat.Token mToken;
    private MediaControllerCompat mediaController;
    protected MediaControllerCompat.TransportControls transportControls;
    private final BaseMediaSessionCallBack sessionCallBack = new BaseMediaSessionCallBack();
    private MediaService.BaseBinder mBinder;
    protected ServiceConnection mServiceConnect;
    //标识是否初始化服务 默认是
    protected boolean isInitService = true;
    //是否自动添加到管理栈中
    private boolean isAddActivityStack = true;
    private LoadingDialog loadingDialog;
    private MusicOperationPopwindow popwindow;
    protected MusicBean mCurrentData;
    //标识是否加载过数据
    protected boolean hasLoadData = false;
    ///////////////////
    protected PlayQueuePopwindow mPlayQueuePop;

    /**
     * MediaSession(受控端)的回调类
     */
    private class BaseMediaSessionCallBack extends MediaControllerCompat.Callback {
        BaseMediaSessionCallBack() {
            super();
        }

        @Override
        public void onSessionEvent(@NonNull String event, @Nullable Bundle extras) {
            if (extras != null && extras.getClassLoader() == null) {
                extras.setClassLoader(getClass().getClassLoader());
            }
            switch (event) {
                case AppConstant.SERVICE_EVENT_PLAYER_BUFFER:
                    int percent = 0;
                    if (extras != null) {
                        percent = extras.getInt(AppConstant.SERVICE_BUNDLE_PLAYER_BUFFER, 0);
                    }
                    onBufferPercentChange(percent);
                    break;
                case AppConstant.SERVICE_EVENT_PLAY_WARN:
                    //错误信息
                    String errorType = null;
                    String errorHint = null;
                    if (extras != null) {
                        errorType = extras.getString(AppConstant.SERVICE_BUNDLE_WARN_TYPE, "");
                        errorHint = extras.getString(AppConstant.SERVICE_BUNDLE_WARN_HINT, "");
                        handleErrorMsg(errorType, errorHint);
                    }
                    onPlayWarn(errorType, errorHint);
                    break;
                case AppConstant.SERVICE_EVENT_MEDIASESSION_DISCONNECT://服务被销毁
                    onServiceDestroyed();
                    break;
                case AppConstant.SERVICE_EVENT_PLAYER_RESUME://播放器从错误中恢复
                    onPlayerResume();
                    break;
                case AppConstant.SERVICE_EVENT_NETWORK_CHANGE://网络改变
                    if (extras != null) {
                        boolean network = extras.getBoolean(AppConstant.BUNDLE_NETWORK_CHANGE, false);
                        boolean isVpn = extras.getBoolean(AppConstant.BUNDLE_NETWORK_VPN, false);
                        if (isVpn) {
                            ToastUtil.showLongNormalMsg("当前你正在使用代理,有可能会导致无法播放的问题,请关闭你的代理");
                        }
                        onNetworkChange(network, isVpn);
                        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
                        for (Fragment fragment : fragmentList) {
                            if (fragment instanceof BaseFragment) {
                                ((BaseFragment) fragment).onNetworkChange(network, isVpn);
                            }
                        }
                    }
                    break;
                case AppConstant.SERVICE_EVENT_PLAYBACKSTATE://播放状态改变
                    LogUtil.test("播放状态改变 TIME====" + System.currentTimeMillis());
                    if (extras != null) {
                        int state = extras.getInt(AppConstant.SERVICE_BUNDLE_PLAYBACKSTATE, EngineEvent.STATUS_NONE);
                        onPlayStatusChange(state);
                    }
                    break;
                case AppConstant.SERVICE_EVENT_AUDIOSESSION_ID:
                    if (extras != null) {
                        onAudioSessionId(extras.getInt(AppConstant.SERVICE_BUNDLE_AUDIOSESSION_ID, 0));
                    }
                    break;
                case AppConstant.SERVICE_EVENT_MUSIC_CHANGE://数据更换了
                    if (extras != null) {
                        MusicBean bean = extras.getParcelable(AppConstant.SERVICE_BUNDLE_MUSIC_CHANGE);
                        mCurrentData = bean;
                        onMediaDataChange(bean);
                    }
                    break;
                case AppConstant.SERVICE_EVENT_LYRIC_CHANGE:
                    //歌词改变
                    String content;
                    if (mCurrentData != null) {
                        content = mCurrentData.getArtistName() + "-" + mCurrentData.getAlbumName();
                    } else {
                        content = "";
                    }
                    MusicBean bean = null;
                    if (extras != null) {
                        content = extras.getString(AppConstant.SERVICE_BUNDLE_LYRIC_CHANGE, content);
                        bean = extras.getParcelable(AppConstant.SERVICE_BUNDLE_LYRIC_BEAN);
                    }
                    onLyricChange(bean, content);
                    break;
                case AppConstant.SERVICE_EVENT_PROGRESS_CHANGE:
                    if (extras != null) {
                        long currentTime = extras.getLong(AppConstant.SERVICE_BUNDLE_PROGRESS_CURRENT, 0);
                        long durationTime = extras.getLong(AppConstant.SERVICE_BUNDLE_PROGRESS_DURATION, 0);
                        onUpdateProgress((int) currentTime, (int) durationTime);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onSessionDestroyed() {
            //同时代表service也已经被销毁
            if (mediaController != null) {
                mediaController.unregisterCallback(sessionCallBack);
                mToken = null;
                mBinder = null;
            }
            onMediaSessionDestroyed();
        }
    }

    private class BaseServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //连接成功
            mBinder = (MediaService.BaseBinder) service;
            mToken = mBinder.getToken(TAG);
            try {
                mediaController = new MediaControllerCompat(mContext, mToken);
            } catch (RemoteException e) {
                mediaController = null;
            }
            if (mediaController != null) {
                mediaController.registerCallback(sessionCallBack);
                transportControls = mediaController.getTransportControls();
                MusicBean bean = mBinder.getPlayMusic(false);
                mCurrentData = bean;
                onMediaSessionConnect(bean, mBinder.getNowPlayState());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            onServiceDestroyed();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isAddActivityStack) {
            MyApplication.getAppManager().addActivity(this);
        }
        mContext = this;
        if (isInitService) {
            Intent intent = new Intent(mContext, MediaService.class);
            startService(intent);
        }
        initTheme();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isInitService) {
            Intent intent = new Intent(mContext, MediaService.class);
            startService(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isInitService) {
            if (mServiceConnect == null) {
                mServiceConnect = new BaseServiceConnection();
            }
            Intent intent = new Intent(this, MediaService.class);
            bindService(intent, mServiceConnect, BIND_AUTO_CREATE);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mediaController != null && isInitService) {
            mediaController.unregisterCallback(sessionCallBack);
        }
        if (isInitService) {
            if (mServiceConnect != null) {
                unbindService(mServiceConnect);
                mServiceConnect = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getAppManager().finishActivity(this);
        mContext = null;
    }


    /////////////////////////////////////////////////////

    /**
     * 初始化主题
     */
    protected void initTheme() {
        Map<String, Integer> themeMap = new HashMap<>();
        for (int i = 0; i < AppConstant.THEME_NAMES.length; i++) {
            themeMap.put(AppConstant.THEME_NAMES[i], AppConstant.THEME_ID[i]);
        }
        String name = SPSetingUtil.getStringValue(AppConstant.SP_KEY_THEME_NAME, AppConstant.THEME_NAMES[0]);
        setTheme(themeMap.get(name));
    }

    /**
     * 处理错误信息
     *
     * @param errorType 错误类型
     * @param errorHint 错误提示
     */
    private void handleErrorMsg(@NonNull String errorType, @Nullable String errorHint) {
        if (!StringUtil.isEmpty(errorHint)) {
            ToastUtil.showNormalMsg(errorHint);
        }
    }

    public void initService(boolean initService) {
        this.isInitService = initService;
    }

    public void addActivityToCustomStack(boolean addActivityStack) {
        this.isAddActivityStack = addActivityStack;
    }

    /**
     * 以下所有的 protected 方法如果子类需要则可以覆盖
     * 来实现自己的功能
     *
     * @param isConnected
     */
    protected void onNetworkChange(boolean isConnected, boolean isVpn) {

    }

    protected void onLyricChange(MusicBean bean, String lyricChange) {

    }

    //媒体播放数据发生改变
    protected void onMediaDataChange(@NonNull MusicBean bean) {
        updateBottomInfo(bean);
    }

    protected void onPlayStatusChange(int playbackState) {

    }

    protected void onMediaSessionConnect(MusicBean bean, int playStatus) {
        updateBottomInfo(bean);
    }

    protected void onMediaSessionDestroyed() {

    }

    protected void onServiceDestroyed() {

    }

    /**
     * 播放警告并不代表后台播放错误
     */
    protected void onPlayWarn(String warnType, String warnHint) {

    }

    protected void onPlayerResume() {
    }

    protected void onUpdateProgress(int currentTime, int durationTime) {

    }

    //数据缓冲进度
    protected void onBufferPercentChange(int percent) {

    }

    protected void onAudioSessionId(int audioSessionId) {
    }

    public void playMusic() {
        if (transportControls == null) {
            transportControls = mediaController.getTransportControls();
        }
        transportControls.play();
    }

    public void playMusic(Uri uri) {
        if (transportControls == null) {
            transportControls = mediaController.getTransportControls();
        }
        transportControls.playFromUri(uri, null);
    }

    public void playMusic(long index) {
        if (transportControls == null) {
            transportControls = mediaController.getTransportControls();
        }
        if (transportControls != null) {
            transportControls.skipToQueueItem(index);
        }

    }

    /**
     * 只播放单独的一首歌
     */
    public void playMusic(MusicBean bean) {
        if (transportControls != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(AppConstant.MEDIA_BUNDLE_SINGLE_MUSIC, bean);
            transportControls.sendCustomAction(AppConstant.MEDIA_ACTION_PLAY_SINGLE_MUSIC, bundle);
            LogUtil.test("开始播放音乐 TIME====" + System.currentTimeMillis());
        }
    }

    public void playMusicList(List<MusicBean> list, boolean clearOldData) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (clearOldData) {
            clearPlayQueue(true);
        }
        addPlayQueueData((ArrayList<MusicBean>) list);
        int playMode = SPSetingUtil.getIntValue(AppConstant.SP_KEY_PLAY_MODE, AppConstant.PLAY_MODE_LOOP);
        if (playMode == AppConstant.PLAY_MODE_RANDOM) {
            int index = (int) (Math.random() * list.size());
            if (index < 0 || index > list.size()) {
                playMusic(list.get(0));
            } else {
                playMusic(index);
            }
        } else {
            playMusic(0);
        }
    }

    public void clearPlayQueue(boolean includeNextQueue) {
        if (transportControls == null) {
            transportControls = mediaController.getTransportControls();
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(AppConstant.MEDIA_BUNDLE_CLEAR_PLAY_QUEUE, includeNextQueue);
        transportControls.sendCustomAction(AppConstant.MEDIA_ACTION_CLEAR_PLAY_QUEUE, bundle);
    }

    /**
     * 添加播放队列的数据
     * 对于数据超过200的list进行分批次传输
     * 解决Bundle 限制数据大小在1Mb的问题
     *
     * @param list 要添加的数据
     */
    public void addPlayQueueData(ArrayList<MusicBean> list) {
        int size = list.size();
        if (size <= 200) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(AppConstant.MEDIA_BUNDLE_PLAYQUEUE, list);
            bundle.putBoolean(AppConstant.MEDIA_BUNDLE_AUTOCLEAR_DATA, true);
            if (transportControls != null) {
                transportControls.sendCustomAction(AppConstant.MEDIA_ACTION_PLAY_QUEUE, bundle);
            }
        } else {
            //数据超过200
            int count = size / 200;
            if (count < 1) {
                //在200 到400之间
                if (transportControls != null) {
                    ArrayList<MusicBean> beanArrayList = new ArrayList<>();
                    for (int i = 0; i <= 200; i++) {
                        beanArrayList.add(list.get(i));
                    }
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(AppConstant.MEDIA_BUNDLE_PLAYQUEUE, beanArrayList);
                    bundle.putBoolean(AppConstant.MEDIA_BUNDLE_AUTOCLEAR_DATA, true);
                    transportControls.sendCustomAction(AppConstant.MEDIA_ACTION_PLAY_QUEUE, bundle);
                    bundle.clear();
                    beanArrayList.clear();
                    for (int i = 201; i < size; i++) {
                        beanArrayList.add(list.get(i));
                    }
                    bundle.putParcelableArrayList(AppConstant.MEDIA_BUNDLE_PLAYQUEUE, beanArrayList);
                    bundle.putBoolean(AppConstant.MEDIA_BUNDLE_AUTOCLEAR_DATA, false);
                    transportControls.sendCustomAction(AppConstant.MEDIA_ACTION_PLAY_QUEUE, bundle);
                }
            } else {
                for (int i = 1; i <= count; i++) {
                    Bundle bundle = new Bundle();
                    ArrayList<MusicBean> dataLit = new ArrayList<>();
                    for (int j = (i - 1) * 200; j < i * 200; j++) {
                        dataLit.add(list.get(j));
                    }
                    if (i == 1) {
                        bundle.putBoolean(AppConstant.MEDIA_BUNDLE_AUTOCLEAR_DATA, true);
                    } else {
                        bundle.putBoolean(AppConstant.MEDIA_BUNDLE_AUTOCLEAR_DATA, false);
                    }
                    bundle.putParcelableArrayList(AppConstant.MEDIA_BUNDLE_PLAYQUEUE, dataLit);
                    transportControls.sendCustomAction(AppConstant.MEDIA_ACTION_PLAY_QUEUE, bundle);
                }
                ArrayList<MusicBean> arrayList = new ArrayList<>();
                for (int j = count * 200; j < size; j++) {
                    arrayList.add(list.get(j));
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean(AppConstant.MEDIA_BUNDLE_AUTOCLEAR_DATA, false);
                bundle.putParcelableArrayList(AppConstant.MEDIA_BUNDLE_PLAYQUEUE, arrayList);
                transportControls.sendCustomAction(AppConstant.MEDIA_ACTION_PLAY_QUEUE, bundle);
            }
        }

    }

    public void pauseMusic() {
        if (transportControls == null) {
            transportControls = mediaController.getTransportControls();
        }
        if (transportControls != null) {
            transportControls.pause();
        }

    }

    public void resumeMusic() {
        if (transportControls == null) {
            transportControls = mediaController.getTransportControls();
        }
        if (transportControls != null) {
            transportControls.play();
        }

    }

    /**
     * 拖动进度条
     */
    public void seekToPosition(long pos) {
        if (transportControls == null) {
            transportControls = mediaController.getTransportControls();
        }
        if (transportControls != null) {
            transportControls.seekTo(pos);
        }
    }

    public void stopMusic() {
        if (transportControls == null) {
            transportControls = mediaController.getTransportControls();
        }
        if (transportControls != null) {
            transportControls.stop();
        }

    }

    public void nextMusic() {
        if (transportControls == null) {
            transportControls = mediaController.getTransportControls();
        }
        if (transportControls != null) {
            transportControls.skipToNext();
        }
    }

    public void lastMusic() {
        if (transportControls == null) {
            transportControls = mediaController.getTransportControls();
        }
        if (transportControls != null) {
            transportControls.skipToPrevious();
        }
    }

    /**
     * 将歌曲从播放队列移除
     *
     * @param includeNextQueue 是否包括下一曲队列里面的
     */
    public void removeMusic(MusicBean bean, boolean includeNextQueue) {
        if (transportControls == null) {
            transportControls = mediaController.getTransportControls();
        }
        if (transportControls != null) {
            //移除歌曲
            Bundle bundle = new Bundle();
            bundle.putParcelable(AppConstant.MEDIA_BUNDLE_REMOVE_MUSIC, bean);
            bundle.putBoolean(AppConstant.MEDIA_BUNDLE_INCLUDE_NEXTQUEUE, includeNextQueue);
            transportControls.sendCustomAction(AppConstant.MEDIA_ACTION_REMOVE_MUSIC, bundle);
        }
    }

    /**
     * 更新当前的播放数据是否为收藏的数据
     */
    public void updateCurrentDataForLiked(boolean isLiked) {
        if (transportControls != null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(AppConstant.MEDIA_BUNDLE_LIKED, isLiked);
            transportControls.sendCustomAction(AppConstant.MEDIA_ACTION_UPDATE_LIKED, bundle);
        }
    }

    /**
     * @param playType PLAYTYPE_LISTLOOP = 2;//列表循环播放
     *                 PLAYTYPE_ONLYLOOP = 3;//单曲循环
     *                 PLAYTYPE_INORDER = 4;//顺序播放 默认模式
     *                 PLAYTYPE_RANDOM = 5;//随机播放
     *                 第一步 写入Sp文件
     *                 第二部  给service发送自定义事件
     */
    public void setPlayMode(int playType) {
        SPSetingUtil.setIntValue(AppConstant.SP_KEY_PLAY_MODE, playType);
        if (transportControls == null) {
            if (mediaController != null) {
                transportControls = mediaController.getTransportControls();
            }
        }
        if (transportControls != null) {
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstant.MEDIA_BUNDLE_PLAYMODE, playType);
            transportControls.sendCustomAction(AppConstant.MEDIA_ACTION_UPDATE_PLAYMODE, bundle);
        }
    }

    /**
     * 获取SP文件存储的播放模式
     */
    public int getPlayMode() {
        return SPSetingUtil.getIntValue(AppConstant.SP_KEY_PLAY_MODE, AppConstant.PLAY_MODE_LOOP);
    }

    public List<MusicBean> getPlayListQueue(boolean includeNextQueue) {
        if (mBinder != null) {
            return mBinder.getListData(includeNextQueue);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 添加下一曲播放
     */
    public void addNextMusic(ArrayList<MusicBean> list) {
        if (transportControls != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(AppConstant.MEDIA_BUNDLE_ADD_NEXT_MUSIC, list);
            transportControls.sendCustomAction(AppConstant.MEDIA_ACTION_ADD_NEXT_MUSIC, bundle);
            if (getNowPlayState() != EngineEvent.STATUS_PLAYING) {
                nextMusic();
            }
        }
    }

    /**
     * 获取现在正在播放的音乐信息
     * 可能为空
     */
    public MusicBean getPlayMusic(boolean onlyCurrentMusic) {
        if (onlyCurrentMusic) {
            return mCurrentData;
        } else {
            return mBinder.getPlayMusic(false);
        }
    }

    public int getNowPlayState() {
        if (mBinder != null) {
            return mBinder.getNowPlayState();
        } else {
            return EngineEvent.STATUS_NONE;
        }
    }

    public int getAudioSessionId() {
        if (mBinder != null) {
            return mBinder.getAudioSessionId();
        } else {
            return 0;
        }
    }

    public void setBackgroundAlpha(float startalpha, float endAlpha, long duration) {
        ValueAnimator animator = ValueAnimator.ofFloat(startalpha, endAlpha)
                .setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.alpha = (float) animation.getAnimatedValue();
            getWindow().setAttributes(layoutParams);
        });
        animator.start();
    }

    public void showMusicInfoPop(final View v, MusicBean bean, final OnDeleteListener deleteListener) {
        popwindow = new MusicOperationPopwindow(mContext, bean, new OnMusicPopItemListener() {
            @Override
            public void onShare(MusicBean bean) {
                if (popwindow != null && popwindow.isShowing()) {
                    popwindow.dismiss();
                }
                ShareBean shareBean = new ShareBean();
                shareBean.setTitle(bean.getMusicName());
                shareBean.setImgUrl(bean.getCoverImgUrl());
                shareBean.setImgpath(bean.getCoverImgUrl());
                if (!bean.getLocalMusic()) {
                    shareBean.setFileUrl("http://music.163.com/#/song?id=" + bean.getMusicId());
                }
                shareBean.setWebUrl("http://music.163.com/#/song?id=" + bean.getMusicId());
                shareBean.setText(bean.getArtistName() + "-" + bean.getAlbumName());
                shareMusic(v, shareBean);
            }

            @Override
            public void onDownload(MusicBean bean) {
                if (popwindow != null && popwindow.isShowing()) {
                    popwindow.dismiss();
                }
                if (!bean.getLocalMusic()) {
                    DownloadUtil.downloadMusic(mContext, bean);
                    ToastUtil.showNormalMsg("开始下赞:" + bean.getMusicName() + "-" + bean.getArtistName());
                } else {
                    ToastUtil.showNormalMsg("不能下载本地歌曲");
                }

            }

            @Override
            public void onDelete(MusicBean bean) {
                if (popwindow != null && popwindow.isShowing()) {
                    popwindow.dismiss();
                }
                if (bean.getLocalMusic()) {
                    deleteLocalMusic(bean, deleteListener);
                } else {
                    ToastUtil.showNormalMsg("不能删除网络歌曲");
                }

            }

            @Override
            public void onSetAlarm(MusicBean bean) {
                if (popwindow != null && popwindow.isShowing()) {
                    popwindow.dismiss();
                }
                if (bean.getLocalMusic()) {
                    setRing(bean.getPlayPath(), bean.getMusicName() + "-" + bean.getArtistName());
                } else {
                    ToastUtil.showNormalMsg("网络歌曲不能设置为闹钟铃声");
                }

            }

            @Override
            public void onNextPlay(MusicBean bean) {
                if (popwindow != null && popwindow.isShowing()) {
                    popwindow.dismiss();
                }
                ArrayList<MusicBean> data = new ArrayList<>();
                data.add(bean);
                addNextMusic(data);
                ToastUtil.showNormalMsg("添加成功");
            }

            @Override
            public void onCommont(MusicBean bean) {
                if (popwindow != null && popwindow.isShowing()) {
                    popwindow.dismiss();
                }
                toCommentActivity(bean);
            }

            @Override
            public void onArtist(MusicBean bean) {
                if (popwindow != null && popwindow.isShowing()) {
                    popwindow.dismiss();
                }
                toArtistDetailActivity(bean);
            }

            @Override
            public void onAlbum(MusicBean bean) {
                if (popwindow != null && popwindow.isShowing()) {
                    popwindow.dismiss();
                }
                toAlbumDetailActivity(bean);
            }

            @Override
            public void onMv(MusicBean bean) {
                if (popwindow != null && popwindow.isShowing()) {
                    popwindow.dismiss();
                }
                toMvDetailActivity(bean);

            }

            @Override
            public void onMusicInfo(final MusicBean bean) {
                if (popwindow != null && popwindow.isShowing()) {
                    popwindow.dismiss();
                }
                if (bean.getLocalMusic()) {
                    MusicInfoPopwindow infoPopwindow = new MusicInfoPopwindow(mContext, bean);
                    infoPopwindow.setOnDismissListener(() -> setBackgroundAlpha(0.5f, 1f, 200));
                    infoPopwindow.setPopItemClick(position -> {
                        EditIDvDialog dvDialog = new EditIDvDialog();
                        dvDialog.setMusicBean(bean);
                        dvDialog.show(getFragmentManager(), "dvDialog");
                    });
                    setBackgroundAlpha(1f, 0.5f, 200);
                    infoPopwindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                }
            }
        });
        popwindow.setOnDismissListener(() -> setBackgroundAlpha(0.5f, 1f, 200));
        setBackgroundAlpha(1, 0.5f, 200);
        popwindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 显示播放列表
     */
    public void showMusicListPopwindow(View view, List<MusicBean> list, PlayQueuePopwindow.OnPopClickListener listener) {
        if (list == null || list.isEmpty()) {
            ToastUtil.showNormalMsg("暂无播放队列");
        } else {
            mPlayQueuePop = new PlayQueuePopwindow(mContext, list);
            mPlayQueuePop.setPopClickListener(listener);
            mPlayQueuePop.setOnDismissListener(() -> {
                setBackgroundAlpha(0.5f, 1f, 200);
                mPlayQueuePop = null;
            });
            setBackgroundAlpha(1f, 0.5f, 200);
            mPlayQueuePop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }
    }

    public void shareMusic(View view, final MusicBean bean) {
        ShareBean shareBean = new ShareBean();
        shareBean.setTitle(bean.getMusicName());
        shareBean.setText(bean.getArtistName() + "-" + bean.getAlbumName());
        if (bean.getLocalMusic()) {
            shareBean.setImgpath(bean.getCoverImgUrl());
            shareBean.setFileUrl(bean.getPlayPath());
        } else {
            shareBean.setImgUrl(bean.getCoverImgUrl());
            shareBean.setWebUrl("http://music.163.com/#/song?id=" + bean.getMusicId());
        }
        shareMusic(view, shareBean);
    }

    public void shareMusic(View view, final ShareBean bean) {
        if (bean != null) {
            SharePopwindow popwindow = new SharePopwindow(mContext);
            popwindow.setItemClickListener((view1, position) -> {
                switch (view1.getId()) {
                    case R.id.share_wx_frends_circle:
                        ShareUtil.shareForWxFriendCircle(bean);
                        break;
                    case R.id.share_wx_friends:
                        ShareUtil.shareForWxFriend(bean);
                        break;
                    case R.id.share_qq_zone:
                        ShareUtil.shareForQQZone(bean);
                        break;
                    case R.id.share_qq:
                        ShareUtil.shareForQQFriend(bean);
                        break;
                    case R.id.share_wb:
                        ShareUtil.shareForWeibo(bean);
                        break;
                    case R.id.share_db:
                        ShareUtil.shareForDb(bean);
                        break;
                    case R.id.share_more:
                        ShareUtil.shareForIntent(mContext, bean);
                        break;
                }
            });
            popwindow.setOnDismissListener(() -> setBackgroundAlpha(0.5f, 1f, 200));
            setBackgroundAlpha(1f, 0.5f, 200);
            popwindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        } else {
            ToastUtil.showNormalMsg("分享内容不能为空");
        }
    }

    protected void toCommentActivity(MusicBean bean) {
        if (bean == null || bean.getLocalMusic()) {
            ToastUtil.showNormalMsg("暂无该歌曲评论信息");
            return;
        }
        String commentId = bean.getCommentId();
        LogUtil.d(TAG, "CommentId=" + commentId + "  musicId=" + bean.getMusicId() + " subCommentId=" + bean.getSubCommentId());
        SubCommentBean sbean = new SubCommentBean();
        if (TextUtils.isEmpty(commentId) && bean.getSubCommentId() != null) {
            if (bean.getMusicOrigin() != null && bean.getMusicOrigin() == AppConstant.MUSIC_ORIGIN_DJ) {
                commentId = "R_DJ_1_" + bean.getSubCommentId();
                sbean.setCommentType(AppConstant.COMMENT_TYPE_DJ);
            } else {
                commentId = "R_SO_4_" + bean.getSubCommentId();
            }
        }
        if (StringUtil.isEmpty(commentId)) {
            ToastUtil.showNormalMsg("暂无该歌曲评论信息");
            return;
        }
        sbean.setCommentType(AppConstant.COMMENT_TYPE_MUSIC);
        sbean.setCommentId(commentId);
        sbean.setCoverImgUrl(bean.getCoverImgUrl());
        sbean.setTitle(bean.getMusicName());
        sbean.setSubTitle(bean.getAlbumName() + "-" + bean.getArtistName());
        sbean.setMusicBean(bean);
        Intent intent = new Intent(mContext, CommentActivity.class);
        intent.putExtra(AppConstant.UI_INTENT_KEY_COMMENT, sbean);
        startActivity(intent);
    }

    protected void toArtistDetailActivity(MusicBean bean) {
        if (bean == null) {
            ToastUtil.showNormalMsg("暂时无法获取歌手信息");
            return;
        }
        if (bean.getMusicOrigin() != null && bean.getMusicOrigin() == AppConstant.MUSIC_ORIGIN_DJ) {
            //是电台歌曲
            UserBean userBean = new UserBean();
            userBean.setUserId(Long.valueOf(bean.getArtistId()));
            userBean.setNickName(bean.getArtistName());
            userBean.setUserCoverImgUrl(bean.getArtistImgUrl());
            Intent intent = new Intent(mContext, UserDetailActivity.class);
            intent.putExtra(AppConstant.UI_INTENT_KEY_USER, userBean);
            startActivity(intent);
            return;
        }
        if (!TextUtils.isEmpty(bean.getArtistId())) {
            String className = BaseActivity.this.getClass().getSimpleName();
            if (className.equals(ArtistDetailActivity.class.getSimpleName())) {
                ArtistDetailActivity artistDetailActivity = (ArtistDetailActivity) BaseActivity.this;
                artistDetailActivity.mvViewpager.setCurrentItem(3, true);
            } else {
                ArtistBean artistBean = new ArtistBean();
                artistBean.setArtistId(bean.getArtistId());
                artistBean.setArtistName(bean.getArtistName());
                artistBean.setLocalArtist(false);
                String artistImgUrl = bean.getArtistImgUrl();
                if (TextUtils.isEmpty(artistImgUrl)) {
                    artistImgUrl = bean.getCoverImgUrl();
                    if (TextUtils.isEmpty(artistImgUrl)) {
                        artistImgUrl = bean.getAlbumImgUrl();
                    }
                }
                artistBean.setArtistImgUrl(artistImgUrl);
                Intent intent = new Intent(mContext, ArtistDetailActivity.class);
                intent.putExtra(AppConstant.UI_INTENT_KEY_ARTIST, artistBean);
                startActivity(intent);
            }
        } else {
            ToastUtil.showNormalMsg("暂时无法获取歌手信息");
        }
    }

    protected void toAlbumDetailActivity(MusicBean bean) {
        if (bean == null) {
            ToastUtil.showNormalMsg("暂时无法获取专辑信息");
            return;
        }
        if (bean.getMusicOrigin() != null && bean.getMusicOrigin() == AppConstant.MUSIC_ORIGIN_DJ) {
            //是电台歌曲
            Intent intent = new Intent(mContext, DjDetailActivity.class);
            RadioBean radioBean = new RadioBean();
            radioBean.setRadioId(bean.getAlbumId());
            radioBean.setCoverImgUrl(bean.getAlbumImgUrl());
            radioBean.setRadioName(bean.getAlbumName());
            intent.putExtra(AppConstant.UI_INTENT_KEY_DJ, radioBean);
            startActivity(intent);
            return;
        }
        if (!TextUtils.isEmpty(bean.getAlbumId())) {
            String className = BaseActivity.this.getClass().getSimpleName();
            if (className.equals(NetAlbumDetailActivity.class.getSimpleName())) {
                ToastUtil.showNormalMsg("不能再跳转了");
            } else {
                AlbumBean albumBean = new AlbumBean();
                albumBean.setLocalAlbum(false);
                albumBean.setAlbumName(bean.getAlbumName());
                albumBean.setAlbumId(bean.getAlbumId());
                String albumImgUrl = TextUtils.isEmpty(bean.getAlbumImgUrl()) ? bean.getCoverImgUrl() : bean.getAlbumImgUrl();
                albumBean.setAlbumImgUrl(albumImgUrl);
                Intent intent = new Intent(mContext, NetAlbumDetailActivity.class);
                intent.putExtra(AppConstant.UI_INTENT_KEY_ALBUM, albumBean);
                startActivity(intent);
            }
        } else {
            ToastUtil.showNormalMsg("暂时无法获取歌手信息");
        }
    }

    protected void toMvDetailActivity(MusicBean bean) {
        if (bean == null || bean.getMvBean() == null || bean.getMvBean().getMvId() == null) {
            ToastUtil.showNormalMsg("暂无MV");
            return;
        }
        Intent intent = new Intent(mContext, MvDetailActivity.class);
        intent.putExtra(AppConstant.UI_INTENT_KEY_MV, bean.getMvBean().getMvId());
        startActivity(intent);
    }

    public void showLoadingView(boolean cancleAble) {
        loadingDialog = new LoadingDialog();
        loadingDialog.setCancelable(cancleAble);
        loadingDialog.show(getSupportFragmentManager(), "loading");
    }

    public void cancleLoadingView() {
        if (loadingDialog != null && loadingDialog.isAdded()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    /**
     * 设置铃声
     *
     * @param path  下载下来的mp3全路径
     * @param title 铃声的名字
     */
    public void setRing(String path, String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(mContext)) {
                ToastUtil.showCustomDialog(mContext, "请到权限设置界面赋予APP修改设置的权限", success -> {
                    if (success) {
                        gotoAppInfo();
                    }
                });
            } else {
                setRealRing(path, title);
            }
        } else {
            setRealRing(path, title);
        }
    }

    /**
     * 设置闹钟铃声
     *
     * @param path
     * @param title
     */
    private void setRealRing(String path, String title) {
        File sdfile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, title);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
        Uri newUri = null;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
            getContentResolver().delete(uri,
                    MediaStore.MediaColumns.DATA + "=\"" + sdfile.getAbsolutePath() + "\"", null);
            newUri = getContentResolver().insert(uri, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (newUri != null) {
            Uri setAlarmUri;
            setAlarmUri = newUri;
            RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_ALARM, setAlarmUri);
        }
        ToastUtil.showNormalMsg("设置成功");
    }

    /**
     * 去APP的信息界面
     */
    private void gotoAppInfo() {
        if (ROMUtil.isMiui()) {
            //小米
            try {
                // MIUI 8
                Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", getPackageName());
                startActivity(localIntent);
            } catch (Exception e) {
                try {
                    // MIUI 5/6/7
                    Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                    localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                    localIntent.putExtra("extra_pkgname", getPackageName());
                    startActivity(localIntent);
                } catch (Exception e1) {
                    // 否则跳转到应用详情
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        } else if (ROMUtil.isEmui()) {
            //华为
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
            intent.setComponent(comp);
            startActivity(intent);
        } else if (ROMUtil.isFlyme()) {
            //魅族
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            startActivity(intent);
        } else if (ROMUtil.is360()) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
            intent.setComponent(comp);
            startActivity(intent);
        } else if (ROMUtil.isOppo()) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
            intent.setComponent(comp);
            startActivity(intent);
        } else if (ROMUtil.isVivo()) {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(localIntent);
        } else if (ROMUtil.isSmartisan()) {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(localIntent);
        } else {

        }
    }

    protected int getMainColor() {
        int mainColor;
        TypedArray array = obtainStyledAttributes(new int[]{
                R.attr.main_color
        });
        mainColor = array.getColor(0, Color.parseColor("#6e75a4"));
        array.recycle();
        return mainColor;
    }

    /**
     * 删除本地音乐
     * 进行数据库的更新
     * 同时有可能还需要刷新数据
     *
     * @param bean
     */
    protected void deleteLocalMusic(final MusicBean bean, final OnDeleteListener deleteListener) {
        ToastUtil.showCustomDialog(mContext, "确定要删除" + bean.getMusicName() + "?", new OnDialogListener() {
            @Override
            public void onResult(boolean success) {
                if (success && bean.getLocalMusic()) {
                    File file = new File(bean.getPlayPath());
                    if (file.exists() && file.isFile() && file.delete()) {
                        DbUtil.getMusicDao().delete(DbUtil.musicBeanToDbMusicEntiy(bean));
                        MusicUtil.updateMediaDb(mContext, bean.getPlayPath(), null);
                        ToastUtil.showNormalMsg("删除成功");
                        if (deleteListener != null) {
                            deleteListener.onDelete();
                        }
                    } else {
                        ToastUtil.showNormalMsg("暂时无法删除该歌曲");
                    }
                }
            }
        });
    }

    private boolean serviceIsRunning(Context context, String serviceName) {
        if (!TextUtils.isEmpty(serviceName) && context != null) {
            ActivityManager activityManager
                    = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ArrayList<ActivityManager.RunningServiceInfo> runningServiceInfoList
                    = (ArrayList<ActivityManager.RunningServiceInfo>) activityManager.getRunningServices(100);
            for (Iterator<ActivityManager.RunningServiceInfo> iterator = runningServiceInfoList.iterator(); iterator.hasNext(); ) {
                ActivityManager.RunningServiceInfo runningServiceInfo = iterator.next();
                if (serviceName.equals(runningServiceInfo.service.getClassName().toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void updateBottomInfo(MusicBean bean) {
    }

    protected void setStatusBarHeight(int hight) {
    }

    protected void setStatusBarColor(int color) {
    }

}
