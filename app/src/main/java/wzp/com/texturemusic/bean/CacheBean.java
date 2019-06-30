package wzp.com.texturemusic.bean;

/**
 * Created by Go_oG
 * Description:缓存实体类
 * on 2018/1/23.
 */

public class CacheBean {
    private Long customMusicCacheSize;
    private Long musicCacheSize;
    private Long imgCacheSize;
    private Long videoCacheSize;
    private Boolean autoClear;

    public Long getCustomMusicCacheSize() {
        return customMusicCacheSize;
    }

    public void setCustomMusicCacheSize(Long customMusicCacheSize) {
        this.customMusicCacheSize = customMusicCacheSize;
    }

    public Long getMusicCacheSize() {
        return musicCacheSize;
    }

    public void setMusicCacheSize(Long musicCacheSize) {
        this.musicCacheSize = musicCacheSize;
    }

    public Long getImgCacheSize() {
        return imgCacheSize;
    }

    public void setImgCacheSize(Long imgCacheSize) {
        this.imgCacheSize = imgCacheSize;
    }

    public Long getVideoCacheSize() {
        return videoCacheSize;
    }

    public void setVideoCacheSize(Long videoCacheSize) {
        this.videoCacheSize = videoCacheSize;
    }

    public Boolean getAutoClear() {
        return autoClear;
    }

    public void setAutoClear(Boolean autoClear) {
        this.autoClear = autoClear;
    }
}
