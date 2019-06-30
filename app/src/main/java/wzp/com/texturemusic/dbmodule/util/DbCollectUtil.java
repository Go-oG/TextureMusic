package wzp.com.texturemusic.dbmodule.util;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.PlayListBean;
import wzp.com.texturemusic.dbmodule.DbUtil;
import wzp.com.texturemusic.dbmodule.bean.DbCollectMusicEntiy;
import wzp.com.texturemusic.dbmodule.bean.DbCollectMusicEntiyDao;
import wzp.com.texturemusic.dbmodule.bean.DbCollectPlaylistEntiy;
import wzp.com.texturemusic.dbmodule.bean.DbCollectPlaylistEntiyDao;
import wzp.com.texturemusic.util.StringUtil;

/**
 * Created by Wang on 2018/3/18.
 * 用于收藏(喜欢)的工具类
 */

public class DbCollectUtil {

    public static void clearAllLikeMusic() {
        DbUtil.getCollectMusicDao().deleteAll();
    }

    public static void clearAllLikePlaylist() {
        DbUtil.getCollectPlaylistDao().deleteAll();
    }

    public static List<DbCollectMusicEntiy> getAllLikedMusic() {
        return DbUtil.getCollectMusicDao().loadAll();
    }

    public static List<DbCollectPlaylistEntiy> getAllLikedPlaylist() {
        return DbUtil.getCollectPlaylistDao().loadAll();
    }

    /**
     * 查询某个数据是否是收藏的
     *
     * @param bean
     * @return
     */
    public static MusicBean queryLiked(MusicBean bean) {
        if (bean == null) {
            return null;
        }
        QueryBuilder<DbCollectMusicEntiy> builder = DbUtil.getCollectMusicDao().queryBuilder();
        if (bean.getDbId() != null) {
            builder.where(DbCollectMusicEntiyDao.Properties.DbId.eq(bean.getDbId()));
        } else {
            if (!StringUtil.isEmpty(bean.getMusicId())) {
                builder.where(DbCollectMusicEntiyDao.Properties.MusicId.eq(Long.valueOf(bean.getMusicId())));
            } else {
                builder.where(DbCollectMusicEntiyDao.Properties.MusicName.eq(bean.getMusicName()));
            }
        }
        List<DbCollectMusicEntiy> list = builder.list();
        if (list != null && list.size() > 0) {
            bean.setDbId(list.get(0).getDbId());
            bean.setLiked(true);
        } else {
            bean.setLiked(false);
        }
        return bean;
    }


    public static boolean queryLiked(PlayListBean playList) {
        QueryBuilder<DbCollectPlaylistEntiy> builder = DbUtil.getCollectPlaylistDao().queryBuilder();
        if (playList.getDbId() != null) {
            builder.where(DbCollectPlaylistEntiyDao.Properties.DbId.eq(playList.getDbId()));
        } else {
            if (playList.getPlaylistId() != null) {
                builder.where(DbCollectPlaylistEntiyDao.Properties.PlaylistId.eq(playList.getPlaylistId()));
            } else {
                builder.where(DbCollectPlaylistEntiyDao.Properties.PlaylistName.eq(playList.getPlaylistName()));
            }
        }
        List<DbCollectPlaylistEntiy> list = builder.list();
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

    public static void deleteLikedMusic(MusicBean bean) {
        if (bean.getDbId() != null) {
            DbUtil.getCollectMusicDao().deleteByKey(bean.getDbId());
            return;
        }
        QueryBuilder<DbCollectMusicEntiy> builder = DbUtil.getCollectMusicDao().queryBuilder();
        if (bean.getMusicId() != null) {
            builder.where(DbCollectMusicEntiyDao.Properties.MusicId.eq(Long.valueOf(bean.getMusicId())));
        } else {
            builder.where(DbCollectMusicEntiyDao.Properties.MusicName.eq(bean.getMusicName()));
        }
        builder.buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public static void deleteLikedPlaylist(PlayListBean bean) {
        if (bean.getDbId() != null) {
            DbUtil.getCollectPlaylistDao().deleteByKey(bean.getDbId());
            return;
        }
        QueryBuilder<DbCollectPlaylistEntiy> builder = DbUtil.getCollectPlaylistDao().queryBuilder();
        if (bean.getPlaylistId() != null) {
            builder.where(DbCollectPlaylistEntiyDao.Properties.PlaylistId.eq(bean.getPlaylistId()));
        } else {
            builder.where(DbCollectPlaylistEntiyDao.Properties.PlaylistName.eq(bean.getPlaylistName()));
        }
        DeleteQuery deleteQuery = builder.buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
    }
    public static void deleteLikedPlaylist(DbCollectPlaylistEntiy entiy){
        DbUtil.getCollectPlaylistDao().delete(entiy);
    }

    public static void addLikedMusic(MusicBean bean) {
        DbCollectMusicEntiy entiy = new DbCollectMusicEntiy();
        entiy.setAlbumId(Long.valueOf(bean.getAlbumId()));
        entiy.setAlbumName(bean.getAlbumName());
        entiy.setArtistId(Long.valueOf(bean.getArtistId()));
        entiy.setArtistName(bean.getArtistName());
        entiy.setCoverImgUrl(bean.getCoverImgUrl());
        entiy.setLocalMusic(bean.getLocalMusic());
        entiy.setMusicId(Long.valueOf(bean.getMusicId()));
        entiy.setMusicName(bean.getMusicName());
        DbUtil.getCollectMusicDao().insertOrReplace(entiy);
    }

    public static void addLikedPlaylist(PlayListBean bean) {
        bean.setLiked(true);
        DbCollectPlaylistEntiy entiy = new DbCollectPlaylistEntiy();
        entiy.setCollectCount(bean.getCollectCount());
        entiy.setCommentCount(bean.getCommentCount());
        entiy.setCommentId(bean.getCommentId());
        entiy.setCoverImgUr(bean.getCoverImgUr());
        entiy.setCreaterHeadPath(bean.getCreater() == null ? null : bean.getCreater().getUserCoverImgUrl());
        entiy.setCreaterId(bean.getCreaterId());
        entiy.setCreaterName(bean.getCreaterName());
        entiy.setCreateTime(bean.getCreateTime());
        entiy.setDbId(bean.getDbId());
        entiy.setDescription(bean.getDescription());
        entiy.setMusicCount(bean.getMusicCount());
        entiy.setPlayCount(bean.getPlayCount());
        entiy.setPlaylistId(bean.getPlaylistId());
        entiy.setPlaylistName(bean.getPlaylistName());
        entiy.setSubDescription(bean.getSubDescription());
        entiy.setShareCount(bean.getShareCount());
        entiy.setLiked(bean.getLiked());
        DbUtil.getCollectPlaylistDao().insertOrReplace(entiy);
    }


}
