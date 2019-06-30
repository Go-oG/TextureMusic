package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Wang on 2017/9/12.
 * 全局的音乐实体类
 */

public class MusicBean implements Parcelable, Cloneable {
    private Long dbId;//在本地数据库的id
    private Long allTime;//播放时长毫秒计算
    private Boolean isLocalMusic;//标识为本地音乐还是网络音乐
    private String coverImgUrl;//封面图片地址
    private String albumName;//专辑名字
    private String albumImgUrl;//专辑图片地址 可以为空
    private String albumId;//可以为null
    private String musicName;//音乐名字
    private String musicId;
    private String artistName;//艺术家名字
    private String artistId;//可以为null 艺术家Id
    private String artistImgUrl;//艺术家图片地址
    private String playPath;//音乐播放地址
    private String downloadUrl;//下载地址
    private String commentId;//评论ID 只有在musicType=NETWORK时才有效 可以为null
    private String subCommentId;
    private Integer commentCount;//评论数 可以为null
    private Boolean hasMV;//是否有MV
    private MvBean mvBean;
    private Boolean hasCheck;//用于在recycleview中标识当前item是否被选中了
    private String musicType;//用于标识Music的类型为 MP3\flv还是其他
    private LyricBean lyricBean;//歌词实体类
    private Integer musicBitrate;//码率
    private Boolean isNextPlay;//下一曲播放
    private Boolean isLocalAlbum;//是否为本地专辑
    private Boolean isLocalArtist;//是否为本地歌手
    private Long musicFileSize;//音乐文件大小
    private String alias;//音乐其他简略信息
    private Boolean isSQMusic;//是否为SQ品质音乐
    private Boolean liked;//是否为喜欢的音乐
    //标识音乐的来源是属于电台还是普通的音乐
    //类型在AppConstant中定义
    private Integer musicOrigin;
    //电台节目的ID由于音乐有可能来自电台但是电台音乐的Id和获取播放地址的ID以及评论地址ID不同
    //因此需要一个额外字段
    private Long djProgramId;

    //代理地址
    private String proxyUrl;


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MusicBean) {
            MusicBean bean = (MusicBean) obj;
            boolean checkOrigin = bean.musicOrigin != null && this.musicOrigin != null
                    && (bean.musicOrigin.equals(this.musicOrigin));
            if ((bean.musicOrigin == null && this.musicOrigin == null) || checkOrigin) {
                boolean checkEquals = (bean.isLocalMusic != null && this.isLocalMusic != null)
                        && (bean.getLocalMusic().equals(this.isLocalMusic));
                if (checkEquals) {
                    if (bean.getLocalMusic()) {
                        return bean.getMusicName().equals(this.getMusicName()) &&
                                bean.getPlayPath().equals(this.getPlayPath());
                    } else {
                        return bean.getMusicName().equals(this.getMusicName()) &&
                                bean.getMusicId().equals(this.getMusicId());
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (isLocalMusic != null) {
            if (isLocalMusic) {
                if (this.getPlayPath() != null) {
                    return this.getPlayPath().hashCode();
                }
            } else {
                if (this.getMusicId() != null) {
                    return this.getMusicId().hashCode();
                }
            }
        }
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "musicName=" + musicName + "  albumName=" + albumName + "  artistName=" + artistName + "  playPath=" + playPath +
                "  coverImgPath=" + coverImgUrl;
    }

    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public Long getAllTime() {
        return allTime;
    }

    public void setAllTime(Long allTime) {
        this.allTime = allTime;
    }

    public Boolean getLocalMusic() {
        if (isLocalMusic==null){
            return false;
        }
        return isLocalMusic;
    }

    public void setLocalMusic(Boolean localMusic) {
        isLocalMusic = localMusic;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumImgUrl() {
        return albumImgUrl;
    }

    public void setAlbumImgUrl(String albumImgUrl) {
        this.albumImgUrl = albumImgUrl;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getArtistImgUrl() {
        return artistImgUrl;
    }

    public void setArtistImgUrl(String artistImgUrl) {
        this.artistImgUrl = artistImgUrl;
    }

    public String getPlayPath() {
        return playPath;
    }

    public void setPlayPath(String playPath) {
        this.playPath = playPath;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getSubCommentId() {
        return subCommentId;
    }

    public void setSubCommentId(String subCommentId) {
        this.subCommentId = subCommentId;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Boolean getHasMV() {
        return hasMV;
    }

    public void setHasMV(Boolean hasMV) {
        this.hasMV = hasMV;
    }

    public MvBean getMvBean() {
        return mvBean;
    }

    public void setMvBean(MvBean mvBean) {
        this.mvBean = mvBean;
    }

    public Boolean getHasCheck() {
        return hasCheck;
    }

    public void setHasCheck(Boolean hasCheck) {
        this.hasCheck = hasCheck;
    }

    public String getMusicType() {
        return musicType;
    }

    public void setMusicType(String musicType) {
        this.musicType = musicType;
    }

    public LyricBean getLyricBean() {
        return lyricBean;
    }

    public void setLyricBean(LyricBean lyricBean) {
        this.lyricBean = lyricBean;
    }

    public Integer getMusicBitrate() {
        return musicBitrate;
    }

    public void setMusicBitrate(Integer musicBitrate) {
        this.musicBitrate = musicBitrate;
    }

    public Boolean getNextPlay() {
        return isNextPlay;
    }

    public void setNextPlay(Boolean nextPlay) {
        isNextPlay = nextPlay;
    }

    public Boolean getLocalAlbum() {
        return isLocalAlbum;
    }

    public void setLocalAlbum(Boolean localAlbum) {
        isLocalAlbum = localAlbum;
    }

    public Boolean getLocalArtist() {
        return isLocalArtist;
    }

    public void setLocalArtist(Boolean localArtist) {
        isLocalArtist = localArtist;
    }

    public Long getMusicFileSize() {
        return musicFileSize;
    }

    public void setMusicFileSize(Long musicFileSize) {
        this.musicFileSize = musicFileSize;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Boolean getSQMusic() {
        return isSQMusic;
    }

    public void setSQMusic(Boolean SQMusic) {
        isSQMusic = SQMusic;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Integer getMusicOrigin() {
        return musicOrigin;
    }

    public void setMusicOrigin(Integer musicOrigin) {
        this.musicOrigin = musicOrigin;
    }

    public Long getDjProgramId() {
        return djProgramId;
    }

    public void setDjProgramId(Long djProgramId) {
        this.djProgramId = djProgramId;
    }

    public String getProxyUrl() {
        return proxyUrl;
    }

    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.dbId);
        dest.writeValue(this.allTime);
        dest.writeValue(this.isLocalMusic);
        dest.writeString(this.coverImgUrl);
        dest.writeString(this.albumName);
        dest.writeString(this.albumImgUrl);
        dest.writeString(this.albumId);
        dest.writeString(this.musicName);
        dest.writeString(this.musicId);
        dest.writeString(this.artistName);
        dest.writeString(this.artistId);
        dest.writeString(this.artistImgUrl);
        dest.writeString(this.playPath);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.commentId);
        dest.writeString(this.subCommentId);
        dest.writeValue(this.commentCount);
        dest.writeValue(this.hasMV);
        dest.writeParcelable(this.mvBean, flags);
        dest.writeValue(this.hasCheck);
        dest.writeString(this.musicType);
        dest.writeParcelable(this.lyricBean, flags);
        dest.writeValue(this.musicBitrate);
        dest.writeValue(this.isNextPlay);
        dest.writeValue(this.isLocalAlbum);
        dest.writeValue(this.isLocalArtist);
        dest.writeValue(this.musicFileSize);
        dest.writeString(this.alias);
        dest.writeValue(this.isSQMusic);
        dest.writeValue(this.liked);
        dest.writeValue(this.musicOrigin);
        dest.writeValue(this.djProgramId);
        dest.writeString(this.proxyUrl);
    }

    public MusicBean() {
    }

    protected MusicBean(Parcel in) {
        this.dbId = (Long) in.readValue(Long.class.getClassLoader());
        this.allTime = (Long) in.readValue(Long.class.getClassLoader());
        this.isLocalMusic = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.coverImgUrl = in.readString();
        this.albumName = in.readString();
        this.albumImgUrl = in.readString();
        this.albumId = in.readString();
        this.musicName = in.readString();
        this.musicId = in.readString();
        this.artistName = in.readString();
        this.artistId = in.readString();
        this.artistImgUrl = in.readString();
        this.playPath = in.readString();
        this.downloadUrl = in.readString();
        this.commentId = in.readString();
        this.subCommentId = in.readString();
        this.commentCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.hasMV = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mvBean = in.readParcelable(MvBean.class.getClassLoader());
        this.hasCheck = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.musicType = in.readString();
        this.lyricBean = in.readParcelable(LyricBean.class.getClassLoader());
        this.musicBitrate = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isNextPlay = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isLocalAlbum = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isLocalArtist = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.musicFileSize = (Long) in.readValue(Long.class.getClassLoader());
        this.alias = in.readString();
        this.isSQMusic = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.liked = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.musicOrigin = (Integer) in.readValue(Integer.class.getClassLoader());
        this.djProgramId = (Long) in.readValue(Long.class.getClassLoader());
        this.proxyUrl = in.readString();
    }

    public static final Creator<MusicBean> CREATOR = new Creator<MusicBean>() {
        @Override
        public MusicBean createFromParcel(Parcel source) {
            return new MusicBean(source);
        }

        @Override
        public MusicBean[] newArray(int size) {
            return new MusicBean[size];
        }
    };

    @Override
    protected Object clone() throws CloneNotSupportedException {
        MusicBean data = null;
        data = (MusicBean) super.clone();
        return data;
    }
}
