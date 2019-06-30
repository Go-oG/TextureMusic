package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Wang on 2017/9/12.
 * 歌词实体类
 */

public class LyricBean implements Parcelable {
    private List<KeyValueBean> list;

    public List<KeyValueBean> getList() {
        return list;
    }

    public void setList(List<KeyValueBean> list) {
        this.list = list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.list);
    }

    public LyricBean() {
    }

    protected LyricBean(Parcel in) {
        this.list = in.createTypedArrayList(KeyValueBean.CREATOR);
    }

    public static final Creator<LyricBean> CREATOR = new Creator<LyricBean>() {
        @Override
        public LyricBean createFromParcel(Parcel source) {
            return new LyricBean(source);
        }

        @Override
        public LyricBean[] newArray(int size) {
            return new LyricBean[size];
        }
    };
}
