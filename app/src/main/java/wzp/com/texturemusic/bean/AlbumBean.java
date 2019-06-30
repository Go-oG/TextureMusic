package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Go_oG
 * Description:
 * on 2017/9/15.
 */

public class AlbumBean implements Parcelable {
    private String albumName;//专辑名字
    private String albumImgUrl;//专辑图片地址 可以为空
    private String albumId;//可以为null
    private Long publishTime;//发行时间
    private String publishCompany;//发行公司
    private String description;
    private String commentId;//评论ID
    private Boolean hasLoadData;
    private ArtistBean artistBean;
    private Boolean isLocalAlbum;
    private List<MusicBean> musicList;
    private Integer musicCount;
    private Integer likedCount;
    private Integer shareCount;
    private Integer commentCount;


    @Override
    public int hashCode() {
        if (albumId != null) {
            return (albumName.hashCode() & albumId.hashCode()) >> 2;
        } else {
            return albumName.hashCode() >> 4;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            if (obj instanceof AlbumBean) {
                AlbumBean albumObj = (AlbumBean) obj;
                if (albumObj.getLocalAlbum() != null && this.getLocalAlbum() != null) {
                    if (albumObj.getLocalAlbum().equals(this.getLocalAlbum())) {
                        if (this.getLocalAlbum()) {
                            return albumObj.getAlbumName().trim().equals(this.getAlbumName().trim());
                        } else {
                            return albumObj.getAlbumId().equals(this.getAlbumId());
                        }
                    }
                }
            }
        }
        return false;
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

    public Long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublishCompany() {
        return publishCompany;
    }

    public void setPublishCompany(String publishCompany) {
        this.publishCompany = publishCompany;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Boolean getHasLoadData() {
        return hasLoadData;
    }

    public void setHasLoadData(Boolean hasLoadData) {
        this.hasLoadData = hasLoadData;
    }

    public ArtistBean getArtistBean() {
        return artistBean;
    }

    public void setArtistBean(ArtistBean artistBean) {
        this.artistBean = artistBean;
    }

    public Boolean getLocalAlbum() {
        return isLocalAlbum;
    }

    public void setLocalAlbum(Boolean localAlbum) {
        isLocalAlbum = localAlbum;
    }

    public List<MusicBean> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<MusicBean> musicList) {
        this.musicList = musicList;
    }

    public Integer getMusicCount() {
        return musicCount;
    }

    public void setMusicCount(Integer musicCount) {
        this.musicCount = musicCount;
    }

    public Integer getLikedCount() {
        return likedCount;
    }

    public void setLikedCount(Integer likedCount) {
        this.likedCount = likedCount;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.albumName);
        dest.writeString(this.albumImgUrl);
        dest.writeString(this.albumId);
        dest.writeValue(this.publishTime);
        dest.writeString(this.publishCompany);
        dest.writeString(this.description);
        dest.writeString(this.commentId);
        dest.writeValue(this.hasLoadData);
        dest.writeParcelable(this.artistBean, flags);
        dest.writeValue(this.isLocalAlbum);
        dest.writeTypedList(this.musicList);
        dest.writeValue(this.musicCount);
        dest.writeValue(this.likedCount);
        dest.writeValue(this.shareCount);
        dest.writeValue(this.commentCount);
    }

    public AlbumBean() {
    }

    protected AlbumBean(Parcel in) {
        this.albumName = in.readString();
        this.albumImgUrl = in.readString();
        this.albumId = in.readString();
        this.publishTime = (Long) in.readValue(Long.class.getClassLoader());
        this.publishCompany = in.readString();
        this.description = in.readString();
        this.commentId = in.readString();
        this.hasLoadData = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.artistBean = in.readParcelable(ArtistBean.class.getClassLoader());
        this.isLocalAlbum = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.musicList = in.createTypedArrayList(MusicBean.CREATOR);
        this.musicCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.likedCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.shareCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.commentCount = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<AlbumBean> CREATOR = new Creator<AlbumBean>() {
        @Override
        public AlbumBean createFromParcel(Parcel source) {
            return new AlbumBean(source);
        }

        @Override
        public AlbumBean[] newArray(int size) {
            return new AlbumBean[size];
        }
    };
}
