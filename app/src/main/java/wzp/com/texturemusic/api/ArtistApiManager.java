package wzp.com.texturemusic.api;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.NetWorkUtil;

/**
 * author:Go_oG
 * date: on 2018/5/20
 * packageName: wzp.com.texturemusic.api
 */
public class ArtistApiManager {

    public static Observable<String> getHotArtists(Integer offset, Integer limit) {
        return WYApiUtil.getInstance().buildArtistService().getHotArtists(offset, limit);
    }

    public static Observable<String> getArtistsHotSong(String artist_id) {
        return WYApiUtil.getInstance().buildArtistService().getArtistsHotSong(artist_id);
    }

    public static Observable<String> getArtistAllSong(String artistId, int offset, int limit) {
        return WYApiUtil.getInstance().buildArtistService().getArtistAllSong(artistId, offset, limit);
    }

    public static Observable<String> getArtistAlbum(String artistId, int offset, int limit) {
        return WYApiUtil.getInstance().buildArtistService().getArtistAlbum(artistId, offset, limit);
    }

    public static Observable<String> getArtistDescription(String artistId) {
        return WYApiUtil.getInstance().buildArtistService().getArtistDescription(artistId);
    }


    public static Observable<String> getArtistMv(String artistId, Integer offset, Integer limit) {
        return WYApiUtil.getInstance().buildArtistService().getArtistMv(artistId, offset, limit);
    }


    public static Observable<String> getArtistTopList(int type, boolean total) {
        final String url = AppConstant.WY_BASE_URL + "toplist/artist";
        if (NetWorkUtil.netWorkIsConnection()) {
            return WYApiUtil.getInstance().buildArtistService().getArtistTopList(type, total)
                    .map(s -> {
                        CacheUtil.cacheData(url, s);
                        return s;
                    });
        } else {
            return Observable.create(emitter -> emitter.onNext(CacheUtil.getCacheData(url)));
        }
    }


}
