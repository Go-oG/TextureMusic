package wzp.com.texturemusic.interf;

import wzp.com.texturemusic.bean.MusicBean;

/**
 * Created by Go_oG
 * Description:用于实现popWindows的回调
 * on 2017/11/7.
 */

public interface OnMusicPopItemListener {
    void onShare(MusicBean bean);

    void onDownload(MusicBean bean);

    void onDelete(MusicBean bean);

    void onSetAlarm(MusicBean bean);

    void onNextPlay(MusicBean bean);

    void onCommont(MusicBean bean);

    void onArtist(MusicBean bean);

    void onAlbum(MusicBean bean);

    void onMv(MusicBean bean);

    void onMusicInfo(MusicBean bean);

}
