package wzp.com.texturemusic.dbmodule.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Go_oG
 * Description: 类名 即为数据库名字
 * 该数据库仅作为存储本地音乐的信息
 * on 2017/10/15.
 */
@Entity
public class DbMusicEntiy {
    @Id(autoincrement = true)
    private Long musicDbId; //在本地数据库的id
    private String coverImgUrl; //封面图片地址
    private String albumName; //专辑名字
    private Long albumId; //专辑网络id
    private String musicName; //音乐名字
    private Long musicId;
    private String artistName; //艺术家名字
    private Long artistId; //可以为null 艺术家Id
    private String artistImgUrl; //艺术家图片地址
    private String musicPathUrl;
    private Boolean isLocalMusic; //标识为本地音乐还是网络音乐
    private Long updateTime;
    private Long durationTime;
    private String alias;
    private Boolean isSQMusic;
    private Boolean hasMv;
    private Long mvId;
    @Generated(hash = 1546856338)
    public DbMusicEntiy(Long musicDbId, String coverImgUrl, String albumName,
            Long albumId, String musicName, Long musicId, String artistName,
            Long artistId, String artistImgUrl, String musicPathUrl,
            Boolean isLocalMusic, Long updateTime, Long durationTime, String alias,
            Boolean isSQMusic, Boolean hasMv, Long mvId) {
        this.musicDbId = musicDbId;
        this.coverImgUrl = coverImgUrl;
        this.albumName = albumName;
        this.albumId = albumId;
        this.musicName = musicName;
        this.musicId = musicId;
        this.artistName = artistName;
        this.artistId = artistId;
        this.artistImgUrl = artistImgUrl;
        this.musicPathUrl = musicPathUrl;
        this.isLocalMusic = isLocalMusic;
        this.updateTime = updateTime;
        this.durationTime = durationTime;
        this.alias = alias;
        this.isSQMusic = isSQMusic;
        this.hasMv = hasMv;
        this.mvId = mvId;
    }
    @Generated(hash = 1686591650)
    public DbMusicEntiy() {
    }
    public Long getMusicDbId() {
        return this.musicDbId;
    }
    public void setMusicDbId(Long musicDbId) {
        this.musicDbId = musicDbId;
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
    public Long getAlbumId() {
        return this.albumId;
    }
    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }
    public String getMusicName() {
        return this.musicName;
    }
    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }
    public Long getMusicId() {
        return this.musicId;
    }
    public void setMusicId(Long musicId) {
        this.musicId = musicId;
    }
    public String getArtistName() {
        return this.artistName;
    }
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
    public Long getArtistId() {
        return this.artistId;
    }
    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }
    public String getArtistImgUrl() {
        return this.artistImgUrl;
    }
    public void setArtistImgUrl(String artistImgUrl) {
        this.artistImgUrl = artistImgUrl;
    }
    public String getMusicPathUrl() {
        return this.musicPathUrl;
    }
    public void setMusicPathUrl(String musicPathUrl) {
        this.musicPathUrl = musicPathUrl;
    }
    public Boolean getIsLocalMusic() {
        return this.isLocalMusic;
    }
    public void setIsLocalMusic(Boolean isLocalMusic) {
        this.isLocalMusic = isLocalMusic;
    }
    public Long getUpdateTime() {
        return this.updateTime;
    }
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
    public Long getDurationTime() {
        return this.durationTime;
    }
    public void setDurationTime(Long durationTime) {
        this.durationTime = durationTime;
    }
    public String getAlias() {
        return this.alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
    public Boolean getIsSQMusic() {
        return this.isSQMusic;
    }
    public void setIsSQMusic(Boolean isSQMusic) {
        this.isSQMusic = isSQMusic;
    }
    public Boolean getHasMv() {
        return this.hasMv;
    }
    public void setHasMv(Boolean hasMv) {
        this.hasMv = hasMv;
    }
    public Long getMvId() {
        return this.mvId;
    }
    public void setMvId(Long mvId) {
        this.mvId = mvId;
    }
}
