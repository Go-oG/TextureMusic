package wzp.com.texturemusic.api;


import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import wzp.com.texturemusic.MyApplication;
import wzp.com.texturemusic.core.config.AppConstant;

/**
 * Created by Go_oG
 * 网易云音乐API工具类
 */

public class WYApiUtil {

    private static volatile WYApiUtil apiUtil;

    private WYApiUtil() {

    }

    public Retrofit buildRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(AppConstant.WY_BASE_URL)
                .client(MyApplication.getOkHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static WYApiUtil getInstance() {
        if (apiUtil == null) {
            synchronized (WYApiUtil.class) {
                if (apiUtil == null) {
                    apiUtil = new WYApiUtil();
                }
            }
        }
        return apiUtil;
    }

    public WYApiForAlbum buildAlbumService() {
        return buildRetrofit().create(WYApiForAlbum.class);
    }

    public WYApiForArtist buildArtistService() {
        return buildRetrofit().create(WYApiForArtist.class);
    }

    public WYAPiForComment buildCommentService() {
        return buildRetrofit().create(WYAPiForComment.class);
    }

    public WYApiForDJ buildDjService() {
        return buildRetrofit().create(WYApiForDJ.class);
    }

    public WYApiForMv buildMvService() {
        return buildRetrofit().create(WYApiForMv.class);
    }

    public WYApiForPlaylist buildPlaylistService() {
        return buildRetrofit().create(WYApiForPlaylist.class);
    }

    public WYApiForSearch buildSearchService() {
        return buildRetrofit().create(WYApiForSearch.class);
    }

    public WYApiForSong buildSongService() {
        return buildRetrofit().create(WYApiForSong.class);
    }

    public WYApiForUser buildUserService() {
        return buildRetrofit().create(WYApiForUser.class);
    }

}
