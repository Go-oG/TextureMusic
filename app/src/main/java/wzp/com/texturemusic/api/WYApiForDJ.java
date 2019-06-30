package wzp.com.texturemusic.api;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by Wang on 2017/6/9.
 * 电台API
 */

public interface WYApiForDJ {

    /**
     * 私人电台
     * 成功
     */
    @GET("radio/get")
    Observable<String> getPersonalFm();

    /**
     * 获得dj频道数据
     *
     * @param type 今日最热（0）, 本周最热（10），历史最热（20），最新节目（30）
     * @return 网页源码 需要自己解析
     * url = "http://music.163.com/discover/djradio?type=" + type + "&offset=" + offset + "&limit=" + limit;
     */
    @GET
    Observable<String> getDjChannels(@Query("type") int type,
                                     @Query("offset") int offset,
                                     @Query("limit") int limit);

    /**
     * 主播电台Fragment 界面电台个性推荐
     * url = BASE_URL + "/api/djradio/hot/v1";
     */
    @POST("djradio/hot/v1")
    Observable<String> getDjHotDatas(@Query("offset") Integer offset, @Query("limit") int limit);

    /**
     * 获得电台频道的单个节目的信息
     * url = "http://music.163.com/api/dj/program/detail?id=" + djId;
     *
     * @param programId 电台单个节目的id
     */
    @GET("dj/program/detail")
    Observable<String> getProgramDetail(@Query("id") long programId);

    /**
     * 获得推荐的电台节目
     */
    @GET("personalized/djprogram")
    Observable<String> getRecommentDjprogram();

    /**
     * 获得 精选电台
     * url = BASE_URL + "/api/djradio/recommend/v1?offset=" + offset + "&limit=" + limit;
     */
    @GET("djradio/recommend/v1")
    Observable<String> getDjRadioRecomment(@Query("offset") int offset, @Query("limit") int limit);


    /**
     * 获得电台排行数据
     *
     * @param type 0=新晋电台排行榜    1=最热电台排行榜(默认)
     *             url = BASE_URL + "/api/djradio/toplist?offset=" + offset + "&limit=" + limit + "&total=true" +&type=" + type;
     */
    @GET("djradio/toplist")
    Observable<String> getTopDjradio(@Query("offset") int offset,
                                     @Query("limit") Integer limit,
                                     @Query("type") Integer type);


    /**
     * 获得电台节目排行榜
     * url = BASE_URL + "/api/program/toplist?offset=" + offset + "&limit=" + limit;
     */
    @GET("program/toplist")
    Observable<String> getTopDjPrograme(@Query("offset") Integer offset, @Query("limit") Integer limit);


    /**
     * 获得电台分类
     */
    @GET("djradio/category/get")
    Observable<String> getDjAllType();


    /**
     * 获得电台某个分类下的普通数据
     * url = BASE_URL + "/api/djradio/recommend?cateId=" + cateId + "&offset=" + offset + "&limit=" + limit;
     *
     * @param cateId 电台类别ID
     */
    @GET("djradio/recommend")
    Observable<String> getDjDiffentTypeRecomment(@Query("cateId") String cateId,
                                                 @Query("offset") Integer offset,
                                                 @Query("limit") Integer limit);

    /**
     * 获取电台某个分类下的热门数据
     *
     * @param cateId 分类ID
     * @param offset
     * @param limit
     * @param type   类型 有0(一般使用) 1
     */
    @GET("djradio/hot")
    Observable<String> getDjDiffentTypeHotData(@Query("cateId") String cateId,
                                               @Query("offset") int offset,
                                               @Query("limit") int limit,
                                               @Query("type") int type);

    /**
     * 获得电台信息数据
     * 成功
     * url = BASE_URL + "/api/djradio/get?id=" + djId;
     *
     * @param djId 电台ID
     */
    @GET("djradio/get")
    Observable<String> getDjInfo(@Query("id") String djId);

    /**
     * 获得一个电台的所有节目
     * 成功
     *
     * @param djId
     * @param asc  asc=1 正序 asc=0 逆序
     */
    @POST("dj/program/byradio")
    Observable<String> getDjPrograme(@Query("radioId") String djId,
                                     @Query("offset") Integer offset,
                                     @Query("limit") Integer limit,
                                     @Query("asc") int asc);

}
