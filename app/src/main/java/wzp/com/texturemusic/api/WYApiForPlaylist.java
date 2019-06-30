package wzp.com.texturemusic.api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by Wang on 2017/6/9.
 * 歌单API
 */

public interface WYApiForPlaylist {

    /**
     * 获得某个类别的歌单数据
     * 成功
     * String url = "http://music.163.com/api/playlist/list?cat=" + categoy + "&order=" + order +
     * "&offset=" + offset + "&total=true&limit=" + limit;
     *
     * @param categoy 歌单类别 如 全部 华语
     * @param order   歌单类型 如hot
     */
    @GET("playlist/list?total=true")
    Observable<String> getDeffrentTypePlayList(@Query("cat") String categoy,
                                               @Query("order") String order,
                                               @Query("offset") Integer offset,
                                               @Query("limit") Integer limit);

    /**
     * 获得歌单详情
     * 成功
     * 有些情况出现只有一首歌
     *
     * @param playListId 歌单id
     */
    @GET("playlist/detail")
    Observable<String> getPlayListDetail(@Query("id") String playListId);

    /**
     * 获取歌单详情v3版
     * 其中t可能为时间
     * http://music.163.com/api/v3/playlist/detail?c=[]&s=5&t&id=1987589980&n=1000
     */
    @GET("v3/playlist/detail?c=[]&s=5&t=&n=1000")
    Observable<String> getPlayListDetailV3(@Query("id") String playlistId);

    /**
     * 获取精品歌单
     * 成功
     *
     * @param cateType 类型 欧美等
     */
    @GET("playlist/highquality/list")
    Observable<String> getHighQualityPlayList(@Query("cat") String cateType,
                                              @Query("offset") Integer offset,
                                              @Query("limit") Integer limit);

    @GET("playlist/highquality/tags")
    Observable<String> getHighQualityPlayListType();


    /**
     * 获得个性推荐歌单
     * 成功
     */
    @GET("personalized/playlist")
    Observable<String> getRecommentSongList();


    /**
     * 获得歌单排行榜的数据
     */
    @GET("toplist/detail")
    Observable<String> getPlaylistRank();


    /**
     * 获得歌单的分类数据
     */
    @GET("playlist/catalogue/v1")
    Observable<String> getPlaylistType();

    /**
     * 获得歌单的热门分类
     */
    @GET("playlist/hottags")
    Observable<String> getPlaylistHotType();

}
