package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Go_oG
 * Description:歌单实体描述类
 * on 2017/9/16.
 */

public class PlayListBean implements Parcelable {
    private Long dbId;
    private Long playlistId;
    private String playlistName;
    private String description;
    private Integer collectCount;
    private Integer commentCount;
    private String commentId;
    private Integer shareCount;
    private String coverImgUr;
    private Integer playCount;
    private String createrName;
    private Long createrId;
    private Long createTime;
    private Integer musicCount;
    private List<MusicBean> musicBeanList;
    private UserBean creater;
    private Boolean isSelect;
    private String subDescription;
    private Boolean liked;

    public Long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Long playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }

    public String getCoverImgUr() {
        return coverImgUr;
    }

    public void setCoverImgUr(String coverImgUr) {
        this.coverImgUr = coverImgUr;
    }

    public Integer getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public Long getCreaterId() {
        return createrId;
    }

    public void setCreaterId(Long createrId) {
        this.createrId = createrId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getMusicCount() {
        return musicCount;
    }

    public void setMusicCount(Integer musicCount) {
        this.musicCount = musicCount;
    }

    public List<MusicBean> getMusicBeanList() {
        return musicBeanList;
    }

    public void setMusicBeanList(List<MusicBean> musicBeanList) {
        this.musicBeanList = musicBeanList;
    }

    public UserBean getCreater() {
        return creater;
    }

    public void setCreater(UserBean creater) {
        this.creater = creater;
    }

    public Boolean getSelect() {
        return isSelect;
    }

    public void setSelect(Boolean select) {
        isSelect = select;
    }

    public String getSubDescription() {
        return subDescription;
    }

    public void setSubDescription(String subDescription) {
        this.subDescription = subDescription;
    }

    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.dbId);
        dest.writeValue(this.playlistId);
        dest.writeString(this.playlistName);
        dest.writeString(this.description);
        dest.writeValue(this.collectCount);
        dest.writeValue(this.commentCount);
        dest.writeString(this.commentId);
        dest.writeValue(this.shareCount);
        dest.writeString(this.coverImgUr);
        dest.writeValue(this.playCount);
        dest.writeString(this.createrName);
        dest.writeValue(this.createrId);
        dest.writeValue(this.createTime);
        dest.writeValue(this.musicCount);
        dest.writeTypedList(this.musicBeanList);
        dest.writeParcelable(this.creater, flags);
        dest.writeValue(this.isSelect);
        dest.writeString(this.subDescription);
        dest.writeValue(this.liked);
    }

    public PlayListBean() {
    }

    protected PlayListBean(Parcel in) {
        this.dbId = (Long) in.readValue(Long.class.getClassLoader());
        this.playlistId = (Long) in.readValue(Long.class.getClassLoader());
        this.playlistName = in.readString();
        this.description = in.readString();
        this.collectCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.commentCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.commentId = in.readString();
        this.shareCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.coverImgUr = in.readString();
        this.playCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.createrName = in.readString();
        this.createrId = (Long) in.readValue(Long.class.getClassLoader());
        this.createTime = (Long) in.readValue(Long.class.getClassLoader());
        this.musicCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.musicBeanList = in.createTypedArrayList(MusicBean.CREATOR);
        this.creater = in.readParcelable(UserBean.class.getClassLoader());
        this.isSelect = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.subDescription = in.readString();
        this.liked = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<PlayListBean> CREATOR = new Creator<PlayListBean>() {
        @Override
        public PlayListBean createFromParcel(Parcel source) {
            return new PlayListBean(source);
        }

        @Override
        public PlayListBean[] newArray(int size) {
            return new PlayListBean[size];
        }
    };
}
