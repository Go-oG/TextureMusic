package wzp.com.texturemusic.dbmodule.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Wang on 2018/3/18.
 * 本地收藏信息数据库的实体类
 * 用于记录收藏的歌曲与歌单
 */
@Entity
public class DbCollectMusicEntiy {
    @Id(autoincrement = true)
    private Long dbId;
    private String musicName;
    private String albumName;
    private String artistName;
    private Boolean localMusic;
    private Long musicId;
    private Long albumId;
    private Long artistId;
    private String coverImgUrl;
    @Generated(hash = 2143104544)
    public DbCollectMusicEntiy(Long dbId, String musicName, String albumName,
            String artistName, Boolean localMusic, Long musicId, Long albumId,
            Long artistId, String coverImgUrl) {
        this.dbId = dbId;
        this.musicName = musicName;
        this.albumName = albumName;
        this.artistName = artistName;
        this.localMusic = localMusic;
        this.musicId = musicId;
        this.albumId = albumId;
        this.artistId = artistId;
        this.coverImgUrl = coverImgUrl;
    }
    @Generated(hash = 882268851)
    public DbCollectMusicEntiy() {
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
    public Boolean getLocalMusic() {
        return this.localMusic;
    }
    public void setLocalMusic(Boolean localMusic) {
        this.localMusic = localMusic;
    }
    public Long getMusicId() {
        return this.musicId;
    }
    public void setMusicId(Long musicId) {
        this.musicId = musicId;
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
    public String getCoverImgUrl() {
        return this.coverImgUrl;
    }
    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }
 

}
