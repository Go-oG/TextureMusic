package wzp.com.texturemusic.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSONObject;

import wzp.com.texturemusic.MyApplication;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.config.AppConstant;

/**
 * Created by Go_oG
 * Description:应用设置工具类
 * on 2017/9/24.
 * xml文件名字为 AppConstant.SP_NAME_APP_SET常量对应的string
 */

public class SPSetingUtil {

    public static void setBooleanValue(String keyName, boolean value) {
        SharedPreferences sharedPreferences = MyApplication.getInstace()
                .getSharedPreferences(AppConstant.SP_NAME_APP_SET, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(keyName, value);
        editor.apply();
    }

    public static boolean getBooleanValue(String keyName, boolean defValue) {
        SharedPreferences sp = MyApplication.getInstace()
                .getSharedPreferences(AppConstant.SP_NAME_APP_SET, Context.MODE_PRIVATE);
        return sp.getBoolean(keyName, defValue);
    }

    public static void setStringValue(String keyName, String value) {
        SharedPreferences sharedPreferences = MyApplication.getInstace()
                .getSharedPreferences(AppConstant.SP_NAME_APP_SET, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(keyName, value);
        editor.apply();
    }

    public static String getStringValue(String keyName, String defValue) {
        SharedPreferences sp = MyApplication.getInstace()
                .getSharedPreferences(AppConstant.SP_NAME_APP_SET, Context.MODE_PRIVATE);
        return sp.getString(keyName, defValue);
    }

    public static void setIntValue(String keyName, int value) {
        SharedPreferences sharedPreferences = MyApplication.getInstace()
                .getSharedPreferences(AppConstant.SP_NAME_APP_SET, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(keyName, value);
        editor.apply();
    }

    public static int getIntValue(String keyName, int defValue) {

        SharedPreferences sp = MyApplication.getInstace()
                .getSharedPreferences(AppConstant.SP_NAME_APP_SET, Context.MODE_PRIVATE);
        return sp.getInt(keyName, defValue);
    }

    public static SharedPreferences getSettingSP() {
        return MyApplication.getInstace()
                .getSharedPreferences(AppConstant.SP_NAME_APP_SET, Context.MODE_PRIVATE);
    }

    /**
     * 保存播放的音乐数据
     *
     * @param musicBean
     */
    public static void saveMusicData(MusicBean musicBean) {
        if (musicBean != null) {
            SharedPreferences sharedPreferences = MyApplication.getInstace()
                    .getSharedPreferences(AppConstant.SP_NAME_SAVE_MUSIC, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String json = JSONObject.toJSONString(musicBean);
            editor.putString(AppConstant.SP_KEY_MUSIC_INFO, json);
            editor.apply();
        }
    }

    public static MusicBean getSaveMusicData() {
        MusicBean bean;
        SharedPreferences sp = MyApplication.getInstace()
                .getSharedPreferences(AppConstant.SP_NAME_SAVE_MUSIC, Context.MODE_PRIVATE);
        String json = sp.getString(AppConstant.SP_KEY_MUSIC_INFO, "");
        if (!StringUtil.isEmpty(json)) {
            bean = JSONObject.parseObject(json, MusicBean.class);
        } else {
            bean = null;
        }
        return bean;
    }

}
