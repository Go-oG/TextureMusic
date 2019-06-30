package wzp.com.texturemusic.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import wzp.com.texturemusic.util.NetWorkUtil;

/**
 * Created by Go_oG
 * Description:自定义拦截器
 * on 2017/10/4.
 */

public class WYApiInterceptor implements Interceptor {
    private volatile static WYApiInterceptor INSTANCE;

    private WYApiInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (NetWorkUtil.netWorkIsConnection()) {
            Request.Builder builder = chain.request().newBuilder();
            builder.header("referer", "http://music.163.com/")
                    .header("Host", "music.163.com")
                    .header("Cookie", "appver=2.0.2");
            return chain.proceed(builder.build());
        } else {
            NoNetworkException exception = new NoNetworkException();
            exception.setErrorCode(0);
            exception.setMsg("网络连接失败,请稍后再试");
            throw exception;
        }
    }


    public static WYApiInterceptor getInstance() {
        if (INSTANCE == null) {
            synchronized (WYApiInterceptor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WYApiInterceptor();
                }
            }
        }
        return INSTANCE;
    }

}
