package wzp.com.texturemusic.dbmodule.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 专门用于保存缓存的信息
 */
@Entity
public class DbCacheEntiy {
    @Id(autoincrement = true)
    private Long dbId;
    private String musicName;
    //用于标识该歌曲在磁盘中的缓存字段
    private String cacheKey;
    private Long musicId;
    private Boolean localMusic;
    private String playPath;
    private Long durationTime;
    private String coverImgUrl;
    private String albumName;
    private String artistName;
    private Long albumId;
    private Long artistId;
    @Generated(hash = 1539487646)
    public DbCacheEntiy(Long dbId, String musicName, String cacheKey, Long musicId,
            Boolean localMusic, String playPath, Long durationTime,
            String coverImgUrl, String albumName, String artistName, Long albumId,
            Long artistId) {
        this.dbId = dbId;
        this.musicName = musicName;
        this.cacheKey = cacheKey;
        this.musicId = musicId;
        this.localMusic = localMusic;
        this.playPath = playPath;
        this.durationTime = durationTime;
        this.coverImgUrl = coverImgUrl;
        this.albumName = albumName;
        this.artistName = artistName;
        this.albumId = albumId;
        this.artistId = artistId;
    }
    @Generated(hash = 516505819)
    public DbCacheEntiy() {
    }
    public Long getDbId() {
        return this.dbId;
    }
    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }
    public String getMusicName() {
        return this.musicName;
    }
    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }
    public String getCacheKey() {
        return this.cacheKey;
    }
    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }
    public Long getMusicId() {
        return this.musicId;
    }
    public void setMusicId(Long musicId) {
        this.musicId = musicId;
    }
    public Boolean getLocalMusic() {
        return this.localMusic;
    }
    public void setLocalMusic(Boolean localMusic) {
        this.localMusic = localMusic;
    }
    public String getPlayPath() {
        return this.playPath;
    }
    public void setPlayPath(String playPath) {
        this.playPath = playPath;
    }
    public Long getDurationTime() {
        return this.durationTime;
    }
    public void setDurationTime(Long durationTime) {
        this.durationTime = durationTime;
    }
    public String getCoverImgUrl() {
        return this.coverImgUrl;
    }
    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }
    public String getAlbumName() {
        return this.albumName;
    }
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
    public String getArtistName() {
        return this.artistName;
    }
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
    public Long getAlbumId() {
        return this.albumId;
    }
    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }
    public Long getArtistId() {
        return this.artistId;
    }
    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    @Override
    public String toString() {
        return "DbCacheEntiy ["+playPath+"  cacheKey"+cacheKey+"]";
    }
}
