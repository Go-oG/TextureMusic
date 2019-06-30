package wzp.com.texturemusic.common.bean;

import java.util.List;
import java.util.Map;

import wzp.com.texturemusic.personalmodule.bean.IndexPersonEntiy;

/**
 * Created by Go_oG
 * Description: 精彩推荐页面的数据实体
 * on 2017/12/27.
 */

public class PersonalCoverBean{
    private Map<String, List<IndexPersonEntiy>> map;

    public Map<String, List<IndexPersonEntiy>> getMap() {
        return map;
    }

    public void setMap(Map<String, List<IndexPersonEntiy>> map) {
        this.map = map;
    }
}
