package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wang on 2017/6/28.
 * 电台实体类
 */

public class RadioBean implements Parcelable {
    private String radioName;
    private String radioId;
    private Integer programCount;
    private String coverImgUrl;
    private String commendId;
    private Integer hotScore;//热度分数
    private Integer rank;
    private Integer lastRank;
    private String createrName;
    private Integer subCount;
    private String updateTime;
    private String djErName;
    private Long djErId;
    private String description;
    private List<RadioProgramBean> programeList;

    public String getRadioName() {
        return radioName;
    }

    public void setRadioName(String radioName) {
        this.radioName = radioName;
    }

    public String getRadioId() {
        return radioId;
    }

    public void setRadioId(String radioId) {
        this.radioId = radioId;
    }

    public Integer getProgramCount() {
        return programCount;
    }

    public void setProgramCount(int programCount) {
        this.programCount = programCount;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getCommendId() {
        return commendId;
    }

    public void setCommendId(String commendId) {
        this.commendId = commendId;
    }

    public Integer getHotScore() {
        return hotScore;
    }

    public void setHotScore(Integer hotScore) {
        this.hotScore = hotScore;
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

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public Integer getSubCount() {
        return subCount;
    }

    public void setSubCount(Integer subCount) {
        this.subCount = subCount;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RadioProgramBean> getProgrameList() {
        return programeList;
    }

    public void setProgrameList(List<RadioProgramBean> programeList) {
        this.programeList = programeList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.radioName);
        dest.writeString(this.radioId);
        dest.writeValue(this.programCount);
        dest.writeString(this.coverImgUrl);
        dest.writeString(this.commendId);
        dest.writeValue(this.hotScore);
        dest.writeValue(this.rank);
        dest.writeValue(this.lastRank);
        dest.writeString(this.createrName);
        dest.writeValue(this.subCount);
        dest.writeString(this.updateTime);
        dest.writeString(this.djErName);
        dest.writeValue(this.djErId);
        dest.writeString(this.description);
        dest.writeList(this.programeList);
    }

    public RadioBean() {
    }

    protected RadioBean(Parcel in) {
        this.radioName = in.readString();
        this.radioId = in.readString();
        this.programCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.coverImgUrl = in.readString();
        this.commendId = in.readString();
        this.hotScore = (Integer) in.readValue(Integer.class.getClassLoader());
        this.rank = (Integer) in.readValue(Integer.class.getClassLoader());
        this.lastRank = (Integer) in.readValue(Integer.class.getClassLoader());
        this.createrName = in.readString();
        this.subCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.updateTime = in.readString();
        this.djErName = in.readString();
        this.djErId = (Long) in.readValue(Long.class.getClassLoader());
        this.description = in.readString();
        this.programeList = new ArrayList<RadioProgramBean>();
        in.readList(this.programeList, RadioProgramBean.class.getClassLoader());
    }

    public static final Creator<RadioBean> CREATOR = new Creator<RadioBean>() {
        @Override
        public RadioBean createFromParcel(Parcel source) {
            return new RadioBean(source);
        }

        @Override
        public RadioBean[] newArray(int size) {
            return new RadioBean[size];
        }
    };
}
