package wzp.com.texturemusic.mvmodule.bean;

/**
 * Created by wang on 2017/5/8.
 *mv评论
 */

public class MvCommentBean {
    private String nickName;
    private Long userId;
    private String imgUrl;
    private Long commentId;
    private Long commentTime;//时间戳
    private Integer likeCound;
    private String content;
    private Integer allCommentCound;

    public Integer getAllCommentCound() {
        return allCommentCound;
    }

    public void setAllCommentCound(Integer allCommentCound) {
        this.allCommentCound = allCommentCound;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Long commentTime) {
        this.commentTime = commentTime;
    }

    public Integer getLikeCound() {
        return likeCound;
    }

    public void setLikeCound(Integer likeCound) {
        this.likeCound = likeCound;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
