package wzp.com.texturemusic.api;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
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
public class AlbumApiManager {

    public static Observable<String> getNewAlbum(String area, int limit, int offset) {
        return WYApiUtil.getInstance().buildAlbumService().getNewAlbum(area, limit, offset);
    }

    public static Observable<String> getRecommentNewAlbum() {
        return WYApiUtil.getInstance().buildAlbumService().getRecommentNewAlbum();
    }

    public static Observable<String> getAlbumDetail(long albumId, long albumsId, int offset, int limit) {
        return WYApiUtil.getInstance().buildAlbumService().getAlbumDetail(albumId, albumsId, offset, limit);
    }

}
