package wzp.com.texturemusic.dbmodule.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Wang on 2018/3/18.
 * 本地播放记录的数据库
 * 用于记录播放历史
 */
@Entity
public class DbHistoryEntiy {
    @Id(autoincrement = true)
    private Long dbId;
    private Long updateTimme;
    private Long musicId;
    private Boolean localMusic;
    private String musicName;
    private String artistName;
    private String albumName;
    private Long artistId;
    private Long albumId;
    private Long duration;
    private Boolean hasMv;
    private Long mvId;
    private String commentId;
    private String coverImgUrl;
    private String playPath;
    private Boolean isSQMusic;
    private String alias;
    @Generated(hash = 2002454899)
    public DbHistoryEntiy(Long dbId, Long updateTimme, Long musicId,
            Boolean localMusic, String musicName, String artistName,
            String albumName, Long artistId, Long albumId, Long duration,
            Boolean hasMv, Long mvId, String commentId, String coverImgUrl,
            String playPath, Boolean isSQMusic, String alias) {
        this.dbId = dbId;
        this.updateTimme = updateTimme;
        this.musicId = musicId;
        this.localMusic = localMusic;
        this.musicName = musicName;
        this.artistName = artistName;
        this.albumName = albumName;
        this.artistId = artistId;
        this.albumId = albumId;
        this.duration = duration;
        this.hasMv = hasMv;
        this.mvId = mvId;
        this.commentId = commentId;
        this.coverImgUrl = coverImgUrl;
        this.playPath = playPath;
        this.isSQMusic = isSQMusic;
        this.alias = alias;
    }
    @Generated(hash = 2005291414)
    public DbHistoryEntiy() {
    }
    public Long getDbId() {
        return this.dbId;
    }
    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }
    public Long getUpdateTimme() {
        return this.updateTimme;
    }
    public void setUpdateTimme(Long updateTimme) {
        this.updateTimme = updateTimme;
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
    public String getMusicName() {
        return this.musicName;
    }
    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }
    public String getArtistName() {
        return this.artistName;
    }
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
    public String getAlbumName() {
        return this.albumName;
    }
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
    public Long getArtistId() {
        return this.artistId;
    }
    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }
    public Long getAlbumId() {
        return this.albumId;
    }
    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }
    public Long getDuration() {
        return this.duration;
    }
    public void setDuration(Long duration) {
        this.duration = duration;
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
    public String getCommentId() {
        return this.commentId;
    }
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
    public String getCoverImgUrl() {
        return this.coverImgUrl;
    }
    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }
    public String getPlayPath() {
        return this.playPath;
    }
    public void setPlayPath(String playPath) {
        this.playPath = playPath;
    }
    public Boolean getIsSQMusic() {
        return this.isSQMusic;
    }
    public void setIsSQMusic(Boolean isSQMusic) {
        this.isSQMusic = isSQMusic;
    }
    public String getAlias() {
        return this.alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }

   


}
