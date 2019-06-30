package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Go_oG
 * Description:话题的实体类
 * on 2017/11/27.
 */

public class TopicBean implements Parcelable {
    private String topicUrl; //话题的网络地址
    private Long topicId;
    private String commentId;
    private Long addTime;
    private String title;
    private String mainTitle;
    private List<String> contentList;
    private Integer readCount;
    private String startText;
    private String summary;
    private UserBean creator;
    private Integer shareCount;
    private Integer commentCount;
    private Integer likeCount;
    private String coverUrl;
    private Integer number;

    public String getTopicUrl() {
        return topicUrl;
    }

    public void setTopicUrl(String topicUrl) {
        this.topicUrl = topicUrl;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public List<String> getContentList() {
        return contentList;
    }

    public void setContentList(List<String> contentList) {
        this.contentList = contentList;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public String getStartText() {
        return startText;
    }

    public void setStartText(String startText) {
        this.startText = startText;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public UserBean getCreator() {
        return creator;
    }

    public void setCreator(UserBean creator) {
        this.creator = creator;
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

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.topicUrl);
        dest.writeValue(this.topicId);
        dest.writeString(this.commentId);
        dest.writeValue(this.addTime);
        dest.writeString(this.title);
        dest.writeString(this.mainTitle);
        dest.writeStringList(this.contentList);
        dest.writeValue(this.readCount);
        dest.writeString(this.startText);
        dest.writeString(this.summary);
        dest.writeParcelable(this.creator, flags);
        dest.writeValue(this.shareCount);
        dest.writeValue(this.commentCount);
        dest.writeValue(this.likeCount);
        dest.writeString(this.coverUrl);
        dest.writeValue(this.number);
    }

    public TopicBean() {
    }

    protected TopicBean(Parcel in) {
        this.topicUrl = in.readString();
        this.topicId = (Long) in.readValue(Long.class.getClassLoader());
        this.commentId = in.readString();
        this.addTime = (Long) in.readValue(Long.class.getClassLoader());
        this.title = in.readString();
        this.mainTitle = in.readString();
        this.contentList = in.createStringArrayList();
        this.readCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.startText = in.readString();
        this.summary = in.readString();
        this.creator = in.readParcelable(UserBean.class.getClassLoader());
        this.shareCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.commentCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.likeCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.coverUrl = in.readString();
        this.number = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<TopicBean> CREATOR = new Parcelable.Creator<TopicBean>() {
        @Override
        public TopicBean createFromParcel(Parcel source) {
            return new TopicBean(source);
        }

        @Override
        public TopicBean[] newArray(int size) {
            return new TopicBean[size];
        }
    };
}
