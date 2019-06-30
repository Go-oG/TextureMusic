package wzp.com.texturemusic.core.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import androidx.core.app.NotificationCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;
import android.widget.RemoteViews;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.eventlistener.EngineEvent;
import wzp.com.texturemusic.core.eventlistener.MediaServiceEventListener;
import wzp.com.texturemusic.core.manger.ControlManager;
import wzp.com.texturemusic.core.reciver.MediaEventReciver;
import wzp.com.texturemusic.indexmodule.IndexActivity;
import wzp.com.texturemusic.interf.OnImageLoadListener;
import wzp.com.texturemusic.localmodule.ui.MainPlayActivity;
import wzp.com.texturemusic.localmodule.ui.activity.LockScreenActivity;
import wzp.com.texturemusic.util.ImageUtil;
import wzp.com.texturemusic.util.LogUtil;
import wzp.com.texturemusic.util.NetWorkUtil;
import wzp.com.texturemusic.util.SPSetingUtil;
import wzp.com.texturemusic.widgetmodule.DesktopWidget;

/**
 * Created by wang on 2017/4/6.
 * 核心后台服务类
 * 进度条转移到特定Activity中
 * 负责和UI进行特定的交互同时也是为了能够实现后台播放
 */
public class MediaService extends Service implements MediaServiceEventListener {
    private static final String TAG = "MediaService";
    private static final String NOTIFICATION_CHANNEL = "wzp.com.texturemusic";
    private static final String CHANNEL_NAME = "TextureMusic";
    private static final int NOTIFY_ID = 14027;//常驻通知栏通知的id
    private Context mContext;
    private MediaSessionCompat mediaSession;
    private MediaSessionCompat.Token mToken;
    private volatile ControlManager mControlManager;
    private final Object lockObject = new Object();
    private BaseBinder mBinder;
    ////通知栏相关
    private NotificationCompat.Builder mNotifBuilder;
    private NotificationManager mNotifManager;
    private Intent play_Intent, next_Intent, close_Intent, last_Intent, activity_Intent, indexActivityIntent;
    private PendingIntent playPI, nextPI, lastPI, closePI, activityPI;
    private RemoteViews remoteViews;
    private MediaEventReciver mRevicer;//广播接收器
    //桌面小部件的注册广播
    private DesktopWidget desktopWidget;
    ////////////////////
    //桌面小部件是否在显示
    private boolean desktopWidgetIsShow = false;
    //当前能够更新进度条的类的名字
    private String[] canUpdateClassArray = new String[]{
            MainPlayActivity.class.getSimpleName(),
            LockScreenActivity.class.getSimpleName()
    };
    //当前绑定的类的名字用于判断能否更新歌词和歌曲
    private String currentBindClassName = "";
    ///////是否清空通知栏
    private boolean clearNotify = false;

    /**
     * service Binder
     */
    public class BaseBinder extends Binder {

        public MediaSessionCompat.Token getToken(String classSimpleName) {
            currentBindClassName = classSimpleName;
            return mToken;
        }

        public List<MusicBean> getListData(boolean includeNextQueue) {
            return mControlManager.getPlayDataQueue(includeNextQueue);
        }

        public MusicBean getPlayMusic(boolean onlyCurrentMusic) {
            return mControlManager.getPlayMusic(onlyCurrentMusic);
        }

        public int getNowPlayState() {
            return mControlManager.getNowPlayStatus();
        }

        public int getAudioSessionId() {
            return mControlManager.getAudioSessionId();
        }

    }

    /**
     * TransportControls(控制端)的回调类
     */
    private class BaseTransportControlCallBack extends MediaSessionCompat.Callback {

        BaseTransportControlCallBack() {
            super();
        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            KeyEvent keyEvent = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (keyEvent == null || keyEvent.getAction() != KeyEvent.ACTION_DOWN) {
                return false;
            } else {
                //耳机控制 默认为true
                boolean erb = SPSetingUtil.getBooleanValue(AppConstant.SP_KEY_ERJI_CONTROL, true);
                if (erb) {
                    int keyCode = keyEvent.getKeyCode();
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            if (mControlManager.getNowPlayStatus() == EngineEvent.STATUS_PAUSE) {
                                mControlManager.onPlay();
                            } else {
                                mControlManager.onPause();
                            }
                            return true;
                        case KeyEvent.KEYCODE_MEDIA_NEXT:
                            mControlManager.onSkipToNext();
                            return true;
                        case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            if (mControlManager.getNowPlayStatus() == EngineEvent.STATUS_PAUSE) {
                                mControlManager.onPlay();
                            } else {
                                mControlManager.onPause();
                            }
                            return true;
                        case KeyEvent.KEYCODE_MEDIA_PLAY:
                            mControlManager.onPlay();
                            return true;
                        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                            mControlManager.onSkipToPrevious();
                            return true;
                        case KeyEvent.KEYCODE_MEDIA_STOP:
                            mControlManager.onStop();
                            return true;
                        default:
                            return false;
                    }
                } else {
                    return true;
                }
            }
        }

        @Override
        public void onPlay() {
            mControlManager.onPlay();
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            mControlManager.onPlayFromUri(uri, extras);
        }

        @Override
        public void onSkipToQueueItem(long index) {
            mControlManager.onSkipToQueueItem(index);
        }

        @Override
        public void onPause() {
            mControlManager.onPause();
        }

        @Override
        public void onSkipToNext() {
            mControlManager.onSkipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            mControlManager.onSkipToPrevious();
        }

        @Override
        public void onStop() {
            mControlManager.onStop();
        }

        @Override
        public void onSeekTo(long pos) {
            mControlManager.onSeekTo(pos);
        }

        @Override
        public void onSetRating(RatingCompat rating) {
            mControlManager.onSetRating(rating);
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            mControlManager.onCustomAction(action, extras);
        }

        @Override
        public void onRemoveQueueItemAt(int index) {
            mControlManager.onRemoveQueueItemAt(index);
        }

        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            mControlManager.onCommand(command, extras, cb);
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initAll();
    }

    private void initAll() {
        init();
        initReciver();
        initNotifition();
        initConfig();
    }

    private void init() {
        createControlManagerIfNeed();
        long option = PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        mediaSession = new MediaSessionCompat(mContext, AppConstant.MEDIASESSION_ID);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mToken = mediaSession.getSessionToken();
        mediaSession.setCallback(new BaseTransportControlCallBack());
        mediaSession.setActive(true);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder().setActions(option).build());
        mediaSession.sendSessionEvent(AppConstant.SERVICE_EVENT_MEDIASESSION_CONNECT, null);
    }

    private void initNotifition() {
        mNotifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifBuilder = buildCustomNotification();
        /**
         * 适配Android 8.0后的通知栏变化
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(false);
            channel.setShowBadge(false);
            channel.enableVibration(false);
            mNotifManager.createNotificationChannel(channel);
        }
    }

    private void initReciver() {
        if (mRevicer == null) {
            mRevicer = new MediaEventReciver(this);
        }
        if (desktopWidget == null) {
            desktopWidget = new DesktopWidget();
        }
        IntentFilter mIntentFilter;
        mIntentFilter = new IntentFilter();
        //监听有线耳机和无线耳机的断开连接事件
        //但是无法监听到有线耳机和蓝牙耳机的接入
        mIntentFilter.addAction(AppConstant.RECIVER_ACTION_NETWORK_CHANGE);
        mIntentFilter.addAction(AppConstant.RECIVER_ACTION_CLOSE);
        mIntentFilter.addAction(AppConstant.RECIVER_ACTION_LAST);
        mIntentFilter.addAction(AppConstant.RECIVER_ACTION_NEXT);
        mIntentFilter.addAction(AppConstant.RECIVER_ACTION_PLAY);
        mIntentFilter.addAction(AppConstant.RECIVER_ACTION_PAUSE);
        mIntentFilter.addAction(AppConstant.RECIVER_ACTION_RESUME);
        mIntentFilter.addAction(AppConstant.RECIVER_ACTION_PLAY_MODE_CHANGE);
        //监听屏幕熄灭时间实现锁屏页
        mIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        //监听用户解锁广播
        mIntentFilter.addAction(Intent.ACTION_USER_PRESENT);
        //监听网络设置（APP设置）的改变
        mIntentFilter.addAction(AppConstant.RECIVER_ACTION_NETWORK_SET_CHANGE);
        //监听桌面小部件删除
        mIntentFilter.addAction(AppConstant.RECIVER_ACTION_DESKTOPWIDGET_CHANGE);
        registerReceiver(mRevicer, mIntentFilter);
        //////////////////////////////////////////
        //////注册桌面小部件的广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.WIDGET_ACTION_ACTIVITY);
        filter.addAction(AppConstant.WIDGET_ACTION_SEARCH);
        filter.addAction(AppConstant.WIDGET_ACTION_PLAY_TYPE);
        filter.addAction(AppConstant.WIDGET_ACTION_UPDATE_DESKTOP);
        filter.addAction(AppConstant.WIDGET_ACTION_CHANGE_SKIN);
        registerReceiver(desktopWidget, filter);
    }

    private void initConfig() {
        desktopWidgetIsShow = SPSetingUtil.getBooleanValue(AppConstant.SP_KEY_DESKTOPWIDGET_IS_SHOW, false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFY_ID, mNotifBuilder.build());
        updateCustomNotify(mControlManager.getPlayMusic(false), mControlManager.getNowPlayStatus());
        createControlManagerIfNeed();
        //更新桌面进度条
        Bundle bundle = new Bundle();
        MusicBean musicBean = mControlManager.getPlayMusic(false);
        bundle.putLong(AppConstant.BUNDLE_KEY_WIDGET_PROGRESS, mControlManager.getCurrentTime());
        bundle.putParcelable(AppConstant.BUNDLE_KEY_WIDGET_MUSIC, musicBean);
        bundle.putInt(AppConstant.BUNDLE_KEY_WIDGET_PLAYSTATUS, mControlManager.getNowPlayStatus());
        if (musicBean != null && musicBean.getAllTime() != null) {
            bundle.putLong(AppConstant.BUNDLE_KEY_WIDGET_PROGRESS_ALL, musicBean.getAllTime());
        } else {
            bundle.putLong(AppConstant.BUNDLE_KEY_WIDGET_PROGRESS_ALL, 0L);
        }
        Intent wintent = new Intent(AppConstant.WIDGET_ACTION_UPDATE_DESKTOP);
        wintent.putExtras(bundle);
        sendBroadcast(wintent);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null) {
            synchronized (BaseBinder.class) {
                if (mBinder == null) {
                    mBinder = new BaseBinder();
                }
            }
        }
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRevicer != null) {
            unregisterReceiver(mRevicer);
            mRevicer = null;
        }
        if (desktopWidget != null) {
            unregisterReceiver(desktopWidget);
            desktopWidget = null;
        }
        if (mediaSession != null) {
            mediaSession.sendSessionEvent(AppConstant.SERVICE_EVENT_MEDIASESSION_DISCONNECT, null);
            mediaSession.release();
            mediaSession = null;
        }
        relese();
    }

    /**
     * 以下函数为接口函数
     * ******************************************
     * ******************************************
     */
    @Override
    public void onPlayStatusChanged(int state) {
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstant.SERVICE_BUNDLE_PLAYBACKSTATE, state);
        mediaSession.sendSessionEvent(AppConstant.SERVICE_EVENT_PLAYBACKSTATE, bundle);
        updateSystemLockScreenUI(mControlManager.getPlayMusic(false), state);
        if (state == EngineEvent.STATUS_STOP && clearNotify) {
            if (mNotifManager != null) {
                mNotifManager.cancelAll();
            }
            clearNotify = false;
        } else {
            updateCustomNotify(mControlManager.getPlayMusic(false), state);
        }

        //更新桌面小部件
        if (desktopWidgetIsShow) {
            Bundle widgetBundle = new Bundle();
            widgetBundle.putInt(AppConstant.BUNDLE_KEY_WIDGET_PLAYSTATUS, state);
            widgetBundle.putLong(AppConstant.BUNDLE_KEY_WIDGET_PROGRESS, mControlManager.getCurrentTime());
            widgetBundle.putParcelable(AppConstant.BUNDLE_KEY_WIDGET_MUSIC, mControlManager.getPlayMusic(true));
            widgetBundle.putInt(AppConstant.BUNDLE_KEY_WIDGET_PLAYSTATUS, mControlManager.getNowPlayStatus());
            Intent intent = new Intent(AppConstant.WIDGET_ACTION_UPDATE_DESKTOP);
            intent.putExtras(widgetBundle);
            sendBroadcast(intent);
        }
    }

    @Override
    public void onAudioSessionIdChange(int audioSessionId) {
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstant.SERVICE_BUNDLE_AUDIOSESSION_ID, audioSessionId);
        mediaSession.sendSessionEvent(AppConstant.SERVICE_EVENT_AUDIOSESSION_ID, bundle);

    }

    /**
     * @param isConnect 该值只是表示网络是否为通的，不代表网络是不是外网
     * @param isVpn     该值表示网络是否为VPN或者为WiFi外网代理
     */
    @Override
    public void onNetWorkChange(boolean isConnect, boolean isVpn) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(AppConstant.BUNDLE_NETWORK_CHANGE, isConnect);
        bundle.putBoolean(AppConstant.BUNDLE_NETWORK_VPN, isVpn);
        mediaSession.sendSessionEvent(AppConstant.SERVICE_EVENT_NETWORK_CHANGE, bundle);
        if (isVpn) {
            mControlManager.setCanConnectNetwork(false);
            return;
        }
        if (!isConnect) {
            mControlManager.setCanConnectNetwork(false);
            return;
        }
        SharedPreferences sp = SPSetingUtil.getSettingSP();
        //是否仅允许在WiFi下联网  默认为false
        boolean wifi = sp.getBoolean(AppConstant.SP_KEY_WIFI, true);
        //当前网络类型是否为WiFi
        boolean nowIsWifi = NetWorkUtil.getNetworkType() == NetWorkUtil.NetworkType.NETWORK_WIFI;
        //是否允许在非WiFi网络下播放歌曲
        boolean g2play = sp.getBoolean(AppConstant.SP_KEY_2G_PLAY, true);
        boolean result = (wifi ? nowIsWifi : g2play);
        mControlManager.setCanConnectNetwork(result);
        String info = "网络状态改变 isConnect===" + isConnect + "  isVPN===" + isVpn + "  wifi==" + wifi +
                "  nowisWifi===" + nowIsWifi + "  g2Play==" + g2play + "   result===" + result;
        LogUtil.test(info);
        LogUtil.saveLog(info, false);
    }

    @Override
    public void onClose() {
        clearNotify = true;
        stopForeground(true);
        if (mControlManager != null) {
            mControlManager.onStop();
        }
    }

    @Override
    public void onNextMusic() {
        mControlManager.onSkipToNext();
    }

    @Override
    public void onLastMusic() {
        mControlManager.onSkipToPrevious();
    }

    @Override
    public void onResumeMusic() {
        mControlManager.onResumeMusic();
    }

    @Override
    public void onNotifiPlayOrPause() {
        if (mControlManager.getNowPlayStatus() == EngineEvent.STATUS_PLAYING) {
            mControlManager.onPause();
        } else {
            mControlManager.onPlay();
        }
    }

    @Override
    public void onLockScreen() {
        SharedPreferences sharedPreferences = SPSetingUtil.getSettingSP();
        boolean showLock = sharedPreferences.getBoolean(AppConstant.SP_KEY_IS_LOCK_SCREEN, false);
        if (!showLock) {
            return;
        }
        boolean isCustom = sharedPreferences.getInt(AppConstant.SP_KEY_LOCK_SCREEN_TYPE, AppConstant.LOCAL_SCREEN_TYPE_CUSTOM) == AppConstant.LOCAL_SCREEN_TYPE_CUSTOM;
        if (isCustom) {
            Intent intent = new Intent(mContext, LockScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        } else {
            updateSystemLockScreenUI(mControlManager.getPlayMusic(false), mControlManager.getNowPlayStatus());
        }
    }

    @Override
    public void onUnLockScreen() {

    }

    @Override
    public void onOutEnvironmentChange() {
        if (mControlManager.getNowPlayStatus() == EngineEvent.STATUS_PLAYING) {
            mControlManager.onPause();
        }

    }

    /**
     * 由用户更改APP的设置所引发的
     */
    @Override
    public void onAppNetworkSetChange() {
        SharedPreferences sp = SPSetingUtil.getSettingSP();
        //是否仅允许在WiFi下联网  默认为false
        boolean wifi = sp.getBoolean(AppConstant.SP_KEY_WIFI, true);
        //当前网络类型是否为WiFi
        boolean nowIsWifi = NetWorkUtil.getNetworkType() == NetWorkUtil.NetworkType.NETWORK_WIFI;
        //是否允许在非WiFi网络下播放歌曲
        boolean g2play = sp.getBoolean(AppConstant.SP_KEY_2G_PLAY, true);
        if (wifi) {
            //仅WiFi联网
            mControlManager.setCanConnectNetwork(nowIsWifi);
        } else {
            //可以其他联网
            mControlManager.setCanConnectNetwork(g2play);
        }
    }

    @Override
    public void onReciverPause() {
        mControlManager.onPause();
    }

    @Override
    public void onLyricsChange(MusicBean bean, Long currentTime, String currentLyrics) {
        if (mediaSession == null) {
            return;
        }
        if (currentLyrics != null) {
            int size = currentLyrics.length();
            if (!currentLyrics.equals("]")) {
                if (size > 1 && currentLyrics.startsWith("]")) {
                    currentLyrics = currentLyrics.substring(1);
                }
            }
            currentLyrics = currentLyrics.trim();
            Bundle bundle = new Bundle();
            bundle.putString(AppConstant.SERVICE_BUNDLE_LYRIC_CHANGE, currentLyrics);
            bundle.putParcelable(AppConstant.SERVICE_BUNDLE_LYRIC_BEAN, bean);
            mediaSession.sendSessionEvent(AppConstant.SERVICE_EVENT_LYRIC_CHANGE, bundle);
        }
    }

    /**
     * 更新进度条
     */
    @Override
    public void onUpdateProgress(Long currentTime, Long durationTime) {
        if (mediaSession == null) {
            return;
        }
        if (hasStringObject(currentBindClassName, canUpdateClassArray)) {
            Bundle bundle = new Bundle();
            bundle.putLong(AppConstant.SERVICE_BUNDLE_PROGRESS_CURRENT, currentTime);
            bundle.putLong(AppConstant.SERVICE_BUNDLE_PROGRESS_DURATION, durationTime);
            mediaSession.sendSessionEvent(AppConstant.SERVICE_EVENT_PROGRESS_CHANGE, bundle);
        }
        //更新桌面小部件进度条
        if (desktopWidgetIsShow) {
            Bundle progressBundle = new Bundle();
            progressBundle.putLong(AppConstant.BUNDLE_KEY_WIDGET_PROGRESS, currentTime);
            progressBundle.putLong(AppConstant.BUNDLE_KEY_WIDGET_PROGRESS_ALL, durationTime);
            progressBundle.putParcelable(AppConstant.BUNDLE_KEY_WIDGET_MUSIC, mControlManager.getPlayMusic(true));
            progressBundle.putInt(AppConstant.BUNDLE_KEY_WIDGET_PLAYSTATUS, mControlManager.getNowPlayStatus());
            Intent intent = new Intent(AppConstant.WIDGET_ACTION_UPDATE_DESKTOP);
            intent.putExtras(progressBundle);
            sendBroadcast(intent);
        }
    }

    /**
     * 播放模式改变
     * 该方法由MediaReciver 调用
     *
     * @param playMode
     */
    @Override
    public void onPlayModeChange(int playMode) {
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstant.MEDIA_BUNDLE_PLAYMODE, playMode);
        mControlManager.onCustomAction(AppConstant.MEDIA_ACTION_UPDATE_PLAYMODE, bundle);
    }


    @Override
    public void onError(String errorType, String errorHint) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.SERVICE_BUNDLE_WARN_TYPE, errorType);
        bundle.putString(AppConstant.SERVICE_BUNDLE_WARN_HINT, errorHint);
        mediaSession.sendSessionEvent(AppConstant.SERVICE_EVENT_PLAY_WARN, bundle);
    }

    /**
     * @param percent 0-100
     */
    @Override
    public void onBufferingUpdate(int percent) {
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstant.SERVICE_BUNDLE_PLAYER_BUFFER, percent);
        mediaSession.sendSessionEvent(AppConstant.SERVICE_EVENT_PLAYER_BUFFER, bundle);
    }

    @Override
    public void onMusicDataChange(final MusicBean newMusicBean) {
        if (mediaSession == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstant.SERVICE_BUNDLE_MUSIC_CHANGE, newMusicBean);
        mediaSession.sendSessionEvent(AppConstant.SERVICE_EVENT_MUSIC_CHANGE, bundle);
        updateCustomNotify(newMusicBean, mControlManager.getNowPlayStatus());
        //更新锁屏
        updateSystemLockScreenUI(newMusicBean, mControlManager.getNowPlayStatus());
        if (desktopWidgetIsShow) {
            //更新桌面小部件
            Bundle widgetBundle = new Bundle();
            widgetBundle.putParcelable(AppConstant.BUNDLE_KEY_WIDGET_MUSIC, newMusicBean);
            widgetBundle.putLong(AppConstant.BUNDLE_KEY_WIDGET_PROGRESS, mControlManager.getCurrentTime());
            widgetBundle.putInt(AppConstant.BUNDLE_KEY_WIDGET_PLAYSTATUS, mControlManager.getNowPlayStatus());
            Intent intent = new Intent(AppConstant.WIDGET_ACTION_UPDATE_DESKTOP);
            intent.putExtras(widgetBundle);
            sendBroadcast(intent);
        }
    }


    @Override
    public void onDesktopWidgetChange(boolean isShow) {
        desktopWidgetIsShow = isShow;
    }

    /***
     * 接口函数结束
     * ******************************************
     * ******************************************
     */
    /**
     * 构建自定义音乐通知状态栏
     * 并显示
     */
    public NotificationCompat.Builder buildCustomNotification() {
        if (activity_Intent == null) {
            activity_Intent = new Intent(this, MainPlayActivity.class);
        }
        if (indexActivityIntent == null) {
            indexActivityIntent = new Intent(this, IndexActivity.class);
        }
        if (play_Intent == null) {
            play_Intent = new Intent();
            play_Intent.setAction(AppConstant.RECIVER_ACTION_PLAY);
        }
        if (next_Intent == null) {
            next_Intent = new Intent();
            next_Intent.setAction(AppConstant.RECIVER_ACTION_NEXT);
        }
        if (close_Intent == null) {
            close_Intent = new Intent();
            close_Intent.setAction(AppConstant.RECIVER_ACTION_CLOSE);
        }
        if (last_Intent == null) {
            last_Intent = new Intent();
            last_Intent.setAction(AppConstant.RECIVER_ACTION_LAST);
        }
        if (activityPI == null) {
            activityPI = PendingIntent.getActivities(mContext, 5, new Intent[]{indexActivityIntent, activity_Intent}, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        if (playPI == null) {
            playPI = PendingIntent.getBroadcast(mContext, 0, play_Intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        if (nextPI == null) {
            nextPI = PendingIntent.getBroadcast(mContext, 1, next_Intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        if (lastPI == null) {
            lastPI = PendingIntent.getBroadcast(mContext, 3, last_Intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        if (closePI == null) {
            closePI = PendingIntent.getBroadcast(mContext, 4, close_Intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        if (remoteViews == null) {
            remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notifi_control_view);
            remoteViews.setOnClickPendingIntent(R.id.notif_item, activityPI);
            remoteViews.setOnClickPendingIntent(R.id.notif_close_img, closePI);
            remoteViews.setOnClickPendingIntent(R.id.notif_play_img, playPI);//play
            remoteViews.setOnClickPendingIntent(R.id.notif_next_img, nextPI);//next
            remoteViews.setOnClickPendingIntent(R.id.notif_last_img, lastPI);//last
        }
        NotificationCompat.Builder mNotifBuilder;
        mNotifBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL);
        mNotifBuilder.setSmallIcon(R.drawable.notice_small_icon)
                .setCustomContentView(remoteViews)
                .setContentIntent(activityPI)
                .setWhen(System.currentTimeMillis())
                .setChannelId(NOTIFICATION_CHANNEL)
                .setLocalOnly(true)
                .setOngoing(true)
                .setAutoCancel(false);
        return mNotifBuilder;
    }

    /**
     * 更新自定义的通知
     */
    public void updateCustomNotify(MusicBean musicBean, @NotNull Integer playState) {
        if (mNotifBuilder == null) {
            mNotifBuilder = buildCustomNotification();
        }
        if (playState == EngineEvent.STATUS_PLAYING) {
            remoteViews.setImageViewResource(R.id.notif_play_img, R.drawable.ic_control_pause);
        } else {
            remoteViews.setImageViewResource(R.id.notif_play_img, R.drawable.ic_ac_mainplay_play);
        }
        mNotifManager.notify(NOTIFY_ID, mNotifBuilder.build());
        if (musicBean != null) {
            remoteViews.setTextViewText(R.id.notif_artistname, musicBean.getArtistName());
            remoteViews.setTextViewText(R.id.notif_music_name, musicBean.getMusicName());
            if (musicBean.getLocalMusic()!=null&&musicBean.getLocalMusic()) {
                ImageUtil.loadImage(mContext, musicBean.getCoverImgUrl(), 200, 200, R.mipmap.logo, new OnImageLoadListener() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        remoteViews.setImageViewBitmap(R.id.notif_img, bitmap);
                        mNotifManager.notify(NOTIFY_ID, mNotifBuilder.build());
                    }
                });
            } else {
                ImageUtil.loadImage(mContext, musicBean.getCoverImgUrl() + AppConstant.WY_IMG_200_200, R.mipmap.logo, new OnImageLoadListener() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        remoteViews.setImageViewBitmap(R.id.notif_img, bitmap);
                        mNotifManager.notify(NOTIFY_ID, mNotifBuilder.build());
                    }
                });
            }
        } else {
            remoteViews.setTextViewText(R.id.notif_artistname, "未知歌手");
            remoteViews.setTextViewText(R.id.notif_music_name, "未知歌曲");
            ImageUtil.loadImage(mContext, R.mipmap.logo, 200, 200, R.mipmap.logo, new OnImageLoadListener() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    remoteViews.setImageViewBitmap(R.id.notif_img, bitmap);
                    mNotifManager.notify(NOTIFY_ID, mNotifBuilder.build());
                }
            });
        }
    }

    private void relese() {
        if (mediaSession != null) {
            mediaSession.sendSessionEvent(AppConstant.SERVICE_EVENT_MEDIASESSION_DISCONNECT, null);
        }
        if (mControlManager != null) {
            mControlManager.release();
            mControlManager = null;
        }
        if (mNotifManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mNotifManager.deleteNotificationChannel(NOTIFICATION_CHANNEL);
            }
            mNotifManager.cancelAll();
            mNotifManager = null;
        }
        if (mediaSession != null) {
            mediaSession.release();
            mediaSession = null;
        }
        mBinder = null;
        mNotifBuilder = null;
        mContext = null;
    }

    private void createControlManagerIfNeed() {
        if (mControlManager == null) {
            synchronized (lockObject) {
                mControlManager = ControlManager.getInstance();
                mControlManager.setServiceCallback(this);
            }
        }
    }

    /**
     * 更新系统的自带的锁屏界面
     *
     * @param bean
     * @param status
     */
    private void updateSystemLockScreenUI(final @Nullable MusicBean bean, int status) {
        final PlaybackStateCompat stateCompat;
        if (status == EngineEvent.STATUS_PLAYING) {
            stateCompat = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1)
                    .build();
        } else if (status == EngineEvent.STATUS_PAUSE) {
            stateCompat = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PAUSED, 0, 1)
                    .build();
        } else {
            stateCompat = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_STOPPED, 0, 1)
                    .build();
        }
        if (bean == null) {
            return;
        }
        String imgUrl;
        if (bean.getLocalMusic()) {
            imgUrl = bean.getCoverImgUrl();
        } else {
            imgUrl = bean.getCoverImgUrl() + AppConstant.WY_IMG_300_300;
        }
        ImageUtil.loadImage(mContext, imgUrl, R.mipmap.logo, new OnImageLoadListener() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                MediaMetadataCompat.Builder metadataCompatBuilder = new MediaMetadataCompat.Builder();
                metadataCompatBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, bean.getAlbumName());
                metadataCompatBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, bean.getArtistName());
                metadataCompatBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, bean.getMusicName());
                metadataCompatBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
                metadataCompatBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap);
                mediaSession.setMetadata(metadataCompatBuilder.build());
                mediaSession.setPlaybackState(stateCompat);
            }
        });
    }

    /**
     * 判断数组里面是否有某个对象
     */
    private boolean hasStringObject(String val, String[] strings) {
        boolean result = false;
        for (String str : strings) {
            if (str.equals(val)) {
                result = true;
                break;
            }
        }
        return result;
    }

}
