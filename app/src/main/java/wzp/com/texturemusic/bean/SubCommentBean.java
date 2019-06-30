package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Go_oG
 * Description:简略的评论实体类
 * 用于在intent之间跳转
 * on 2018/1/6.
 */

public class SubCommentBean implements Parcelable {
    private String commentId;//评论Id
    private Integer commentType;//评论类型
    private String coverImgUrl;
    private String title;
    private String subTitle;
    private PlayListBean playList;
    private MusicBean musicBean;
    private MvBean mvBean;
    private ArtistBean artistBean;
    private AlbumBean albumBean;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Integer getCommentType() {
        return commentType;
    }

    public void setCommentType(Integer commentType) {
        this.commentType = commentType;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public PlayListBean getPlayList() {
        return playList;
    }

    public void setPlayList(PlayListBean playList) {
        this.playList = playList;
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

    public ArtistBean getArtistBean() {
        return artistBean;
    }

    public void setArtistBean(ArtistBean artistBean) {
        this.artistBean = artistBean;
    }

    public AlbumBean getAlbumBean() {
        return albumBean;
    }

    public void setAlbumBean(AlbumBean albumBean) {
        this.albumBean = albumBean;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.commentId);
        dest.writeValue(this.commentType);
        dest.writeString(this.coverImgUrl);
        dest.writeString(this.title);
        dest.writeString(this.subTitle);
        dest.writeParcelable(this.playList, flags);
        dest.writeParcelable(this.musicBean, flags);
        dest.writeParcelable(this.mvBean, flags);
        dest.writeParcelable(this.artistBean, flags);
        dest.writeParcelable(this.albumBean, flags);
    }

    public SubCommentBean() {
    }

    protected SubCommentBean(Parcel in) {
        this.commentId = in.readString();
        this.commentType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.coverImgUrl = in.readString();
        this.title = in.readString();
        this.subTitle = in.readString();
        this.playList = in.readParcelable(PlayListBean.class.getClassLoader());
        this.musicBean = in.readParcelable(MusicBean.class.getClassLoader());
        this.mvBean = in.readParcelable(MvBean.class.getClassLoader());
        this.artistBean = in.readParcelable(ArtistBean.class.getClassLoader());
        this.albumBean = in.readParcelable(AlbumBean.class.getClassLoader());
    }

    public static final Creator<SubCommentBean> CREATOR = new Creator<SubCommentBean>() {
        @Override
        public SubCommentBean createFromParcel(Parcel source) {
            return new SubCommentBean(source);
        }

        @Override
        public SubCommentBean[] newArray(int size) {
            return new SubCommentBean[size];
        }
    };
}
