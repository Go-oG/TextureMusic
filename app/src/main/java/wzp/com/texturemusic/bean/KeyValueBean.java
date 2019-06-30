package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Go_oG
 * Description:用于存储一些特殊的键值对 对象
 * on 2017/11/15.
 */

public class KeyValueBean implements Parcelable {
    private String key;
    private String value;
    private Integer intVal;
    private Integer tag;


    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            if (obj instanceof KeyValueBean) {
                KeyValueBean bean = (KeyValueBean) obj;
                if (bean.key != null && this.key != null && bean.value != null && this.value != null) {
                    return bean.key.equals(this.key) && bean.getValue().equals(this.value);
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (key != null && value != null) {
            return key.hashCode() & value.hashCode();
        } else {
            if (value != null) {
                return value.hashCode();
            }
        }
        return super.hashCode();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getIntVal() {
        return intVal;
    }

    public void setIntVal(Integer intVal) {
        this.intVal = intVal;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.value);
        dest.writeValue(this.intVal);
        dest.writeValue(this.tag);
    }

    public KeyValueBean() {
    }

    protected KeyValueBean(Parcel in) {
        this.key = in.readString();
        this.value = in.readString();
        this.intVal = (Integer) in.readValue(Integer.class.getClassLoader());
        this.tag = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<KeyValueBean> CREATOR = new Creator<KeyValueBean>() {
        @Override
        public KeyValueBean createFromParcel(Parcel source) {
            return new KeyValueBean(source);
        }

        @Override
        public KeyValueBean[] newArray(int size) {
            return new KeyValueBean[size];
        }
    };
}
