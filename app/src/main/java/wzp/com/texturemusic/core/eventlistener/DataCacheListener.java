package wzp.com.texturemusic.core.eventlistener;

import java.io.File;

import wzp.com.texturemusic.bean.MusicBean;


/**
 * Created by Wang on 2018/3/6.
 * 数据缓存接口
 */

public interface DataCacheListener {
    void onCacheProgress(float percent, File cacheFile, String url);

    /**
     * 当DataManager数据管理者被创建完成后会回调该方法
     */
    void onDataManagerIsCreate(MusicBean data);
}
