package wzp.com.texturemusic.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;
import org.jaudiotagger.tag.images.AndroidArtwork;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.reference.PictureTypes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.MyApplication;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.config.AppFileConstant;


/**
 * Created by Go_oG
 * Description:
 * on 2017/9/16.
 */
public class MusicUtil {
    private static final String TAG = "MusicUtil";

    public static List<MusicBean> getLocalMusicData(Context context) {
        List<MusicBean> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.AudioColumns.IS_MUSIC,
                        MediaStore.Audio.AudioColumns.ARTIST,
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        MediaStore.Audio.AudioColumns.DATA,
                        MediaStore.Audio.AudioColumns.DURATION,
                        MediaStore.Audio.AudioColumns.TITLE
                },
                null,
                null,
                MediaStore.Audio.Media.DISPLAY_NAME);
        if (cursor == null) {
            return list;
        }
        while (cursor.moveToNext()) {
            // 是否为音乐 魅族手机上始终为0
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            if (!ROMUtil.isFlyme() && isMusic == 0) {
                continue;
            }
            // 标题
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            //文件存在，且播放时间大于60秒
            if (duration < 60 * 1000 || StringUtil.isEmpty(path)) {
                continue;
            } else {
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                    String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
                    long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
                    String coverImg = getAlbumArt(context, albumId);
                    String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
                    MusicBean musicBean = new MusicBean();
                    musicBean.setLocalMusic(true);
                    musicBean.setLocalArtist(true);
                    musicBean.setLocalAlbum(true);
                    musicBean.setCoverImgUrl(coverImg);
                    musicBean.setMusicName(StringUtil.dealMusicName(fileName, album, artist));
                    musicBean.setPlayPath(path);
                    musicBean.setAllTime(duration);
                    musicBean.setAlbumName(album);
                    musicBean.setArtistName(artist);
                    list.add(musicBean);
                }
            }
        }
        cursor.close();
        return list;
    }

    private static String getAlbumArt(Context context, long album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = context.getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + album_id),
                projection, null, null, null);
        String album_art = null;
        if (cur != null) {
            if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
                cur.moveToNext();
                album_art = cur.getString(0);
            }
            cur.close();
            cur = null;
        } else {
            album_art = "";
        }
        return album_art;
    }

    /**
     * 扫描手机上所有的音乐文件数据
     */
    public static List<MusicBean> searchLocalMusicFile(File file, List<MusicBean> list) {
        if (file != null) {
            if (file.isDirectory()) {//如果是文件夹
                File[] listFile = file.listFiles();//列出所有的文件放在listFile这个File类型数组中
                if (listFile != null) {
                    for (File fil : listFile) {
                        searchLocalMusicFile(fil, list);//递归，直到把所有文件遍历完
                    }
                }
            } else {//是文件
                String filePath = file.getAbsolutePath();//返回抽象路径名的绝对路径名字符串
                String name = file.getName();//获得文件的名称
                int size = AppConstant.MUSIC_FILE_TYPE.length;
                for (int i = 0; i < size; i++) {
                    if (filePath.endsWith(AppConstant.MUSIC_FILE_TYPE[i])) {//判断文件后缀名是否包含我们定义的格式
                        if (file.length() >= 2 * 1024 * 1024) {
                            //只保留大于2Mde
                            MusicBean bean = new MusicBean();
                            bean.setMusicName(name);
                            bean.setLocalMusic(true);
                            bean.setPlayPath(filePath);
                            list.add(bean);
                        }
                        break;
                    }
                }
            }
        }
        return list;
    }

    /**
     * 刷新本地媒体库
     *
     * @param context
     * @param fileAbsultPth
     * @param listener
     */
    public static void updateMediaDb(Context context, String fileAbsultPth, MediaScannerConnection.OnScanCompletedListener listener) {
        if (!StringUtil.isEmpty(fileAbsultPth)) {
            MediaScannerConnection.scanFile(context, new String[]{fileAbsultPth}, null, listener);
        }
    }

    /**
     * 编辑本地歌曲的信息
     * 为异步方法
     *
     * @param filePath 被编辑文件的绝对地址
     * @param bean     需要录入的信息
     */
    @SuppressLint("CheckResult")
    public static void editMusicInfo(final Context context, final MusicBean bean, final String filePath) {
        if (bean != null) {
            final File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                if (file.exists() && file.isFile()) {
                    Observable.create(new ObservableOnSubscribe<Boolean>() {
                        @Override
                        public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                            //该地方有可能会出现OOM
                            Bitmap bitmap = ImageUtil.getBitmap(context, bean.getCoverImgUrl(), 500, 890);
                            if (file.getPath().endsWith(".mp3")) {
                                AudioFile audioFile = AudioFileIO.read(file);
                                Tag tag = audioFile.getTagOrCreateDefault();
                                tag.setField(FieldKey.ARTIST, bean.getArtistName());
                                tag.setField(FieldKey.ALBUM, bean.getAlbumName());
                                tag.setField(FieldKey.TITLE, bean.getMusicName());
                                AndroidArtwork artwork = new AndroidArtwork();
                                artwork.setImageUrl(bean.getCoverImgUrl());
                                artwork.setBinaryData(BitmapUtil.Bitmap2Bytes(bitmap));
                                artwork.setMimeType(ImageFormats.MIME_TYPE_JPG);
                                artwork.setPictureType(PictureTypes.DEFAULT_ID);
                                tag.setField(artwork);
                                audioFile.commit();
                                audioFile.getAudioHeader().getTrackLength();
                                MusicUtil.updateMediaDb(context, filePath, null);
                                e.onNext(true);
                            } else if (file.getPath().endsWith(".flac")) {
                                AudioFile audioFile = AudioFileIO.read(file);
                                FlacTag tag = (FlacTag) audioFile.getTagOrCreateDefault();
                                if (tag != null) {
                                    tag.setField(FieldKey.ARTIST, bean.getArtistName());
                                    tag.setField(FieldKey.ALBUM, bean.getAlbumName());
                                    tag.setField(FieldKey.TITLE, bean.getMusicName());
                                    if (bitmap != null) {
                                        AndroidArtwork artwork = new AndroidArtwork();
                                        artwork.setImageUrl(bean.getCoverImgUrl());
                                        artwork.setBinaryData(BitmapUtil.Bitmap2Bytes(bitmap));
                                        artwork.setMimeType(ImageFormats.MIME_TYPE_JPG);
                                        artwork.setPictureType(PictureTypes.DEFAULT_ID);
                                        artwork.setWidth(500);
                                        artwork.setHeight(890);
                                        tag.setField(artwork);
                                    }
                                    audioFile.commit();
                                    MusicUtil.updateMediaDb(context, filePath, null);
                                    e.onNext(true);
                                } else {
                                    e.onNext(false);
                                }
                            } else {
                                e.onNext(false);
                            }
                        }
                    }).subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean aBoolean) throws Exception {
                                    if (aBoolean && file.exists()) {
                                        MusicUtil.updateMediaDb(context, file.getAbsolutePath(), null);
                                    }

                                }
                            });
                }
            }
        }
    }

    /**
     * 编辑歌曲信息 同步方法
     *
     * @param context
     * @param bean
     * @param filePath
     */
    public static void editMusicInfoSync(final Context context, final MusicBean bean, final String filePath) throws ExecutionException, InterruptedException, TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException, CannotWriteException {
        if (bean != null) {
            final File file = new File(filePath);
            if ((!file.exists()) || file.isDirectory()) {
                return;
            }
            Bitmap bitmap = ImageUtil.getBitmap(context, bean.getCoverImgUrl(), 500, 890);
            if (file.getPath().endsWith(".mp3")) {
                AudioFile audioFile = AudioFileIO.read(file);
                Tag tag = audioFile.getTagOrCreateDefault();
                tag.setField(FieldKey.ARTIST, bean.getArtistName());
                tag.setField(FieldKey.ALBUM, bean.getAlbumName());
                tag.setField(FieldKey.TITLE, bean.getMusicName());
                AndroidArtwork artwork = new AndroidArtwork();
                artwork.setImageUrl(bean.getCoverImgUrl());
                artwork.setBinaryData(BitmapUtil.Bitmap2Bytes(bitmap));
                artwork.setMimeType(ImageFormats.MIME_TYPE_JPG);
                artwork.setPictureType(PictureTypes.DEFAULT_ID);
                tag.setField(artwork);
                audioFile.commit();
                audioFile.getAudioHeader().getTrackLength();
            } else if (file.getPath().endsWith(".flac")) {
                AudioFile audioFile = AudioFileIO.read(file);
                FlacTag tag = (FlacTag) audioFile.getTagOrCreateDefault();
                if (tag != null) {
                    tag.setField(FieldKey.ARTIST, bean.getArtistName());
                    tag.setField(FieldKey.ALBUM, bean.getAlbumName());
                    tag.setField(FieldKey.TITLE, bean.getMusicName());
                    if (bitmap != null) {
                        AndroidArtwork artwork = new AndroidArtwork();
                        artwork.setImageUrl(bean.getCoverImgUrl());
                        artwork.setBinaryData(BitmapUtil.Bitmap2Bytes(bitmap));
                        artwork.setMimeType(ImageFormats.MIME_TYPE_JPG);
                        artwork.setPictureType(PictureTypes.DEFAULT_ID);
                        artwork.setWidth(500);
                        artwork.setHeight(890);
                        tag.setField(artwork);
                    }
                    audioFile.commit();
                }
            }
            MusicUtil.updateMediaDb(context, file.getAbsolutePath(), null);
        }
    }


    /**
     * 获取音乐文件 的IDV3信息
     * 有耗时操作 需要在子线程操作
     *
     * @param absolutePath 文件的绝对路径
     */
    public static MusicBean getMusicIDV3Info(String absolutePath) {
        final File file = new File(absolutePath);
        if (file.exists() && file.isFile()) {
            MusicBean musicBean = new MusicBean();
            musicBean.setPlayPath(absolutePath);
            AudioFile audioFile;
            try {
                audioFile = AudioFileIO.read(file);
                Tag tag = audioFile.getTagOrCreateDefault();
                if (tag != null) {
                    musicBean.setAlbumName(tag.getFirst(FieldKey.ALBUM));
                    musicBean.setArtistName(tag.getFirst(FieldKey.ARTIST));
                    musicBean.setMusicName(tag.getFirst(FieldKey.TITLE));
                    musicBean.setLocalAlbum(true);
                    musicBean.setLocalArtist(true);
                    musicBean.setLocalMusic(true);
                    Artwork artwork = tag.getFirstArtwork();
                    if (artwork != null) {
                        Bitmap bitmap = BitmapUtil.butes2Bitmap(artwork.getBinaryData());
                        if (bitmap != null) {
                            File imgFile = new File(AppFileConstant.ALBUM_IMAGE_DRESS + musicBean.getMusicName() + ".jpg");
                            BitmapUtil.saveBitmap(bitmap, file);
                            musicBean.setCoverImgUrl(imgFile.getAbsolutePath());
                            musicBean.setAlbumImgUrl(imgFile.getAbsolutePath());
                        }
                    }
                }
                AudioHeader audioHeader = audioFile.getAudioHeader();
                if (audioHeader != null) {
                    //获取的时长为秒数，需要自己乘以1000 变为毫秒
                    musicBean.setAllTime(audioHeader.getTrackLength() * 1000L);
                }
                return musicBean;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据URI 获取真实地址
     */
    public static MusicBean queryLoaclMusic(Uri uri) {
        if (uri == null) {
            return null;
        }
        String path = uri.getPath();
        String startStr = "/external_files/";
        if (path.startsWith(startStr)) {
            String realPath = "/storage/emulated/0/" + path.substring(startStr.length(), path.length());
            File file = new File(realPath);
            if (file.exists() && file.isFile()) {
                Context context = MyApplication.getInstace();
                String where = "_data = '" + realPath + "'";
                Cursor cursor = context.getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
                                MediaStore.Audio.AudioColumns.IS_MUSIC,
                                MediaStore.Audio.AudioColumns.ARTIST,
                                MediaStore.Audio.AudioColumns.ALBUM,
                                MediaStore.Audio.AudioColumns.ALBUM_ID,
                                MediaStore.Audio.AudioColumns.DATA,
                                MediaStore.Audio.AudioColumns.DURATION,
                                MediaStore.Audio.AudioColumns.TITLE
                        }, where, null, MediaStore.Audio.Media.DISPLAY_NAME);
                if (null != cursor) {
                    MusicBean musicBean = new MusicBean();
                    if (cursor.moveToFirst()) {
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                        String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
                        long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
                        String coverImg = getAlbumArt(context, albumId);
                        String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
                        musicBean.setLocalMusic(true);
                        musicBean.setLocalArtist(true);
                        musicBean.setLocalAlbum(true);
                        musicBean.setCoverImgUrl(coverImg);
                        musicBean.setMusicName(StringUtil.dealMusicName(fileName, album, artist));
                        musicBean.setPlayPath(filePath);
                        musicBean.setAllTime(duration);
                        musicBean.setAlbumName(album);
                        musicBean.setArtistName(artist);
                    }
                    cursor.close();
                    return musicBean;
                }
            }
        }
        return null;
    }

}
