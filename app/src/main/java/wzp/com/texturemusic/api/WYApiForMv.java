package wzp.com.texturemusic.api;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by Wang on 2017/6/9.
 * 和MV相关的API
 */

public interface WYApiForMv {
    /**
     * 获得最新MV
     * url = BASE_URL + "/api/mv/first?offset=" + offset + "&limit=" + limit + "&area=" + type;
     *
     * @param area 地区   内地、港台 欧美 韩国 日本 （汉字）
     * 成功
     */
    @GET("mv/first")
    Observable<String> getFirstNewMv(@Query("offset") Integer offset,
                                     @Query("limit") Integer limit,
                                     @Query("area") String area);


    /**
     * 获得mv排行
     * url = BASE_URL + "/api/mv/toplist?offset=" + offset + "&limit=" + limit + "&area=" + area;
    *
     * @param area 内地 港台 欧美 日本 韩国
     * @return
     */
    @GET("mv/toplist")
    Observable<String> getTopMv(@Query("offset") Integer offset,
                                @Query("limit") Integer limit,
                                @Query("area") String area);


    /**
     * 获得MV频道中精选MV的封面数据
     * String url = BASE_URL + "/api/mv/first/allarea";
     */
    @GET("mv/first/allarea")
    Observable<String> getMvChannelsCoverData();

    /**
     * 获得全部不同分类的mv数据
     * 参数无效（不知道）
     * 只能获得默认的数据（地区：全部 类型：全部 排序：上升最快） 没有完善
     * String url = BASE_URL + "/api/mv/all";
     * asc 排序方式 0 正1反
     */
    @POST("mv/all")
    Observable<String> getAllMvData(@Query("area") String area,
                                    @Query("asc") int asc,
                                    @Query("offset") Integer offset,
                                    @Query("limit") Integer limit);


    /**
     * 获得MV信息
     * String url = "http://music.163.com/api/mv/detail?id=" + mvId + "&type=mp4";
     */
    @GET("mv/detail?type=mp4")
    Observable<String> getMvDetail(@Query("id") long mvId);

    /**
     * 获得个性推荐中的推荐MV
     * String url = BASE_URL + "/api/personalized/mv";
     */
    @GET("personalized/mv")
    Observable<String> getRecommentMv();

    /**
     * 获得mv频道中精选MV中的网易出品数据
     * String url = "http://music.163.com/api/mv/exclusive/rcmd";
     */
    @POST("mv/exclusive/rcmd")
    Observable<String> getCoverForWY(@Query("offset") Integer offset, @Query("limit") Integer limit);

    /**
     * 获得mv的分类数据
     *
     * @return
     */
    @GET("mv/tags")
    Observable<String> getMvType();


    /**
     * 获取相似的MV
     * 有效
     */
    @GET("discovery/simiMV")
    Observable<String> getSimilarMv(@Query("mvid") long mvId);


    /**
     * 获取MV播放地址
     * 如果为付费歌曲
     * 其返回的数据URL为null
     *
     * @param mvId
     * @param resolution 分辨率 240 720 1080等
     */
    @GET("song/enhance/play/mv/url")
    Observable<String> getMvPlayUrl(@Query("id") long mvId, @Query("r") Integer resolution);

    /**
     * 获取Mv的下载地址
     *
     * @param mvId
     * @param resolution 分辨率 240 720 1080等
     */
    @GET("song/enhance/download/mv/url")
    Observable<String> getMvDownloadUrl(@Query("id") long mvId, @Query("r") Integer resolution);

}
