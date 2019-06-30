package wzp.com.texturemusic.dbmodule.util;

import android.os.SystemClock;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.dbmodule.DbUtil;
import wzp.com.texturemusic.dbmodule.bean.DbHistoryEntiy;
import wzp.com.texturemusic.dbmodule.bean.DbHistoryEntiyDao;
import wzp.com.texturemusic.util.LogUtil;
import wzp.com.texturemusic.util.StringUtil;

/**
 * Created by Wang on 2018/3/18.
 * 用于操作播放历史的工具类
 */

public class DbHistoryUtil {

    /**
     * 清除历史播放记录
     */
    public static void clearHistoryMusicData() {
        DbUtil.getHistoryDao().deleteAll();
    }

    /**
     * 获取所有的播放历史
     */
    public static List<MusicBean> getAllHistoryDataForManager() {
        List<MusicBean> list = new ArrayList<>();
        QueryBuilder<DbHistoryEntiy> queryBuilder = DbUtil.getHistoryDao().queryBuilder();
        queryBuilder.orderDesc(DbHistoryEntiyDao.Properties.UpdateTimme);
        List<DbHistoryEntiy> dataList = queryBuilder.list();
        for (DbHistoryEntiy entiy : dataList) {
            MusicBean bean = new MusicBean();
            if (entiy.getArtistId() != null) {
                bean.setArtistId(String.valueOf(entiy.getArtistId()));
            }
            if (entiy.getAlbumId() != null) {
                bean.setAlbumId(String.valueOf(entiy.getAlbumId()));
            }
            bean.setArtistName(entiy.getArtistName());
            bean.setCoverImgUrl(entiy.getCoverImgUrl());
            bean.setAlbumName(entiy.getAlbumName());
            bean.setPlayPath(entiy.getPlayPath());
            bean.setLocalMusic(entiy.getLocalMusic());
            bean.setSQMusic(entiy.getIsSQMusic());
            if (entiy.getMusicId() != null) {
                bean.setMusicId(String.valueOf(entiy.getMusicId()));
            }
            bean.setMusicName(entiy.getMusicName());
            bean.setAllTime(entiy.getDuration());
            bean.setDbId(entiy.getDbId());
            bean.setHasMV(entiy.getHasMv());
            if (entiy.getMvId() != null) {
                MvBean mvBean = new MvBean();
                mvBean.setMvId(entiy.getMvId());
            }
            bean.setLocalArtist(entiy.getLocalMusic());
            bean.setCommentId(entiy.getCommentId());
            list.add(bean);
        }
        return list;
    }

    /**
     * 获取所有的播放历史
     */
    public static List<MusicBean> getAllHistoryDataForManager(int limit) {
        List<MusicBean> list = new ArrayList<>();
        QueryBuilder<DbHistoryEntiy> queryBuilder = DbUtil.getHistoryDao().queryBuilder();
        queryBuilder.orderDesc(DbHistoryEntiyDao.Properties.UpdateTimme)
                .offset(0)
                .limit(limit);
        List<DbHistoryEntiy> dataList = queryBuilder.list();
        for (DbHistoryEntiy entiy : dataList) {
            MusicBean bean = new MusicBean();
            if (entiy.getArtistId() != null) {
                bean.setArtistId(String.valueOf(entiy.getArtistId()));
            }
            if (entiy.getAlbumId() != null) {
                bean.setAlbumId(String.valueOf(entiy.getAlbumId()));
            }
            bean.setArtistName(entiy.getArtistName());
            bean.setCoverImgUrl(entiy.getCoverImgUrl());
            bean.setAlbumName(entiy.getAlbumName());
            bean.setPlayPath(entiy.getPlayPath());
            bean.setLocalMusic(entiy.getLocalMusic());
            bean.setSQMusic(entiy.getIsSQMusic());
            if (entiy.getMusicId() != null) {
                bean.setMusicId(String.valueOf(entiy.getMusicId()));
            }
            bean.setMusicName(entiy.getMusicName());
            bean.setAllTime(entiy.getDuration());
            bean.setDbId(entiy.getDbId());
            bean.setHasMV(entiy.getHasMv());
            if (entiy.getMvId() != null) {
                MvBean mvBean = new MvBean();
                mvBean.setMvId(entiy.getMvId());
            }
            bean.setLocalArtist(entiy.getLocalMusic());
            bean.setCommentId(entiy.getCommentId());
            list.add(bean);
        }
        return list;
    }

    public static List<DbHistoryEntiy> getAllHistoryData() {
        QueryBuilder<DbHistoryEntiy> queryBuilder = DbUtil.getHistoryDao().queryBuilder();
        queryBuilder.orderDesc(DbHistoryEntiyDao.Properties.UpdateTimme);
        return queryBuilder.list();
    }

    public static void insertMusicHistory(MusicBean bean) {
        List<DbHistoryEntiy> list;
        QueryBuilder queryBuilder = DbUtil.getHistoryDao().queryBuilder();
        if (bean.getLocalMusic()) {
            //本地音乐
            if (bean.getPlayPath() != null) {
                queryBuilder.where(DbHistoryEntiyDao.Properties.LocalMusic.eq(true),
                        DbHistoryEntiyDao.Properties.PlayPath.eq(bean.getPlayPath()));
            } else {
                queryBuilder.where(DbHistoryEntiyDao.Properties.LocalMusic.eq(true),
                        DbHistoryEntiyDao.Properties.MusicName.eq(bean.getMusicName()));
            }
        } else {
            if (!StringUtil.isEmpty(bean.getMusicId())) {
                queryBuilder.where(DbHistoryEntiyDao.Properties.LocalMusic.eq(false),
                        DbHistoryEntiyDao.Properties.MusicId.eq(Long.valueOf(bean.getMusicId())));
            } else {
                queryBuilder.where(DbHistoryEntiyDao.Properties.LocalMusic.eq(false),
                        DbHistoryEntiyDao.Properties.MusicName.eq(Long.valueOf(bean.getMusicName())));
            }
        }
        list = queryBuilder.list();
        if (!list.isEmpty()) {
            DbHistoryEntiy entiy1 = list.get(0);
            entiy1.setUpdateTimme(System.currentTimeMillis());
            DbUtil.getHistoryDao().update(entiy1);
        } else {
            DbHistoryEntiy entiy = new DbHistoryEntiy();
            if (!StringUtil.isEmpty(bean.getAlbumId())) {
                entiy.setAlbumId(Long.valueOf(bean.getAlbumId()));
            }
            entiy.setAlbumName(bean.getAlbumName());
            if (!StringUtil.isEmpty(bean.getArtistId())) {
                entiy.setArtistId(Long.valueOf(bean.getArtistId()));
            }
            entiy.setArtistName(bean.getArtistName());
            entiy.setCommentId(bean.getCommentId());
            entiy.setCoverImgUrl(bean.getCoverImgUrl());
            entiy.setDuration(bean.getAllTime());
            entiy.setHasMv(bean.getHasMV());
            entiy.setIsSQMusic(bean.getSQMusic());
            if (bean.getHasMV() != null && bean.getHasMV()) {
                entiy.setMvId(bean.getMvBean().getMvId());
            }
            entiy.setLocalMusic(bean.getLocalMusic());
            if (!StringUtil.isEmpty(bean.getMusicId())) {
                entiy.setMusicId(Long.valueOf(bean.getMusicId()));
            }
            entiy.setMusicName(bean.getMusicName());
            entiy.setUpdateTimme(System.currentTimeMillis());
            entiy.setPlayPath(bean.getPlayPath());
            entiy.setAlias(bean.getAlias());
            DbUtil.getHistoryDao().insert(entiy);
        }
    }

}
