package wzp.com.texturemusic;

import android.content.Context;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.config.AppFileConstant;
import wzp.com.texturemusic.util.SPSetingUtil;


/**
 * Created by Wang on 2017/9/12.
 * Glide 的自定义配置
 */
@GlideModule
public class MyGlideModule extends AppGlideModule {

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        int memoryCacheSize = 40 * 1024 * 1024;//40Mb
        builder.setMemoryCache(new LruResourceCache(memoryCacheSize));
        //默认为200MB
        int size = SPSetingUtil.getIntValue(AppConstant.SP_KEY_IMG_CACHE_SIZE, AppConstant.DEFAULT_IMG_CACHE_SIZE);
        int diskCacheSize = size * 1024 * 1024;
        //如果文件夹不存在
        //则在第一次进入APp时会出现无法加载图片的情况
        builder.setDiskCache(new DiskLruCacheFactory(AppFileConstant.IMAGE_CACHE_DRESS, diskCacheSize));
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
    }

}
