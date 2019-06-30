package wzp.com.texturemusic.api;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by Wang on 2017/6/9.
 * 用户API
 */

public interface WYApiForUser {
    /**
     * 获得用户的歌单 GET请求
     * 成功
     *
     * @param userID
     * @return
     */
    @GET("user/playlist")
    Observable<String> getUserPlayList(@Query("uid") long userID, @Query("offset") int offset, @Query("limit") int limit);

    /**
     * 获得用户电台
     * 成功
     */
    @POST("dj/program/{userId}")
    Observable<String> getUserDjRadio(@Path("userId") long uid,
                                      @Query("offset") int offset,
                                      @Query("limit") int limit);

    /**
     * 获得用户关注的列表
     * 成功
     */
    @GET("user/getfollows/{userId}")
    Observable<String> getUserFollws(@Path("userId") long userid,
                                     @Query("offset") int offset,
                                     @Query("limit") int limit,
                                     @Query("order") boolean order);

    /**
     * 获得用户粉丝数
     * 成功
     */
    @GET("user/getfolloweds")
    Observable<String> getUserFollweds(@Query("userId") long userid,
                                       @Query("offset") Integer offset,
                                       @Query("limit") Integer limit);

    /**
     * 获得独家放送
     * 成功
     */
    @GET("personalized/privatecontent")
    Observable<String> getPrivateContent();


    /**
     * 获取用户部分详情
     *
     * @param userId
     * @return
     */
    @GET("v1/user/detail/{userId}")
    Observable<String> getUserDetail(@Path("userId") long userId);

    @GET("event/get/{userId}")
    Observable<String> getUserEvent(@Path("userId") long userId, @Query("offset") int offset, @Query("limit") int limit);
}
