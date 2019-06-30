package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Go_oG
 * Description: 用户实体类
 * on 2017/9/16.
 */

public class UserBean implements Parcelable {
    private Long userId;
    private String nickName;
    private String userCoverImgUrl;
    private String signnature;//签名介绍
    private Integer gender;//性别
    private String province;
    private String city;
    private String area;
    private Integer age;
    private Long phoneNumber;
    private Long qqNumber;
    private String emailCount;
    private String backgroundUrl;
    private String description;
    private Integer follows;
    private Integer followeds;
    private Integer eventCount;
    private Integer playlistCount;
    private Integer playlistSubscribedCount;
    private Integer listenSongsCount;
    private Integer createDays;
    private Integer level;
    private Integer userType;//用户类型 2为认证用户

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserCoverImgUrl() {
        return userCoverImgUrl;
    }

    public void setUserCoverImgUrl(String userCoverImgUrl) {
        this.userCoverImgUrl = userCoverImgUrl;
    }

    public String getSignnature() {
        return signnature;
    }

    public void setSignnature(String signnature) {
        this.signnature = signnature;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getQqNumber() {
        return qqNumber;
    }

    public void setQqNumber(Long qqNumber) {
        this.qqNumber = qqNumber;
    }

    public String getEmailCount() {
        return emailCount;
    }

    public void setEmailCount(String emailCount) {
        this.emailCount = emailCount;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFollows() {
        return follows;
    }

    public void setFollows(Integer follows) {
        this.follows = follows;
    }

    public Integer getFolloweds() {
        return followeds;
    }

    public void setFolloweds(Integer followeds) {
        this.followeds = followeds;
    }

    public Integer getEventCount() {
        return eventCount;
    }

    public void setEventCount(Integer eventCount) {
        this.eventCount = eventCount;
    }

    public Integer getPlaylistCount() {
        return playlistCount;
    }

    public void setPlaylistCount(Integer playlistCount) {
        this.playlistCount = playlistCount;
    }

    public Integer getPlaylistSubscribedCount() {
        return playlistSubscribedCount;
    }

    public void setPlaylistSubscribedCount(Integer playlistSubscribedCount) {
        this.playlistSubscribedCount = playlistSubscribedCount;
    }

    public Integer getListenSongsCount() {
        return listenSongsCount;
    }

    public void setListenSongsCount(Integer listenSongsCount) {
        this.listenSongsCount = listenSongsCount;
    }

    public Integer getCreateDays() {
        return createDays;
    }

    public void setCreateDays(Integer createDays) {
        this.createDays = createDays;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.userId);
        dest.writeString(this.nickName);
        dest.writeString(this.userCoverImgUrl);
        dest.writeString(this.signnature);
        dest.writeValue(this.gender);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.area);
        dest.writeValue(this.age);
        dest.writeValue(this.phoneNumber);
        dest.writeValue(this.qqNumber);
        dest.writeString(this.emailCount);
        dest.writeString(this.backgroundUrl);
        dest.writeString(this.description);
        dest.writeValue(this.follows);
        dest.writeValue(this.followeds);
        dest.writeValue(this.eventCount);
        dest.writeValue(this.playlistCount);
        dest.writeValue(this.playlistSubscribedCount);
        dest.writeValue(this.listenSongsCount);
        dest.writeValue(this.createDays);
        dest.writeValue(this.level);
        dest.writeValue(this.userType);
    }

    public UserBean() {
    }

    protected UserBean(Parcel in) {
        this.userId = (Long) in.readValue(Long.class.getClassLoader());
        this.nickName = in.readString();
        this.userCoverImgUrl = in.readString();
        this.signnature = in.readString();
        this.gender = (Integer) in.readValue(Integer.class.getClassLoader());
        this.province = in.readString();
        this.city = in.readString();
        this.area = in.readString();
        this.age = (Integer) in.readValue(Integer.class.getClassLoader());
        this.phoneNumber = (Long) in.readValue(Long.class.getClassLoader());
        this.qqNumber = (Long) in.readValue(Long.class.getClassLoader());
        this.emailCount = in.readString();
        this.backgroundUrl = in.readString();
        this.description = in.readString();
        this.follows = (Integer) in.readValue(Integer.class.getClassLoader());
        this.followeds = (Integer) in.readValue(Integer.class.getClassLoader());
        this.eventCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.playlistCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.playlistSubscribedCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.listenSongsCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.createDays = (Integer) in.readValue(Integer.class.getClassLoader());
        this.level = (Integer) in.readValue(Integer.class.getClassLoader());
        this.userType = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel source) {
            return new UserBean(source);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };
}
