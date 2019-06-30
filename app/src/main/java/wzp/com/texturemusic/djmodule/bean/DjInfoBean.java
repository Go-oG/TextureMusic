package wzp.com.texturemusic.djmodule.bean;

import java.util.List;

import wzp.com.texturemusic.bean.CommentBean;

/**
 * Created by Go_oG
 * Description:
 * on 2017/11/17.
 */

public class DjInfoBean {
    private String userName;
    private long userId;
    private String userSignature;
    private String userImgUrl;
    private String djName;
    private long djId;
    private String djImgUrl;
    private String description;
    private int subCount;
    private int programCount;
    private long createTime;
    private long categoryId;
    private String category;
    private String shortInfo;
    private int shareCount;
    private int likedCount;
    private int commentCount;
    private List<CommentBean> commentList;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserSignature() {
        return userSignature;
    }

    public void setUserSignature(String userSignature) {
        this.userSignature = userSignature;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }

    public String getDjName() {
        return djName;
    }

    public void setDjName(String djName) {
        this.djName = djName;
    }

    public long getDjId() {
        return djId;
    }

    public void setDjId(long djId) {
        this.djId = djId;
    }

    public String getDjImgUrl() {
        return djImgUrl;
    }

    public void setDjImgUrl(String djImgUrl) {
        this.djImgUrl = djImgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSubCount() {
        return subCount;
    }

    public void setSubCount(int subCount) {
        this.subCount = subCount;
    }

    public int getProgramCount() {
        return programCount;
    }

    public void setProgramCount(int programCount) {
        this.programCount = programCount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getShortInfo() {
        return shortInfo;
    }

    public void setShortInfo(String shortInfo) {
        this.shortInfo = shortInfo;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public int getLikedCount() {
        return likedCount;
    }

    public void setLikedCount(int likedCount) {
        this.likedCount = likedCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public List<CommentBean> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentBean> commentList) {
        this.commentList = commentList;
    }
}
