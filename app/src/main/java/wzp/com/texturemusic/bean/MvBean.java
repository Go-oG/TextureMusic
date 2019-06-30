package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Go_oG
 * Description:MV的描述实体类
 * on 2017/9/15.
 */

public class MvBean implements Parcelable{
    private Long mvId;//mvId
    private String mvName;
    private String mvCommentId;// mv的评论Id
    private String mvUrl;
    private String artistName;
    private Long artistId;
    private Long albumId;
    private String albumName;
    private String musicName;
    private Long musicId;
    private String musicUrl;
    private String coverImgUrl;
    private Integer playCount;
    private Long mvTime;
    private String publishTime;
    private Boolean isLocalMv;
    private Integer subCount;
    private Integer shareCount;
    private Integer commentCount;
    private Long durationTime;
    private String description;
    private String shortDes;
    private Integer likeCount;
    private List<KeyValueBean> playPathList;
    private List<MvBean> similarList;
    private List<CommentBean> commentList;

    @Override
    public int hashCode() {
        if (mvId!=null){
            return mvId.hashCode();
        }
        if (mvUrl!=null){
            return mvUrl.hashCode();
        }
        if (mvName!=null){
            return mvName.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof MvBean) {
            MvBean mvBean = (MvBean) obj;
            if (mvBean.getLocalMv() != null && this.getLocalMv() != null) {
                if (mvBean.getLocalMv().equals(this.getLocalMv())) {
                    if (mvBean.getLocalMv()) {
                        return mvBean.getMvUrl().equals(this.getMvUrl());
                    } else {
                        return mvBean.getMvId().equals(this.getMvId());
                    }
                }
            }
        }
        return false;
    }

    public Long getMvId() {
        return mvId;
    }

    public void setMvId(Long mvId) {
        this.mvId = mvId;
    }

    public String getMvName() {
        return mvName;
    }

    public void setMvName(String mvName) {
        this.mvName = mvName;
    }

    public String getMvCommentId() {
        return mvCommentId;
    }

    public void setMvCommentId(String mvCommentId) {
        this.mvCommentId = mvCommentId;
    }

    public String getMvUrl() {
        return mvUrl;
    }

    public void setMvUrl(String mvUrl) {
        this.mvUrl = mvUrl;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public Long getMusicId() {
        return musicId;
    }

    public void setMusicId(Long musicId) {
        this.musicId = musicId;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public Integer getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }

    public Long getMvTime() {
        return mvTime;
    }

    public void setMvTime(Long mvTime) {
        this.mvTime = mvTime;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public Boolean getLocalMv() {
        return isLocalMv;
    }

    public void setLocalMv(Boolean localMv) {
        isLocalMv = localMv;
    }

    public Integer getSubCount() {
        return subCount;
    }

    public void setSubCount(Integer subCount) {
        this.subCount = subCount;
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

    public Long getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(Long durationTime) {
        this.durationTime = durationTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDes() {
        return shortDes;
    }

    public void setShortDes(String shortDes) {
        this.shortDes = shortDes;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public List<KeyValueBean> getPlayPathList() {
        return playPathList;
    }

    public void setPlayPathList(List<KeyValueBean> playPathList) {
        this.playPathList = playPathList;
    }

    public List<MvBean> getSimilarList() {
        return similarList;
    }

    public void setSimilarList(List<MvBean> similarList) {
        this.similarList = similarList;
    }

    public List<CommentBean> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentBean> commentList) {
        this.commentList = commentList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.mvId);
        dest.writeString(this.mvName);
        dest.writeString(this.mvCommentId);
        dest.writeString(this.mvUrl);
        dest.writeString(this.artistName);
        dest.writeValue(this.artistId);
        dest.writeValue(this.albumId);
        dest.writeString(this.albumName);
        dest.writeString(this.musicName);
        dest.writeValue(this.musicId);
        dest.writeString(this.musicUrl);
        dest.writeString(this.coverImgUrl);
        dest.writeValue(this.playCount);
        dest.writeValue(this.mvTime);
        dest.writeString(this.publishTime);
        dest.writeValue(this.isLocalMv);
        dest.writeValue(this.subCount);
        dest.writeValue(this.shareCount);
        dest.writeValue(this.commentCount);
        dest.writeValue(this.durationTime);
        dest.writeString(this.description);
        dest.writeString(this.shortDes);
        dest.writeValue(this.likeCount);
        dest.writeTypedList(this.playPathList);
        dest.writeTypedList(this.similarList);
        dest.writeTypedList(this.commentList);
    }

    public MvBean() {
    }

    protected MvBean(Parcel in) {
        this.mvId = (Long) in.readValue(Long.class.getClassLoader());
        this.mvName = in.readString();
        this.mvCommentId = in.readString();
        this.mvUrl = in.readString();
        this.artistName = in.readString();
        this.artistId = (Long) in.readValue(Long.class.getClassLoader());
        this.albumId = (Long) in.readValue(Long.class.getClassLoader());
        this.albumName = in.readString();
        this.musicName = in.readString();
        this.musicId = (Long) in.readValue(Long.class.getClassLoader());
        this.musicUrl = in.readString();
        this.coverImgUrl = in.readString();
        this.playCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mvTime = (Long) in.readValue(Long.class.getClassLoader());
        this.publishTime = in.readString();
        this.isLocalMv = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.subCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.shareCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.commentCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.durationTime = (Long) in.readValue(Long.class.getClassLoader());
        this.description = in.readString();
        this.shortDes = in.readString();
        this.likeCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.playPathList = in.createTypedArrayList(KeyValueBean.CREATOR);
        this.similarList = in.createTypedArrayList(MvBean.CREATOR);
        this.commentList = in.createTypedArrayList(CommentBean.CREATOR);
    }

    public static final Creator<MvBean> CREATOR = new Creator<MvBean>() {
        @Override
        public MvBean createFromParcel(Parcel source) {
            return new MvBean(source);
        }

        @Override
        public MvBean[] newArray(int size) {
            return new MvBean[size];
        }
    };

}
