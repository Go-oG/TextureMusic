package wzp.com.texturemusic.dbmodule.util;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.List;

import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.dbmodule.DbUtil;
import wzp.com.texturemusic.dbmodule.bean.DbDownloadEntiy;
import wzp.com.texturemusic.dbmodule.bean.DbDownloadEntiyDao;
import wzp.com.texturemusic.dbmodule.bean.DbMusicEntiy;
import wzp.com.texturemusic.dbmodule.bean.DbMusicEntiyDao;

/**
 * Created by Go_oG
 * Description:用于记录文件下载完成的数据库的工具类
 * on 2018/2/7.
 */

public class DbDownloadUtil {

    public static List<DbDownloadEntiy> getAllDowlaodEntiy() {
        return DbUtil.getDownloadDao().loadAll();
    }

    public static void addDownloadEntiy(MusicBean bean, File file) {
        DbDownloadEntiy entiy = musicToEntiy(bean);
        entiy.setFileSize(bean.getMusicFileSize());
        entiy.setFilePath(file.getAbsolutePath());
        entiy.setFileSize(file.length());
        entiy.setDownloadTime(System.currentTimeMillis());
        DbUtil.getDownloadDao().insertOrReplace(entiy);
    }

    public static void addDownloadEntiy(MvBean bean, File file) {
        DbDownloadEntiy entiy = mvToEntiy(bean);
        entiy.setFileSize(file.length());
        entiy.setFilePath(file.getAbsolutePath());
        entiy.setDownloadTime(System.currentTimeMillis());
        DbUtil.getDownloadDao().insert(entiy);
    }

    public static void deleteDownloadEntiy(DbDownloadEntiy entiy) {
        DbUtil.getDownloadDao().delete(entiy);
    }

    public static void deleteDownloadMusic(MusicBean bean) {
        if (bean == null) {
            return;
        }
        QueryBuilder builder = DbUtil.getDownloadDao().queryBuilder();
        builder.where(DbDownloadEntiyDao.Properties.IsMv.isNotNull(),
                DbDownloadEntiyDao.Properties.IsMv.eq(false),
                DbDownloadEntiyDao.Properties.MusicId.eq(Long.valueOf(bean.getMusicId())));
        builder.buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public static List<DbDownloadEntiy> getAllDownloadEntiy() {
        return DbUtil.getDownloadDao().loadAll();
    }

    public static List<DbDownloadEntiy> getAllDownloadEntiyForMusic() {
        QueryBuilder<DbDownloadEntiy> builder = DbUtil.getDownloadDao().queryBuilder();
        builder.where(DbDownloadEntiyDao.Properties.IsMv.isNotNull(),
                DbDownloadEntiyDao.Properties.IsMv.eq(false));
        return builder.list();
    }

    public static List<DbDownloadEntiy> getAllDownloadEntiyForMv() {
        QueryBuilder<DbDownloadEntiy> builder = DbUtil.getDownloadDao().queryBuilder();
        builder.where(DbDownloadEntiyDao.Properties.IsMv.isNotNull(),
                DbDownloadEntiyDao.Properties.IsMv.eq(true));
        return builder.list();
    }

    public static void clearDownloadHisoryForMusic() {
        QueryBuilder builder = DbUtil.getDownloadDao().queryBuilder();
        builder.where(DbDownloadEntiyDao.Properties.IsMv.isNotNull(),
                DbDownloadEntiyDao.Properties.IsMv.eq(false));
        builder.buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public static void clearDownloadHisoryForMv() {
        QueryBuilder builder = DbUtil.getDownloadDao().queryBuilder();
        builder.where(DbDownloadEntiyDao.Properties.IsMv.isNotNull(),
                DbDownloadEntiyDao.Properties.IsMv.eq(true));
        builder.buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 查询下载文件的详细信息
     */
    public static MusicBean queryMusic(DbDownloadEntiy entiy) {
        if (entiy == null) {
            return null;
        }
        QueryBuilder<DbDownloadEntiy> builder = DbUtil.getDownloadDao().queryBuilder();
        if (entiy.getFilePath() != null) {
            builder.where(DbDownloadEntiyDao.Properties.FilePath.isNotNull(),
                    DbDownloadEntiyDao.Properties.FilePath.eq(entiy.getFilePath()));
        } else {
            builder.where(DbDownloadEntiyDao.Properties.Name.isNotNull(),
                    DbDownloadEntiyDao.Properties.Name.eq(entiy.getName()));
        }
        List<DbDownloadEntiy> list = builder.list();
        if (list != null && list.size() > 0) {
            return downloadEntiyToMusic(list.get(0));
        }
        return null;
    }

    public static DbDownloadEntiy musicToEntiy(MusicBean bean) {
        DbDownloadEntiy entiy = new DbDownloadEntiy();
        entiy.setName(bean.getMusicName());
        entiy.setAlbumId(Long.valueOf(bean.getAlbumId()));
        entiy.setAlbumName(bean.getAlbumName());
        entiy.setAlias(bean.getAlias());
        entiy.setArtistId(Long.valueOf(bean.getArtistId()));
        entiy.setArtistName(bean.getArtistName());
        entiy.setIsMv(false);
        entiy.setFileSize(bean.getMusicFileSize());
        entiy.setDownloadTime(System.currentTimeMillis());
        entiy.setMusicInfo(JSONObject.toJSONString(bean));
        return entiy;
    }

    public static MusicBean downloadEntiyToMusic(DbDownloadEntiy entiy) {
        if (entiy == null) {
            return null;
        }
        MusicBean bean = JSONObject.parseObject(entiy.getMusicInfo(), MusicBean.class);
        if (bean != null) {
            bean.setLocalMusic(true);
            bean.setPlayPath(entiy.getFilePath());
        }
        return bean;
    }

    public static DbDownloadEntiy mvToEntiy(MvBean bean) {
        DbDownloadEntiy entiy = new DbDownloadEntiy();
        entiy.setMvInfo(JSONObject.toJSONString(bean));
        entiy.setName(bean.getMvName());
        entiy.setIsMv(true);
        entiy.setCoverImgUrl(bean.getCoverImgUrl());
        entiy.setAlbumId(bean.getAlbumId());
        entiy.setAlbumName(bean.getAlbumName());
        entiy.setArtistId(bean.getArtistId());
        entiy.setArtistName(bean.getArtistName());
        entiy.setDownloadTime(System.currentTimeMillis());
        entiy.setMvId(bean.getMvId());
        return entiy;
    }


}
