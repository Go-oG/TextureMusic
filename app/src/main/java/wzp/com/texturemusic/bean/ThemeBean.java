package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Go_oG
 * Description:主题实体类
 * on 2017/12/26.
 */

public class ThemeBean implements Parcelable {
    private Integer themeImgResId;
    private Integer themeIndex;//主题索引
    private Integer showThemeImgResId;
    private Boolean isCheck;
    private String themeName;

    public Integer getThemeImgResId() {
        return themeImgResId;
    }

    public void setThemeImgResId(Integer themeImgResId) {
        this.themeImgResId = themeImgResId;
    }

    public Integer getThemeIndex() {
        return themeIndex;
    }

    public void setThemeIndex(Integer themeIndex) {
        this.themeIndex = themeIndex;
    }

    public Integer getShowThemeImgResId() {
        return showThemeImgResId;
    }

    public void setShowThemeImgResId(Integer showThemeImgResId) {
        this.showThemeImgResId = showThemeImgResId;
    }

    public Boolean getCheck() {
        return isCheck;
    }

    public void setCheck(Boolean check) {
        isCheck = check;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.themeImgResId);
        dest.writeValue(this.themeIndex);
        dest.writeValue(this.showThemeImgResId);
        dest.writeValue(this.isCheck);
        dest.writeString(this.themeName);
    }

    public ThemeBean() {
    }

    protected ThemeBean(Parcel in) {
        this.themeImgResId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.themeIndex = (Integer) in.readValue(Integer.class.getClassLoader());
        this.showThemeImgResId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isCheck = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.themeName = in.readString();
    }

    public static final Parcelable.Creator<ThemeBean> CREATOR = new Parcelable.Creator<ThemeBean>() {
        @Override
        public ThemeBean createFromParcel(Parcel source) {
            return new ThemeBean(source);
        }

        @Override
        public ThemeBean[] newArray(int size) {
            return new ThemeBean[size];
        }
    };
}
