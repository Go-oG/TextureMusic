package wzp.com.texturemusic.widgetmodule;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.RemoteViews;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.eventlistener.EngineEvent;
import wzp.com.texturemusic.indexmodule.IndexActivity;
import wzp.com.texturemusic.interf.OnImageLoadListener;
import wzp.com.texturemusic.searchmodule.SearchActivity;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;
import wzp.com.texturemusic.util.SPSetingUtil;


/**
 * Created by Go_oG
 * Description:桌面小部件
 * on 2018/2/13.
 */

public class DesktopWidget extends AppWidgetProvider {
    private RemoteViews mRemoteViews;

    /**
     * 该方法在窗口小部件被点击时发送的广播
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (AppConstant.WIDGET_ACTION_PLAY_TYPE.equals(intent.getAction())) {
            //调整播放模式
            int nowMode = SPSetingUtil.getIntValue(AppConstant.SP_KEY_PLAY_MODE, AppConstant.PLAY_MODE_LOOP);
            nowMode++;
            nowMode = nowMode % 3;
            SPSetingUtil.setIntValue(AppConstant.SP_KEY_PLAY_MODE, nowMode);
            Intent playModeIntent = new Intent(AppConstant.RECIVER_ACTION_PLAY_MODE_CHANGE);
            intent.putExtra(AppConstant.RECIVER_BUNDLE_PLAY_MODE_NAME, nowMode);
            context.sendBroadcast(playModeIntent);
        } else if (AppConstant.WIDGET_ACTION_ACTIVITY.equals(intent.getAction())) {
            //打开播放主界面
            Intent activityIntent = new Intent(context, IndexActivity.class);
            context.startActivity(activityIntent);
        } else if (AppConstant.WIDGET_ACTION_CHANGE_SKIN.equals(intent.getAction())) {
            //更换皮肤颜色

        } else if (AppConstant.WIDGET_ACTION_SEARCH.equals(intent.getAction())) {
            //点击了搜索图标跳转到搜索界面
            Intent searchIntent = new Intent(context, SearchActivity.class);
            context.startActivity(searchIntent);
        } else if (AppConstant.WIDGET_ACTION_UPDATE_DESKTOP.equals(intent.getAction())) {
            //更新桌面小部件
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                MusicBean musicBean = bundle.getParcelable(AppConstant.BUNDLE_KEY_WIDGET_MUSIC);
                int playStatus = bundle.getInt(AppConstant.BUNDLE_KEY_WIDGET_PLAYSTATUS, EngineEvent.STATUS_NONE);
                long currentTime = bundle.getLong(AppConstant.BUNDLE_KEY_WIDGET_PROGRESS, 0L);
                long allTime = bundle.getLong(AppConstant.BUNDLE_KEY_WIDGET_PROGRESS_ALL, 0L);
                if (allTime == 0L) {
                    if (musicBean != null&&musicBean.getAllTime()!=null) {
                        allTime = musicBean.getAllTime();
                    }
                }
                RemoteViews remoteViews = buildRemoteViews(context, musicBean, playStatus, currentTime, allTime);
                AppWidgetManager.getInstance(context)
                        .updateAppWidget(new ComponentName(context, DesktopWidget.class), remoteViews);
            }
        }
    }

    /**
     * 当窗口小部件第一次被添加到桌面时调用该方法
     *
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        SPSetingUtil.setBooleanValue(AppConstant.SP_KEY_DESKTOPWIDGET_IS_SHOW, true);
        Intent intent = new Intent();
        intent.setAction(AppConstant.RECIVER_ACTION_DESKTOPWIDGET_CHANGE);
        intent.putExtra(AppConstant.RECIVER_BUNDLE_DESKTOPWIDGET_CHANGE, true);
        context.sendBroadcast(intent);
    }


    @Override
    public void onDisabled(Context context) {
        SPSetingUtil.setBooleanValue(AppConstant.SP_KEY_DESKTOPWIDGET_IS_SHOW, false);
        Intent intent = new Intent();
        intent.setAction(AppConstant.RECIVER_ACTION_DESKTOPWIDGET_CHANGE);
        intent.putExtra(AppConstant.RECIVER_BUNDLE_DESKTOPWIDGET_CHANGE, false);
        context.sendBroadcast(intent);
    }

    private RemoteViews buildRemoteViews(Context context, @Nullable MusicBean bean, int playStatus, long currentTime, long allTime) {
        if (mRemoteViews == null) {
            mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_view);
            PendingIntent activityIntent = PendingIntent.getBroadcast(context, 1,
                    new Intent(AppConstant.WIDGET_ACTION_ACTIVITY), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent playIntent = PendingIntent.getBroadcast(context, 2,
                    new Intent(AppConstant.RECIVER_ACTION_PLAY), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent lastIntent = PendingIntent.getBroadcast(context, 3,
                    new Intent(AppConstant.RECIVER_ACTION_LAST), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent nextIntent = PendingIntent.getBroadcast(context, 4,
                    new Intent(AppConstant.RECIVER_ACTION_LAST), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent playTypeIntent = PendingIntent.getBroadcast(context, 5,
                    new Intent(AppConstant.WIDGET_ACTION_PLAY_TYPE), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent searchIntent = PendingIntent.getBroadcast(context, 6,
                    new Intent(AppConstant.WIDGET_ACTION_SEARCH), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent skinIntent = PendingIntent.getBroadcast(context, 7,
                    new Intent(AppConstant.WIDGET_ACTION_CHANGE_SKIN), PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.widget_img, activityIntent);
            mRemoteViews.setOnClickPendingIntent(R.id.widget_play_img, playIntent);
            mRemoteViews.setOnClickPendingIntent(R.id.widget_last_img, lastIntent);
            mRemoteViews.setOnClickPendingIntent(R.id.widget_next_img, nextIntent);
            mRemoteViews.setOnClickPendingIntent(R.id.widget_play_type_img, playTypeIntent);
            mRemoteViews.setOnClickPendingIntent(R.id.widget_search, searchIntent);
            mRemoteViews.setOnClickPendingIntent(R.id.widget_skin_img, skinIntent);
        }
        if (bean != null) {
            String imgUrl = bean.getCoverImgUrl();
            if (bean.getLocalMusic() != null && !bean.getLocalMusic()) {
                imgUrl = bean.getCoverImgUrl() + AppConstant.WY_IMG_300_300;
            }
            ImageUtil.loadImage(context, imgUrl, R.drawable.ic_large_album, R.drawable.ic_large_album,
                    new OnImageLoadListener() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            mRemoteViews.setImageViewBitmap(R.id.widget_img, bitmap);
                        }
                    });

            mRemoteViews.setTextViewText(R.id.widget_music_name_tv, bean.getMusicName());
            mRemoteViews.setTextViewText(R.id.widget_album_name_tv, bean.getAlbumName() + "-" + bean.getArtistName());
        } else {
            mRemoteViews.setTextViewText(R.id.widget_music_name_tv, "暂无");
            mRemoteViews.setTextViewText(R.id.widget_album_name_tv, "暂无");
        }
        if (allTime == 0) {
            if (bean != null&&bean.getAllTime()!=null) {
                allTime = bean.getAllTime();
            }
        }
        if (currentTime <= allTime) {
            mRemoteViews.setTextViewText(R.id.widget_time_tv, FormatData.timeValueToString(currentTime) + "/" + FormatData.timeValueToString(allTime));
            int val = (int) (((float) currentTime / (float) allTime) * 1000);
            mRemoteViews.setProgressBar(R.id.widget_progress_bar, 1000, val, false);
        } else {
            mRemoteViews.setTextViewText(R.id.widget_time_tv, "暂无时间");
            mRemoteViews.setProgressBar(R.id.widget_progress_bar, 1000, 0, false);
        }
        if (playStatus == EngineEvent.STATUS_PLAYING) {
            mRemoteViews.setImageViewResource(R.id.widget_play_img, R.drawable.ic_control_pause);
        } else {
            mRemoteViews.setImageViewResource(R.id.widget_play_img, R.drawable.ic_ac_mainplay_play);
        }
        return mRemoteViews;
    }

}
