package wzp.com.texturemusic.api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Wang on 2017/6/9.
 * 评论API
 */

public interface WYAPiForComment {
    /**
     * 获得评论
     *
     * @param commentThreds 评论id
     *                      url = "http://music.163.com/api/v1/resource/comments/commentThreds/?
     *                      rid=" + commentThreds + "&offset=" + offset + "&total=" + total + "&limit=" + limit;
     *                      <p>
     *                      MV评论ID组成为 R_MV_5_ +mvId;
     */
    @GET("v1/resource/comments/{thredId}")
    Observable<String> getComments(@Path("thredId") String commentThreds,
                                   @Query("offset") int offset,
                                   @Query("limit") int limit,
                                   @Query("total") boolean total);

}
