package wzp.com.texturemusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Go_oG
 * Description:
 * on 2017/9/15.
 */

public class ArtistBean implements Parcelable {
    private String artistName;//艺术家名字
    private String artistId;//可以为null 艺术家Id
    private String artistImgUrl;//艺术家图片地址
    private Integer albumCount;
    private Integer musicCount;
    private Boolean isLocalArtist;
    private List<AlbumBean> albumList;
    private List<MusicBean> musicList;
    private List<TopicBean> topicList;
    private List<ArtistExperience> artistExperienceList;
    private String decriptions;

    public static class ArtistExperience implements Parcelable {
        private String title;
        private String info;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.title);
            dest.writeString(this.info);
        }

        public ArtistExperience() {
        }

        protected ArtistExperience(Parcel in) {
            this.title = in.readString();
            this.info = in.readString();
        }

        public static final Creator<ArtistExperience> CREATOR = new Creator<ArtistExperience>() {
            @Override
            public ArtistExperience createFromParcel(Parcel source) {
                return new ArtistExperience(source);
            }

            @Override
            public ArtistExperience[] newArray(int size) {
                return new ArtistExperience[size];
            }
        };
    }


    @Override
    public int hashCode() {
        if (artistId != null) {
            return (artistName.hashCode() & artistId.hashCode()) >> 2;
        } else {
            return artistName.hashCode() >> 4;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            if (obj instanceof ArtistBean) {
                ArtistBean bean = (ArtistBean) obj;
                if (bean.getLocalArtist() != null && this.getLocalArtist() != null) {
                    if (bean.getLocalArtist().equals(this.getLocalArtist())) {
                        if (bean.getLocalArtist()) {
                            return this.getArtistName().trim().equals(bean.getArtistName().trim());
                        } else {
                            return this.getArtistId().equals(bean.getArtistId());
                        }
                    }
                }
            }
        }
        return false;
    }


    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getArtistImgUrl() {
        return artistImgUrl;
    }

    public void setArtistImgUrl(String artistImgUrl) {
        this.artistImgUrl = artistImgUrl;
    }

    public Integer getAlbumCount() {
        return albumCount;
    }

    public void setAlbumCount(Integer albumCount) {
        this.albumCount = albumCount;
    }

    public Integer getMusicCount() {
        return musicCount;
    }

    public void setMusicCount(Integer musicCount) {
        this.musicCount = musicCount;
    }

    public Boolean getLocalArtist() {
        return isLocalArtist;
    }

    public void setLocalArtist(Boolean localArtist) {
        isLocalArtist = localArtist;
    }

    public List<AlbumBean> getAlbumList() {
        return albumList;
    }

    public void setAlbumList(List<AlbumBean> albumList) {
        this.albumList = albumList;
    }

    public List<MusicBean> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<MusicBean> musicList) {
        this.musicList = musicList;
    }

    public List<TopicBean> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<TopicBean> topicList) {
        this.topicList = topicList;
    }

    public List<ArtistExperience> getArtistExperienceList() {
        return artistExperienceList;
    }

    public void setArtistExperienceList(List<ArtistExperience> artistExperienceList) {
        this.artistExperienceList = artistExperienceList;
    }

    public String getDecriptions() {
        return decriptions;
    }

    public void setDecriptions(String decriptions) {
        this.decriptions = decriptions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.artistName);
        dest.writeString(this.artistId);
        dest.writeString(this.artistImgUrl);
        dest.writeValue(this.albumCount);
        dest.writeValue(this.musicCount);
        dest.writeValue(this.isLocalArtist);
        dest.writeTypedList(this.albumList);
        dest.writeTypedList(this.musicList);
        dest.writeList(this.topicList);
        dest.writeList(this.artistExperienceList);
        dest.writeString(this.decriptions);
    }

    public ArtistBean() {
    }

    protected ArtistBean(Parcel in) {
        this.artistName = in.readString();
        this.artistId = in.readString();
        this.artistImgUrl = in.readString();
        this.albumCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.musicCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isLocalArtist = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.albumList = in.createTypedArrayList(AlbumBean.CREATOR);
        this.musicList = in.createTypedArrayList(MusicBean.CREATOR);
        this.topicList = new ArrayList<>();
        in.readList(this.topicList, TopicBean.class.getClassLoader());
        this.artistExperienceList = new ArrayList<>();
        in.readList(this.artistExperienceList, ArtistExperience.class.getClassLoader());
        this.decriptions = in.readString();
    }

    public static final Creator<ArtistBean> CREATOR = new Creator<ArtistBean>() {
        @Override
        public ArtistBean createFromParcel(Parcel source) {
            return new ArtistBean(source);
        }

        @Override
        public ArtistBean[] newArray(int size) {
            return new ArtistBean[size];
        }
    };
}
