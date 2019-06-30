package wzp.com.texturemusic.mvmodule.bean;

import java.util.List;

/**
 * Created by wang on 2017/5/8.
 */

public class MvContentBean {
    private Long mvId;
    private String mvName;
    private Long artistId;
    private String artistName;
    private String coverImgUrl;
    private Integer playCount;
    private Integer subCount;
    private Integer shareCount;
    private Long durationTime;
    private String creatTime;
    private String mvUrl240;
    private String mvUrl480;
    private String mvUrl720;
    private String mvUrl1080;
    private String description;
    private String commentThreaId;
    private int mvIndex;

    private List<MvCommentBean> normalList;
    private List<MvCommentBean> hotList;

    public List<MvCommentBean> getNormalList() {
        return normalList;
    }

    public void setNormalList(List<MvCommentBean> normalList) {
        this.normalList = normalList;
    }

    public List<MvCommentBean> getHotList() {
        return hotList;
    }

    public void setHotList(List<MvCommentBean> hotList) {
        this.hotList = hotList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMvUrl1080() {
        return mvUrl1080;
    }

    public void setMvUrl1080(String mvUrl1080) {
        this.mvUrl1080 = mvUrl1080;
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

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
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

    public Long getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(Long durationTime) {
        this.durationTime = durationTime;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public String getMvUrl240() {
        return mvUrl240;
    }

    public void setMvUrl240(String mvUrl240) {
        this.mvUrl240 = mvUrl240;
    }

    public String getMvUrl480() {
        return mvUrl480;
    }

    public void setMvUrl480(String mvUrl480) {
        this.mvUrl480 = mvUrl480;
    }

    public String getMvUrl720() {
        return mvUrl720;
    }

    public void setMvUrl720(String mUrl720) {
        this.mvUrl720 = mUrl720;
    }

    public String getCommentThreaId() {
        return commentThreaId;
    }

    public void setCommentThreaId(String commentThreaId) {
        this.commentThreaId = commentThreaId;
    }

    public int getMvIndex() {
        return mvIndex;
    }

    public void setMvIndex(int mvIndex) {
        this.mvIndex = mvIndex;
    }
}
