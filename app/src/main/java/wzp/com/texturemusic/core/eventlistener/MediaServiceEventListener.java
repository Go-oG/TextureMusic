package wzp.com.texturemusic.core.eventlistener;


import wzp.com.texturemusic.bean.MusicBean;

/**
 * Created by Wang on 2017/5/27.
 * 服务类回调
 * 用于向service发送消息
 */

public interface MediaServiceEventListener {

    void onPlayStatusChanged(int playStatus);//播放状态改变

    void onError(String errorType,String errorHint);

    void onBufferingUpdate(int percent);

    void onMusicDataChange(MusicBean newMusicBean);

    void onAudioSessionIdChange(int audioSessionId);

    void onNetWorkChange(boolean isConnect,boolean isVpn);

    void onNextMusic();

    void onLastMusic();

    void onResumeMusic();

    /**
     * 该方法由通知栏发起
     */
    void onClose();

    /**
     * 点击通知栏的播放按钮会回调该方法
     */
    void onNotifiPlayOrPause();

    void onLockScreen();

    void onUnLockScreen();

    //音频输出环境改变
    //例如拔出耳机
    void onOutEnvironmentChange();

    /**
     * APP中关于网络的设置改变了
     */
    void onAppNetworkSetChange();

    /**
     * 是通过广播暂停的
     * 目前一个作用为设置了播放时长后暂停播放
     */
    void onReciverPause();

    void onLyricsChange(MusicBean bean, Long currentTime, String currentLyrics);

    void onUpdateProgress(Long currentTime, Long durationTime);

    /**
     * 播放模式改变
     */
    void onPlayModeChange(int playMode);

    /**
     * 当桌面小部件被添加或者删除时会触发该回调
     * @param isShow true为被添加
     */
    void onDesktopWidgetChange(boolean isShow);

}
