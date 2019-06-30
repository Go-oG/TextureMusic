package wzp.com.texturemusic.dbmodule.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Wang on 2018/3/10.
 * 保存歌单的基本信息
 * 用于当歌单被收藏后的存储
 */
@Entity
public class DbCollectPlaylistEntiy {
    @Id(autoincrement = true)
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
    private String createrHeadPath;
    private Integer musicCount;
    private String subDescription;
    private Boolean liked;
    @Generated(hash = 915417468)
    public DbCollectPlaylistEntiy(Long dbId, Long playlistId, String playlistName,
            String description, Integer collectCount, Integer commentCount,
            String commentId, Integer shareCount, String coverImgUr,
            Integer playCount, String createrName, Long createrId, Long createTime,
            String createrHeadPath, Integer musicCount, String subDescription,
            Boolean liked) {
        this.dbId = dbId;
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.description = description;
        this.collectCount = collectCount;
        this.commentCount = commentCount;
        this.commentId = commentId;
        this.shareCount = shareCount;
        this.coverImgUr = coverImgUr;
        this.playCount = playCount;
        this.createrName = createrName;
        this.createrId = createrId;
        this.createTime = createTime;
        this.createrHeadPath = createrHeadPath;
        this.musicCount = musicCount;
        this.subDescription = subDescription;
        this.liked = liked;
    }
    @Generated(hash = 776884293)
    public DbCollectPlaylistEntiy() {
    }
    public Long getDbId() {
        return this.dbId;
    }
    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }
    public Long getPlaylistId() {
        return this.playlistId;
    }
    public void setPlaylistId(Long playlistId) {
        this.playlistId = playlistId;
    }
    public String getPlaylistName() {
        return this.playlistName;
    }
    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getCollectCount() {
        return this.collectCount;
    }
    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }
    public Integer getCommentCount() {
        return this.commentCount;
    }
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
    public String getCommentId() {
        return this.commentId;
    }
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
    public Integer getShareCount() {
        return this.shareCount;
    }
    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }
    public String getCoverImgUr() {
        return this.coverImgUr;
    }
    public void setCoverImgUr(String coverImgUr) {
        this.coverImgUr = coverImgUr;
    }
    public Integer getPlayCount() {
        return this.playCount;
    }
    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }
    public String getCreaterName() {
        return this.createrName;
    }
    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }
    public Long getCreaterId() {
        return this.createrId;
    }
    public void setCreaterId(Long createrId) {
        this.createrId = createrId;
    }
    public Long getCreateTime() {
        return this.createTime;
    }
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
    public String getCreaterHeadPath() {
        return this.createrHeadPath;
    }
    public void setCreaterHeadPath(String createrHeadPath) {
        this.createrHeadPath = createrHeadPath;
    }
    public Integer getMusicCount() {
        return this.musicCount;
    }
    public void setMusicCount(Integer musicCount) {
        this.musicCount = musicCount;
    }
    public String getSubDescription() {
        return this.subDescription;
    }
    public void setSubDescription(String subDescription) {
        this.subDescription = subDescription;
    }
    public Boolean getLiked() {
        return this.liked;
    }
    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

}
