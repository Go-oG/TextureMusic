package wzp.com.texturemusic.api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by Wang on 2017/6/9.
 * 专辑
 */

public interface WYApiForAlbum {

    /**
     * 新碟(专辑)上架
     * @param type 类型:全部（默认）= ALL   华语 =ZH   欧美 = EA  韩国=KR  日本=JP
     *成功
     */
    @GET("album/new")
    Observable<String> getNewAlbum(@Query("area") String type, @Query("limit") int limit, @Query("offset") int offset);

    /**
     * 获得专辑的所有歌曲
     * url = "http://music.163.com/api/album/" + albumId + "?ext=true&id=" +
     * albumId + "&offset=" + offset + "&total=true&limit=" + limit;
     * 成功
     */
    @GET("album/{albumId}")
    Observable<String> getAlbumAllSong(@Path("albumId") long albumId, @Query("offset") int offset, @Query("limit") Integer limit);

    /**
     * 获得最新的推荐的专辑
     */
    @GET("personalized/newalbum?type=recommend")
    Observable<String> getRecommentNewAlbum();

    /**
     * 新碟上架
     * @param year  年 2017
     * @param month 月 6
     *              成功
     */
    @POST("discovery/new/albums")
    Observable<String> getFirstNewAlbum(@Query("year") String year, @Query("month") String month);

    //获得专辑详情
    //已经包含了音乐数据
    @GET("album/{albumId}?ext=true&total=true")
    Observable<String> getAlbumDetail(@Path("albumId") long albumId, @Query("id") long albumsId, @Query("offset") int offset, @Query("limit") int limit);

}
