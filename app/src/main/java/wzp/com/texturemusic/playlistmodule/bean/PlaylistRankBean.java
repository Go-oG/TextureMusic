package wzp.com.texturemusic.playlistmodule.bean;

import java.util.List;

import wzp.com.texturemusic.bean.MusicBean;

/**
 * Created by Go_oG
 * Description: 歌单排行榜的bean
 * on 2017/11/17.
 */

public class PlaylistRankBean {
    private String coverImgUrl;
    private long playlistId;
    private String playlistName;
    private List<MusicBean> musicList;
    private String tips;

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }

    public List<MusicBean> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<MusicBean> musicList) {
        this.musicList = musicList;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }
}
