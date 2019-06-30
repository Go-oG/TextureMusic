package wzp.com.texturemusic.common.bean;

import java.util.List;

import wzp.com.texturemusic.bean.PlayListBean;
import wzp.com.texturemusic.playlistmodule.bean.PlayListHeadBean;

/**
 * Created by Wang on 2018/3/7.
 * 用于保存歌单界面的数据
 */

public class PlaylistCoverBean {
    private PlayListHeadBean headBean;
    private List<PlayListBean> listBeans;

    public PlayListHeadBean getHeadBean() {
        return headBean;
    }

    public void setHeadBean(PlayListHeadBean headBean) {
        this.headBean = headBean;
    }

    public List<PlayListBean> getListBeans() {
        return listBeans;
    }

    public void setListBeans(List<PlayListBean> listBeans) {
        this.listBeans = listBeans;
    }
}
