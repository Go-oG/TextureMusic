package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Wang on 2017/6/28.
 * 评论实体类
 */

public class CommentBean implements Parcelable {
    private String commentId;
    private String commnetCreaterName;
    private Long commentCreaterId;
    private String commentCreaterImgUrl;
    private String description;
    private String createTime;
    private Integer likeCount;
    private Integer shareCount;
    private Integer commentCount;
    private String val;
    private UserBean userBean;
    private List<String> imgUrls;
    private MusicBean musicBean;
    private List<CommentBean> replayList;
    private List<CommentBean> hotComment;//精彩评论

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommnetCreaterName() {
        return commnetCreaterName;
    }

    public void setCommnetCreaterName(String commnetCreaterName) {
        this.commnetCreaterName = commnetCreaterName;
    }

    public Long getCommentCreaterId() {
        return commentCreaterId;
    }

    public void setCommentCreaterId(Long commentCreaterId) {
        this.commentCreaterId = commentCreaterId;
    }

    public String getCommentCreaterImgUrl() {
        return commentCreaterImgUrl;
    }

    public void setCommentCreaterImgUrl(String commentCreaterImgUrl) {
        this.commentCreaterImgUrl = commentCreaterImgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
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

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public List<String> getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(List<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    public MusicBean getMusicBean() {
        return musicBean;
    }

    public void setMusicBean(MusicBean musicBean) {
        this.musicBean = musicBean;
    }

    public List<CommentBean> getReplayList() {
        return replayList;
    }

    public void setReplayList(List<CommentBean> replayList) {
        this.replayList = replayList;
    }

    public List<CommentBean> getHotComment() {
        return hotComment;
    }

    public void setHotComment(List<CommentBean> hotComment) {
        this.hotComment = hotComment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.commentId);
        dest.writeString(this.commnetCreaterName);
        dest.writeValue(this.commentCreaterId);
        dest.writeString(this.commentCreaterImgUrl);
        dest.writeString(this.description);
        dest.writeString(this.createTime);
        dest.writeValue(this.likeCount);
        dest.writeValue(this.shareCount);
        dest.writeValue(this.commentCount);
        dest.writeString(this.val);
        dest.writeParcelable(this.userBean, flags);
        dest.writeStringList(this.imgUrls);
        dest.writeParcelable(this.musicBean, flags);
        dest.writeTypedList(this.replayList);
        dest.writeTypedList(this.hotComment);
    }

    public CommentBean() {
    }

    protected CommentBean(Parcel in) {
        this.commentId = in.readString();
        this.commnetCreaterName = in.readString();
        this.commentCreaterId = (Long) in.readValue(Long.class.getClassLoader());
        this.commentCreaterImgUrl = in.readString();
        this.description = in.readString();
        this.createTime = in.readString();
        this.likeCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.shareCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.commentCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.val = in.readString();
        this.userBean = in.readParcelable(UserBean.class.getClassLoader());
        this.imgUrls = in.createStringArrayList();
        this.musicBean = in.readParcelable(MusicBean.class.getClassLoader());
        this.replayList = in.createTypedArrayList(CommentBean.CREATOR);
        this.hotComment = in.createTypedArrayList(CommentBean.CREATOR);
    }

    public static final Creator<CommentBean> CREATOR = new Creator<CommentBean>() {
        @Override
        public CommentBean createFromParcel(Parcel source) {
            return new CommentBean(source);
        }

        @Override
        public CommentBean[] newArray(int size) {
            return new CommentBean[size];
        }
    };
}
