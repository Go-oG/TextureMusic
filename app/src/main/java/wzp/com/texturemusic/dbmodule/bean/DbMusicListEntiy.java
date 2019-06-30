package wzp.com.texturemusic.dbmodule.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Go_oG
 * Description:
 * 该数据表专门用于存储
 * 还没有播放的数据
 * 例如DataMAnager里面的队列
 * on 2017/11/23.
 */
@Entity
public class DbMusicListEntiy {
    @Id(autoincrement = true)
    private Long dbId;//在本地数据库的id
    private String jsonInfo;
    @Generated(hash = 2113027634)
    public DbMusicListEntiy(Long dbId, String jsonInfo) {
        this.dbId = dbId;
        this.jsonInfo = jsonInfo;
    }
    @Generated(hash = 1850946523)
    public DbMusicListEntiy() {
    }
    public Long getDbId() {
        return this.dbId;
    }
    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }
    public String getJsonInfo() {
        return this.jsonInfo;
    }
    public void setJsonInfo(String jsonInfo) {
        this.jsonInfo = jsonInfo;
    }

}
