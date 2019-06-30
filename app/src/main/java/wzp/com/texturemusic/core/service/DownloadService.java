package wzp.com.texturemusic.core.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.api.MvApiManager;
import wzp.com.texturemusic.api.WYApiForSong;
import wzp.com.texturemusic.api.WYApiUtil;
import wzp.com.texturemusic.bean.DownloadBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.config.AppFileConstant;
import wzp.com.texturemusic.dbmodule.DbUtil;
import wzp.com.texturemusic.dbmodule.util.DbDownloadUtil;
import wzp.com.texturemusic.dbmodule.util.DbMusicUtil;
import wzp.com.texturemusic.downloadmodule.AbstractDownloadListener;
import wzp.com.texturemusic.util.FileUtil;
import wzp.com.texturemusic.util.LogUtil;
import wzp.com.texturemusic.util.MusicUtil;
import wzp.com.texturemusic.util.SPSetingUtil;
import wzp.com.texturemusic.util.StringUtil;

/**
 * Created by Go_oG
 * Description:用于下载音乐
 * on 2017/11/20.
 */
public class DownloadService extends Service {
    private static final String PLAY_URL_ERROR = "96000Error";
    private static final String TAG = "DownloadService";
    //用于实现多线程访问时的控制
    private final Object object = new Object();
    private Disposable disposable;//定时器
    private Context mContext;
    private final MyFileListener fileListener = new MyFileListener();
    private volatile DownloadBean currentDownloadBean;
    private volatile DownloadBinder mBinder;
    private volatile List<DownloadBean> downloadQueue;
    private volatile BaseDownloadTask downloadTask;
    private boolean isDownloading = false;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        FileDownloader.setup(this);
        downloadQueue = new ArrayList<>();
        File file = new File(AppFileConstant.FILE_DRESS + "downloadQueue.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String json = FileUtil.fileToString(file);
            if (StringUtil.isNotEmpty(json)) {
                List<DownloadBean> list = JSONObject.parseArray(json, DownloadBean.class);
                if (list != null) {
                    downloadQueue.addAll(list);
                }
            }
        }
        initTimeThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getExtras() != null) {
            List<DownloadBean> beanList = intent.getExtras().getParcelableArrayList(AppConstant.DOWNLOAD_QUEUE);
            if (beanList != null) {
                downloadQueue.addAll(beanList);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        String filePath = AppFileConstant.FILE_DRESS + "downloadQueue.txt";
        if (downloadQueue != null && !downloadQueue.isEmpty()) {
            String json = JSONArray.toJSONString(downloadQueue);
            FileUtil.stringToFile(json, filePath, true);
        } else {
            FileUtil.stringToFile("", filePath, true);
        }
        FileDownloader.getImpl().unBindServiceIfIdle();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null) {
            synchronized (DownloadBinder.class) {
                if (mBinder == null) {
                    mBinder = new DownloadBinder();
                }
            }
        }
        return mBinder;
    }

    private String jsonToUrl(String json) {
        LogUtil.d(TAG, "JSON=" + json);
        String url = "";
        JSONObject rootObject = JSONObject.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            JSONObject dataObject = rootObject.getJSONObject("data");
            if (dataObject != null) {
                String path = dataObject.getString("url");
                if (!TextUtils.isEmpty(path)) {
                    url = path;
                }
            }
        }
        return url;
    }

    /**
     * 该类的所有回调都在后台下载线程中，
     * 而不是在UI线程
     */
    private class MyFileListener extends AbstractDownloadListener {
        @Override
        protected void started(BaseDownloadTask task) {
            super.started(task);
            downloadTask = task;
            isDownloading = false;
        }

        @Override
        protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
            downloadTask = task;
            isDownloading = false;
        }

        @Override
        protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
            downloadTask = task;
            isDownloading = true;

        }

        @Override
        protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
            downloadTask = task;
            isDownloading = false;
        }

        @SuppressLint("CheckResult")
        @Override
        protected void completed(final BaseDownloadTask task) {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                    if (currentDownloadBean != null) {
                        Boolean isMv = currentDownloadBean.getMvData();
                        if (isMv != null && isMv && currentDownloadBean.getMvBean() != null) {
                            MvBean mvBean = currentDownloadBean.getMvBean();
                            File file = new File(task.getPath());
                            MediaScannerConnection.scanFile(mContext, new String[]{task.getPath()}, null, null);
                            DbDownloadUtil.addDownloadEntiy(mvBean, file);
                        } else if (isMv != null && !isMv) {
                            MusicBean bean = currentDownloadBean.getMusicBean();
                            File file = new File(task.getPath());
                            MusicUtil.editMusicInfoSync(mContext, bean, task.getPath());
                            DbDownloadUtil.addDownloadEntiy(bean, file);
                        }
                        DbUtil.getMusicDao().deleteAll();
                        List<MusicBean> dataList = MusicUtil.getLocalMusicData(mContext);
                        DbMusicUtil.insertMusicData(dataList);
                    }
                    e.onNext(true);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            synchronized (object) {
                                downloadTask = null;
                                isDownloading = false;
                                currentDownloadBean = null;
                            }
                        }
                    });
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            synchronized (object) {
                downloadTask = null;
                isDownloading = false;
                currentDownloadBean = null;
            }
        }
    }

    @SuppressLint("CheckResult")
    private void startDownload(final DownloadBean downlaodEntiy) {
        currentDownloadBean = downlaodEntiy;
        int bit = SPSetingUtil.getIntValue(AppConstant.SP_KEY_DOWNLOAD_QUALITY, AppConstant.MUSIC_BITRATE_HIGHT);
        if (downlaodEntiy.getMvData() && downlaodEntiy.getMvBean() != null) {
            int res = 240;
            if (bit == AppConstant.MUSIC_BITRATE_NORMAL) {
                res = 480;
            } else if (bit == AppConstant.MUSIC_BITRATE_HIGHT) {
                res = 720;
            } else if (bit == AppConstant.MUSIC_BITRATE_SUPER_HIGHT) {
                res = 1080;
            }
            long mvId = downlaodEntiy.getMvBean().getMvId();
            Observable.concatArray(getDownloadUrlForMV(mvId, res),
                    getMvPlayPath(mvId, res))
                    .filter(s -> StringUtil.isNotEmpty(s)).take(1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(url -> {
                        if ((!TextUtils.isEmpty(url)) && (!url.equals(PLAY_URL_ERROR))) {
                            String path = AppFileConstant.DOWNLOAD_MV_DRESS + downlaodEntiy.getMvBean().getMvName() + ".mp4";
                            MvBean mvBean = downlaodEntiy.getMvBean();
                            currentDownloadBean = new DownloadBean();
                            currentDownloadBean.setComplete(false);
                            currentDownloadBean.setMvData(true);
                            currentDownloadBean.setFileName(mvBean.getMvName());
                            currentDownloadBean.setFilePath(path);
                            currentDownloadBean.setMvBean(mvBean);
                            FileDownloader.getImpl().create(url)
                                    .setPath(path, false)
                                    .setCallbackProgressMinInterval(500)
                                    .setAutoRetryTimes(1)
                                    .setSyncCallback(true)
                                    .setListener(fileListener)
                                    .start();
                        } else {
                            currentDownloadBean = null;
                            downloadTask = null;
                            isDownloading = false;
                        }
                        downloadQueue.remove(downlaodEntiy);
                    }, throwable -> {
                        downloadQueue.remove(downlaodEntiy);
                        synchronized (object) {
                            downloadTask = null;
                            currentDownloadBean = null;
                            isDownloading = false;
                        }
                    });
        } else if ((!downlaodEntiy.getMvData()) && downlaodEntiy.getMusicBean() != null) {
            String musicID = downlaodEntiy.getMusicBean().getMusicId();
            Observable.concatArray(getDownloadUrlForMusic(musicID, bit),
                    getMusicPlayPath(musicID, bit))
                    .filter(new Predicate<String>() {
                        @Override
                        public boolean test(String s) throws Exception {
                            return StringUtil.isNotEmpty(s);
                        }
                    }).take(1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String url) throws Exception {
                            if ((!TextUtils.isEmpty(url)) && (!url.equals(PLAY_URL_ERROR))) {
                                String path = AppFileConstant.DOWNLOAD_MUSIC_DRESS + downlaodEntiy.getMusicBean().getMusicName() + ".mp3";
                                MusicBean musicBean = downlaodEntiy.getMusicBean();
                                currentDownloadBean = new DownloadBean();
                                currentDownloadBean.setComplete(false);
                                currentDownloadBean.setMvData(false);
                                currentDownloadBean.setFileName(musicBean.getMusicName());
                                currentDownloadBean.setFilePath(path);
                                currentDownloadBean.setMusicBean(musicBean);
                                FileDownloader.getImpl().create(url)
                                        .setPath(path, false)
                                        .setCallbackProgressMinInterval(1000)
                                        .setAutoRetryTimes(1)
                                        .setListener(fileListener)
                                        .start();
                            } else {
                                currentDownloadBean = null;
                                downloadTask = null;
                                isDownloading = false;
                            }
                            downloadQueue.remove(downlaodEntiy);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            downloadQueue.remove(downlaodEntiy);
                            synchronized (object) {
                                downloadTask = null;
                                currentDownloadBean=null;
                                isDownloading = false;
                            }
                        }
                    });
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    private Observable<String> getDownloadUrlForMusic(String musicId, int bit) {
        return WYApiUtil.getInstance().buildSongService()
                .getDownloadUrl(musicId, bit)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        return jsonToUrl(s);
                    }
                });
    }

    private Observable<String> getMusicPlayPath(final String musicId, int bit) {
        return Observable.concatArray(
                getMusicUrlForBean(musicId, bit),
                getMusicUrlFor999000(musicId),
                getMusicUrlFor320000(musicId),
                getMusicUrlFor160000(musicId),
                getMusicUrlFor96000(musicId)
        ).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return !StringUtil.isEmpty(s);
            }
        }).take(1);
    }

    private Observable<String> getMusicUrlFor96000(String musicid) {
        String musicUrl = AppConstant.WY_BASE_URL + "song/enhance/player/url?ids=[" + musicid + "]" + "&br=96000";
        WYApiForSong forSong = WYApiUtil.getInstance().buildSongService();
        return forSong.getMusicUrl(musicUrl)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        String url = jsonToPlayUrlForMusic(s);
                        if (StringUtil.isEmpty(url)) {
                            return PLAY_URL_ERROR;
                        } else {
                            return url;
                        }
                    }
                });
    }

    private Observable<String> getMusicUrlFor160000(String musicId) {
        String musicUrl = AppConstant.WY_BASE_URL + "song/enhance/player/url?ids=[" + musicId + "]" + "&br=160000";
        WYApiForSong forSong = WYApiUtil.getInstance().buildSongService();
        return forSong.getMusicUrl(musicUrl)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        String url = jsonToPlayUrlForMusic(s);
                        if (StringUtil.isEmpty(url)) {
                            return "";
                        } else {
                            return url;
                        }
                    }
                });
    }

    private Observable<String> getMusicUrlFor320000(String musicId) {
        String musicUrl = AppConstant.WY_BASE_URL + "song/enhance/player/url?ids=[" + musicId + "]" + "&br=320000";
        WYApiForSong forSong = WYApiUtil.getInstance().buildSongService();
        return forSong.getMusicUrl(musicUrl)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        String url = jsonToPlayUrlForMusic(s);
                        if (StringUtil.isEmpty(url)) {
                            return "";
                        } else {
                            return url;
                        }
                    }
                });
    }

    private Observable<String> getMusicUrlFor999000(String musicId) {
        String musicUrl = AppConstant.WY_BASE_URL + "song/enhance/player/url?ids=[" + musicId + "]" + "&br=999000";
        WYApiForSong forSong = WYApiUtil.getInstance().buildSongService();
        return forSong.getMusicUrl(musicUrl)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        String url = jsonToPlayUrlForMusic(s);
                        if (StringUtil.isEmpty(url)) {
                            return "";
                        } else {
                            return url;
                        }
                    }
                });
    }

    private Observable<String> getMusicUrlForBean(final String musicId, int bit) {
        String musicUrl = AppConstant.WY_BASE_URL + "song/enhance/player/url?ids=[" + musicId + "]" + "&br=" + bit;
        WYApiForSong forSong = WYApiUtil.getInstance().buildSongService();
        return forSong.getMusicUrl(musicUrl)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        String url = jsonToPlayUrlForMusic(s);
                        if (StringUtil.isEmpty(url)) {
                            return "";
                        } else {
                            return url;
                        }
                    }
                });
    }

    private String jsonToPlayUrlForMusic(String json) {
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
///////////////////////////////////////////////////////////////////

    private Observable<String> getDownloadUrlForMV(long mvId, int res) {
        return MvApiManager
                .getMvDownloadUrl(mvId, res)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        return jsonToMvDownloadUrl(s);
                    }
                });
    }

    private Observable<String> getMvPlayPath(long mvID, int res) {
        return Observable.concatArray(
                getMvUrlFor1080(mvID),
                getMvUrlFor720(mvID),
                getMvUrlUrlFor480(mvID),
                getMvUrlFor240(mvID)
        ).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return !StringUtil.isEmpty(s);
            }
        }).take(1);
    }

    private Observable<String> getMvUrlFor240(long mvId) {
        return MvApiManager.getMvPlayUrl(mvId, 240)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        String str = jsonToMvPlayPathUrl(s);
                        if (StringUtil.isEmpty(str)) {
                            return PLAY_URL_ERROR;
                        } else {
                            return str;
                        }
                    }
                });
    }

    private Observable<String> getMvUrlFor720(long mvId) {
        return MvApiManager.getMvPlayUrl(mvId, 720)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return jsonToMvPlayPathUrl(s);
                    }
                });
    }

    private Observable<String> getMvUrlFor1080(long mvId) {
        return MvApiManager.getMvPlayUrl(mvId, 1080)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return jsonToMvPlayPathUrl(s);
                    }
                });
    }

    private Observable<String> getMvUrlUrlFor480(long mvId) {
        return MvApiManager.getMvPlayUrl(mvId, 480)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return jsonToMvPlayPathUrl(s);
                    }
                });
    }

    private String jsonToMvDownloadUrl(String json) {
        String result = "";
        JSONObject rootObject = JSONObject.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            result = rootObject.getJSONObject("data").getString("url");
        }
        return result;
    }

    private String jsonToMvPlayPathUrl(String json) {
        String result = "";
        JSONObject rootObject = JSONObject.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            result = rootObject.getJSONObject("data").getString("url");
        }
        return result;
    }

    private void initTimeThread() {
        disposable = Observable.interval(0, 800, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        synchronized (object) {
                            if ((!isDownloading) || currentDownloadBean == null) {
                                if (downloadQueue != null && downloadQueue.size() > 0) {
                                    startDownload(downloadQueue.get(0));
                                } else {
                                    stopSelf();
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (downloadQueue.size()>0){
                            downloadQueue.remove(0);
                        }
                        synchronized (object) {
                            downloadTask = null;
                            currentDownloadBean = null;
                            isDownloading = false;
                        }
                    }
                });
    }

    public class DownloadBinder extends Binder {
        public List<DownloadBean> getAllDownloadData() {
            synchronized (object) {
                List<DownloadBean> list = new ArrayList<>();
                if (currentDownloadBean != null) {
                    DownloadBean bean = currentDownloadBean;
                    Boolean mv = currentDownloadBean.getMvData();
                    if (mv != null && mv) {
                        bean.setMvBean(currentDownloadBean.getMvBean());
                        bean.setMvData(true);
                    } else {
                        bean.setMusicBean(currentDownloadBean.getMusicBean());
                        bean.setMvData(false);
                    }
                    if (downloadTask != null) {
                        bean.setSpeed(downloadTask.getSpeed() + "");
                        bean.setTotalByte((long) downloadTask.getSmallFileTotalBytes());
                        bean.setCurrentByte(downloadTask.getLargeFileSoFarBytes());
                    } else {
                        bean.setSpeed(0 + "");
                        bean.setTotalByte(0L);
                        bean.setCurrentByte(0L);
                    }
                    bean.setDownloading(true);
                    list.add(bean);
                }
                if (downloadQueue != null) {
                    list.addAll(downloadQueue);
                }
                return list;
            }
        }

        public void pauseDonwload() {
            if (downloadTask != null) {
                downloadTask.pause();
            }
        }

        public void cancleDownload(int index) {
            if (index == 0) {
                //将当前下载的取消掉
                if (downloadTask != null) {
                    downloadTask.cancel();
                }
            } else {
                if (downloadQueue != null) {
                    downloadQueue.remove(index);
                }
            }
        }

        public void cancleDownload(DownloadBean bean) {
            if (bean.equals(currentDownloadBean)) {
                downloadTask.cancel();
            }
            downloadQueue.remove(bean);
        }
    }

}
