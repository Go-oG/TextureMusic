package wzp.com.texturemusic.dbmodule.util;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.config.AppFileConstant;
import wzp.com.texturemusic.dbmodule.DbUtil;
import wzp.com.texturemusic.dbmodule.bean.DbCacheEntiy;
import wzp.com.texturemusic.dbmodule.bean.DbCacheEntiyDao;
import wzp.com.texturemusic.util.LogUtil;
import wzp.com.texturemusic.util.StringUtil;

/**
 * 只保存缓存的音乐信息
 */
public class DbCacheUtil {
    private static final String TAG = "DbCacheUtil";

    public static void addCacheData(MusicBean bean, String cacheKey) {
       if(bean==null||StringUtil.isEmpty(cacheKey)){
           return;
       }
        QueryBuilder<DbCacheEntiy> builder = DbUtil.getCacheDao().queryBuilder();
        builder.where(DbCacheEntiyDao.Properties.CacheKey.eq(cacheKey));
        DbCacheEntiy entiy = builder.unique();
        if (entiy != null) {
            //存在 更新数据
            Long dbId = entiy.getDbId();
            entiy = musicToCacheEntiy(bean);
            if (entiy != null) {
                entiy.setDbId(dbId);
                entiy.setCacheKey(cacheKey);
                DbUtil.getCacheDao().update(entiy);
            }
        } else {
            //不存在 插入数据
            entiy = musicToCacheEntiy(bean);
            if (entiy == null) {
                return;
            }
            entiy.setCacheKey(cacheKey);
            DbUtil.getCacheDao().insert(entiy);
        }
    }

    public static void clearCacheData() {
        DbUtil.getCacheDao().deleteAll();
    }

    public static void deleteCacheData(DbCacheEntiy entiy) {
        if (entiy.getDbId() != null) {
            DbUtil.getCacheDao().delete(entiy);
            return;
        }
        QueryBuilder<DbCacheEntiy> builder = DbUtil.getCacheDao().queryBuilder();
        if (entiy.getCacheKey() != null) {
            builder.where(DbCacheEntiyDao.Properties.CacheKey.eq(entiy.getCacheKey()));
            DeleteQuery deleteQuery = builder.buildDelete();
            deleteQuery.executeDeleteWithoutDetachingEntities();
            return;
        }
        if (entiy.getMusicId() != null) {
            builder.where(DbCacheEntiyDao.Properties.MusicId.eq(entiy.getMusicId()));
            builder.buildDelete().executeDeleteWithoutDetachingEntities();
            return;
        }
        if (entiy.getMusicName() != null) {
            builder.where(DbCacheEntiyDao.Properties.MusicName.eq(entiy.getMusicName()));
            builder.buildDelete().executeDeleteWithoutDetachingEntities();
        }
    }

    public static void deleteCacheData(MusicBean bean) {
        deleteCacheData(musicToCacheEntiy(bean));
    }

    public static DbCacheEntiy queryCacheData(DbCacheEntiy entiy) {
        if (entiy.getCacheKey() != null) {
            QueryBuilder<DbCacheEntiy> builder = DbUtil.getCacheDao().queryBuilder();
            builder.where(DbCacheEntiyDao.Properties.CacheKey.eq(entiy.getCacheKey()));
            return builder.unique();
        }
        return null;
    }

    /**
     * 根据ID 或者音乐名字 或者原始播放地址来查询数据
     * @param bean
     * @return
     */
    public static DbCacheEntiy queryCacheData(MusicBean bean) {
        QueryBuilder<DbCacheEntiy> builder = DbUtil.getCacheDao().queryBuilder();
        if (bean.getMusicId() != null) {
            builder.where(DbCacheEntiyDao.Properties.MusicId.eq(Long.valueOf(bean.getMusicId())));
            return builder.unique();
        }
        if (bean.getMusicName() != null) {
            builder.where(DbCacheEntiyDao.Properties.MusicName.eq(bean.getMusicName()));
            return builder.unique();
        }
        if (!StringUtil.isEmpty(bean.getPlayPath())) {
            builder.where(DbCacheEntiyDao.Properties.PlayPath.eq(bean.getPlayPath()));
            return builder.unique();
        }

        return null;
    }

    public static DbCacheEntiy musicToCacheEntiy(MusicBean bean) {
        if (bean != null) {
            DbCacheEntiy entiy = new DbCacheEntiy();
            entiy.setMusicName(bean.getMusicName());
            entiy.setLocalMusic(bean.getLocalMusic());
            entiy.setAlbumName(bean.getAlbumName());
            entiy.setArtistName(bean.getArtistName());
            entiy.setCoverImgUrl(bean.getCoverImgUrl());
            entiy.setPlayPath(bean.getPlayPath());
            entiy.setDurationTime(bean.getAllTime());
            if (StringUtil.isEmpty(bean.getAlbumId())) {
                entiy.setAlbumId(null);
            } else {
                entiy.setAlbumId(Long.valueOf(bean.getAlbumId()));
            }
            if (StringUtil.isEmpty(bean.getArtistId())) {
                entiy.setArtistId(null);
            } else {
                entiy.setArtistId(Long.valueOf(bean.getArtistId()));
            }
            if (StringUtil.isEmpty(bean.getMusicId())) {
                entiy.setMusicId(null);
            } else {
                entiy.setMusicId(Long.valueOf(bean.getMusicId()));
            }
            return entiy;
        }
        return null;
    }

    public static MusicBean cacheEntiyToMusic(DbCacheEntiy entiy) {
        if (entiy != null) {
            MusicBean bean = new MusicBean();
            bean.setLocalMusic(entiy.getLocalMusic());
            bean.setMusicName(entiy.getMusicName());
            bean.setArtistName(entiy.getArtistName());
            bean.setAlbumName(entiy.getAlbumName());
            bean.setCoverImgUrl(entiy.getCoverImgUrl());
            bean.setPlayPath(entiy.getPlayPath());
            bean.setAllTime(entiy.getDurationTime());
            Long albumId = entiy.getAlbumId();
            if (albumId != null && albumId != 0) {
                bean.setAlbumId(String.valueOf(albumId));
            }
            Long artistId = entiy.getArtistId();
            if (artistId != null && artistId != 0) {
                bean.setArtistId(String.valueOf(artistId));
            }
            Long musicId = entiy.getMusicId();
            if (musicId != null && musicId != 0) {
                bean.setMusicId(String.valueOf(musicId));
            }
            if (entiy.getCacheKey() != null) {
                String playPath = AppFileConstant.MEDIA_CACHE_DRESS + entiy.getCacheKey();
                bean.setPlayPath(playPath);
            }
            return bean;
        }
        return null;
    }


}
