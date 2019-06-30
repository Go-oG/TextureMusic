package wzp.com.texturemusic.core.config;

import android.os.Environment;

/**
 * Created by Go_oG
 * Description:用于保存文件目录树相关的配置
 * on 2018/1/7.
 */

public class AppFileConstant {
    private static String BASE_DRESS;

    static {
        BASE_DRESS = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (BASE_DRESS.endsWith("/")) {
            BASE_DRESS = BASE_DRESS + "TextureMusic/";
        } else {
            BASE_DRESS = BASE_DRESS + "/TextureMusic/";
        }
    }

    //正常播放时音乐和mv的缓存地址
    public static final String MEDIA_CACHE_DRESS = BASE_DRESS + "Cache/Media/";
    //Glide缓存的图片的地址
    public static final String IMAGE_CACHE_DRESS = BASE_DRESS + "Cache/Image/";

    public static final String CACHE_DATA_INFO = BASE_DRESS + "Cache/Data/";

    //下载的mv地址
    public static final String DOWNLOAD_MV_DRESS = BASE_DRESS + "Download/MV/";
    //下载的音乐地址
    public static final String DOWNLOAD_MUSIC_DRESS = BASE_DRESS + "Download/Music/";
    //下载的歌曲位置
    public static final String DOWNLOAD_LYRICS_DRESS = BASE_DRESS + "Download/Lyric/";

    //专辑图片的存放地址
    public static final String ALBUM_IMAGE_DRESS = BASE_DRESS + "Image/Album/";
    //艺术家图片地址
    public static final String ARTIST_IMAGE_DRESS = BASE_DRESS + "Image/Artist/";

    public static final String COVER_IMG = BASE_DRESS + "/Image/Cover/";

    public static final String FILE_DRESS = BASE_DRESS + "/File/";


}
