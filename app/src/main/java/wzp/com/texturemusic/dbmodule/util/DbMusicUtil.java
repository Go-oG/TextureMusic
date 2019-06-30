package wzp.com.texturemusic.dbmodule.util;

import androidx.annotation.Nullable;
import android.text.TextUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.dbmodule.DbUtil;
import wzp.com.texturemusic.dbmodule.bean.DbDownloadEntiy;
import wzp.com.texturemusic.dbmodule.bean.DbMusicEntiy;
import wzp.com.texturemusic.dbmodule.bean.DbMusicEntiyDao;
import wzp.com.texturemusic.util.StringUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2018/2/7.
 */

public class DbMusicUtil {
    private static final String TAG = "DbMusicUtil";

    /**
     * 获取在 本地数据库中所有的音乐
     */
    public static List<DbMusicEntiy> queryAllLocalMusic() {
        return DbUtil.getMusicDao()
                .queryBuilder()
                .where(DbMusicEntiyDao.Properties.IsLocalMusic.eq(true))
                .orderAsc(DbMusicEntiyDao.Properties.MusicName)
                .list();
    }

    /**
     * 通过下载的数据库查询
     * 本地数据库数据
     * @param entiy
     * @return
     */
    public static MusicBean queryMusic(DbDownloadEntiy entiy) {
        if (entiy == null) {
            return null;
        }
        QueryBuilder<DbMusicEntiy> builder = DbUtil.getMusicDao().queryBuilder();
        if (entiy.getFilePath() != null) {
            builder.where(DbMusicEntiyDao.Properties.MusicPathUrl.isNotNull(),
                    DbMusicEntiyDao.Properties.MusicPathUrl.eq(entiy.getFilePath()));
        } else {
            builder.where(DbMusicEntiyDao.Properties.MusicName.isNotNull(),
                    DbMusicEntiyDao.Properties.MusicName.eq(entiy.getName()));
        }
        List<DbMusicEntiy> list = builder.list();
        if (list != null && list.size() > 0) {
            return DbUtil.dbMusicEntiyToMusicBean(list.get(0));
        }
        return null;
    }

    /**
     * 获取在 本地数据库中所有的专辑
     */
    public static List<MusicBean> queryLocalAlbumMusic(AlbumBean bean) {
        List<MusicBean> list = new ArrayList<>();
        if (bean.getLocalAlbum()) {
            List<DbMusicEntiy> entiyList = DbUtil.getMusicDao()
                    .queryBuilder()
                    .where(DbMusicEntiyDao.Properties.AlbumName.eq(bean.getAlbumName()),
                            DbMusicEntiyDao.Properties.IsLocalMusic.eq(true))
                    .orderAsc(DbMusicEntiyDao.Properties.AlbumName)
                    .list();
            for (DbMusicEntiy entiy : entiyList) {
                entiy.setIsLocalMusic(true);
                list.add(DbUtil.dbMusicEntiyToMusicBean(entiy));
            }
        }
        return list;
    }

    /**
     * 获取在 本地数据库中所有的歌手
     */
    public static List<MusicBean> queryLocalArtistMusic(ArtistBean bean) {
        List<MusicBean> list = new ArrayList<>();
        if (bean.getLocalArtist()) {
            List<DbMusicEntiy> entiyList = DbUtil.getMusicDao()
                    .queryBuilder()
                    .where(DbMusicEntiyDao.Properties.ArtistName.eq(bean.getArtistName()),
                            DbMusicEntiyDao.Properties.IsLocalMusic.eq(true))
                    .orderAsc(DbMusicEntiyDao.Properties.ArtistName)
                    .list();
            for (DbMusicEntiy entiy : entiyList) {
                entiy.setIsLocalMusic(true);
                list.add(DbUtil.dbMusicEntiyToMusicBean(entiy));
            }
        }
        return list;
    }

    /**
     * 将音乐插入自己维护的数据库中
     */
    public static void insertMusicData(List<MusicBean> list) {
        List<DbMusicEntiy> entiyList = new ArrayList<>();
        for (MusicBean bean : list) {
            bean.setMusicName(StringUtil.dealMusicName(bean));
            entiyList.add(DbUtil.musicBeanToDbMusicEntiy(bean));
        }
        DbUtil.getMusicDao().insertOrReplaceInTx(entiyList);
    }

    public static List<DbMusicEntiy> queryBlurData(@Nullable String musicName) {
        return DbUtil.getMusicDao()
                .queryBuilder()
                .where(DbMusicEntiyDao.Properties.MusicName.like(musicName))
                .orderAsc(DbMusicEntiyDao.Properties.MusicName)
                .list();
    }

    public static List<AlbumBean> getLocalAllAlbum() {
        List<DbMusicEntiy> musicBeanList = DbUtil.getMusicDao()
                .queryBuilder()
                .where(DbMusicEntiyDao.Properties.AlbumName.isNotNull(),
                        DbMusicEntiyDao.Properties.IsLocalMusic.eq(true),
                        DbMusicEntiyDao.Properties.CoverImgUrl.isNotNull())
                .orderAsc(DbMusicEntiyDao.Properties.AlbumName)
                .list();
        Set<AlbumBean> beanSet = new LinkedHashSet<>();//去重
        for (DbMusicEntiy entiy : musicBeanList) {
            if (StringUtil.isNotEmpty(entiy.getCoverImgUrl())){
                AlbumBean bean = new AlbumBean();
                bean.setLocalAlbum(true);
                bean.setAlbumName(entiy.getAlbumName());
                bean.setAlbumImgUrl(entiy.getCoverImgUrl());
                ArtistBean artistBean = new ArtistBean();
                artistBean.setArtistName(entiy.getArtistName());
                bean.setArtistBean(artistBean);
                beanSet.add(bean);
            }
        }
        return new ArrayList<>(beanSet);
    }

    public static List<ArtistBean> getLocalAllArtist() {
        Set<ArtistBean> set = new LinkedHashSet<>();
        List<DbMusicEntiy> musicBeanList = DbUtil.getMusicDao()
                .queryBuilder()
                .where(DbMusicEntiyDao.Properties.ArtistName.isNotNull(),
                        DbMusicEntiyDao.Properties.IsLocalMusic.eq(true))
                .orderAsc(DbMusicEntiyDao.Properties.ArtistName)
                .list();
        for (DbMusicEntiy entiy : musicBeanList) {
            String artistUrl = entiy.getArtistImgUrl();
            if (TextUtils.isEmpty(artistUrl)) {
                artistUrl = entiy.getCoverImgUrl();
            }
            if (!StringUtil.isEmpty(artistUrl)) {
                ArtistBean bean = new ArtistBean();
                bean.setLocalArtist(true);
                bean.setArtistName(entiy.getArtistName() == null ? "未知" : entiy.getArtistName());
                bean.setArtistImgUrl(artistUrl);
                set.add(bean);
            }
        }
        return new ArrayList<>(set);
    }


}
