package wzp.com.texturemusic.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;

import wzp.com.texturemusic.MyApplication;

/**
 * Created by wang on 2017/2/17.
 * 基础 工具类
 */

public class BaseUtil {
    public static final int RING_ALARM = 1;//闹铃
    public static final int RING_NOTIFICATION = 2;//通知铃声
    public static final int RING_PHOTO_CALL = 3;//来电铃声

    /**
     * 设置某个音乐为不同类型的声音
     *
     * @param ringType RING_ALARM :闹钟;
     *                 RING_NOTIFICATION :通知;
     *                 RING_RINGTONE :来电铃声;
     */
    public static void setRingtone(int ringType, String fileAbsolutepath) {
        File sdfile = new File(fileAbsolutepath);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
        Uri newUri = MyApplication.getInstace().getContentResolver().insert(uri, values);
        switch (ringType) {
            case RING_ALARM://闹铃
                RingtoneManager.setActualDefaultRingtoneUri(MyApplication.getInstace(), RingtoneManager.TYPE_ALARM, newUri);
                break;
            case RING_NOTIFICATION://通知
                RingtoneManager.setActualDefaultRingtoneUri(MyApplication.getInstace(), RingtoneManager.TYPE_NOTIFICATION, newUri);
                break;
            case RING_PHOTO_CALL://来电
                RingtoneManager.setActualDefaultRingtoneUri(MyApplication.getInstace(), RingtoneManager.TYPE_RINGTONE, newUri);
                break;
        }
    }

    /**
     * dp转px
     */
    public static int dp2px(float dpValue) {
        final float scale = MyApplication.getInstace().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     */
    public static int px2dp(float pxValue) {
        final float scale = MyApplication.getInstace().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp转px
     */
    public static int sp2px(float spValue) {
        final float fontScale = MyApplication.getInstace().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px转sp
     */
    public static int px2sp(float pxValue) {
        final float fontScale = MyApplication.getInstace().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 获取状态栏高度
     */
    public static int getStackBarHight() {
        return MyApplication.getInstace().getResources().getDimensionPixelOffset(MyApplication
                .getInstace().getResources().getIdentifier("status_bar_height", "dimen", "android"));
    }

    /**
     * 像素
     *
     * @param activity
     * @return
     */
    public static int getScreenWidth(Activity activity) {
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 像素
     *
     * @param activity
     * @return
     */
    public static int getScreenHeight(Activity activity) {
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);

        return outMetrics.heightPixels;
    }


    /**
     * 屏幕亮度相关
     */

    /**
     * 是否开启了自动调节亮度
     *
     * @param context Activity的Context
     */
    public static boolean isAutoBrightness(Context context) {
        boolean automicBrightness = false;
        ContentResolver aContentResolver = context.getContentResolver();
        try {
            automicBrightness = Settings.System.getInt(aContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    /**
     * 获取屏幕亮度
     */
    public static int getScreenBrightness(Context activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 设置屏幕亮度
     */
    public static void setBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = brightness * (1f / 255f);
        activity.getWindow().setAttributes(lp);
    }



    /**
     * 保存亮度设置状态
     */
    public static void saveBrightness(Activity activity, int brightness) {
        ContentResolver resolver = activity.getContentResolver();
        Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
        android.provider.Settings.System.putInt(resolver, "screen_brightness", brightness);
        resolver.notifyChange(uri, null);
    }

}
