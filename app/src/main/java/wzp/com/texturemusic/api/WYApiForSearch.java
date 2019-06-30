package wzp.com.texturemusic.api;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by Wang on 2017/6/9.
 * 搜索API
 */

public interface WYApiForSearch {
    /**
     * 搜索数据
     *
     * @param searchString 要搜索的词
     * @param type         搜索 单曲(1)，歌手(100)，专辑(10)，歌单(1000)，用户(1002)
     *                     mv(1004)  dj 1009
     */
    @POST("search/get")
    Observable<String> getSearchData(@Query("s") String searchString,
                                     @Query("type") int type,
                                     @Query("offset") int offset,
                                     @Query("total") boolean total,
                                     @Query("limit") int limit);
    /**
     * 搜索多重匹配
     * 可以用来匹配本地数据
     */
    @GET("search/suggest/multimatch?type=1")
    Observable<String> getSearchMultimatch(@Query("s") String searchStr);


    /**
     * 搜索建议
     */
    @POST("search/suggest/web?type=1")
    Observable<String> getSearchSuggest(@Query("s") String searchStr);

    /**
     * 热门搜索词汇
     */
    @GET("search/hot?type=1111")
    Observable<String> getHotSearch();


}
