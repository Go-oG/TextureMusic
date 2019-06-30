package wzp.com.texturemusic.common.bean;

import java.util.List;
import java.util.Map;

import wzp.com.texturemusic.playlistmodule.bean.PlaylistRankBean;

/**
 * Created by Wang on 2018/3/7.
 * 用于保存首页歌单排行榜的数据
 */

public class RankCoverBean {
    private Map<String, List<PlaylistRankBean>> map;

    public Map<String, List<PlaylistRankBean>> getMap() {
        return map;
    }

    public void setMap(Map<String, List<PlaylistRankBean>> map) {
        this.map = map;
    }

}
