package wzp.com.texturemusic.dbmodule.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Wang on 2018/2/21.
 * 用于保存搜索历史
 */
@Entity
public class DbSearchHistoryBean {
    @Id(autoincrement = true)
    private Long dbId;
    private String searchStr;
    private Long searchTime;
    @Generated(hash = 1748097309)
    public DbSearchHistoryBean(Long dbId, String searchStr, Long searchTime) {
        this.dbId = dbId;
        this.searchStr = searchStr;
        this.searchTime = searchTime;
    }
    @Generated(hash = 100095561)
    public DbSearchHistoryBean() {
    }
    public Long getDbId() {
        return this.dbId;
    }
    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }
    public String getSearchStr() {
        return this.searchStr;
    }
    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }
    public Long getSearchTime() {
        return this.searchTime;
    }
    public void setSearchTime(Long searchTime) {
        this.searchTime = searchTime;
    }
}
