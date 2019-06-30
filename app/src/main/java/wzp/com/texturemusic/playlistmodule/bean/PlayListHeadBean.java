package wzp.com.texturemusic.playlistmodule.bean;

/**
 * Created by Go_oG
 * Description: playlistfragment 的头部数据
 * on 2017/9/28.
 */

public class PlayListHeadBean {
    private String playlistName;
    private String playlistDesc;
    private String coverImgUrl;

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistDesc() {
        return playlistDesc;
    }

    public void setPlaylistDesc(String playlistDesc) {
        this.playlistDesc = playlistDesc;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }
}
