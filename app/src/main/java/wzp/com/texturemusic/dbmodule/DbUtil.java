package wzp.com.texturemusic.dbmodule;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import wzp.com.texturemusic.MyApplication;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.core.manger.DataManager;
import wzp.com.texturemusic.dbmodule.bean.DaoMaster;
import wzp.com.texturemusic.dbmodule.bean.DaoSession;
import wzp.com.texturemusic.dbmodule.bean.DbCacheEntiyDao;
import wzp.com.texturemusic.dbmodule.bean.DbCollectMusicEntiyDao;
import wzp.com.texturemusic.dbmodule.bean.DbCollectPlaylistEntiyDao;
import wzp.com.texturemusic.dbmodule.bean.DbDownloadEntiyDao;
import wzp.com.texturemusic.dbmodule.bean.DbDownloadList;
import wzp.com.texturemusic.dbmodule.bean.DbDownloadListDao;
import wzp.com.texturemusic.dbmodule.bean.DbHistoryEntiy;
import wzp.com.texturemusic.dbmodule.bean.DbHistoryEntiyDao;
import wzp.com.texturemusic.dbmodule.bean.DbMusicEntiy;
import wzp.com.texturemusic.dbmodule.bean.DbMusicEntiyDao;
import wzp.com.texturemusic.dbmodule.bean.DbMusicListEntiy;
import wzp.com.texturemusic.dbmodule.bean.DbMusicListEntiyDao;
import wzp.com.texturemusic.dbmodule.bean.DbSearchHistoryBeanDao;
import wzp.com.texturemusic.util.LogUtil;

/**
 * Created by Go_oG
 * Description:数据库工具类
 * on 2017/10/15.
 */
public class DbUtil {
    private static final String TAG = "DbUtil";
    private static final String DB_NAME = "texturemusic.db";
    private static DaoSession daoSession;

    public static DaoSession initDaoSession() {
        if (daoSession == null) {
            synchronized (DaoSession.class) {
                if (daoSession == null) {
                    DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(MyApplication.getInstace(), DB_NAME, null);
                    DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
                    daoSession = daoMaster.newSession();
                }
            }
        }
        return daoSession;
    }

    public static DbMusicEntiyDao getMusicDao() {
        return initDaoSession().getDbMusicEntiyDao();
    }

    public static DbDownloadEntiyDao getDownloadDao() {
        return initDaoSession().getDbDownloadEntiyDao();
    }

    public static DbCollectPlaylistEntiyDao getCollectPlaylistDao() {
        return initDaoSession().getDbCollectPlaylistEntiyDao();
    }

    public static DbCollectMusicEntiyDao getCollectMusicDao() {
        return initDaoSession().getDbCollectMusicEntiyDao();
    }

    public static DbHistoryEntiyDao getHistoryDao() {
        return initDaoSession().getDbHistoryEntiyDao();
    }

    public static DbDownloadListDao getDownloadListDao() {
        return initDaoSession().getDbDownloadListDao();
    }

    public static DbMusicListEntiyDao getMusicListDao() {
        return initDaoSession().getDbMusicListEntiyDao();
    }

    public static DbSearchHistoryBeanDao getSearchDao() {
        return initDaoSession().getDbSearchHistoryBeanDao();
    }

    public static DbCacheEntiyDao getCacheDao() {
        return initDaoSession().getDbCacheEntiyDao();
    }


    public static void insertDownloadListHistory(String id, Integer type) {
        DbDownloadList list = new DbDownloadList(null, id, type);
        getDownloadListDao().insert(list);
        closeDb();
    }

    public static boolean alreadyDownload(String id, Integer type) {
        QueryBuilder builder = getDownloadListDao().queryBuilder();
        List<DbDownloadListDao> list = builder.where(DbDownloadListDao.Properties.Id.eq(id),
                DbDownloadListDao.Properties.Type.eq(type)).list();
        closeDb();
        if (list != null && list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取保存的未播放数据
     */
    public static List<MusicBean> getSaveWillPlayMusicList() {
        List<DbMusicListEntiy> listEntiys = DbUtil.getMusicListDao().loadAll();
        List<MusicBean> list = new ArrayList<>();
        for (DbMusicListEntiy entiy : listEntiys) {
            MusicBean bean = JSONObject.parseObject(entiy.getJsonInfo(), MusicBean.class);
            list.add(bean);
        }
        return list;
    }

    /**
     * 保存未播放的数据
     */
    public static void saveWillPlayMusicList(List<MusicBean> list) {
        List<DbMusicListEntiy> entiyList = new ArrayList<>();
        for (MusicBean bean : list) {
            DbMusicListEntiy entiy = new DbMusicListEntiy();
            entiy.setJsonInfo(JSONObject.toJSONString(bean));
            entiyList.add(entiy);
        }
        DbMusicListEntiyDao dao = getMusicListDao();
        dao.deleteAll();
        dao.insertInTx(entiyList);
    }

    public static MusicBean dbMusicEntiyToMusicBean(DbMusicEntiy dbMusicEntiy) {
        MusicBean musicBean = new MusicBean();
        musicBean.setDbId(dbMusicEntiy.getMusicDbId());
        musicBean.setPlayPath(dbMusicEntiy.getMusicPathUrl());
        musicBean.setAllTime(dbMusicEntiy.getDurationTime());
        musicBean.setAlbumId(String.valueOf(dbMusicEntiy.getAlbumId()));
        musicBean.setAlbumName(dbMusicEntiy.getAlbumName());
        musicBean.setArtistId(String.valueOf(dbMusicEntiy.getArtistId()));
        musicBean.setArtistName(dbMusicEntiy.getArtistName());
        musicBean.setLocalMusic(dbMusicEntiy.getIsLocalMusic());
        musicBean.setMusicId(String.valueOf(dbMusicEntiy.getMusicId()));
        musicBean.setMusicName(dbMusicEntiy.getMusicName());
        musicBean.setCoverImgUrl(dbMusicEntiy.getCoverImgUrl());
        musicBean.setAllTime(dbMusicEntiy.getDurationTime());
        musicBean.setHasCheck(false);
        musicBean.setSQMusic(dbMusicEntiy.getIsSQMusic());
        musicBean.setAlias(dbMusicEntiy.getAlias());
        Boolean hasMv = dbMusicEntiy.getHasMv();
        musicBean.setHasMV(hasMv);
        if (hasMv != null && hasMv) {
            MvBean mvBean = new MvBean();
            mvBean.setMvId(dbMusicEntiy.getMvId());
            musicBean.setMvBean(mvBean);
        }
        return musicBean;
    }

    public static MusicBean dbHistoryEntiyToMusicBean(DbHistoryEntiy dbMusicEntiy) {
        MusicBean musicBean = new MusicBean();
        musicBean.setDbId(dbMusicEntiy.getDbId());
        musicBean.setPlayPath(dbMusicEntiy.getPlayPath());
        musicBean.setPlayPath(dbMusicEntiy.getPlayPath());
        musicBean.setAllTime(dbMusicEntiy.getDuration());
        musicBean.setAlbumId(String.valueOf(dbMusicEntiy.getAlbumId()));
        musicBean.setAlbumName(dbMusicEntiy.getAlbumName());
        musicBean.setArtistId(String.valueOf(dbMusicEntiy.getArtistId()));
        musicBean.setArtistName(dbMusicEntiy.getArtistName());
        musicBean.setLocalMusic(dbMusicEntiy.getLocalMusic());
        musicBean.setMusicId(String.valueOf(dbMusicEntiy.getMusicId()));
        musicBean.setMusicName(dbMusicEntiy.getMusicName());
        musicBean.setCoverImgUrl(dbMusicEntiy.getCoverImgUrl());
        musicBean.setAllTime(dbMusicEntiy.getDuration());
        musicBean.setHasCheck(false);
        musicBean.setSQMusic(dbMusicEntiy.getIsSQMusic());
        musicBean.setAlias(dbMusicEntiy.getAlias());
        Boolean hasMv = dbMusicEntiy.getHasMv();
        musicBean.setHasMV(hasMv);
        if (hasMv != null && hasMv) {
            MvBean mvBean = new MvBean();
            mvBean.setMvId(dbMusicEntiy.getMvId());
            musicBean.setMvBean(mvBean);
        }
        return musicBean;
    }

    public static DbMusicEntiy musicBeanToDbMusicEntiy(MusicBean bean) {
        DbMusicEntiy dbMusicEntiy = new DbMusicEntiy();
        String albumId = bean.getAlbumId();
        if (TextUtils.isEmpty(albumId) || albumId.equals("null")) {
            dbMusicEntiy.setAlbumId(null);
        } else {
            dbMusicEntiy.setAlbumId(Long.valueOf(albumId));
        }
        dbMusicEntiy.setAlbumName(bean.getAlbumName());
        String artistId = bean.getArtistId();
        if (TextUtils.isEmpty(artistId) || artistId.equals("null")) {
            dbMusicEntiy.setArtistId(null);
        } else {
            dbMusicEntiy.setArtistId(Long.valueOf(artistId));
        }
        String musicId = bean.getMusicId();
        if (TextUtils.isEmpty(musicId) || musicId.equals("null")) {
            dbMusicEntiy.setMusicId(null);
        } else {
            dbMusicEntiy.setMusicId(Long.parseLong(musicId));
        }
        dbMusicEntiy.setArtistName(bean.getArtistName());
        dbMusicEntiy.setMusicName(bean.getMusicName());
        dbMusicEntiy.setIsLocalMusic(bean.getLocalMusic());
        dbMusicEntiy.setMusicPathUrl(bean.getPlayPath());
        dbMusicEntiy.setCoverImgUrl(bean.getCoverImgUrl());
        dbMusicEntiy.setMusicDbId(bean.getDbId());
        dbMusicEntiy.setDurationTime(bean.getAllTime());
        dbMusicEntiy.setIsSQMusic(bean.getSQMusic());
        dbMusicEntiy.setAlias(bean.getAlias());
        dbMusicEntiy.setUpdateTime(System.currentTimeMillis());
        Boolean hasMv = bean.getHasMV();
        if (hasMv != null && hasMv && bean.getMvBean() != null) {
            dbMusicEntiy.setHasMv(true);
            dbMusicEntiy.setMvId(bean.getMvBean().getMvId());
        } else {
            dbMusicEntiy.setHasMv(false);
        }
        return dbMusicEntiy;
    }

    public static void closeDb() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }

}
