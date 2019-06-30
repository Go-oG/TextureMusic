package wzp.com.texturemusic.core.manger;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import androidx.annotation.NonNull;
import android.support.v4.media.RatingCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.eventlistener.DataCacheListener;
import wzp.com.texturemusic.core.eventlistener.EngineEvent;
import wzp.com.texturemusic.core.eventlistener.MediaServiceEventListener;
import wzp.com.texturemusic.dbmodule.bean.DbCacheEntiy;
import wzp.com.texturemusic.dbmodule.util.DbCacheUtil;
import wzp.com.texturemusic.dbmodule.util.DbCollectUtil;
import wzp.com.texturemusic.util.LogUtil;
import wzp.com.texturemusic.util.MusicUtil;
import wzp.com.texturemusic.util.NetWorkUtil;
import wzp.com.texturemusic.util.SPSetingUtil;
import wzp.com.texturemusic.util.StringUtil;

/**
 * Created by Wang on 2017/5/27.
 * 最核心的中间代理类
 * 负责统筹整个后台的控制行为
 * 主要起一个协调作用
 */
public class ControlManager implements DataCacheListener {
    private static final String TAG = "ControlManager";
    private final Object object = new Object();
    private MediaServiceEventListener mServiceCallback;
    private volatile ExoPlayerEngineManager mEngineManager;
    private volatile DataManager mDataManager;
    private volatile PlayerListener mPlayerListener;
    //是否能够联网 受到设置的影响 默认为true
    private boolean canConnectNetwork = true;
    private volatile static ControlManager instance;

    public static ControlManager getInstance() {
        if (instance == null) {
            synchronized (ControlManager.class) {
                if (instance == null) {
                    instance = new ControlManager();
                }
            }
        }
        return instance;
    }

    public void setServiceCallback(MediaServiceEventListener serviceCallback) {
        this.mServiceCallback = serviceCallback;
    }

    private ControlManager() {
        canConnectNetwork = netWorkCanConnection();
        createPlayerManagerIfNeed();
    }

    private boolean netWorkCanConnection() {
        SharedPreferences sp = SPSetingUtil.getSettingSP();
        //仅WiFi下联网 true
        boolean wifi = sp.getBoolean(AppConstant.SP_KEY_WIFI, true);
        //是否可以用非WiFi网络进行网络播放  默认为true
        boolean g2play = sp.getBoolean(AppConstant.SP_KEY_2G_PLAY, true);
        boolean nowIsWifi = (NetWorkUtil.getNetworkType() == NetWorkUtil.NetworkType.NETWORK_WIFI);
        boolean isVpn = NetWorkUtil.isVpnUsed();
        if (isVpn) {
            return false;
        } else {
            return (wifi ? nowIsWifi : g2play);
        }
    }

    public void setCanConnectNetwork(boolean canConnectNetwork) {
        this.canConnectNetwork = canConnectNetwork;
    }

    /////////////////////DataManager事件回调///////////////////////
    @Override
    public void onCacheProgress(float percent, File cacheFile, String url) {
        if (mServiceCallback != null) {
            mServiceCallback.onBufferingUpdate((int) percent);
        }
    }

    @Override
    public void onDataManagerIsCreate(MusicBean data) {

    }

    /////////////////////DataManager事件回调结束///////////////////////

    /**
     * 播放器的回调
     */
    private class PlayerListener implements EngineEvent {
        @Override
        public void onPlayStatusChanged(int state) {
            mServiceCallback.onPlayStatusChanged(state);
            if (state == EngineEvent.STATUS_END) {
                //准备开始播放下一曲
                onSkipToNext();
            }
        }

        @Override
        public void onPlayEngineError(int error) {
            switch (error) {
                case EngineEvent.PLAYER_ERROR_SOUCE:
                    //资源错误 播放下一曲//IO异常
                    onSkipToNext();
                    break;
                case EngineEvent.PLAYER_ERROR_INTERNAL:
                    if (mServiceCallback != null) {
                        mServiceCallback.onError(AppConstant.SERVICE_WARN_REDER_ERROR, "播放错误，已自动播放下一曲");
                    }
                    onSkipToNext();
                    break;
                case EngineEvent.PLAYER_ERROR_OTHER:
                    //未知错误
                    if (mEngineManager != null) {
                        mEngineManager.release();
                        mEngineManager = null;
                    }
                    createPlayerManagerIfNeed();
                    mServiceCallback.onError(AppConstant.SERVICE_WARN_NO_REASON, "未知错误");
                    break;
            }
        }

        @Override
        public void onRequestAudioFouceFail() {
            onSkipToNext();
        }

        @Override
        public void onAudioSessionIdChange(int audioSessionId) {
            if (mServiceCallback != null) {
                mServiceCallback.onAudioSessionIdChange(audioSessionId);
            }
        }

        @Override
        public void onLyricChange(MusicBean bean, long currentTime, String currentLyric) {
            if (mServiceCallback != null) {
                mServiceCallback.onLyricsChange(bean, currentTime, currentLyric);
            }
        }

        @Override
        public void onPlayError(String errorMsg) {
            if (mServiceCallback != null) {
                mServiceCallback.onError(errorMsg, "播放错误");
                if (errorMsg.equals(AppConstant.SERVICE_WARN_MUSIC_URL_IS_EMPTY)) {
                    onSkipToNext();
                }
            }
        }

        @Override
        public void onUpdatePregress(Long currentTime, Long durationTime) {
            if (mServiceCallback != null) {
                mServiceCallback.onUpdateProgress(currentTime, durationTime);
            }
        }


    }

    /**
     * 播放音乐
     * 空指针处理
     */
    public void onPlay() {
        if (mEngineManager.getPlayStatus() == EngineEvent.STATUS_PAUSE) {
            onResumeMusic();
        } else {
            MusicBean bean = mDataManager.getPlayMusic(false);
            if (bean != null) {
                startPlay(bean);
            } else {
                mServiceCallback.onError(AppConstant.SERVICE_WARN_NO_MUSIC_DATA, "没有播放数据了,试试添加播放数据");
            }
        }
    }

    /**
     * 从特定数据源播放数据
     */
    public void onPlayFromUri(Uri uri, Bundle extras) {
        createPlayerManagerIfNeed();
        if (uri == null) {
            mServiceCallback.onError(AppConstant.SERVICE_WARN_NO_MUSIC_DATA, "无法播放当前歌曲");
            onSkipToNext();
            return;
        }
        String path = uri.getPath();
        MusicBean bean = new MusicBean();
        if (path.startsWith("http")) {
            bean.setPlayPath(path);
            bean.setProxyUrl(path);
            bean.setLocalMusic(false);
            bean.setAlbumName("");
            bean.setArtistName("");
            bean.setAlbumId("");
            bean.setCoverImgUrl("");
        } else {
            //本地音乐
            bean = MusicUtil.queryLoaclMusic(uri);
            if (bean != null) {
                bean.setProxyUrl(bean.getPlayPath());
            }
        }
        if (bean != null) {
            mEngineManager.playMusic(bean);
            queryAndUpdateData(bean);
        } else {
            mServiceCallback.onError(AppConstant.SERVICE_WARN_MUSIC_URL_IS_EMPTY, "无法播放该歌曲");
            onSkipToNext();
        }
    }

    /**
     * 播放 在播放队列Index位置的音乐
     */
    public void onSkipToQueueItem(final long index) {
        MusicBean bean = mDataManager.getMusicAtQueue((int) index);
        if (bean != null) {
            startPlay(bean);
        } else {
            mServiceCallback.onError(AppConstant.SERVICE_WARN_NO_MUSIC_DATA, "没有播放数据了,试试添加数据");
            onSkipToNext();
        }
    }

    public void onPause() {
        createPlayerManagerIfNeed();
        mEngineManager.pauseMusic();
    }

    public void onResumeMusic() {
        createPlayerManagerIfNeed();
        mEngineManager.resumeMusic();
    }

    /**
     * 准备播放下一曲
     */
    public void onSkipToNext() {
        LogUtil.test("onSkipToNext()  开始播放下一曲数据");
        createPlayerManagerIfNeed();
        MusicBean bean = mDataManager.getNextMusic();
        if (bean == null) {
            //已经没有下一曲的数据了
            if (mEngineManager.getPlayStatus() == EngineEvent.STATUS_PLAYING) {
                //没有数据但还是在播放
                mServiceCallback.onError(AppConstant.SERVICE_WARN_NO_MUSIC_DATA, "暂无下一曲数据");
            } else {
                mServiceCallback.onError(AppConstant.SERVICE_WARN_NO_MUSIC_DATA, "暂无下一曲数据");
                //onStop();
                mServiceCallback.onPlayStatusChanged(EngineEvent.STATUS_COMPLET);
            }
            return;
        }
        LogUtil.test("onSkipToNext()  播放下一曲数据");
        startPlay(bean);
    }

    /**
     * 上一曲
     */
    public void onSkipToPrevious() {
        MusicBean bean = mDataManager.getLastMusic();
        if (bean != null) {
            startPlay(bean);
        } else {
            //已经没有下一曲的数据了
            mServiceCallback.onError(AppConstant.SERVICE_WARN_NO_MUSIC_DATA, "暂无上一曲数据,试试添加数据");
        }

    }

    /**
     * 停止播放
     * 出现情况为用户手动调用
     * 或者音乐数据队列为空
     */
    public void onStop() {
        mEngineManager.stopMusic();
    }

    /**
     * 指定当前播放音乐从哪个位置开始播放
     */
    public void onSeekTo(long position) {
        createPlayerManagerIfNeed();
        int status = mEngineManager.getPlayStatus();
        if (status == EngineEvent.STATUS_PLAYING) {
            mEngineManager.seekToPosition(position);
        } else if (status == EngineEvent.STATUS_PAUSE) {
            mEngineManager.seekToPosition(position);
        } else {
            MusicBean bean = mDataManager.getPlayMusic(true);
            if (bean == null) {
                mServiceCallback.onError(AppConstant.SERVICE_WARN_NO_MUSIC_DATA, "暂无数据，无法播放");
                return;
            }
            startPlay(bean);
        }
    }

    /**
     * 设置评分
     */
    public void onSetRating(RatingCompat rating) {

    }

    /**
     * 自定义操作
     */
    public void onCustomAction(String action, Bundle extras) {
        if (extras == null) {
            return;
        }
        if (extras.getClassLoader() == null) {
            extras.setClassLoader(getClass().getClassLoader());
        }
        switch (action) {
            case AppConstant.MEDIA_ACTION_PLAY_QUEUE://设置播放队列
                List<MusicBean> queue = extras.getParcelableArrayList(AppConstant.MEDIA_BUNDLE_PLAYQUEUE);
                boolean clearOldData = extras.getBoolean(AppConstant.MEDIA_BUNDLE_AUTOCLEAR_DATA, true);
                setPlayDataQueue(queue, clearOldData);
                break;
            case AppConstant.MEDIA_ACTION_UPDATE_PLAYMODE://播放模式改变
                int playMode = extras.getInt(AppConstant.MEDIA_BUNDLE_PLAYMODE, AppConstant.PLAY_MODE_SINGLE);
                mDataManager.setPlayMode(playMode);
                break;
            case AppConstant.MEDIA_ACTION_PLAY_SINGLE_MUSIC://单独播放一首歌
                MusicBean bean = extras.getParcelable(AppConstant.MEDIA_BUNDLE_SINGLE_MUSIC);
                if (bean != null) {
                    startPlay(bean);
                } else {
                    mServiceCallback.onError(AppConstant.SERVICE_WARN_NO_MUSIC_DATA, "已经没有数据了,赶快去添加数据吧");
                }
                break;
            case AppConstant.MEDIA_ACTION_ADD_NEXT_MUSIC://添加下一曲播放歌曲
                ArrayList<MusicBean> data = extras.getParcelableArrayList(AppConstant.MEDIA_BUNDLE_ADD_NEXT_MUSIC);
                mDataManager.addNextMusic(data);
                break;
            case AppConstant.MEDIA_ACTION_UPDATE_LIKED:
                //更新新当前数据的liked属性
                boolean islike = extras.getBoolean(AppConstant.MEDIA_BUNDLE_LIKED, false);
                if (mDataManager.getPlayMusic(true) != null) {
                    mDataManager.getPlayMusic(true).setLiked(islike);
                }
                break;
            case AppConstant.MEDIA_ACTION_CLEAR_PLAY_QUEUE:
                //清空播放队列
                boolean includeNextQueue = extras.getBoolean(AppConstant.MEDIA_BUNDLE_CLEAR_PLAY_QUEUE);
                if (includeNextQueue) {
                    mDataManager.clearNextQueueData();
                }
                mDataManager.clearPlayQueueData();
                break;
            case AppConstant.MEDIA_ACTION_REMOVE_MUSIC:
                MusicBean musicBean = extras.getParcelable(AppConstant.MEDIA_BUNDLE_REMOVE_MUSIC);
                boolean includenext = extras.getBoolean(AppConstant.MEDIA_BUNDLE_INCLUDE_NEXTQUEUE, false);
                mDataManager.removeMusicData(musicBean, includenext);
                break;
        }
    }

    /**
     * 移除播放队列里面的某首歌
     */
    public void onRemoveQueueItemAt(int index) {
        mDataManager.removeMusicData(index);
    }

    /**
     * 评论歌曲
     */
    public void onCommand(String command, Bundle extras, ResultReceiver cb) {

    }

    /**
     * 设置播放的数据队列
     */
    private void setPlayDataQueue(List<MusicBean> dataQueue, boolean clearOldData) {
        if (mDataManager == null) {
            mDataManager = DataManager.getInstance(this);
        }
        if (clearOldData) {
            mDataManager.clearPlayQueueData();
        }
        mDataManager.addPlayQueue(dataQueue);
    }

    public List<MusicBean> getPlayDataQueue(boolean includeNextQueue) {
        if (mDataManager == null) {
            mDataManager = DataManager.getInstance(this);
        }
        return mDataManager.getPlayQueue(includeNextQueue);

    }

    public MusicBean getPlayMusic(boolean onlyCurrentMusic) {
        if (mDataManager == null) {
            mDataManager = DataManager.getInstance(this);
        }
        return mDataManager.getPlayMusic(onlyCurrentMusic);
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mDataManager != null) {
            mDataManager.release();
            mDataManager = null;
        }
        if (mEngineManager != null) {
            mEngineManager.release();
            mEngineManager = null;
        }
        mServiceCallback = null;
        mPlayerListener = null;
    }

    public int getNowPlayStatus() {
        createPlayerManagerIfNeed();
        return mEngineManager.getPlayStatus();
    }

    public int getAudioSessionId() {
        createPlayerManagerIfNeed();
        return mEngineManager.getAudioSessionId();
    }

    public long getCurrentTime() {
        createPlayerManagerIfNeed();
        return mEngineManager.getCurrentTime();
    }

    /**
     * 资源已经准备好马上开始播放
     */
    private void startPlay(@NonNull final MusicBean bean) {
        MusicBean nowBean = getPlayMusic(true);
        if (nowBean == null) {
            if (bean.getLocalMusic()) {
                startPlayLocalMusic(bean);
            } else {
                startPlayNetMusic(bean);
            }
            return;
        }
        if (nowBean.equals(bean)) {
            //两者是同一个对象
            startPlayIfSingle();
        } else {
            if (bean.getLocalMusic()) {
                startPlayLocalMusic(bean);
            } else {
                startPlayNetMusic(bean);
            }
        }
    }

    /**
     * 将要播放的和当前的是同一首歌
     * 有可能都是网络歌曲
     * 也有可能都是本地歌曲
     */
    private void startPlayIfSingle() {
        int state = getNowPlayStatus();
        if (state == EngineEvent.STATUS_PLAYING) {
            onPause();
        } else if (state == EngineEvent.STATUS_PAUSE) {
            mEngineManager.resumeMusic();
        } else {
            MusicBean bean = getPlayMusic(true);
            if (bean == null) {
                mServiceCallback.onError(AppConstant.SERVICE_WARN_NO_MUSIC_DATA, "暂无数据");
                return;
            }
            if (bean.getLocalMusic()) {
                startPlayLocalMusic(bean);
            } else {
                startPlayNetMusic(bean);
            }
        }
    }

    /**
     * 开始播放网络歌曲
     */
    @SuppressLint("CheckResult")
    private void startPlayNetMusic(@NonNull final MusicBean bean) {
        if (!canConnectNetwork) {
            canConnectNetwork = netWorkCanConnection();
        }
        if (!canConnectNetwork) {
            DbCacheEntiy entiy = DbCacheUtil.queryCacheData(bean);
            if (entiy != null) {
                MusicBean musicBean = DbCacheUtil.cacheEntiyToMusic(entiy);
                startPlayNetMusicForCache(musicBean);
                return;
            }
            boolean netWorkisConnect = NetWorkUtil.netWorkIsConnection();
            if (!netWorkisConnect) {
                //网络无法连接
                mServiceCallback.onError(AppConstant.SERVICE_WARN_NETWORK_ERROR, "请检查网络设置");
            } else {
                //网络能连接
                boolean isVpn = NetWorkUtil.isVpnUsed();
                if (isVpn) {
                    mServiceCallback.onError(AppConstant.SERVICE_WARN_NETWORK_ERROR, "请关闭网络代理后再次尝试播放");
                    return;
                }
                mServiceCallback.onError(AppConstant.SERVICE_WARN_UNENABLE_PLAY_FOR_SETTING, "请检查网络设置");
            }
            return;
        }
        if (bean.getLocalMusic() == null || bean.getLocalMusic()) {
            onSkipToNext();
            return;
        }
        playOptimizationNetMusic(bean);
    }

    @SuppressLint("CheckResult")
    private void startPlayLocalMusic(@NonNull final MusicBean bean) {
        if (bean.getLocalMusic() == null || !bean.getLocalMusic()) {
            mDataManager.removeMusicData(bean);
            onSkipToNext();
            return;
        }
        if (StringUtil.isEmpty(bean.getPlayPath())) {
            mDataManager.removeMusicData(bean);
            onSkipToNext();
            return;
        }
        File file = new File(bean.getPlayPath());
        if (!file.exists()) {
            //本地歌曲已经被移除了
            mDataManager.removeMusicData(bean);
            if (mServiceCallback != null) {
                mServiceCallback.onError(AppConstant.SERVICE_WARN_LOCAL_FILE_NOT_EXIST, "该歌曲可能被移除了现无法播放");
            }
            onSkipToNext();
            return;
        }
        bean.setProxyUrl(file.getAbsolutePath());
        mEngineManager.playMusic(bean);
        queryAndUpdateData(bean);
    }

    /**
     * 播放针对无网环境下 且为网络歌曲的播放
     */
    @SuppressLint("CheckResult")
    private void startPlayNetMusicForCache(MusicBean bean) {
        bean.setLocalMusic(false);
        if (StringUtil.isEmpty(bean.getPlayPath())) {
            onSkipToNext();
            return;
        }
        File file = new File(bean.getPlayPath());
        if (file.exists()) {
            //缓存歌曲存在
            bean.setLyricBean(mDataManager.getMusicLyricsForNotNet(bean));
            bean.setProxyUrl(file.getAbsolutePath());
            mEngineManager.playMusic(bean);
            queryAndUpdateData(bean);
        } else {
            mDataManager.removeMusicData(bean);
            onSkipToNext();
        }
        queryAndUpdateData(bean);
    }

    /**
     * 创建各种Manager
     */
    private void createPlayerManagerIfNeed() {
        synchronized (object) {
            if (mDataManager == null) {
                mDataManager = DataManager.getInstance(this);
            }
            if (mPlayerListener == null) {
                mPlayerListener = new PlayerListener();
            }
            if (mEngineManager == null) {
                mEngineManager = ExoPlayerEngineManager.getInstance();
                mEngineManager.setEngineEventListener(mPlayerListener);
            }
        }
    }

    /**
     * 优化播放的网络资源
     */
    @SuppressLint("CheckResult")
    private void playOptimizationNetMusic(@NonNull final MusicBean bean) {
        mDataManager.stopCurrentCacheService();
        mDataManager.getPlayProxyPath(bean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(strings -> {
                    if (!StringUtil.isEmpty(strings[0])) {
                        return true;
                    } else {
                        //todo 新增移除异常歌曲
                        mDataManager.removeMusicData(bean);
                        if (getNowPlayStatus() != EngineEvent.STATUS_PLAYING) {
                            mServiceCallback.onError(AppConstant.SERVICE_WARN_MUSIC_URL_IS_EMPTY, "");
                            onSkipToNext();
                        } else {
                            mServiceCallback.onError(AppConstant.SERVICE_WARN_MUSIC_URL_IS_EMPTY, "由于版权,暂无法播放该歌曲");
                        }
                        return false;
                    }
                }).map(strings -> {
                    bean.setPlayPath(strings[0]);
                    bean.setProxyUrl(strings[1]);
                    mEngineManager.updateCurrentData(bean);
                    mDataManager.setCurrentPlayMusic(bean);
                    mEngineManager.playMusic(bean);
                    return bean;
                }).observeOn(Schedulers.io())
                .flatMap((Function<MusicBean, ObservableSource<MusicBean>>) bean1 -> mDataManager.getNetMusicAllInfo(DbCollectUtil.queryLiked(bean1))).map(new Function<MusicBean, MusicBean>() {
            @Override
            public MusicBean apply(MusicBean bean) throws Exception {
                SPSetingUtil.saveMusicData(bean);
                mDataManager.saveMusicToHistoryDbSync(bean);
                return bean;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(bean12 -> {
                    mEngineManager.updateCurrentData(bean12);
                    mDataManager.setCurrentPlayMusic(bean12);
                    mDataManager.addLastMusic(bean12);
                    //通知UI数据已经发生改变了
                    mServiceCallback.onMusicDataChange(bean12);
                }, throwable -> {
                    if (throwable instanceof NullPointerException) {
                        if (mEngineManager.getPlayStatus() == EngineEvent.STATUS_PLAYING) {
                            mEngineManager.pauseMusic();
                        }
                        mServiceCallback.onError(AppConstant.SERVICE_WARN_NO_MUSIC_DATA, "");
                    }
                });
    }

    /**
     * 更新数据并通知UI
     */
    @SuppressLint("CheckResult")
    private void queryAndUpdateData(final MusicBean bean) {
        Observable.create((ObservableOnSubscribe<MusicBean>) emitter -> emitter.onNext(DbCollectUtil.queryLiked(bean)))
                .subscribeOn(Schedulers.io())
                .map(bean1 -> {
                    SPSetingUtil.saveMusicData(bean1);
                    mDataManager.saveMusicToHistoryDbSync(bean1);
                    return bean1;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bean12 -> {
                    mEngineManager.updateCurrentData(bean12);
                    mDataManager.setCurrentPlayMusic(bean12);
                    mDataManager.addLastMusic(bean12);
                    if (mServiceCallback != null) {
                        mServiceCallback.onMusicDataChange(bean12);
                    }
                });
    }


}
