package wzp.com.texturemusic.core.engine;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;


import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.eventlistener.EngineEvent;

/**
 * Created by Wang on 2018/3/13.
 * 抽象的引擎类
 */

public abstract class AbstractEngineManger implements AudioManager.OnAudioFocusChangeListener {
    protected final String TAG = this.getClass().getName();
    protected int mAudioSessionId = 0;
    protected AudioManager mAudioManager;
    private AudioFocusRequest mFocusRequest;
    protected volatile MusicBean mData;
    protected EngineEvent mEngineEventListener;
    protected int mPlayStatus = EngineEvent.STATUS_NONE;
    protected Context mContext;

    /**
     * 声音淡入淡出动画的监听器
     */
    public interface OnVolumeChangeListener {
        void onEnd();

        void onChange(float val);
    }

    public int getPlayStatus() {
        return mPlayStatus;
    }

    public void setPlayStatus(int mPlayStatus) {
        this.mPlayStatus = mPlayStatus;
    }

    public void setEngineEventListener(EngineEvent mCallBack) {
        this.mEngineEventListener = mCallBack;
    }

    public AbstractEngineManger(Context context) {
        mContext=context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    protected boolean requestAudioFoucs() {
        boolean result;
        AudioAttributes mPlaybackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(mPlaybackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(this)
                    .build();
            result = (mAudioManager.requestAudioFocus(mFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
        } else {
            result = (mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) ==
                    AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
        }
        return result;
    }

    /**
     * 释放焦点资源
     */
    protected void releaseAudioFoucs() {
        if (mAudioManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (mFocusRequest != null) {
                    mAudioManager.abandonAudioFocusRequest(mFocusRequest);
                }
            } else {
                mAudioManager.abandonAudioFocus(this);
            }
        }
    }

    public int getAudioSessionId() {
        return mAudioSessionId;
    }

    public MusicBean getCurrentData() {
        return mData;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }


    public abstract void playMusic(@NonNull MusicBean bean);

    public abstract void playMusic(Uri uri);

    /**
     * 暂停播放
     */
    public abstract void pauseMusic();

    /**
     * 恢复播放
     */
    public abstract void resumeMusic();

    /**
     * 停止播放
     */
    public abstract void stopMusic();

    public abstract long getCurrentTime();

    public abstract void seekToPosition(long position);

    public abstract void setPlayVoice(float startVolume, float endVolume, OnVolumeChangeListener listener);

    /**
     * 重置播放器
     */
    public void resetPlayer() {

    }

    public void release() {
    }


}
