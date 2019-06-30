package wzp.com.texturemusic.api;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.NetWorkUtil;

/**
 * author:Go_oG
 * date: on 2018/5/20
 * packageName: wzp.com.texturemusic.api
 */
public class CommentApiManager {

    public static Observable<String> getComments(String commentThreds, int offset, int limit, boolean total) {
        return WYApiUtil.getInstance().buildCommentService().getComments(commentThreds,offset, limit,total);
    }
}
