package wzp.com.texturemusic.core.config;

import wzp.com.texturemusic.R;

/**
 * Created by Go_oG
 * Description: 用于存放全局静态常量
 * on 2017/9/20.
 */

public class AppConstant {
    public static final String WY_BASE_URL = "http://music.163.com/api/";//网易API的基础url
    public static final String DOWNLOAD_QUEUE = "downloadqueue";
    public static final String[] MUSIC_FILE_TYPE = new String[]{
            ".mp3", ".flac", ".ape", ".aac", ".mpc", ".wma"
    };
    public static final String[] THEME_NAMES = new String[]{
            "theme_1", "theme_2", "theme_3", "theme_4", "theme_5", "theme_6",
            "theme_7", "theme_8", "theme_9", "theme_10", "theme_11", "theme_12",
    };
    public static final int[] THEME_ID = new int[]{
            R.style.theme_1, R.style.theme_2, R.style.theme_3, R.style.theme_4,
            R.style.theme_5, R.style.theme_6, R.style.theme_7, R.style.theme_8,
            R.style.theme_9, R.style.theme_10, R.style.theme_11, R.style.theme_12,
    };
    public static final int[] THEME_IMG_RES_ID = new int[]{
            R.mipmap.theme_color1, R.mipmap.theme_color2, R.mipmap.theme_color3, R.mipmap.theme_color4,
            R.mipmap.theme_color5, R.mipmap.theme_color6, R.mipmap.theme_color7, R.mipmap.theme_color8,
            R.mipmap.theme_color9, R.mipmap.theme_color10, R.mipmap.theme_color11, R.mipmap.theme_color12,
    };
    public static final int[] THEME_SHOW_IMG_RES_ID = new int[]{
            R.mipmap.theme_show_img_1, R.mipmap.theme_show_img_2, R.mipmap.theme_show_img_3, R.mipmap.theme_show_img_4,
            R.mipmap.theme_show_img_5, R.mipmap.theme_show_img_6, R.mipmap.theme_show_img_7, R.mipmap.theme_show_img_8,
            R.mipmap.theme_show_img_9, R.mipmap.theme_show_img_10, R.mipmap.theme_show_img_11, R.mipmap.theme_show_img_12,

    };
    //用于限制图片的大小
    public static final String WY_IMG_50_50 = "?param=50y50&quality=80";
    public static final String WY_IMG_100_100 = "?param=100y100&quality=80";
    public static final String WY_IMG_200_200 = "?param=200y200&quality=80";
    public static final String WY_IMG_300_200 = "?param=300y200&quality=80";
    public static final String WY_IMG_300_300 = "?param=300y300&quality=80";
    public static final String WY_IMG_200_300 = "?param=200y300&quality=80";
    public static final String WY_IMG_400_300 = "?param=400y300&quality=80";
    public static final String WY_IMG_400_400 = "?param=400y400&quality=80";
    public static final String WY_IMG_500_500 = "?param=500y500&quality=80";
    public static final String WY_IMG_500_300 = "?param=500y300&quality=80";
    public static final String WY_IMG_800_400 = "?param=800y400&quality=80";


    //service向UI发送缓冲流的key
    public static final String SERVICE_EVENT_PLAYER_BUFFER = "player_buffer_update";
    public static final String SERVICE_BUNDLE_PLAYER_BUFFER = "player_buffer_bundle";
    //service通知activity 播放的数据发生改变了
    public static final String SERVICE_EVENT_MUSIC_CHANGE = "music_data_change";
    public static final String SERVICE_BUNDLE_MUSIC_CHANGE = "bundle_music_change";
    //service向UI发送网络改变的信息
    public static final String SERVICE_EVENT_NETWORK_CHANGE = "app_network_has_change";
    public static final String BUNDLE_NETWORK_CHANGE = "bundle_network_change";
    public static final String BUNDLE_NETWORK_VPN = "bundle_network_vpn";

    //service 向UI发送歌词改变的信息
    public static final String SERVICE_EVENT_LYRIC_CHANGE = "lyric_change";
    public static final String SERVICE_BUNDLE_LYRIC_CHANGE = "bundle_lyric_change";
    public static final String SERVICE_BUNDLE_LYRIC_BEAN = "bundle_lyrics_bean";

    //service 向UI发送更新进度条的信息
    public static final String SERVICE_EVENT_PROGRESS_CHANGE = "progress_change";
    public static final String SERVICE_BUNDLE_PROGRESS_CURRENT = "bundle_progress_change";
    public static final String SERVICE_BUNDLE_PROGRESS_DURATION = "bundle_progress_duration";
    ////////////////////////////////////
    //用于service在播放出现警告时向UI发送信息的key
    public static final String SERVICE_EVENT_PLAY_WARN = "play_on_error";
    //表明错误类型
    public static final String SERVICE_BUNDLE_WARN_TYPE = "bundle_error_type";
    //表明错误的提示信息
    public static final String SERVICE_BUNDLE_WARN_HINT = "bundle_error_hint";
    //没有播放数据
    public static final String SERVICE_WARN_NO_MUSIC_DATA = "warn_no_music_data";
    //其他错误
    public static final String SERVICE_WARN_NO_REASON = "warn_no_reason";
    //播放器内部错误
    public static final String SERVICE_WARN_REDER_ERROR = "warn_player_reader_error";
    //音乐地址为空
    public static final String SERVICE_WARN_MUSIC_URL_IS_EMPTY = "warn_music_url_is_empty";
    //本地音乐文件不存在
    public static final String SERVICE_WARN_LOCAL_FILE_NOT_EXIST = "warn_local_file_not_exist";
    //当前设置下无法播放音乐
    public static final String SERVICE_WARN_UNENABLE_PLAY_FOR_SETTING = "warn_unenable_paly_for_setting";
    //网络错误
    public static final String SERVICE_WARN_NETWORK_ERROR = "warn_network_disconnect";
    ///////////////////////////////////////////////////////////////////
    //MediaSession创建时需要的Id
    public static final String MEDIASESSION_ID = "mediasessionId";

    //MediaSession断开与连接
    public static final String SERVICE_EVENT_MEDIASESSION_CONNECT = "mediasessionconnect";
    public static final String SERVICE_EVENT_MEDIASESSION_DISCONNECT = "mediasessiondisconnect";

    //播放器从错误状态恢复为正常
    public static final String SERVICE_EVENT_PLAYER_RESUME = "player_is_resume";

    //audioSessionId改变
    public static final String SERVICE_EVENT_AUDIOSESSION_ID = "audio_session_id_change";
    public static final String SERVICE_BUNDLE_AUDIOSESSION_ID = "bundle_audiosession_id";

    //用于向service设置播放队列的key
    public static final String MEDIA_ACTION_PLAY_QUEUE = "MediaSessionActionPlayqueue";
    public static final String MEDIA_BUNDLE_PLAYQUEUE = "bundle_set_playqueue_data";
    //用于在传输大数据时是否要清空数据
    public static final String MEDIA_BUNDLE_AUTOCLEAR_DATA = "bundle_auto_clear_data";

    //用于更新播放模式
    public static final String MEDIA_ACTION_UPDATE_PLAYMODE = "action_update_playmode";
    public static final String MEDIA_BUNDLE_PLAYMODE = "bundle_play_mode";

    //用于单独播放一首歌
    public static final String MEDIA_ACTION_PLAY_SINGLE_MUSIC = "action_play_sinle_music";
    public static final String MEDIA_BUNDLE_SINGLE_MUSIC = "bundle_single_music";

    //添加歌曲
    public static final String MEDIA_ACTION_ADD_NEXT_MUSIC = "add_next_music";
    public static final String MEDIA_BUNDLE_ADD_NEXT_MUSIC = "bundle_next_music";

    //移除歌曲
    public static final String MEDIA_ACTION_REMOVE_MUSIC = "remove_music_for_queue";
    public static final String MEDIA_BUNDLE_REMOVE_MUSIC = "bundle_remove_music_queue";
    public static final String MEDIA_BUNDLE_INCLUDE_NEXTQUEUE = "bundle_include_nextqueue";

    //清空播放队列
    public static final String MEDIA_ACTION_CLEAR_PLAY_QUEUE = "action_clear_play_queue";
    public static final String MEDIA_BUNDLE_CLEAR_PLAY_QUEUE = "bundle_clear_play_queue";

    //用于更新当前歌曲是否为收藏的
    public static final String MEDIA_ACTION_UPDATE_LIKED = "action_update_liked";
    public static final String MEDIA_BUNDLE_LIKED = "bundle_liked_boolean";

    //音乐播放模式
    public static final int PLAY_MODE_SINGLE = 0;//单曲循环（默认值）
    public static final int PLAY_MODE_LOOP = 1;//列表循环且按序播放
    public static final int PLAY_MODE_RANDOM = 2;//随机播放

    //音乐的码率
    public static final int MUSIC_BITRATE_LOW = 96000;
    public static final int MUSIC_BITRATE_NORMAL = 160000;
    public static final int MUSIC_BITRATE_HIGHT = 320000;
    public static final int MUSIC_BITRATE_SUPER_HIGHT = 999000;//超高码率

    //拓展音乐播放状态
    //下列字段用于向UI传递消息
    public static final String SERVICE_BUNDLE_PLAYBACKSTATE = "bundle_playbackstate";
    public static final String SERVICE_EVENT_PLAYBACKSTATE = "intent_playbundle_state";

    //广播的action列表
    public static final String RECIVER_ACTION_RESUME = "wzp.texturemusic.resume";
    public static final String RECIVER_ACTION_PLAY = "wzp.texturemusic.play_or_pause";
    public static final String RECIVER_ACTION_LAST = "wzp.texturemusic.last_music";
    public static final String RECIVER_ACTION_NEXT = "wzp.texturemusic.ext_music";
    public static final String RECIVER_ACTION_CLOSE = "wzp.texturemusic.close_music";
    public static final String RECIVER_ACTION_PAUSE = "wzp.texturemusic.pause";
    public static final String RECIVER_ACTION_NETWORK_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String RECIVER_ACTION_NETWORK_SET_CHANGE = "wzp.texturemusic.set_network_change";
    public static final String RECIVER_ACTION_PLAY_MODE_CHANGE = "wzp.texturemusic.play_mode_change";
    public static final String RECIVER_BUNDLE_PLAY_MODE_NAME = "wzp.texturemusic._mode_name";

    public static final String RECIVER_ACTION_DESKTOPWIDGET_CHANGE = "wzp.texturemusic.desktopwidget_change";
    public static final String RECIVER_BUNDLE_DESKTOPWIDGET_CHANGE = "wzp_bundle_desktop_widget_change";

    //桌面小部件的广播
    public static final String WIDGET_ACTION_PLAY_TYPE = "wzp.texturemusic.widget_play_type";
    public static final String WIDGET_ACTION_ACTIVITY = "wzp.texturemusic.widget_activity";
    public static final String WIDGET_ACTION_SEARCH = "wzp.texturemusic.widget_search";
    public static final String WIDGET_ACTION_CHANGE_SKIN = "wzp.texturemusic.widget_change_skin";

    //用于service通知桌面小部件进行更新
    public static final String WIDGET_ACTION_UPDATE_DESKTOP = "wzp.texturemusic.widget_update";
    public static final String BUNDLE_KEY_WIDGET_MUSIC = "bundle_key_widget_music";
    public static final String BUNDLE_KEY_WIDGET_PLAYSTATUS = "bundle_key_widget_playstatus";
    public static final String BUNDLE_KEY_WIDGET_PROGRESS = "bundle_key_widget_progress";
    public static final String BUNDLE_KEY_WIDGET_PROGRESS_ALL = "bundle_key_widget_progress_all";

    //Intent跳转携带数据key
    public static final String UI_INTENT_KEY_PLAYLIST = "intent_playlist";
    public static final String UI_INTENT_KEY_PLAYLIST_IMG = "intent_playList_img";
    public static final String UI_BUNDLE_KEY_TIME = "bundle_time";
    public static final String UI_INTENT_KEY_USER = "intent_key_user";
    public static final String UI_INTENT_KEY_MV = "mvactivity_mv";
    public static final String UI_INTENT_KEY_DJ = "djdetailactivity_dj";
    public static final String UI_INTENT_KEY_ARTIST = "artistdetail_artist";
    public static final String UI_INTENT_KEY_ALBUM = "albumdetail_album";
    public static final String UI_INTENT_KEY_DJ_TYPE = "dj_type_list";
    public static final String UI_INTENT_KEY_COMMENT = "comment_detail";

    public static final int COMMENT_TYPE_PLAYLIST = 0;
    public static final int COMMENT_TYPE_MUSIC = 1;
    public static final int COMMENT_TYPE_MV = 2;
    public static final int COMMENT_TYPE_ALBUM = 3;
    public static final int COMMENT_TYPE_DJ = 4;

    //app设置相关
    //系统锁屏
    public static final int LOCAL_SCREEN_TYPE_SYSTEM = 0;
    //自定义锁屏
    public static final int LOCAL_SCREEN_TYPE_CUSTOM = 1;
    //sp文件名字
    public static final String SP_NAME_APP_SET = "appsettingforsp";
    //仅WiFi下联网 boolean
    public static final String SP_KEY_WIFI = "sp_wifi";
    //使用移动网络播放 boolean
    public static final String SP_KEY_2G_PLAY = "sp_2G_play";
    //移动网络下载 boolean
    public static final String SP_KEY_2G_DOWNLOAD = "sp_2G_download";
    //在线播放音质 int
    public static final String SP_KEY_PLAY_QUALITY = "sp_play_quality";
    //下载质量 int
    public static final String SP_KEY_DOWNLOAD_QUALITY = "sp_download_quailty";
    //缓存大小 int
    public static final String SP_KEY_IMG_CACHE_SIZE = "sp_cache_size";
    public static final String SP_KEY_MUSIC_CACHE_SIZE = "sp_cache_music_size";
    public static final String SP_KEY_AUTO_CLEAR_CACHE = "sp_auto_clear_cache";
    //默认的音乐缓存大小
    public static final int DEFAULT_MUSIC_CACHE_SIZE = 500;//MB
    //默认的图片缓存大小
    public static final int DEFAULT_IMG_CACHE_SIZE = 200;//MB
    //桌面歌词 boolean
    public static final String SP_KEY_DESKTY_LRC = "sp_deskty_lrc";
    //耳机控制 boolean
    public static final String SP_KEY_ERJI_CONTROL = "sp_erji_control";
    //播放模式 int
    public static final String SP_KEY_PLAY_MODE = "sp_play_mode";
    //是否显示锁屏 boolean
    public static final String SP_KEY_IS_LOCK_SCREEN = "sp_is_lock_screen";
    //锁屏的类型 0 系统 1 自定义 int
    public static final String SP_KEY_LOCK_SCREEN_TYPE = "sp_lock_screen_type";
    //是否打开音乐可是化开关  boolean
    public static final String SP_KEY_OPEN_FFT_VIEW = "sp_open_fft_view";
    //是否让主界面图片旋转 boolean
    public static final String SP_KEY_OPEN_ANIMATION = "sp_open_animation";
    //是不是第一次更新自己维护的数据库信息 默认true
    //boolean
    public static final String SP_KEY_IS_FIRST_INSERT_DB_DATA = "app_is_first_insert_db_data";
    //主题的名字
    public static final String SP_KEY_THEME_NAME = "app_theme_name";
    //保存Music 与均衡器相关信息
    public static final String SP_NAME_SAVE_MUSIC = "musicinfoSp";
    public static final String SP_KEY_MUSIC_INFO = "sp_key_music_info";

    public static final String SP_KEY_DESKTOPWIDGET_IS_SHOW = "sp_desktopwidget_is_show";
    //音乐来源
    //来自电台
    public static final int MUSIC_ORIGIN_DJ = 0;
    //普通来源
    public static final int MUSIC_ORIGIN_NORMAL = 1;


}
