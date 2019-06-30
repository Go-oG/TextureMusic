package wzp.com.texturemusic.util;

import android.content.Context;
import android.content.SharedPreferences;

import wzp.com.texturemusic.MyApplication;

/**
 * Created by Wang on 2018/3/7.
 * 数据缓存工具类
 * 该类仅仅只是缓存IndexActivity界面上的数据
 */

public class DataCacheUtil {
    private static final String CACHE_SP_NAME = "cache_cover_data_sp";
    public static final String PERSONAL_COVER_DATA = "cache_personal_cover_data";
    public static final String PLAYLIST_COVER_DATA = "cache_playlist_cover_data";
    public static final String RADIO_COVER_DATA = "cache_radio_cover_data";
    public static final String RANK_COVER_DATA = "cache_rank_cover_data";

    public static void savePersonalCoverData(String json) {
        SharedPreferences sharedPreferences = MyApplication.getInstace()
                .getSharedPreferences(CACHE_SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PERSONAL_COVER_DATA, json);
        editor.apply();
    }

    public static String getPersonalCoverData() {
        SharedPreferences sp = MyApplication.getInstace()
                .getSharedPreferences(CACHE_SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(PERSONAL_COVER_DATA, "");
    }


    public static void savePlaylistCoverData(String json) {
        SharedPreferences sharedPreferences = MyApplication.getInstace()
                .getSharedPreferences(CACHE_SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PLAYLIST_COVER_DATA, json);
        editor.apply();
    }

    public static String getPlaylistCoverData() {
        SharedPreferences sp = MyApplication.getInstace()
                .getSharedPreferences(CACHE_SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(PLAYLIST_COVER_DATA, "");
    }


    public static void saveRadioCoverData(String json) {
        SharedPreferences sharedPreferences = MyApplication.getInstace()
                .getSharedPreferences(CACHE_SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(RADIO_COVER_DATA, json);
        editor.apply();
    }

    public static String getRadioCoverData() {
        SharedPreferences sp = MyApplication.getInstace()
                .getSharedPreferences(CACHE_SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(RADIO_COVER_DATA, "");
    }


    public static void saveRankCoverData(String json) {
        SharedPreferences sharedPreferences = MyApplication.getInstace()
                .getSharedPreferences(CACHE_SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(RANK_COVER_DATA, json);
        editor.apply();
    }

    public static String getRankCoverData() {
        SharedPreferences sp = MyApplication.getInstace()
                .getSharedPreferences(CACHE_SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(RANK_COVER_DATA, "");
    }

}
