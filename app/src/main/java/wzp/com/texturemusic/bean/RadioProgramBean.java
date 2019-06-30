package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Wang on 2017/6/28.
 * 电台节目实体类
 */

public class RadioProgramBean implements Parcelable {
    private String createTime;
    private String allTime;
    private String djErName;
    private Long djErId;
    private String netDress;//网络地址 下载地址
    private Integer programeAllCount;
    private Integer currentIndex;//当前序号
    private String coverImgUrl;
    private String hotScore;
    private Long programId;
    private String programName;
    private Integer rank;
    private Integer lastRank;
    private Integer commentCount;
    private Integer likeCount;//点赞数
    private String commentId;//评论Id
    private Integer durationTime;//时长
    private Integer playCount;
    private Integer hotScord;//热度
    private Integer trackCount;//排序好
    private Long djId;
    private String djName;
    private String userName;//电台创建人的name
    private Long userId;//电台创建人的ID
    private String updateTime;
    private String mp3Url;
    private String musicId;
    private List<CommentBean> commentDatas;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAllTime() {
        return allTime;
    }

    public void setAllTime(String allTime) {
        this.allTime = allTime;
    }

    public String getDjErName() {
        return djErName;
    }

    public void setDjErName(String djErName) {
        this.djErName = djErName;
    }

    public Long getDjErId() {
        return djErId;
    }

    public void setDjErId(Long djErId) {
        this.djErId = djErId;
    }

    public String getNetDress() {
        return netDress;
    }

    public void setNetDress(String netDress) {
        this.netDress = netDress;
    }

    public Integer getProgrameAllCount() {
        return programeAllCount;
    }

    public void setProgrameAllCount(Integer programeAllCount) {
        this.programeAllCount = programeAllCount;
    }

    public Integer getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(Integer currentIndex) {
        this.currentIndex = currentIndex;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getHotScore() {
        return hotScore;
    }

    public void setHotScore(String hotScore) {
        this.hotScore = hotScore;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getLastRank() {
        return lastRank;
    }

    public void setLastRank(Integer lastRank) {
        this.lastRank = lastRank;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Integer getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(Integer durationTime) {
        this.durationTime = durationTime;
    }

    public Integer getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }

    public Integer getHotScord() {
        return hotScord;
    }

    public void setHotScord(Integer hotScord) {
        this.hotScord = hotScord;
    }

    public Integer getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(Integer trackCount) {
        this.trackCount = trackCount;
    }

    public Long getDjId() {
        return djId;
    }

    public void setDjId(Long djId) {
        this.djId = djId;
    }

    public String getDjName() {
        return djName;
    }

    public void setDjName(String djName) {
        this.djName = djName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getMp3Url() {
        return mp3Url;
    }

    public void setMp3Url(String mp3Url) {
        this.mp3Url = mp3Url;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public List<CommentBean> getCommentDatas() {
        return commentDatas;
    }

    public void setCommentDatas(List<CommentBean> commentDatas) {
        this.commentDatas = commentDatas;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.createTime);
        dest.writeString(this.allTime);
        dest.writeString(this.djErName);
        dest.writeValue(this.djErId);
        dest.writeString(this.netDress);
        dest.writeValue(this.programeAllCount);
        dest.writeValue(this.currentIndex);
        dest.writeString(this.coverImgUrl);
        dest.writeString(this.hotScore);
        dest.writeValue(this.programId);
        dest.writeString(this.programName);
        dest.writeValue(this.rank);
        dest.writeValue(this.lastRank);
        dest.writeValue(this.commentCount);
        dest.writeValue(this.likeCount);
        dest.writeString(this.commentId);
        dest.writeValue(this.durationTime);
        dest.writeValue(this.playCount);
        dest.writeValue(this.hotScord);
        dest.writeValue(this.trackCount);
        dest.writeValue(this.djId);
        dest.writeString(this.djName);
        dest.writeString(this.userName);
        dest.writeValue(this.userId);
        dest.writeString(this.updateTime);
        dest.writeString(this.mp3Url);
        dest.writeString(this.musicId);
        dest.writeTypedList(this.commentDatas);
    }

    public RadioProgramBean() {
    }

    protected RadioProgramBean(Parcel in) {
        this.createTime = in.readString();
        this.allTime = in.readString();
        this.djErName = in.readString();
        this.djErId = (Long) in.readValue(Long.class.getClassLoader());
        this.netDress = in.readString();
        this.programeAllCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.currentIndex = (Integer) in.readValue(Integer.class.getClassLoader());
        this.coverImgUrl = in.readString();
        this.hotScore = in.readString();
        this.programId = (Long) in.readValue(Long.class.getClassLoader());
        this.programName = in.readString();
        this.rank = (Integer) in.readValue(Integer.class.getClassLoader());
        this.lastRank = (Integer) in.readValue(Integer.class.getClassLoader());
        this.commentCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.likeCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.commentId = in.readString();
        this.durationTime = (Integer) in.readValue(Integer.class.getClassLoader());
        this.playCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.hotScord = (Integer) in.readValue(Integer.class.getClassLoader());
        this.trackCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.djId = (Long) in.readValue(Long.class.getClassLoader());
        this.djName = in.readString();
        this.userName = in.readString();
        this.userId = (Long) in.readValue(Long.class.getClassLoader());
        this.updateTime = in.readString();
        this.mp3Url = in.readString();
        this.musicId = in.readString();
        this.commentDatas = in.createTypedArrayList(CommentBean.CREATOR);
    }

    public static final Creator<RadioProgramBean> CREATOR = new Creator<RadioProgramBean>() {
        @Override
        public RadioProgramBean createFromParcel(Parcel source) {
            return new RadioProgramBean(source);
        }

        @Override
        public RadioProgramBean[] newArray(int size) {
            return new RadioProgramBean[size];
        }
    };
}
