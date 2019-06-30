package wzp.com.texturemusic.api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;


/**
 * Created by Wang on 2017/6/9.
 * 单曲API
 */

public interface WYApiForSong {
    /**
     * 获得歌曲详情
     * GET
     * 成功
     * String url = "http://music.163.com/api/song/detail?id=" + musicid + "&ids=[" + musicid + "]";
     * 需特别注意后面有[]
     *
     * @param musicid
     */
    @GET("song/detail")
    Observable<String> getSongDetatil(@Query("id") String musicid, @Query("ids") String musicId);

    /**
     * 获得歌曲的歌词
     * 成功
     * String url = "http://music.163.com/api/song/lyric?os=osx&id=" + musicId + "&lv=-1&kv=-1&tv=-1";
     */
    @GET("song/lyric?os=osx&lv=-1&kv=-1&tv=-1")
    Observable<String> getSongLyric(@Query("id") String musicId);

    /**
     * 获得推荐的新音乐
     * 成功
     */
    @GET("personalized/newsong?type=recommend")
    Observable<String> getRecommentNewSongs();

    /**
     * 获得音乐的播放地址
     * <p>
     * //   * @param musicId
     * //  * @param bitrate 比特率 高：320000 中：160000 低：96000
     * Observable<String> getMusicUrl(@Path("idss") String musicId, @Query("br") Integer bitrate);
     *    String musicUrl = AppConstant.WY_BASE_URL + "song/enhance/player/url?ids=[" + bean.getMusicId() + "]" + "&br=96000";
     * @return
     */
    @GET
    Observable<String> getMusicUrl(@Url String musiclUrl);

    /**
     * 获得最新音乐成功
     * 参数 areaId ===0 为华语    areaId==8为日本    areaId===16为韩国  areaId====96为欧美
     */
    @POST("v1/discovery/new/songs")
    Observable<String> getFirstNewMusic(@Query("areaId") String areaId);

    /**
     * 新歌上架
     * 参数有 type area cat 其中 每个的取值都必须为 ALL ZH EA KR JP
     * 中的一个且必须三个参数选择都一样
     *参数 areaId
     * @param type ALL 、ZH、EA、KR、JP
     */
    @POST("v1/discovery/new/songs?type={type}&area={type}&cat={type}")
    Observable<String> getNewMusic(@Path("type") String type);

    /**
     * 获取歌曲下载地址
     * 如果为付费歌曲
     * 其返回的数据URL为null
     *
     * @return
     */
    @GET("song/enhance/download/url")
    Observable<String> getDownloadUrl(@Query("id") String musicId, @Query("br") int bit);

    /**
     * 获取相似歌曲
     */
    @GET("v1/discovery/simiSong")
    Observable<String> getSimiMusic(@Query("songid") String musicId, @Query("offset") int offset, @Query("limit") int limit);

}
