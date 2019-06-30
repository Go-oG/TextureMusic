package wzp.com.texturemusic.core.reciver;

/**
 * Created by Go_oG
 * Description:基础的广播接收器
 * 只用于接收与媒体播放相关的广播
 * on 2017/11/28.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.text.TextUtils;

import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.eventlistener.MediaServiceEventListener;
import wzp.com.texturemusic.util.NetWorkUtil;
import wzp.com.texturemusic.util.SPSetingUtil;

public class MediaEventReciver extends BroadcastReceiver {
    private MediaServiceEventListener mCallback;

    public MediaEventReciver(MediaServiceEventListener mediaServiceEventListener) {
        this.mCallback = mediaServiceEventListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action) || mCallback == null) {
            return;
        }
        switch (action) {
            case AppConstant.RECIVER_ACTION_NETWORK_CHANGE:
                boolean isVpn = NetWorkUtil.isVpnUsed();
                mCallback.onNetWorkChange(NetWorkUtil.netWorkIsConnection(), isVpn);
                break;
            case AppConstant.RECIVER_ACTION_NEXT:
                mCallback.onNextMusic();
                break;
            case AppConstant.RECIVER_ACTION_LAST:
                mCallback.onLastMusic();
                break;
            case AppConstant.RECIVER_ACTION_PLAY:
                mCallback.onNotifiPlayOrPause();
                break;
            case AppConstant.RECIVER_ACTION_RESUME:
                mCallback.onResumeMusic();
                break;
            case AppConstant.RECIVER_ACTION_CLOSE:
                mCallback.onClose();
                break;
            case AppConstant.RECIVER_ACTION_PAUSE:
                mCallback.onReciverPause();
                break;
            case Intent.ACTION_SCREEN_OFF:
                //屏幕锁屏
                mCallback.onLockScreen();
                break;
            case Intent.ACTION_USER_PRESENT:
                //解锁广播
                mCallback.onUnLockScreen();
                break;
            case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                //耳机断开或者蓝牙断开
                mCallback.onOutEnvironmentChange();
                break;
            case AppConstant.RECIVER_ACTION_NETWORK_SET_CHANGE:
                mCallback.onAppNetworkSetChange();
                break;
            case AppConstant.RECIVER_ACTION_PLAY_MODE_CHANGE:
                //播放模式改变
                //该广播由桌面小部件触发
                int playMode = intent.getIntExtra(AppConstant.RECIVER_BUNDLE_PLAY_MODE_NAME, AppConstant.PLAY_MODE_LOOP);
                mCallback.onPlayModeChange(playMode);
                break;
            case AppConstant.RECIVER_ACTION_DESKTOPWIDGET_CHANGE:
                //桌面被添加或者被删除
                boolean isShow = intent.getBooleanExtra(AppConstant.RECIVER_BUNDLE_DESKTOPWIDGET_CHANGE, false);
                mCallback.onDesktopWidgetChange(isShow);
                break;
        }
    }

}
