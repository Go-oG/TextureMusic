package wzp.com.texturemusic.localmodule.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.CacheBean;
import wzp.com.texturemusic.common.dialog.InputDialog;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.config.AppFileConstant;
import wzp.com.texturemusic.core.manger.CacheManager;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.dbmodule.util.DbCacheUtil;
import wzp.com.texturemusic.interf.OnDialogInputListener;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.SPSetingUtil;
import wzp.com.texturemusic.util.ToastUtil;

;

/**
 * Created by Go_oG
 * Description:缓存二级设置界面
 * on 2018/1/23.
 */

public class CacheSetActivity extends BaseActivityWrapper {
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;
    @BindView(R.id.cache_custom_size_tv)
    TextView mCustomSizeTv;
    @BindView(R.id.cache_clear_music_size_tv)
    TextView mClearMusicSizeTv;
    @BindView(R.id.cache_clear_img_size_tv)
    TextView mClearImgSizeTv;
    @BindView(R.id.cache_clear_video_size_tv)
    TextView mClearVideoSizeTv;
    @BindView(R.id.cache_auto_clear_switch)
    SwitchCompat mAutoClearSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initService(false);
        super.onCreate(savedInstanceState);
        showBottomView(false);
        setContentView(R.layout.activity_cache_set);
        ButterKnife.bind(this);
        toolbarTitleTv.setText("缓存设置");
        mAutoClearSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean val = mAutoClearSwitch.isChecked();
                SPSetingUtil.setBooleanValue(AppConstant.SP_KEY_AUTO_CLEAR_CACHE, val);
                ToastUtil.showNormalMsg("设置成功");
            }
        });
        initData();
    }

    @OnClick({R.id.toolar_return_img, R.id.cache_custom_size_relative, R.id.cache_clear_music_relative,
            R.id.cache_clear_img_relative, R.id.cache_clear_video_relative, R.id.cache_auto_clear_relative})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolar_return_img:
                finish();
                break;
            case R.id.cache_custom_size_relative:
                showCustomDialog();
                break;
            case R.id.cache_clear_music_relative:
                clearMusicCache();
                mClearMusicSizeTv.setText("0MB");
                ToastUtil.showNormalMsg("清除音乐缓存成功");
                break;
            case R.id.cache_clear_img_relative:
                clearImgCache();
                mClearImgSizeTv.setText("0MB");
                ToastUtil.showNormalMsg("清除图片缓存成功");
                break;
            case R.id.cache_clear_video_relative:
                clearVideoCache();
                mClearVideoSizeTv.setText("0MB");
                ToastUtil.showNormalMsg("清除视频缓存成功");
                break;
            case R.id.cache_auto_clear_relative:
                boolean check = mAutoClearSwitch.isChecked();
                SPSetingUtil.setBooleanValue(AppConstant.SP_KEY_AUTO_CLEAR_CACHE, !check);
                mAutoClearSwitch.setChecked(!check);
                ToastUtil.showNormalMsg("设置成功");
                break;
        }
    }

    @SuppressLint("CheckResult")
    private void initData() {
        Observable.create(new ObservableOnSubscribe<CacheBean>() {
            @Override
            public void subscribe(ObservableEmitter<CacheBean> e) throws Exception {
                CacheBean bean = new CacheBean();
                bean.setAutoClear(autoClearCache());
                bean.setCustomMusicCacheSize((long) getCustomMusicCacheSize());
                bean.setImgCacheSize(getImgCacheSize());
                bean.setVideoCacheSize(getVideoCacheSize());
                bean.setMusicCacheSize(getMusicCacheSize());
                e.onNext(bean);
            }
        }).subscribeOn(Schedulers.io())
                .compose(this.<CacheBean>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CacheBean>() {
                    @Override
                    public void accept(CacheBean cacheBean) throws Exception {
                        mAutoClearSwitch.setChecked(cacheBean.getAutoClear());
                        mClearImgSizeTv.setText(FormatData.fileSizeToString(cacheBean.getImgCacheSize()));
                        mClearMusicSizeTv.setText(FormatData.fileSizeToString(cacheBean.getMusicCacheSize()));
                        mClearVideoSizeTv.setText(FormatData.fileSizeToString(cacheBean.getVideoCacheSize()));
                        mCustomSizeTv.setText(cacheBean.getCustomMusicCacheSize() + "MB");
                    }
                });
    }

    /**
     * 返回自定义的音乐缓存大小
     * 默认 500 还需要自己乘以相关进制转换
     *
     * @return
     */
    private int getCustomMusicCacheSize() {
        return SPSetingUtil.getIntValue(AppConstant.SP_KEY_MUSIC_CACHE_SIZE, AppConstant.DEFAULT_MUSIC_CACHE_SIZE);
    }

    /**
     * 返回已经缓存了音乐的文件大小
     * 单位b
     *
     * @return
     */
    private long getMusicCacheSize() {
        long size = 0;
        File file = new File(AppFileConstant.MEDIA_CACHE_DRESS);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fi : files) {
                if (fi.exists() && fi.isFile() && fi.getName().endsWith(CacheManager.CACHE_END_TAG_MC)) {
                    size += fi.length();
                }
            }
        }
        return size;
    }

    /**
     * 获取缓存的图片大小
     * 单位b
     *
     * @return
     */
    private long getImgCacheSize() {
        long size = 0;
        File file = new File(AppFileConstant.IMAGE_CACHE_DRESS);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            int length = files.length;
            if (length > 0) {
                for (File fi : files) {
                    if (!fi.getName().equals("journal")) {
                        size += fi.length();
                    }
                }
            }
        }
        return size;
    }

    /**
     * 获得缓存的mv大小
     * 单位b
     *
     * @return
     */
    private long getVideoCacheSize() {
        long size = 0;
        File file = new File(AppFileConstant.MEDIA_CACHE_DRESS);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            int length = files.length;
            if (length > 0) {
                for (File fi : files) {
                    if (fi.exists() && fi.isFile() && fi.getName().endsWith(CacheManager.CACHE_END_TAG_MV)) {
                        size += fi.length();
                    }
                }
            }
        }
        return size;
    }

    private boolean autoClearCache() {
        return SPSetingUtil.getBooleanValue(AppConstant.SP_KEY_AUTO_CLEAR_CACHE, false);
    }

    private void clearMusicCache() {
        DbCacheUtil.clearCacheData();
        File file = new File(AppFileConstant.MEDIA_CACHE_DRESS);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fi : files) {
                if (fi.getName().endsWith(CacheManager.CACHE_END_TAG_MC)) {
                    fi.delete();
                }
            }
        }
    }

    private void clearVideoCache() {
        File file = new File(AppFileConstant.MEDIA_CACHE_DRESS);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fi : files) {
                if (fi.getName().endsWith(CacheManager.CACHE_END_TAG_MV)) {
                    fi.delete();
                }
            }
        }
    }

    private void clearImgCache() {
        File file = new File(AppFileConstant.IMAGE_CACHE_DRESS);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fi : files) {
                fi.delete();
            }
        }
    }

    private void showCustomDialog() {
        int zise = SPSetingUtil.getIntValue(AppConstant.SP_KEY_MUSIC_CACHE_SIZE, AppConstant.DEFAULT_MUSIC_CACHE_SIZE);
        InputDialog dialog = new InputDialog();
        dialog.setDefaultValue(zise + "");
        dialog.setSubTitle("MB");
        dialog.setTitle("自定义缓存");
        dialog.setTips("一首高品质歌曲约占10M");
        dialog.setInputListener(new OnDialogInputListener() {
            @Override
            public void onResult(String result, boolean sure) {
                if (sure) {
                    int val = Integer.valueOf(result);
                    SPSetingUtil.setIntValue(AppConstant.SP_KEY_MUSIC_CACHE_SIZE, val);
                    mCustomSizeTv.setText(val + "MB");
                    ToastUtil.showNormalMsg("设置成功");
                }
            }
        });
        dialog.setCancelable(true);
        dialog.show(getFragmentManager(), "cache");
    }

}
