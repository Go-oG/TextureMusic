package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Go_oG
 * Description:下载信息描述的实体bean
 * on 2018/1/19.
 */

public class DownloadBean implements Parcelable {
    private MusicBean musicBean;
    private MvBean mvBean;
    //标识下载的数据类型
    private Boolean isMvData;
    private Long currentByte;
    private Long totalByte;
    private String fileName;
    private String filePath;
    //是否完成了
    private Boolean complete;
    private String tag;//用于一些其他标签
    private Boolean isDownloading;//是否在下载
    private String speed;//下载速度

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof DownloadBean) {
            DownloadBean bean = (DownloadBean) obj;
            if (bean.getMvBean() != null && this.getMvBean() != null) {
                return bean.getMvBean().equals(this.getMvBean());
            }
            if (bean.getMusicBean() != null && this.getMusicBean() != null) {
                return bean.getMusicBean().equals(this.getMusicBean());
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (musicBean != null) {
            return musicBean.hashCode();
        } else if (mvBean != null) {
            return mvBean.hashCode();
        }
        return super.hashCode();
    }

    public MusicBean getMusicBean() {
        return musicBean;
    }

    public void setMusicBean(MusicBean musicBean) {
        this.musicBean = musicBean;
    }

    public MvBean getMvBean() {
        return mvBean;
    }

    public void setMvBean(MvBean mvBean) {
        this.mvBean = mvBean;
    }

    public Boolean getMvData() {
        return isMvData;
    }

    public void setMvData(Boolean mvData) {
        isMvData = mvData;
    }

    public Long getCurrentByte() {
        return currentByte;
    }

    public void setCurrentByte(Long currentByte) {
        this.currentByte = currentByte;
    }

    public Long getTotalByte() {
        return totalByte;
    }

    public void setTotalByte(Long totalByte) {
        this.totalByte = totalByte;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Boolean getDownloading() {
        return isDownloading;
    }

    public void setDownloading(Boolean downloading) {
        isDownloading = downloading;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.musicBean, flags);
        dest.writeParcelable(this.mvBean, flags);
        dest.writeValue(this.isMvData);
        dest.writeValue(this.currentByte);
        dest.writeValue(this.totalByte);
        dest.writeString(this.fileName);
        dest.writeString(this.filePath);
        dest.writeValue(this.complete);
        dest.writeString(this.tag);
        dest.writeValue(this.isDownloading);
        dest.writeString(this.speed);
    }

    public DownloadBean() {
    }

    protected DownloadBean(Parcel in) {
        this.musicBean = in.readParcelable(MusicBean.class.getClassLoader());
        this.mvBean = in.readParcelable(MvBean.class.getClassLoader());
        this.isMvData = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.currentByte = (Long) in.readValue(Long.class.getClassLoader());
        this.totalByte = (Long) in.readValue(Long.class.getClassLoader());
        this.fileName = in.readString();
        this.filePath = in.readString();
        this.complete = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.tag = in.readString();
        this.isDownloading = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.speed = in.readString();
    }

    public static final Creator<DownloadBean> CREATOR = new Creator<DownloadBean>() {
        @Override
        public DownloadBean createFromParcel(Parcel source) {
            return new DownloadBean(source);
        }

        @Override
        public DownloadBean[] newArray(int size) {
            return new DownloadBean[size];
        }
    };
}
