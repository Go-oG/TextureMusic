package wzp.com.texturemusic.localmodule.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.CancellationSignal;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.commit451.nativestackblur.NativeStackBlur;
import com.freedom.lauzy.playpauseviewlib.PlayPauseView;
import com.ncorti.slidetoact.SlideToActView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import app.minimize.com.seek_bar_compat.SeekBarCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.common.popwindow.PlayQueuePopwindow;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.eventlistener.EngineEvent;
import wzp.com.texturemusic.core.ui.BaseActivity;
import wzp.com.texturemusic.interf.OnDialogListener;
import wzp.com.texturemusic.interf.OnImageLoadListener;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.DownloadUtil;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:锁屏界面通过一个activity来伪造一个锁屏界面
 * on 2017/11/8.
 */

public class LockScreenActivity extends BaseActivity {
    @BindView(R.id.lock_seek_bar)
    SeekBarCompat playPercentSeekBar;
    @BindView(R.id.start_time_tv)
    TextView startTimeTv;
    @BindView(R.id.end_time_tv)
    TextView endTimeTv;
    @BindView(R.id.music_name_tv)
    TextView musicNameTv;
    @BindView(R.id.artist_name)
    TextView artistName;
    @BindView(R.id.play_control_img)
    PlayPauseView playControlImg;
    @BindView(R.id.last_music_img)
    ImageView lastMusicImg;
    @BindView(R.id.play_next_img)
    ImageView playNextImg;
    @BindView(R.id.playqueue_img)
    ImageView playQueueImg;
    @BindView(R.id.m_Play_Type_Button)
    ImageView playPlaytypeImg;
    @BindView(R.id.lock_seek_bar2)
    SeekBarCompat volumeSeekBar;
    @BindView(R.id.sweep_unlock_view)
    SlideToActView mUnlockView;
    int maxVolume = 0;
    int currentVolume = 0;
    @BindView(R.id.music_img)
    ImageView musicImg;
    @BindView(R.id.img_switcher)
    ImageSwitcher imgSwitcher;
    @BindView(R.id.m_status_bar)
    View mStatusBar;
    private AudioManager mAudioManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        setStatusBarHeight(0);
        ButterKnife.bind(this);
        //用于去掉系统锁屏（当且仅当系统没有设置密码时才有用）
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int statckbarHight = BaseUtil.getStackBarHight();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mStatusBar.getLayoutParams();
            params.height = statckbarHight;
            mStatusBar.setLayoutParams(params);
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        initVolumeSeekbar();
        mUnlockView.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                finish();
            }
        });
        mUnlockView.setOnSlideResetListener(new SlideToActView.OnSlideResetListener() {
            @Override
            public void onSlideReset(SlideToActView slideToActView) {

            }
        });
        playPercentSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    //表示是用户拖动的seekbar
                    seekToPosition(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (mAudioManager == null) {
                        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    }
                    if (mAudioManager != null) {
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        imgSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });
    }

    @OnClick({R.id.play_control_img, R.id.last_music_img, R.id.play_next_img, R.id.playqueue_img,
            R.id.m_Play_Type_Button, R.id.volume_sub, R.id.volume_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.play_control_img:
                int state = getNowPlayState();
                if (state == EngineEvent.STATUS_PLAYING) {
                    pauseMusic();
                } else if (state == EngineEvent.STATUS_PAUSE) {
                    resumeMusic();
                } else {
                    playMusic();
                }
                break;
            case R.id.last_music_img:
                lastMusic();
                break;
            case R.id.play_next_img:
                nextMusic();
                break;
            case R.id.playqueue_img:
                showPlayQueuePop();
                break;
            case R.id.m_Play_Type_Button:
                break;
            case R.id.volume_sub:
                adjustVolume(AudioManager.ADJUST_LOWER, 0);
                break;
            case R.id.volume_add:
                adjustVolume(AudioManager.ADJUST_RAISE, 0);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //返回true 表示让事件不在传递
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                initVolumeSeekbar();
                return true;
            case KeyEvent.KEYCODE_BACK: {
                return true;
            }
            case KeyEvent.KEYCODE_MENU: {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void adjustVolume(int direction, int flag) {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }
        if (mAudioManager != null) {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, flag);
        }
        initVolumeSeekbar();
    }

    private void initVolumeSeekbar() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }
        if (mAudioManager != null) {
            maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        volumeSeekBar.setMax(maxVolume);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            volumeSeekBar.setProgress(currentVolume, true);
        } else {
            volumeSeekBar.setProgress(currentVolume);
        }
    }


    @Override
    protected void onPlayStatusChange(int playbackState) {
        switch (playbackState) {
            case EngineEvent.STATUS_PLAYING:
                playControlImg.play();
                break;
            default:
                playControlImg.pause();
                break;
        }
    }

    @Override
    protected void onMediaSessionConnect(MusicBean bean, int playStatus) {
        switch (playStatus) {
            case EngineEvent.STATUS_PLAYING:
                playControlImg.play();
                break;
            default:
                playControlImg.pause();
                break;
        }
        if (bean != null) {
            musicNameTv.setText(bean.getMusicName());
            artistName.setText(bean.getArtistName() + "-" + bean.getAlbumName());
            updateImg(bean);
        }
    }

    @Override
    protected void onMediaDataChange(@NonNull MusicBean bean) {
        musicNameTv.setText(bean.getMusicName());
        artistName.setText(bean.getArtistName() + "-" + bean.getAlbumName());
        updateImg(bean);
    }

    @Override
    protected void onUpdateProgress(int currentTime, int durationTime) {
        if (durationTime <= 0) {
            durationTime = 0;
        }
        if (currentTime <= 0) {
            currentTime = 0;
        }
        if (durationTime == 0) {
            playPercentSeekBar.setMax(currentTime);
            playPercentSeekBar.setProgress(currentTime);
            startTimeTv.setText(FormatData.timeValueToString(currentTime));
            endTimeTv.setText("未知");
        } else {
            playPercentSeekBar.setMax(durationTime);
            playPercentSeekBar.setProgress(currentTime);
            startTimeTv.setText(FormatData.timeValueToString(currentTime));
            endTimeTv.setText(FormatData.timeValueToString(durationTime));
        }
    }

    @Override
    protected void onServiceDestroyed() {
        super.onServiceDestroyed();
        if (playControlImg.isPlaying()) {
            playControlImg.pause();
        }
    }

    /**
     * 开始指纹识别识别
     */
    private void startFingerPrintListening() {
        if (!isFingerprintAuthAvailable()) {
            return;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                RxPermissions rxPermissions = new RxPermissions(this);
                rxPermissions.request(Manifest.permission.USE_FINGERPRINT)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    finish();
                                }
                            }
                        });
            } else {
                finish();
            }
        }
    }

    /**
     * 判断指纹识别功能是否可用 且APP有无权限
     *
     * @return
     */
    public boolean isFingerprintAuthAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
            if (!mKeyguardManager.isKeyguardSecure()) {
                return false;
            }
            if (checkSelfPermission(Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
                FingerprintManager mFingerprintManager = (FingerprintManager) getSystemService(Activity.FINGERPRINT_SERVICE);
                CancellationSignal mCancellationSignal = new CancellationSignal();
                if (mFingerprintManager != null) {
                    return mFingerprintManager.isHardwareDetected() && mFingerprintManager.hasEnrolledFingerprints();
                }
            }
        }
        return false;
    }

    private void showPlayQueuePop() {
        final List<MusicBean> dataList = getPlayListQueue(false);
        showMusicListPopwindow(mUnlockView, dataList, new PlayQueuePopwindow.OnPopClickListener() {
            @Override
            public void clickPlayMode() {

            }

            @Override
            public void clickDownlaodAll(List<MusicBean> list) {
                DownloadUtil.downloadPlaylist(mContext, list);
            }

            @Override
            public void clickDeleteAll() {
                //移除所有的数据
                ToastUtil.showCustomDialog(mContext, "确定要清空播放列表?", new OnDialogListener() {
                    @Override
                    public void onResult(boolean success) {
                        if (success) {
                            clearPlayQueue(false);
                            ToastUtil.showNormalMsg("清除成功");
                        }
                    }
                });
            }

            @Override
            public void clickRecycleItem(View view, int position) {
                if (view.getId() == R.id.item_pop_close_img) {
                    removeMusic(dataList.get(position), false);
                    dataList.remove(position);
                    if (mPlayQueuePop != null && mPlayQueuePop.isShowing()) {
                        mPlayQueuePop.updateUI(dataList);
                    }
                    ToastUtil.showNormalMsg("成功移除该音乐");
                } else {
                    playMusic(dataList.get(position));
                }
            }
        });
    }

    private void updateImg(MusicBean bean) {
        if (bean == null) {
            return;
        }
        if (bean.getLocalMusic() == null) {
            bean.setLocalMusic(true);
        }
        if (bean.getLocalMusic()) {
            ImageUtil.loadImage(mContext, bean.getCoverImgUrl(), musicImg);
            ImageUtil.loadImage(mContext, bean.getCoverImgUrl(), 100, 100, R.mipmap.main_play_bk,
                    new OnImageLoadListener() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            imgSwitcher.setImageDrawable(new BitmapDrawable(getResources(), NativeStackBlur.process(bitmap, 50)));
                        }
                    });
        }
        if (!bean.getLocalMusic()) {
            ImageUtil.loadImage(mContext, bean.getCoverImgUrl() + AppConstant.WY_IMG_500_500, musicImg);
            ImageUtil.loadImage(mContext, bean.getCoverImgUrl() + AppConstant.WY_IMG_100_100, R.mipmap.main_play_bk,
                    new OnImageLoadListener() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            imgSwitcher.setImageDrawable(new BitmapDrawable(getResources(), NativeStackBlur.process(bitmap, 50)));
                        }
                    });
        }
    }

    @Override
    protected void onPlayWarn(String warnType, String warnHint) {
        int playStatus = getNowPlayState();
        if (playStatus == EngineEvent.STATUS_PLAYING) {
            if (playControlImg != null && !playControlImg.isPlaying()) {
                playControlImg.play();
            }
        } else {
            playControlImg.pause();
        }
    }
}
