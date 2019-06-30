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
public class DJApiManager {


    public static Observable<String> getDjHotDatas(Integer offset, int limit) {
        final String url = AppConstant.WY_BASE_URL + "djradio/hot/v1";
        if (NetWorkUtil.netWorkIsConnection()) {
            return WYApiUtil.getInstance().buildDjService().getDjHotDatas(offset, limit)
                    .map(s -> {
                        CacheUtil.cacheData(url, s);
                        return s;
                    });
        } else {
            return Observable.create(emitter -> emitter.onNext(CacheUtil.getCacheData(url)));
        }
    }


    public static Observable<String> getProgramDetail(long programId) {
        return WYApiUtil.getInstance().buildDjService().getProgramDetail(programId);
    }


    public static Observable<String> getRecommentDjprogram() {
        return WYApiUtil.getInstance().buildDjService().getRecommentDjprogram();
    }


    public static Observable<String> getTopDjradio(int offset, Integer limit, Integer type) {
        final String url = AppConstant.WY_BASE_URL + "djradio/toplist";
        if (NetWorkUtil.netWorkIsConnection()) {
            return WYApiUtil.getInstance().buildDjService().getTopDjradio(offset, limit, type)
                    .map(s -> {
                        CacheUtil.cacheData(url, s);
                        return s;
                    });
        } else {
            return Observable.create(emitter -> emitter.onNext(CacheUtil.getCacheData(url)));
        }
    }


    public static Observable<String> getTopDjPrograme(int offset, Integer limit) {
        final String url = AppConstant.WY_BASE_URL + "program/toplist";
        if (NetWorkUtil.netWorkIsConnection()) {
            return WYApiUtil.getInstance().buildDjService().getTopDjPrograme(offset, limit)
                    .map(new Function<String, String>() {
                        @Override
                        public String apply(String s) throws Exception {
                            CacheUtil.cacheData(url, s);
                            return s;
                        }
                    });
        } else {
            return Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                    emitter.onNext(CacheUtil.getCacheData(url));
                }
            });
        }
    }


    public static Observable<String> getDjDiffentTypeRecomment(String cateId, int offset, Integer limit) {
        return WYApiUtil.getInstance().buildDjService().getDjDiffentTypeRecomment(cateId, offset, limit);
    }


    public static Observable<String> getDjDiffentTypeHotData(String cateId, int offset, Integer limit, int type) {
        return WYApiUtil.getInstance().buildDjService().getDjDiffentTypeHotData(cateId, offset, limit, type);
    }


    public static Observable<String> getDjInfo(String djId) {
        return WYApiUtil.getInstance().buildDjService().getDjInfo(djId);
    }

    public static Observable<String> getDjPrograme(String djId, Integer offset, Integer limit, int asc) {
        return WYApiUtil.getInstance().buildDjService().getDjPrograme(djId, offset, limit, asc);
    }


}
