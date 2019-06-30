package wzp.com.texturemusic.dbmodule.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Go_oG
 * Description:下载歌曲的数据库
 * 用于记录下载完文件信息
 * on 2017/11/20.
 */
@Entity
public class DbDownloadEntiy {
    @Id(autoincrement = true)
    private Long dbId;
    private String name;
    private String filePath;
    private String artistName;
    private String albumName;
    private Long artistId;
    private Long albumId;
    private Long musicId;
    private Long mvId;
    private Boolean isLocalAlbum;
    private Boolean isLocalArtist;
    private Boolean isMv;//标识是Mv还是音乐
    private String alias;
    private Long fileSize;
    private String musicInfo;//json字符串
    private String mvInfo;//json字符串
    private Long downloadTime;//完成时间
    private String coverImgUrl;
    @Generated(hash = 1010150446)
    public DbDownloadEntiy(Long dbId, String name, String filePath,
            String artistName, String albumName, Long artistId, Long albumId,
            Long musicId, Long mvId, Boolean isLocalAlbum, Boolean isLocalArtist,
            Boolean isMv, String alias, Long fileSize, String musicInfo,
            String mvInfo, Long downloadTime, String coverImgUrl) {
        this.dbId = dbId;
        this.name = name;
        this.filePath = filePath;
        this.artistName = artistName;
        this.albumName = albumName;
        this.artistId = artistId;
        this.albumId = albumId;
        this.musicId = musicId;
        this.mvId = mvId;
        this.isLocalAlbum = isLocalAlbum;
        this.isLocalArtist = isLocalArtist;
        this.isMv = isMv;
        this.alias = alias;
        this.fileSize = fileSize;
        this.musicInfo = musicInfo;
        this.mvInfo = mvInfo;
        this.downloadTime = downloadTime;
        this.coverImgUrl = coverImgUrl;
    }
    @Generated(hash = 1967202565)
    public DbDownloadEntiy() {
    }
    public Long getDbId() {
        return this.dbId;
    }
    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
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
    public Long getMusicId() {
        return this.musicId;
    }
    public void setMusicId(Long musicId) {
        this.musicId = musicId;
    }
    public Long getMvId() {
        return this.mvId;
    }
    public void setMvId(Long mvId) {
        this.mvId = mvId;
    }
    public Boolean getIsLocalAlbum() {
        return this.isLocalAlbum;
    }
    public void setIsLocalAlbum(Boolean isLocalAlbum) {
        this.isLocalAlbum = isLocalAlbum;
    }
    public Boolean getIsLocalArtist() {
        return this.isLocalArtist;
    }
    public void setIsLocalArtist(Boolean isLocalArtist) {
        this.isLocalArtist = isLocalArtist;
    }
    public Boolean getIsMv() {
        return this.isMv;
    }
    public void setIsMv(Boolean isMv) {
        this.isMv = isMv;
    }
    public String getAlias() {
        return this.alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
    public Long getFileSize() {
        return this.fileSize;
    }
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    public String getMusicInfo() {
        return this.musicInfo;
    }
    public void setMusicInfo(String musicInfo) {
        this.musicInfo = musicInfo;
    }
    public String getMvInfo() {
        return this.mvInfo;
    }
    public void setMvInfo(String mvInfo) {
        this.mvInfo = mvInfo;
    }
    public Long getDownloadTime() {
        return this.downloadTime;
    }
    public void setDownloadTime(Long downloadTime) {
        this.downloadTime = downloadTime;
    }
    public String getCoverImgUrl() {
        return this.coverImgUrl;
    }
    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }


    @Override
    public String toString() {
        return "DbDownloadEntiy INFO path="+filePath+" name="+name;
    }
}
