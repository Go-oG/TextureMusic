package wzp.com.texturemusic;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import okhttp3.OkHttpClient;
import wzp.com.texturemusic.api.WYApiInterceptor;


/**
 * Created by Wang on 2017/9/12.
 * 应用类
 */

public class MyApplication extends Application {
    private static Context context;
    private static volatile OkHttpClient HTTPCLIENT;
    public static volatile OkHttpClient CLIENT2;
    private static AppManager appManager;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        appManager = AppManager.getAppManager();
        HTTPCLIENT = getOkHttpClient();
        CLIENT2 = new OkHttpClient();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getInstace() {
        return context;
    }

    public static OkHttpClient getOkHttpClient() {
        if (HTTPCLIENT == null) {
            synchronized (OkHttpClient.class) {
                if (HTTPCLIENT == null) {
                    HTTPCLIENT = new OkHttpClient.Builder().
                            connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                            .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true)
                            .addInterceptor(WYApiInterceptor.getInstance())
                            .build();
                }
            }
        }
        return HTTPCLIENT;
    }

    public static OkHttpClient getOkHttpClient2() {
        if (CLIENT2 == null) {
            synchronized (OkHttpClient.class) {
                if (CLIENT2 == null) {
                    CLIENT2 = new OkHttpClient();
                }
            }
        }
        return CLIENT2;
    }

    public static AppManager getAppManager() {
        return appManager;
    }


}
