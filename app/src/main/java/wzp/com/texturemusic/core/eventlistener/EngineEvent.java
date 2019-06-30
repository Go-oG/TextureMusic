package wzp.com.texturemusic.core.eventlistener;


import wzp.com.texturemusic.bean.MusicBean;


/**
 * Created by Wang on 2017/5/27.
 * 用于播放引擎的事件回调
 */

public interface EngineEvent {
    //播放错误
    int STATUS_ERROR = 0;
    //播放完成表示的是根据系统设置，播放完整个数据队列的数据
    int STATUS_COMPLET = 1;
    //正在播放
    int STATUS_PLAYING = 2;
    //播放暂停
    int STATUS_PAUSE = 3;
    //正在缓冲
    int STATUS_BUFFERING = 4;
    //停止播放
    int STATUS_STOP = 5;
    //播放结束表示当前的一首歌播放结束
    int STATUS_END = 6;
    //空
    int STATUS_NONE = 7;
    //播放器错误类型
    int PLAYER_ERROR_IO = 8;
    int PLAYER_ERROR_SOUCE = 9;
    int PLAYER_ERROR_INTERNAL = 10;
    int PLAYER_ERROR_OTHER = 11;


    /**
     * 播放状态改变
     * 如播放暂停
     *
     * @param status @see EngineEvent
     */
    void onPlayStatusChanged(int status);

    /**
     * 播放引擎的错误
     *
     * @param errorType
     */
    void onPlayEngineError(int errorType);

    void onRequestAudioFouceFail();

    void onAudioSessionIdChange(int audioSessionId);

    void onLyricChange(MusicBean bean, long currentTime, String currentLyric);

    /**
     * 其他错误
     *
     * @param errorMsg 错误类型
     */
    void onPlayError(String errorMsg);

    void onUpdatePregress(Long currentTime, Long durationTime);



}
