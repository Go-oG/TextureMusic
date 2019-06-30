package wzp.com.texturemusic.core.manger;

import android.net.Uri;
import androidx.annotation.Nullable;


import java.io.File;

import wzp.com.texturemusic.MyApplication;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.config.AppFileConstant;
import wzp.com.texturemusic.core.eventlistener.DataCacheListener;
import wzp.com.texturemusic.core.videocache.CacheListener;
import wzp.com.texturemusic.core.videocache.HttpProxyCacheServer;
import wzp.com.texturemusic.core.videocache.file.FileNameGenerator;
import wzp.com.texturemusic.dbmodule.util.DbCacheUtil;
import wzp.com.texturemusic.util.SPSetingUtil;

/**
 * Created by Go_oG
 * Description:缓存帮助类
 * on 2018/1/7.
 */

public class CacheManager {
    public static final String CACHE_END_TAG_MC = "mc";
    public static final String CACHE_END_TAG_MV = "mv";
    private static final String[] musicFileType = AppConstant.MUSIC_FILE_TYPE;
    private HttpProxyCacheServer proxyCacheServer;
    private static CacheManager cacheManager;
    private CacheListener cacheListener;
    private DataCacheListener dataCallback;
    private MusicBean currentCacheMusic;

    /**
     * 用于更改缓存的命名方式
     */
    private static class MyFileNameGenerator implements FileNameGenerator {

        @Override
        public String generate(String url) {
            return getCacheKey(url);
        }
    }

    private class MyCacheListener implements CacheListener {
        @Override
        public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
            if (dataCallback != null) {
                dataCallback.onCacheProgress(percentsAvailable, cacheFile, url);
            }
            if (percentsAvailable == 100) {
                //文件缓存完成
                proxyCacheServer.unregisterCacheListener(cacheListener, url);
                String cacheKey = getCacheKey(url);
                if (cacheKey.endsWith(CACHE_END_TAG_MC)){
                    DbCacheUtil.addCacheData(currentCacheMusic, cacheKey);
                }
            }
        }
    }

    private CacheManager() {
        createCacheServiceIfNeed();
    }

    public static CacheManager getInstance() {
        if (cacheManager == null) {
            synchronized (CacheManager.class) {
                if (cacheManager == null) {
                    cacheManager = new CacheManager();
                }
            }
        }
        return cacheManager;
    }

    public void releaseSouce() {
        if (proxyCacheServer != null) {
            proxyCacheServer.shutdown();
            proxyCacheServer = null;
        }
        cacheListener = null;
        dataCallback = null;
        currentCacheMusic = null;
    }

    public String getCacheUrl(MusicBean bean, @Nullable DataCacheListener callback) {
        currentCacheMusic = bean;
        createCacheServiceIfNeed();
        if (bean.getPlayPath() == null) {
            throw new NullPointerException("将要缓存的歌曲的网络地址不能为空");
        }
        String netUrl = bean.getPlayPath();
        String url = "";
        if (!bean.getLocalMusic()) {
            url = proxyCacheServer.getProxyUrl(netUrl);
        }
        dataCallback = callback;
        proxyCacheServer.registerCacheListener(cacheListener, netUrl);
        return url;
    }

    public String getCacheUrl(String mvPath, @Nullable DataCacheListener callback) {
        createCacheServiceIfNeed();
        String url = proxyCacheServer.getProxyUrl(mvPath);
        dataCallback = callback;
        proxyCacheServer.registerCacheListener(cacheListener, mvPath);
        return url;
    }

    public void stopCacheCurrentData() {
        if (proxyCacheServer != null) {
            proxyCacheServer.shutdownClients();
        }
    }

    private void createCacheServiceIfNeed() {
        if (proxyCacheServer == null) {
            synchronized (HttpProxyCacheServer.class) {
                if (proxyCacheServer == null) {
                    int cacheSize = SPSetingUtil.getIntValue(AppConstant.SP_KEY_MUSIC_CACHE_SIZE, AppConstant.DEFAULT_MUSIC_CACHE_SIZE);
                    MyFileNameGenerator fileNameGenerator = new MyFileNameGenerator();
                    proxyCacheServer = new HttpProxyCacheServer.Builder(MyApplication.getInstace())
                            .maxCacheSize(cacheSize * 1024 * 1024) //500MB
                            .fileNameGenerator(fileNameGenerator)
                            .cacheDirectory(new File(AppFileConstant.MEDIA_CACHE_DRESS))
                            .build();
                    cacheListener = new MyCacheListener();
                }
            }
        }
    }

    /**
     * 获取缓存的URL
     *
     * @param netUrl 网络Url地址
     * @return 返回缓存文件的关键值
     */
    public static String getCacheKey(String netUrl) {
        Uri uri = Uri.parse(netUrl);
        String cacheUrl = uri.getLastPathSegment();
        if (cacheUrl.endsWith(".mp4") || cacheUrl.endsWith(".flv")) {
            //缓存的是MV
            cacheUrl = cacheUrl.substring(0, cacheUrl.length() - 4) + CACHE_END_TAG_MV;
        } else {
            //缓存的是Music
            int size = musicFileType.length;
            for (int i = 0; i < size; i++) {
                if (cacheUrl.endsWith(musicFileType[i])) {
                    cacheUrl = cacheUrl.substring(0, cacheUrl.length() - musicFileType[i].length()) + CACHE_END_TAG_MC;
                    break;
                }
            }
        }
        return cacheUrl;
    }

}
