package wzp.com.texturemusic.api;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import retrofit2.http.GET;
import retrofit2.http.Query;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.NetWorkUtil;

/**
 * author:Go_oG
 * date: on 2018/5/20
 * packageName: wzp.com.texturemusic.api
 */
public class PlaylistApiManager {

    public static Observable<String> getDeffrentTypePlayList(String categoy, String order, Integer offset, Integer limit) {
        return WYApiUtil.getInstance().buildPlaylistService().getDeffrentTypePlayList(categoy, order, offset, limit);
    }

    public static Observable<String> getPlayListDetailV3(String playlistId) {
        return WYApiUtil.getInstance().buildPlaylistService().getPlayListDetailV3(playlistId);
    }

    public static Observable<String> getHighQualityPlayList(String cateType, Integer offset, Integer limit) {
        return WYApiUtil.getInstance().buildPlaylistService().getHighQualityPlayList(cateType, offset, limit);
    }

    public static Observable<String> getHighQualityPlayListType() {
        final String url = AppConstant.WY_BASE_URL + "playlist/highquality/tags";
        if (NetWorkUtil.netWorkIsConnection()) {
            return WYApiUtil.getInstance().buildPlaylistService().getHighQualityPlayListType()
                    .map(s -> {
                        CacheUtil.cacheData(url, s);
                        return s;
                    });
        } else {
            return Observable.create(emitter -> emitter.onNext(CacheUtil.getCacheData(url)));
        }
    }

    public static Observable<String> getRecommentSongList() {
        return WYApiUtil.getInstance().buildPlaylistService().getRecommentSongList();
    }

    public static Observable<String> getPlaylistRank() {
        final String url = AppConstant.WY_BASE_URL + "toplist/detail";
        if (NetWorkUtil.netWorkIsConnection()) {
            return WYApiUtil.getInstance().buildPlaylistService().getPlaylistRank()
                    .map(s -> {
                        CacheUtil.cacheData(url, s);
                        return s;
                    });
        } else {
            return Observable.create(emitter -> emitter.onNext(CacheUtil.getCacheData(url)));
        }
    }


}
