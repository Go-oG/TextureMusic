package wzp.com.texturemusic.api;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.NetWorkUtil;

/**
 * author:Go_oG
 * date: on 2018/5/20
 * packageName: wzp.com.texturemusic.api
 */
public class MvApiManager {

    public static Observable<String> getTopMv(Integer offset, int limit, String area) {
        final String url = AppConstant.WY_BASE_URL + "mv/toplist";
        if (NetWorkUtil.netWorkIsConnection()) {
            return WYApiUtil.getInstance().buildMvService().getTopMv(offset, limit, area)
                    .map(s -> {
                        CacheUtil.cacheData(url, s);
                        return s;
                    });
        } else {
            return Observable.create(emitter -> emitter.onNext(CacheUtil.getCacheData(url)));
        }
    }

    public static Observable<String> getMvChannelsCoverData() {
        final String url = AppConstant.WY_BASE_URL + "mv/first/allarea";
        if (NetWorkUtil.netWorkIsConnection()) {
            return WYApiUtil.getInstance().buildMvService().getMvChannelsCoverData()
                    .map(s -> {
                        CacheUtil.cacheData(url, s);
                        return s;
                    });
        } else {
            return Observable.create(emitter -> emitter.onNext(CacheUtil.getCacheData(url)));
        }
    }

    public static Observable<String> getAllMvData(String area, int asc, Integer offset, Integer limit) {
        final String url = AppConstant.WY_BASE_URL + "mv/all";
        if (NetWorkUtil.netWorkIsConnection()) {
            return WYApiUtil.getInstance().buildMvService().getAllMvData(area, asc, offset, limit)
                    .map(s -> {
                        CacheUtil.cacheData(url, s);
                        return s;
                    });
        } else {
            return Observable.create(emitter -> emitter.onNext(CacheUtil.getCacheData(url)));
        }
    }

    public static Observable<String> getMvDetail(long mvId) {
        return WYApiUtil.getInstance().buildMvService().getMvDetail(mvId);
    }

    public static Observable<String> getRecommentMv() {
        final String url = AppConstant.WY_BASE_URL + "personalized/mv";
        if (NetWorkUtil.netWorkIsConnection()) {
            return WYApiUtil.getInstance().buildMvService().getRecommentMv()
                    .map(s -> {
                        CacheUtil.cacheData(url, s);
                        return s;
                    });
        } else {
            return Observable.create(emitter -> emitter.onNext(CacheUtil.getCacheData(url)));
        }
    }

    public static Observable<String> getCoverForWY(Integer offset, Integer limit) {
        final String url = AppConstant.WY_BASE_URL + "mv/exclusive/rcmd";
        if (NetWorkUtil.netWorkIsConnection()) {
            return WYApiUtil.getInstance().buildMvService().getCoverForWY(offset, limit)
                    .map(s -> {
                        CacheUtil.cacheData(url, s);
                        return s;
                    });
        } else {
            return Observable.create(emitter -> emitter.onNext(CacheUtil.getCacheData(url)));
        }
    }

    public static Observable<String> getSimilarMv(long mvId) {
        return WYApiUtil.getInstance().buildMvService().getSimilarMv(mvId);
    }

    public static Observable<String> getMvPlayUrl(long mvId, Integer resolution) {
        return WYApiUtil.getInstance().buildMvService().getMvPlayUrl(mvId, resolution);
    }

    public static Observable<String> getMvDownloadUrl(long mvId, Integer resolution) {
        return WYApiUtil.getInstance().buildMvService().getMvDownloadUrl(mvId, resolution);
    }


}
