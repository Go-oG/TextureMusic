package wzp.com.texturemusic.api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Wang on 2017/6/9.
 * 歌手API
 */

public interface WYApiForArtist {
    /**
     * 获得热门歌手
     * "http://music.163.com/api/artist/top?offset=" + offset + "&total=false&limit=" + limit;
     */
    @GET("artist/top")
    Observable<String> getHotArtists(@Query("offset") Integer offset, @Query("limit") Integer limit);

    /**
     * 获得歌手的热门歌曲
     * String url = "http://music.163.com/api/artist/" + artist_id;
     */
    @GET("artist/{artistId}")
    Observable<String> getArtistsHotSong(@Path("artistId") String artist_id);

    /**
     * 获取歌手全部的歌曲
     *
     * @param artistId
     * @param offset
     * @param limit
     * @return
     */
    @POST("v1/artist/songs")
    Observable<String> getArtistAllSong(@Query("id") String artistId,
                                        @Query("offset") int offset,
                                        @Query("limit") int limit);


    /**
     * 获得歌手的专辑
     * url = "http://music.163.com/api/artist/albums/" + artistId + "?offset=" + offset + "&limit=" + limit;
     */
    @GET("artist/albums/{artistId}")
    Observable<String> getArtistAlbum(@Path("artistId") String artistId,
                                      @Query("offset") int offset,
                                      @Query("limit") int limit);

    /**
     * 获得艺术家的介绍
     * url = BASE_URL + "/api/artist/introduction?id=" + artistId;
     */
    @GET("artist/introduction")
    Observable<String> getArtistDescription(@Query("id") String artistId);

    /**
     * 获得艺术家的mv
     * 成功
     */
    @GET("artist/mvs?total=true")
    Observable<String> getArtistMv(@Query("artistId") String artistId,
                                   @Query("offset") Integer offset,
                                   @Query("limit") Integer limit);

    /**
     * 获得艺术家的列表
     * 但是数据顺序基本不会变
     *
     * @param offset
     * @param limit
     * @return 成功
     */
    @GET("v1/artist/list")
    Observable<String> getArtistList(@Query("offset") int offset,
                                     @Query("limit") int limit);

    /**
     * 获取不同类别的歌手列表
     *
     * @param categoryCode 列表标识符
     *                     入驻歌手 5001          华语男歌手 1001
     *                     华语女歌手 1002        华语组合/乐队 1003
     *                     欧美男歌手 2001        欧美女歌手 2002
     *                     欧美组合/乐队 2003     日本男歌手 6001
     *                     日本女歌手 6002        日本组合/乐队 6003
     *                     韩国男歌手 7001        韩国女歌手 7002
     *                     韩国组合/乐队 7003     其他男歌手 4001
     *                     其他女歌手 4002        其他组合/乐队 4003
     * @param offset
     * @param limit
     * @return 成功
     */
    @POST("artist/list")
    Observable<String> getDifferentArtist(@Query("categoryCode") int categoryCode,
                                          @Query("offset") int offset,
                                          @Query("limit") int limit ,
                                          @Query("total") boolean total);

    /**
     * 获取歌手的排行榜
     * @param type
     * 华语==1  欧美 ===2  韩国====3   日本====4
     * @param total
     * @return
     */
    @POST("toplist/artist")
    Observable<String> getArtistTopList(@Query("type") int type, @Query("total") boolean total);

}
