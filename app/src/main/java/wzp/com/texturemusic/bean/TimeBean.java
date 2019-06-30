package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wang on 2017/3/10.
 * 用于定时 */

public class TimeBean implements Parcelable {
    private Integer requestCode;
    private Long times;

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public long getTimes() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.requestCode);
        dest.writeLong(this.times);
    }

    public TimeBean() {
    }

    protected TimeBean(Parcel in) {
        this.requestCode = in.readInt();
        this.times = in.readLong();
    }

    public static final Creator<TimeBean> CREATOR = new Creator<TimeBean>() {
        @Override
        public TimeBean createFromParcel(Parcel source) {
            return new TimeBean(source);
        }

        @Override
        public TimeBean[] newArray(int size) {
            return new TimeBean[size];
        }
    };
}
