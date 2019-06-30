package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Go_oG
 * Description: 电台音乐bean
 * on 2017/11/16.
 */

public class DjSongBean implements Parcelable {
    private MusicBean musicBean;
    private Integer playCount;
    private Long creatTime;
    private Integer programCount;
    private Integer shareCount;
    private Integer likedCount;

    public MusicBean getMusicBean() {
        return musicBean;
    }

    public void setMusicBean(MusicBean musicBean) {
        this.musicBean = musicBean;
    }

    public Integer getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }

    public Long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Long creatTime) {
        this.creatTime = creatTime;
    }

    public Integer getProgramCount() {
        return programCount;
    }

    public void setProgramCount(Integer programCount) {
        this.programCount = programCount;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }

    public Integer getLikedCount() {
        return likedCount;
    }

    public void setLikedCount(Integer likedCount) {
        this.likedCount = likedCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.musicBean, flags);
        dest.writeValue(this.playCount);
        dest.writeValue(this.creatTime);
        dest.writeValue(this.programCount);
        dest.writeValue(this.shareCount);
        dest.writeValue(this.likedCount);
    }

    public DjSongBean() {
    }

    protected DjSongBean(Parcel in) {
        this.musicBean = in.readParcelable(MusicBean.class.getClassLoader());
        this.playCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.creatTime = (Long) in.readValue(Long.class.getClassLoader());
        this.programCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.shareCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.likedCount = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<DjSongBean> CREATOR = new Creator<DjSongBean>() {
        @Override
        public DjSongBean createFromParcel(Parcel source) {
            return new DjSongBean(source);
        }

        @Override
        public DjSongBean[] newArray(int size) {
            return new DjSongBean[size];
        }
    };
}
