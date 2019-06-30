package wzp.com.texturemusic.common.bean;

import java.util.List;
import java.util.Map;

import wzp.com.texturemusic.bean.AlbumBean;


/**
 * Created by Wang on 2018/3/7.
 * 用于保存首页电台的数据
 */

public class RadioCoverBean {
    private Map<String, List<AlbumBean>> map;

    public Map<String, List<AlbumBean>> getMap() {
        return map;
    }

    public void setMap(Map<String, List<AlbumBean>> map) {
        this.map = map;
    }
}
