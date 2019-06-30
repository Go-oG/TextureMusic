package wzp.com.texturemusic.dbmodule.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Go_oG
 * Description:用于保存下载的歌曲或者歌单id，在再次下载时提醒用户已经下载 过了
 * on 2017/11/21.
 */
@Entity
public class DbDownloadList {
    @Id(autoincrement = true)
    private Long dbId;
    private String id;
    private Integer type;//0 歌单 1 歌曲 2 MV
    @Generated(hash = 1616878168)
    public DbDownloadList(Long dbId, String id, Integer type) {
        this.dbId = dbId;
        this.id = id;
        this.type = type;
    }
    @Generated(hash = 482415757)
    public DbDownloadList() {
    }
    public Long getDbId() {
        return this.dbId;
    }
    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Integer getType() {
        return this.type;
    }
    public void setType(Integer type) {
        this.type = type;
    }

}
