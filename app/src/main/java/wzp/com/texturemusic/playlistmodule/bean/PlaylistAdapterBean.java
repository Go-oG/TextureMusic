package wzp.com.texturemusic.playlistmodule.bean;

import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.PlayListBean;

/**
 * author:Go_oG
 * date: on 2018/5/14
 * packageName: wzp.com.texturemusic.playlistmodule.bean
 */
public class PlaylistAdapterBean {
    private int type;//0 1  2
    private PlayListBean playList;

    private MusicBean musicBean;
    private int musicSize;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getMusicSize() {
        return musicSize;
    }

    public void setMusicSize(int musicSize) {
        this.musicSize = musicSize;
    }
}
