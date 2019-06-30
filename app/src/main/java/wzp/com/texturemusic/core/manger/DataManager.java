package wzp.com.texturemusic.core.manger;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.api.ArtistApiManager;
import wzp.com.texturemusic.api.DJApiManager;
import wzp.com.texturemusic.api.WYApiForDJ;
import wzp.com.texturemusic.api.WYApiForSong;
import wzp.com.texturemusic.api.WYApiUtil;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.KeyValueBean;
import wzp.com.texturemusic.bean.LyricBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.config.AppFileConstant;
import wzp.com.texturemusic.core.eventlistener.DataCacheListener;
import wzp.com.texturemusic.dbmodule.DbUtil;
import wzp.com.texturemusic.dbmodule.bean.DbCacheEntiy;
import wzp.com.texturemusic.dbmodule.util.DbCacheUtil;
import wzp.com.texturemusic.dbmodule.util.DbHistoryUtil;
import wzp.com.texturemusic.util.FileUtil;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.LogUtil;
import wzp.com.texturemusic.util.SPSetingUtil;
import wzp.com.texturemusic.util.StringUtil;

/**
 * Created by Wang on 2017/5/27.
 * 全局数据管理者
 * 负责对外提供数据
 * 全局单例
 */
public class DataManager {
    private static final String TAG = "DataManager";
    private static final String PLAY_URL_ERROR = "96000Error";
    private static final String MAP_SP_COVER = "map_sp_cover";
    private static final String MAP_WILL_DATA = "map_will_data";
    private static final String MAP_KEY_LAST_DATA = "map_last_data";
    //保留最大的上一曲数据
    private static final int MAX_LAST_DATA_NUMBER = 20;
    private int currentMusicIndex;//用于存放当前播放的音乐在队列中的位置
    private MusicBean currentMusic = null;//表示当前播放的音乐(其可能为播放队列里面的也可能为其他单独设置的)
    private List<MusicBean> playQueue;//音乐播放队列
    private List<Integer> randomList;//随机队列
    private List<MusicBean> nextPlayQueue;//下一曲的队列
    private List<MusicBean> lastPlayQueue;//上一曲的数据队列
    private int playMode;//播放的模式
    private volatile static DataManager mDataManager;
    private CacheManager cacheManager;
    private DataCacheListener mDataCallback;
    private Disposable disposable;

    /**
     * 构造方法
     */
    private DataManager() {
        cacheManager = CacheManager.getInstance();
        playMode = SPSetingUtil.getIntValue(AppConstant.SP_KEY_PLAY_MODE, AppConstant.PLAY_MODE_LOOP);
        playQueue = new ArrayList<>();
        randomList = new ArrayList<>();
        nextPlayQueue = new ArrayList<>();
        lastPlayQueue = new ArrayList<>();
        //恢复数据
        disposable = Observable.create(new ObservableOnSubscribe<Map<String, List<MusicBean>>>() {
            @Override
            public void subscribe(ObservableEmitter<Map<String, List<MusicBean>>> e) throws Exception {
                MusicBean bean = SPSetingUtil.getSaveMusicData();
                List<MusicBean> beanList = new ArrayList<>();
                beanList.add(bean);
                Map<String, List<MusicBean>> map = new HashMap<>();
                map.put(MAP_WILL_DATA, DbUtil.getSaveWillPlayMusicList());
                map.put(MAP_SP_COVER, beanList);
                map.put(MAP_KEY_LAST_DATA, DbHistoryUtil.getAllHistoryDataForManager(MAX_LAST_DATA_NUMBER));
                e.onNext(map);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Map<String, List<MusicBean>>>() {
                    @Override
                    public void accept(Map<String, List<MusicBean>> stringListMap) throws Exception {
                        lastPlayQueue.addAll(stringListMap.get(MAP_KEY_LAST_DATA));
                        List<MusicBean> list = stringListMap.get(MAP_WILL_DATA);
                        for (MusicBean bean : list) {
                            Boolean isNext = bean.getNextPlay();
                            if (isNext != null && isNext) {
                                nextPlayQueue.add(bean);
                            } else {
                                playQueue.add(bean);
                            }
                        }
                        currentMusic = stringListMap.get(MAP_SP_COVER).get(0);
                        if (mDataCallback != null) {
                            mDataCallback.onDataManagerIsCreate(currentMusic);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mDataCallback != null) {
                            mDataCallback.onDataManagerIsCreate(null);
                        }
                    }
                });
    }

    public static DataManager getInstance(DataCacheListener callback) {
        if (mDataManager == null) {
            synchronized (DataManager.class) {
                if (mDataManager == null) {
                    mDataManager = new DataManager();
                    mDataManager.setDataCallback(callback);
                }
            }
        }
        return mDataManager;
    }

    private void setDataCallback(DataCacheListener mDataCallback) {
        this.mDataCallback = mDataCallback;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }

    public List<MusicBean> getPlayQueue(boolean includeNextQueue) {
        List<MusicBean> list = new ArrayList<>();
        if (playQueue != null) {
            list.addAll(playQueue);
        }
        if (includeNextQueue&&nextPlayQueue!=null){
            list.addAll(nextPlayQueue);
        }
        return list;
    }

    private List<MusicBean> getNextPlayQueue() {
        return nextPlayQueue;
    }

    public void addPlayQueue(List<MusicBean> dataList) {
        if (playQueue == null) {
            playQueue = new ArrayList<>();
        }
        playQueue.addAll(dataList);
        randomList.clear();
        for (int i = 0; i < playQueue.size(); i++) {
            randomList.add(i);
        }
    }

    public void setCurrentPlayMusic(MusicBean bean) {
        this.currentMusic = bean;
    }

    public void saveMusicToHistoryDbSync(MusicBean bean) {
        DbHistoryUtil.insertMusicHistory(bean);
    }

    /**
     * 获取一切情况下的
     * 音乐实体
     * 当 信息为空 返回null
     * 当当前正在播放时 返回当前正在播放的
     * 若 当前播放状态不是正在播放，查找sp文件
     */
    public MusicBean getPlayMusic(boolean onlyCurrentMusic) {
        if (onlyCurrentMusic) {
            return currentMusic;
        } else {
            if (currentMusic != null) {
                return currentMusic;
            } else {
                if (nextPlayQueue != null && nextPlayQueue.size() > 0) {
                    MusicBean bean = nextPlayQueue.get(nextPlayQueue.size() - 1);
                    nextPlayQueue.remove(nextPlayQueue.size() - 1);
                    return bean;
                } else {
                    if (playQueue != null && playQueue.size() > 0) {
                        return playQueue.get(0);
                    } else {
                        return SPSetingUtil.getSaveMusicData();
                    }
                }
            }
        }
    }

    /**
     * 获得上一曲播放的数据
     */
    public MusicBean getLastMusic() {
        if (lastPlayQueue.isEmpty()) {
            return null;
        } else {
            MusicBean bean = lastPlayQueue.get(0);
            lastPlayQueue.remove(0);
            return bean;
        }
    }

    public synchronized void addLastMusic(MusicBean bean) {
        if (lastPlayQueue == null) {
            lastPlayQueue = new ArrayList<>();
        }
        if (lastPlayQueue.size() >= MAX_LAST_DATA_NUMBER) {
            lastPlayQueue.remove(0);
        }
        lastPlayQueue.add(bean);
    }

    public MusicBean getNextMusic() {
        MusicBean bean;
        if (nextPlayQueue != null && nextPlayQueue.size() > 0) {
            int index = nextPlayQueue.size() - 1;
            bean = nextPlayQueue.get(index);
            nextPlayQueue.remove(index);
        } else {
            if (playMode == AppConstant.PLAY_MODE_SINGLE) {
                bean = currentMusic;
            } else {
                int index = produceNextMusicIndex();
                currentMusicIndex = index;
                if (playQueue == null || index < 0 || index > playQueue.size() || playQueue.size() == 0) {
                    bean = null;
                } else {
                    bean = playQueue.get(index);
                }
            }
        }
        return bean;
    }

    /**
     * 产生下一曲的索引
     * 结果受到播放模式的影响
     * 非单曲循环才有效
     */
    private int produceNextMusicIndex() {
        int index = currentMusicIndex;
        switch (playMode) {
            case AppConstant.PLAY_MODE_LOOP:
                index++;
                if (index >= playQueue.size() || index < 0) {
                    index = 0;
                }
                break;
            case AppConstant.PLAY_MODE_RANDOM:
                if (playQueue.size() > 0) {
                    if (randomList.size() == 0) {
                        for (int i = 0; i < playQueue.size(); i++) {
                            randomList.add(i);
                        }
                    }
                    index = (int) (Math.random() * randomList.size());
                    randomList.remove(index);
                } else {
                    index = -1;
                }
                break;
        }
        return index;
    }

    /**
     * 移除播放队列里面的某首歌
     */
    public void removeMusicData(int index) {
        if (playQueue != null && playQueue.size() > 0 && index >= 0 && index < playQueue.size()) {
            playQueue.remove(index);
        }
    }

    public void removeMusicData(MusicBean bean) {
        if (bean == null) {
            return;
        }
        if (playQueue != null && !playQueue.isEmpty()) {
            playQueue.remove(bean);
        }
    }

    public void removeMusicData(MusicBean bean, boolean includeNext) {
        removeMusicData(bean);
        if (includeNext) {
            if (nextPlayQueue != null && nextPlayQueue.size() > 0) {
                nextPlayQueue.remove(bean);
            }
        }
    }

    public void clearPlayQueueData() {
        if (playQueue != null) {
            playQueue.clear();
        }
    }

    public void clearNextQueueData() {
        if (nextPlayQueue != null) {
            nextPlayQueue.clear();
        }
    }

    /**
     * 获取指定位置的音乐信息可能为空
     */
    public MusicBean getMusicAtQueue(int index) {
        if (playQueue == null || index < 0 || index > playQueue.size() || playQueue.size() == 0) {
            return null;
        } else {
            currentMusicIndex = index;
            return playQueue.get(index);
        }
    }

    public void addNextMusic(List<MusicBean> list) {
        if (list != null && !list.isEmpty()) {
            if (nextPlayQueue == null) {
                nextPlayQueue = new ArrayList<>();
            }
            nextPlayQueue.addAll(list);
        }
    }

    public void release() {
        List<MusicBean> beanList = getAllPlayDataQueue();
        DbUtil.saveWillPlayMusicList(beanList);
        playQueue.clear();
        nextPlayQueue.clear();
        randomList.clear();
        if (cacheManager != null) {
            cacheManager.releaseSouce();
            cacheManager = null;
        }
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    /**
     * 获取DataManager里面的所有数据
     */
    private List<MusicBean> getAllPlayDataQueue() {
        List<MusicBean> list = new ArrayList<>();
        for (MusicBean bean : mDataManager.getPlayQueue(false)) {
            bean.setNextPlay(false);
            list.add(bean);
        }
        for (MusicBean bean : mDataManager.getNextPlayQueue()) {
            bean.setNextPlay(true);
            list.add(bean);
        }
        return list;
    }

    public void stopCurrentCacheService() {
        if (cacheManager != null) {
            cacheManager.stopCacheCurrentData();
        }
    }
//////////////////////////////网络方法////////////////////////////////////

    /**
     * 获取网络音乐信息
     * 但不包括播放地址
     * 播放地址在ControlManager 中通过调用getProxyPlaypath()获得
     */
    public Observable<MusicBean> getNetMusicAllInfo(MusicBean bean) {
        if (bean.getMusicOrigin() != null && bean.getMusicOrigin() == AppConstant.MUSIC_ORIGIN_DJ) {
            return getDjNetMusicAllInfo(bean);
        } else {
            return getMormalNetMusicAllInfo(bean);
        }
    }

    /**
     * 获取当歌曲来源是普通音乐时的所有信息
     * 返回内容中没有播放地址
     */
    private Observable<MusicBean> getMormalNetMusicAllInfo(final MusicBean bean) {
        bean.setLocalMusic(false);
        WYApiForSong forSong = WYApiUtil.getInstance().buildSongService();
        Observable<MusicBean> observable;
        //其中获取艺术家详情会耗费很多时间
        observable = Observable.zip(
                forSong.getSongDetatil(bean.getMusicId(), "[" + bean.getMusicId() + "]"),
                getLyrics(bean.getMusicId()),
                ArtistApiManager.getArtistsHotSong(bean.getArtistId()),
                (s2, lyricBean, s3) -> {
                    MusicBean musicBean = jsonToDetail(s2);
                    ArtistBean artistBean = jsonToArtist(s3);
                    if (musicBean != null) {
                        musicBean.setLyricBean(lyricBean);
                        musicBean.setCommentCount(bean.getCommentCount());
                        musicBean.setMusicFileSize(bean.getMusicFileSize());
                        musicBean.setMusicType(bean.getMusicType());
                        musicBean.setDbId(bean.getDbId());
                        musicBean.setLocalArtist(bean.getLocalArtist());
                        if (artistBean != null) {
                            musicBean.setArtistImgUrl(artistBean.getArtistImgUrl());
                            musicBean.setArtistName(artistBean.getArtistName());
                            musicBean.setArtistId(artistBean.getArtistId());
                        } else {
                            musicBean.setArtistImgUrl(bean.getArtistImgUrl());
                            musicBean.setArtistName(bean.getArtistName());
                            musicBean.setArtistId(bean.getArtistId());
                        }
                        musicBean.setLocalMusic(false);
                        return musicBean;
                    } else {
                        bean.setLyricBean(lyricBean);
                        if (artistBean != null) {
                            bean.setArtistImgUrl(artistBean.getArtistImgUrl());
                            bean.setArtistName(artistBean.getArtistName());
                            bean.setArtistId(artistBean.getArtistId());
                        }
                        return bean;
                    }
                });
        return observable;
    }

    /**
     * 获取当歌曲来源是电台节目时的所有信息
     * 返回内容中没有播放地址
     */
    private Observable<MusicBean> getDjNetMusicAllInfo(final MusicBean bean) {
        WYApiForDJ apiForDJ = WYApiUtil.getInstance().buildDjService();
        Observable<MusicBean> observable;
        observable = Observable.zip(
                DJApiManager.getProgramDetail(bean.getDjProgramId()),
                getLyrics(bean.getMusicId()),
                (s2, lyricBean) -> {
                    MusicBean musicBean = jsonDjToDetail(s2);
                    if (musicBean != null) {
                        musicBean.setLyricBean(lyricBean);
                        musicBean.setMusicBitrate(bean.getMusicBitrate());
                        musicBean.setMusicFileSize(bean.getMusicFileSize());
                        musicBean.setMusicType(bean.getMusicType());
                        musicBean.setDownloadUrl(bean.getDownloadUrl());
                        musicBean.setNextPlay(bean.getNextPlay());
                        musicBean.setDbId(bean.getDbId());
                        musicBean.setHasCheck(bean.getHasCheck());
                        musicBean.setMusicOrigin(AppConstant.MUSIC_ORIGIN_DJ);
                        return musicBean;
                    } else {
                        bean.setMusicOrigin(AppConstant.MUSIC_ORIGIN_NORMAL);
                        bean.setLyricBean(lyricBean);
                        return bean;
                    }
                });
        return observable;
    }

    private Observable<LyricBean> getLyrics(final String musicId) {
        File file = new File(AppFileConstant.DOWNLOAD_LYRICS_DRESS + musicId);
        if (file.exists() && file.isFile()) {
            String data = FileUtil.fileToString(file);
            final LyricBean lyricBean = JSON.parseObject(data, LyricBean.class);
            return Observable.create(e -> {
                if (lyricBean != null) {
                    e.onNext(lyricBean);
                } else {
                    e.onNext(new LyricBean());
                }
            });
        } else {
            //没有下载歌词
            return WYApiUtil.getInstance().buildSongService()
                    .getSongLyric(musicId)
                    .map(s -> {
                        LyricBean bean = null;
                        try {
                            bean = jsonToLyrics(s);
                        } catch (Exception e) {
                            bean = new LyricBean();
                        }
                        if (bean != null && bean.getList() != null && !bean.getList().isEmpty()) {
                            File fi = new File(AppFileConstant.DOWNLOAD_LYRICS_DRESS);
                            if (!fi.exists()) {
                                fi.mkdirs();//创建文件夹
                            }
                            FileUtil.stringToFile(JSON.toJSONString(bean), AppFileConstant.DOWNLOAD_LYRICS_DRESS + musicId, true);
                        }
                        return bean;
                    });
        }
    }

    /**
     * 获取当没有网络时的歌词
     */
    public LyricBean getMusicLyricsForNotNet(MusicBean bean) {
        if (bean == null) {
            return null;
        }
        File file = new File(AppFileConstant.DOWNLOAD_LYRICS_DRESS + bean.getMusicId());
        if (file.exists() && file.isFile()) {
            String data = FileUtil.fileToString(file);
            return JSON.parseObject(data, LyricBean.class);
        }
        return null;
    }

    /**
     * 尽可能的获取播放的地址
     * 直到全部都获取完了
     *
     * @return String[] 永远返回长度为2的字符串数组 string[0]为真实地址String[1]为对应的代理地址
     */
    public Observable<String[]> getPlayProxyPath(final MusicBean bean) {
        return Observable.concatArray(
                getMusicUrlForBean(bean),
                getMusicUrlFor999000(bean),
                getMusicUrlFor320000(bean),
                getMusicUrlFor160000(bean),
                getMusicUrlFor96000(bean)
        ).filter(s -> !StringUtil.isEmpty(s)).take(1)
                .map(realNetUrl -> {
                    String[] result = new String[2];
                    result[0] = realNetUrl;
                    if (StringUtil.isEmpty(realNetUrl) || realNetUrl.equals(PLAY_URL_ERROR)) {
                        result[1] = "";
                        return result;
                    }
                    //缓存校验
                    //s为音乐的网络地址
                    //cacheUrl为地址解析后的数值
                    String cacheUrl = CacheManager.getCacheKey(realNetUrl);
                    String filePath = AppFileConstant.MEDIA_CACHE_DRESS + cacheUrl;
                    File file = new File(filePath);
                    boolean exists = file.exists() && file.isFile();
                    if (exists) {
                        //该文件已经被缓存了 直接返回文件地址
                        result[1] = file.getAbsolutePath();
                    } else {
                        //文件没有被缓存
                        if (cacheManager == null) {
                            cacheManager = CacheManager.getInstance();
                        }
                        //下面这个不能缺少
                        //是为了在 CacheManager中通过网络地址获得缓存地址
                        bean.setPlayPath(realNetUrl);
                        result[1] = cacheManager.getCacheUrl(bean, mDataCallback);
                    }
                    return result;
                });
    }

    private Observable<String> getMusicUrlFor96000(MusicBean bean) {
        String musicUrl = AppConstant.WY_BASE_URL + "song/enhance/player/url?ids=[" + bean.getMusicId() + "]" + "&br=96000";
        WYApiForSong forSong = WYApiUtil.getInstance().buildSongService();
        return forSong.getMusicUrl(musicUrl)
                .map(s -> {
                    String url = jsonToPlayUrl(s);
                    if (StringUtil.isEmpty(url)) {
                        return PLAY_URL_ERROR;
                    } else {
                        return url;
                    }
                });
    }

    private Observable<String> getMusicUrlFor160000(MusicBean bean) {
        String musicUrl = AppConstant.WY_BASE_URL + "song/enhance/player/url?ids=[" + bean.getMusicId() + "]" + "&br=160000";
        WYApiForSong forSong = WYApiUtil.getInstance().buildSongService();
        return forSong.getMusicUrl(musicUrl)
                .map(s -> {
                    String url = jsonToPlayUrl(s);
                    if (StringUtil.isEmpty(url)) {
                        return "";
                    } else {
                        return url;
                    }
                });
    }

    private Observable<String> getMusicUrlFor320000(MusicBean bean) {
        String musicUrl = AppConstant.WY_BASE_URL + "song/enhance/player/url?ids=[" + bean.getMusicId() + "]" + "&br=320000";
        WYApiForSong forSong = WYApiUtil.getInstance().buildSongService();
        return forSong.getMusicUrl(musicUrl)
                .map(s -> {
                    String url = jsonToPlayUrl(s);
                    if (StringUtil.isEmpty(url)) {
                        return "";
                    } else {
                        return url;
                    }
                });
    }

    private Observable<String> getMusicUrlFor999000(MusicBean bean) {
        String musicUrl = AppConstant.WY_BASE_URL + "song/enhance/player/url?ids=[" + bean.getMusicId() + "]" + "&br=999000";
        WYApiForSong forSong = WYApiUtil.getInstance().buildSongService();
        return forSong.getMusicUrl(musicUrl)
                .map(s -> {
                    String url = jsonToPlayUrl(s);
                    if (StringUtil.isEmpty(url)) {
                        return "";
                    } else {
                        return url;
                    }
                });
    }

    private Observable<String> getMusicUrlForBean(final MusicBean bean) {
        DbCacheEntiy entiy = DbCacheUtil.queryCacheData(bean);
        if (entiy != null) {
            final MusicBean musicBean = DbCacheUtil.cacheEntiyToMusic(entiy);
            if (musicBean != null && StringUtil.isNotEmpty(musicBean.getPlayPath())) {
                return Observable.create(e -> e.onNext(musicBean.getPlayPath()));
            }
        }
        int bit = SPSetingUtil.getIntValue(AppConstant.SP_KEY_PLAY_QUALITY, AppConstant.MUSIC_BITRATE_HIGHT);
        String musicUrl = AppConstant.WY_BASE_URL + "song/enhance/player/url?ids=[" + bean.getMusicId() + "]" + "&br=" + bit;
        WYApiForSong forSong = WYApiUtil.getInstance().buildSongService();
        return forSong.getMusicUrl(musicUrl)
                .map(s -> {
                    String url = jsonToPlayUrl(s);
                    if (StringUtil.isEmpty(url)) {
                        return "";
                    } else {
                        return url;
                    }
                });
    }

    private String jsonToPlayUrl(String json) {
        String url = "";
        JSONObject object = JSON.parseObject(json);
        if (object.getIntValue("code") == 200) {
            JSONArray data = object.getJSONArray("data");
            if (data != null && data.size() > 0) {
                url = data.getJSONObject(0).getString("url");
            }
        }
        return url;
    }

    private MusicBean jsonToDetail(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        if (jsonObject.getIntValue("code") == 200) {
            MusicBean musicBean = new MusicBean();
            JSONArray array = jsonObject.getJSONArray("songs");
            JSONObject dataObject;
            if (array.size() > 0) {
                dataObject = array.getJSONObject(0);
                musicBean.setMusicName(dataObject.getString("name"));
                String id = String.valueOf(dataObject.getLongValue("id"));
                musicBean.setMusicId(id);
                musicBean.setSubCommentId(id);
                musicBean.setCommentId(dataObject.getString("commentThreadId"));
                musicBean.setAllTime(dataObject.getLongValue("duration"));
                musicBean.setMusicOrigin(AppConstant.MUSIC_ORIGIN_NORMAL);
                JSONArray alias = dataObject.getJSONArray("alias");
                if (alias != null && alias.size() > 0) {
                    StringBuilder buffer = new StringBuilder();
                    for (int i = 0; i < alias.size(); i++) {
                        buffer.append(alias.getString(i));
                    }
                    musicBean.setAlias(buffer.toString());
                } else {
                    musicBean.setAlias("");
                }
                long mvid = dataObject.getLongValue("mvid");
                if (mvid == 0L) {
                    musicBean.setHasMV(false);
                } else {
                    musicBean.setHasMV(true);
                    MvBean mvBean = new MvBean();
                    mvBean.setLocalMv(false);
                    mvBean.setMvId(mvid);
                    musicBean.setMvBean(mvBean);
                }
                JSONObject albumObject = dataObject.getJSONObject("album");
                if (albumObject != null) {
                    musicBean.setAlbumName(albumObject.getString("name"));
                    musicBean.setLocalAlbum(false);
                    musicBean.setAlbumId(String.valueOf(albumObject.getLongValue("id")));
                    musicBean.setCoverImgUrl(albumObject.getString("picUrl"));
                    musicBean.setAlbumImgUrl(albumObject.getString("picUrl"));
                } else {
                    musicBean.setAlbumName("");
                    musicBean.setAlbumId("0");
                    musicBean.setCoverImgUrl("");
                    musicBean.setAlbumImgUrl("");
                }
                JSONArray artistObject = dataObject.getJSONArray("artists");
                if (artistObject != null && artistObject.size() > 0) {
                    musicBean.setArtistName(artistObject.getJSONObject(0).getString("name"));
                    musicBean.setArtistId(String.valueOf(artistObject.getJSONObject(0).getLongValue("id")));
                } else {
                    musicBean.setArtistName("");
                    musicBean.setArtistId("0");
                }
                musicBean.setLocalMusic(false);
                return musicBean;
            }
        }
        return null;
    }

    private MusicBean jsonDjToDetail(String json) {
        JSONObject rootObject = JSONObject.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            JSONObject jsonObject = rootObject.getJSONObject("program");
            if (jsonObject != null) {
                MusicBean musicBean = new MusicBean();
                musicBean.setMusicId(String.valueOf(jsonObject.getLongValue("mainTrackId")));
                musicBean.setSubCommentId(String.valueOf(jsonObject.getLongValue("id")));
                musicBean.setDjProgramId(jsonObject.getLongValue("id"));
                musicBean.setMusicName(jsonObject.getString("name"));
                musicBean.setAllTime(jsonObject.getLongValue("duration"));
                musicBean.setCoverImgUrl(jsonObject.getString("coverUrl"));
                musicBean.setMusicOrigin(AppConstant.MUSIC_ORIGIN_DJ);
                musicBean.setLocalMusic(false);
                musicBean.setLocalArtist(false);
                musicBean.setLocalAlbum(false);
                musicBean.setCommentId(jsonObject.getString("commentThreadId"));
                musicBean.setCommentCount(jsonObject.getIntValue("commentCount"));
                musicBean.setHasMV(false);
                JSONObject artistObject = jsonObject.getJSONObject("dj");
                if (artistObject != null) {
                    musicBean.setArtistName(artistObject.getString("nickname"));
                    musicBean.setArtistId(String.valueOf(artistObject.getLongValue("userId")));
                    musicBean.setArtistImgUrl(artistObject.getString("avatarUrl"));
                } else {
                    musicBean.setArtistName("");
                    musicBean.setArtistId(null);
                    musicBean.setArtistImgUrl("");
                }
                JSONObject albumObject = jsonObject.getJSONObject("radio");
                if (albumObject != null) {
                    musicBean.setAlbumId(String.valueOf(albumObject.getLongValue("id")));
                    musicBean.setAlbumName(albumObject.getString("name"));
                    musicBean.setAlbumImgUrl(albumObject.getString("picUrl"));
                } else {
                    musicBean.setAlbumId(null);
                    musicBean.setAlbumName("");
                    musicBean.setAlbumImgUrl("");
                }
                return musicBean;
            }
        }
        return null;
    }

    private LyricBean jsonToLyrics(String json) {
        LyricBean bean = new LyricBean();
        JSONObject jsonObject = JSONObject.parseObject(json);
        if (jsonObject.getIntValue("code") == 200) {
            JSONObject lrcObjecct = jsonObject.getJSONObject("lrc");
            if (lrcObjecct != null) {
                String lyrics = lrcObjecct.getString("lyric");
                if (!TextUtils.isEmpty(lyrics)) {
                    bean.setList(analysiLyricNew(lyrics));
                }
            }
        }
        return bean;
    }

    private List<KeyValueBean> analysiLyricNew(String str) {
        String reg = "\\[(\\d{1,4}:\\d{1,4}\\.\\d{1,4})\\]";
        List<KeyValueBean> valueBeanList = new ArrayList<>();
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        String[] content = pattern.split(str);
        int i = 0;
        while (matcher.find()) {
            String time = matcher.group();
            int currentTime = FormatData.lyricsTimeToString(time.substring(1, time.length()));
            //下面这个if是为了判断歌词是否正确
            if (currentTime >= 0 && i < content.length) {
                KeyValueBean bean = new KeyValueBean();
                bean.setIntVal(currentTime);
                bean.setValue(content[i + 1]);
                valueBeanList.add(bean);
            } else {
                valueBeanList.clear();
                break;
            }
            i++;
        }
        return valueBeanList;
    }

    private ArtistBean jsonToArtist(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        if (jsonObject.getIntValue("code") == 200) {
            JSONObject artistObject = jsonObject.getJSONObject("artist");
            if (artistObject != null) {
                ArtistBean bean = new ArtistBean();
                bean.setArtistName(artistObject.getString("name"));
                bean.setArtistId(String.valueOf(artistObject.getLongValue("id")));
                bean.setArtistImgUrl(artistObject.getString("picUrl"));
                bean.setLocalArtist(false);
                return bean;
            }
        }
        return null;
    }


}