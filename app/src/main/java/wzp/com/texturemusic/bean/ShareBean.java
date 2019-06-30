package wzp.com.texturemusic.bean;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Go_oG
 * Description:分享的实体类
 * on 2018/1/26.
 */

public class ShareBean implements Parcelable {
    private String title;
    private String text;
    private String imgpath;
    private String imgUrl;
    private String webUrl;
    private String fileUrl;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.text);
        dest.writeString(this.imgpath);
        dest.writeString(this.imgUrl);
        dest.writeString(this.webUrl);
        dest.writeString(this.fileUrl);
    }

    public ShareBean() {
    }

    protected ShareBean(Parcel in) {
        this.title = in.readString();
        this.text = in.readString();
        this.imgpath = in.readString();
        this.imgUrl = in.readString();
        this.webUrl = in.readString();
        this.fileUrl = in.readString();
    }

    public static final Creator<ShareBean> CREATOR = new Creator<ShareBean>() {
        @Override
        public ShareBean createFromParcel(Parcel source) {
            return new ShareBean(source);
        }

        @Override
        public ShareBean[] newArray(int size) {
            return new ShareBean[size];
        }
    };
}
