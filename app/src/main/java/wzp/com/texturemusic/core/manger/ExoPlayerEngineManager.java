package wzp.com.texturemusic.core.manger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.view.animation.LinearInterpolator;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.MyApplication;
import wzp.com.texturemusic.bean.KeyValueBean;
import wzp.com.texturemusic.bean.LyricBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.engine.AbstractEngineManger;
import wzp.com.texturemusic.core.eventlistener.EngineEvent;
import wzp.com.texturemusic.core.eventlistener.ExoPlayerEventListener;
import wzp.com.texturemusic.util.LogUtil;
import wzp.com.texturemusic.util.MusicUtil;
import wzp.com.texturemusic.util.StringUtil;

/**
 * Created by Go_oG
 * Description:媒体播放器的管理者
 * 用于管理播放器的媒体事件和播放状态的控制
 * on 2017/9/20.
 */
public class ExoPlayerEngineManager extends AbstractEngineManger {
    private static final String USERAGEN = "GooG14027";
    private static final long VOICE_TIME = 800;//声音改变的时间 单位毫秒
    private volatile SimpleExoPlayer mExoPlayer;
    private DefaultTrackSelector mTrackSelector;
    private TrackSelection.Factory mTrackFactory;
    private DataSource.Factory dataSourceFactory;
    private final Object lockObject = new Object();
    private ExoPlayerListener playerListener;
    //用于实现周期性询问歌词,在应用退出时应该释放
    private Disposable timeDisposable;
    //用于实现当暂停后长时间没有播放音乐时，终止暂停状态 在应用退出时应该释放
    private Disposable pauseDisposable;
    private volatile static ExoPlayerEngineManager mPlayHelper;
    //上次更新进度条的时间
    private long lastUpdateProgressTime = 0;
    //上次更新的歌词
    private String lastUpdateLyrics = "";

    public static ExoPlayerEngineManager getInstance() {
        if (mPlayHelper == null) {
            synchronized (ExoPlayerEngineManager.class) {
                if (mPlayHelper == null) {
                    mPlayHelper = new ExoPlayerEngineManager();
                }
            }
        }
        return mPlayHelper;
    }

    private ExoPlayerEngineManager() {
        super(MyApplication.getInstace());
        createExoPlayerIfNeed();
        createTimeDisposed();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        //处理音频焦点的事件回调
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                //成功
                if (mExoPlayer != null && (!mExoPlayer.getPlayWhenReady()) &&
                        mExoPlayer.getPlaybackState() == Player.STATE_READY) {
                    mExoPlayer.setPlayWhenReady(true);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                pauseMusic();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pauseMusic();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // 降低声音
                if (mExoPlayer == null) {
                    return;
                }
                ValueAnimator valAnimator = ValueAnimator.ofFloat(1f, 0.4f)
                        .setDuration(500);
                valAnimator.setInterpolator(new LinearInterpolator());
                valAnimator.addUpdateListener(animation -> mExoPlayer.setVolume((Float) animation.getAnimatedValue()));
                valAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mExoPlayer.setVolume(1f);
                    }
                });
                valAnimator.start();
                break;
        }
    }

    private class ExoPlayerListener extends ExoPlayerEventListener  {

        /**
         * 播放器状态回调
         *
         * @param playWhenReady 资源已经准备好了
         * @param playbackState 播放状态改变
         */
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_BUFFERING:
                    //正在缓冲
                    mPlayStatus = EngineEvent.STATUS_BUFFERING;
                    break;
                case Player.STATE_ENDED:
                    //一首歌播放结束
                    releaseAudioFoucs();
                    mPlayStatus = EngineEvent.STATUS_END;
                    if (mEngineEventListener != null) {
                        mEngineEventListener.onPlayStatusChanged(mPlayStatus);
                    }
                    break;
                case Player.STATE_IDLE:
                    //没有数据
                    mPlayStatus = EngineEvent.STATUS_NONE;
                    break;
                case Player.STATE_READY:
                    if (playWhenReady) {
                        //已经开始播放
                        mPlayStatus = EngineEvent.STATUS_PLAYING;
                        if (mEngineEventListener != null) {
                            mEngineEventListener.onPlayStatusChanged(mPlayStatus);
                        }
                        setPlayVoice(0F, 1F, null);
                    } else {
                        //暂停了
                        mPlayStatus = EngineEvent.STATUS_PAUSE;
                        mEngineEventListener.onPlayStatusChanged(mPlayStatus);
                    }
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            mPlayStatus = EngineEvent.STATUS_ERROR;
            mAudioSessionId = 0;
            if (mEngineEventListener == null) {
                return;
            }
            int errorType = EngineEvent.PLAYER_ERROR_OTHER;
            if (error.type == ExoPlaybackException.TYPE_RENDERER) {
                errorType = EngineEvent.PLAYER_ERROR_INTERNAL;
            }
            if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                errorType = EngineEvent.PLAYER_ERROR_SOUCE;
            }
            if (error.type == ExoPlaybackException.TYPE_UNEXPECTED) {
                errorType = EngineEvent.PLAYER_ERROR_OTHER;
            }
            mEngineEventListener.onPlayEngineError(errorType);
        }

        ///////////////////////////////////AudioRendererEventListener接口/////////
        @Override
        public void onAudioSessionId(int audioSessionId) {
            mAudioSessionId = audioSessionId;
            if (mEngineEventListener != null) {
                mEngineEventListener.onAudioSessionIdChange(audioSessionId);
            }
        }

        @Override
        public void onBandwidthSample(int elapsedMs, long bytes, long bitrate) {
            LogUtil.e(TAG, "onBandwidthSample() elapsedMs=" + elapsedMs + "  bytes=" + bytes + "  bitrate=" + bitrate);
        }
    }

    /**
     * 创建一个单例的播放器
     */
    private void createExoPlayerIfNeed() {
        if (mExoPlayer == null) {
            synchronized (lockObject) {
                if (mExoPlayer == null) {
                    playerListener = new ExoPlayerListener();//监听器

                    mTrackFactory=new AdaptiveTrackSelection.Factory();
                    mTrackSelector = new DefaultTrackSelector(mTrackFactory);
                    dataSourceFactory = new DefaultDataSourceFactory(MyApplication.getInstace(), null,
                            new OkHttpDataSourceFactory(MyApplication.getOkHttpClient2(), USERAGEN));
                    mExoPlayer = ExoPlayerFactory.newSimpleInstance(MyApplication.getInstace(), mTrackSelector);
                    mExoPlayer.addListener(playerListener);
                    mExoPlayer.addAudioListener(playerListener);
                    mAudioSessionId = 0;
                    mPlayStatus = EngineEvent.STATUS_NONE;
                    if (mAudioManager == null) {
                        mAudioManager = (AudioManager) MyApplication.getInstace().getSystemService(Context.AUDIO_SERVICE);
                    }
                }
            }
        }
    }

    @Override
    public void playMusic(@NonNull MusicBean bean) {
        createTimeDisposed();
        createExoPlayerIfNeed();
        setExoplayerSouce(bean);
    }

    @Override
    public void playMusic(Uri uri) {
        String path = uri.getPath();
        if (path.startsWith("http")) {
            MusicBean bean = new MusicBean();
            bean.setPlayPath(path);
            bean.setProxyUrl(path);
            bean.setLocalMusic(false);
            bean.setAlbumName("");
            bean.setArtistName("");
            bean.setAlbumId("");
            bean.setCoverImgUrl("");
            playMusic(bean);
        } else {
            //本地音乐
            MusicBean bean = MusicUtil.queryLoaclMusic(uri);
            if (bean != null) {
                bean.setProxyUrl(bean.getPlayPath());
                playMusic(bean);
            } else {
                mEngineEventListener.onPlayError(AppConstant.SERVICE_WARN_MUSIC_URL_IS_EMPTY);
            }
        }
    }

    @Override
    public void pauseMusic() {
        releaseTimeDisposed();
        if (mExoPlayer == null || mExoPlayer.getPlaybackState() != Player.STATE_READY || !mExoPlayer.getPlayWhenReady()) {
            return;
        }
        setPlayVoice(1F, 0F, new OnVolumeChangeListener() {
            @Override
            public void onEnd() {
                releaseAudioFoucs();
                mExoPlayer.setPlayWhenReady(false);
                createPauseDisposed();
            }

            @Override
            public void onChange(float val) {

            }
        });
    }

    @Override
    public void resumeMusic() {
        createTimeDisposed();
        boolean hasAudio = requestAudioFoucs();
        if (hasAudio) {
            if (mExoPlayer != null &&
                    mExoPlayer.getPlaybackState() == Player.STATE_READY &&
                    mPlayStatus == EngineEvent.STATUS_PAUSE) {
                mExoPlayer.setPlayWhenReady(true);
            }
        } else {
            if (mEngineEventListener != null) {
                mEngineEventListener.onRequestAudioFouceFail();
            }
        }
    }

    @Override
    public void stopMusic() {
        releaseTimeDisposed();
        releasePauseDisposed();
        mAudioSessionId = 0;
        mPlayStatus = EngineEvent.STATUS_STOP;
        if (mEngineEventListener != null) {
            mEngineEventListener.onPlayStatusChanged(mPlayStatus);
        }
        releaseAudioFoucs();
        setPlayVoice(1F, 0F, new OnVolumeChangeListener() {
            @Override
            public void onEnd() {
                if (mExoPlayer != null) {
                    mExoPlayer.stop();
                }
            }

            @Override
            public void onChange(float val) {

            }
        });

    }

    @Override
    public long getCurrentTime() {
        if (mExoPlayer != null) {
            return mExoPlayer.getCurrentPosition();
        }
        return 0L;
    }

    @Override
    public void seekToPosition(long position) {
        int status = mExoPlayer.getPlaybackState();
        if (status == Player.STATE_READY && !mExoPlayer.getPlayWhenReady()) {
            mExoPlayer.seekTo(position);
        } else if (mExoPlayer.getPlayWhenReady() && status == Player.STATE_READY) {
            mExoPlayer.seekTo(position);
        } else {
            mEngineEventListener.onPlayError(AppConstant.SERVICE_WARN_MUSIC_URL_IS_EMPTY);
        }
    }

    @Override
    public void setPlayVoice(float startVolume, float endVolume, final OnVolumeChangeListener listener) {
        ValueAnimator volumeAnimator = ValueAnimator.ofFloat(startVolume, endVolume)
                .setDuration(VOICE_TIME);
        volumeAnimator.setInterpolator(new LinearInterpolator());
        volumeAnimator.addUpdateListener(animation -> {
            float val = (float) animation.getAnimatedValue();
            if (listener != null) {
                listener.onChange(val);
            }
            if (mExoPlayer != null) {
                mExoPlayer.setVolume(val);
            }
        });
        volumeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                    listener.onEnd();
                }
            }
        });
        volumeAnimator.start();
    }

    @Override
    public void resetPlayer() {
        mPlayStatus = EngineEvent.STATUS_NONE;
        mAudioSessionId = 0;
        mData = null;
        if (timeDisposable != null && !timeDisposable.isDisposed()) {
            timeDisposable.dispose();
            timeDisposable = null;
        }
    }

    @Override
    public void release() {
        releaseAudioFoucs();
        mAudioSessionId = 0;
        mPlayStatus = EngineEvent.STATUS_NONE;
        releaseTimeDisposed();
        releasePauseDisposed();
        if (mExoPlayer != null) {
            mExoPlayer.removeListener(playerListener);
            mExoPlayer.stop();
            mExoPlayer.release();
            mTrackFactory = null;//自适应
            mTrackSelector = null;
            dataSourceFactory = null;
            playerListener = null;
            mExoPlayer = null;
        }
    }

    private void createTimeDisposed() {
        if (timeDisposable == null || timeDisposable.isDisposed()) {
            timeDisposable = Observable.interval(0, 300, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(aLong -> {
                        if (mData != null) {
                            updateProgress();
                            updateLyrics();
                        }
                    });
        }
    }

    private void releaseTimeDisposed() {
        if (timeDisposable != null && !timeDisposable.isDisposed()) {
            timeDisposable.dispose();
            timeDisposable = null;
        }
    }

    private void createPauseDisposed() {
        pauseDisposable = Observable.timer(30, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (mExoPlayer != null && mPlayStatus == EngineEvent.STATUS_PAUSE) {
                        stopMusic();
                    }
                    LogUtil.d(TAG, "服务由于长时间没有使用,已经自动停止播放服务了");
                }, throwable -> {

                });
    }

    private void releasePauseDisposed() {
        if (pauseDisposable != null && !pauseDisposable.isDisposed()) {
            pauseDisposable.dispose();
            pauseDisposable = null;
        }
    }

    private void updateLyrics() {
        //歌词实体类
        if (mEngineEventListener == null || mExoPlayer == null) {
            return;
        }
        LyricBean bean = mData.getLyricBean();
        boolean aBoolean = (bean != null && (bean.getList() != null && bean.getList().size() > 0))
                && mPlayStatus == EngineEvent.STATUS_PLAYING;
        if (!aBoolean) {
            return;
        }
        long currentTime = mExoPlayer.getCurrentPosition();
        int size = bean.getList().size();
        String content = "";
        KeyValueBean valueBean;
        KeyValueBean nextValBean;
        for (int i = 0; i < size; i++) {
            valueBean = bean.getList().get(i);
            if (i < size - 1) {
                nextValBean = bean.getList().get(i + 1);
                int starTime = valueBean.getIntVal();
                int endTime = nextValBean.getIntVal();
                if (currentTime >= starTime && currentTime < endTime) {
                    content = valueBean.getValue();
                    break;
                }
            } else {
                //最后一个即 i=size-1
                if (currentTime >= valueBean.getIntVal()) {
                    content = valueBean.getValue();
                    break;
                }
            }
        }
        if ((!StringUtil.isEmpty(content)) && (!content.equals(lastUpdateLyrics))) {
            lastUpdateLyrics = content;
            mEngineEventListener.onLyricChange(mData, currentTime, content);
        }
    }

    private void updateProgress() {
        if (mPlayStatus == EngineEvent.STATUS_PLAYING) {
            long timeMillis = System.currentTimeMillis();
            if (timeMillis - lastUpdateProgressTime < 500) {
                return;
            }
            lastUpdateProgressTime = timeMillis;
            long currentTime = mExoPlayer == null ? 0 : mExoPlayer.getCurrentPosition();

            long durationTime;
            if (mData == null) {
                durationTime = 0;
            } else {
                if (mData.getAllTime() != null) {
                    durationTime = mData.getAllTime();
                } else {
                    durationTime = mExoPlayer.getDuration();
                }
            }
            if (mEngineEventListener != null) {
                mEngineEventListener.onUpdatePregress(currentTime, durationTime);
            }
        }
    }

    /**
     * 设置播放器的资源
     * 保证了其播放地址一定存在
     */
    private void setExoplayerSouce(MusicBean bean) {
        mData = bean;
        if (!requestAudioFoucs()) {
            mEngineEventListener.onRequestAudioFouceFail();
            return;
        }
        String playPath = mData.getProxyUrl();
        Uri uri = Uri.parse(playPath);
        if (uri != null) {
            ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri, null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        } else {
            if (mEngineEventListener != null) {
                mEngineEventListener.onPlayError(AppConstant.SERVICE_WARN_MUSIC_URL_IS_EMPTY);
            }
        }
    }

    public void updateCurrentData(MusicBean bean) {
        mData = bean;
    }

}
