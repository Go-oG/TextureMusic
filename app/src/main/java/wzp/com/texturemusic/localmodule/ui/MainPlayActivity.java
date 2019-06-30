package wzp.com.texturemusic.localmodule.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.palette.graphics.Palette;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.commit451.nativestackblur.NativeStackBlur;
import com.freedom.lauzy.playpauseviewlib.PlayPauseView;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import me.tankery.lib.circularseekbar.CircularSeekBar;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.common.popwindow.PlayQueuePopwindow;
import wzp.com.texturemusic.common.ui.SimilarMusicActivity;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.eventlistener.EngineEvent;
import wzp.com.texturemusic.core.ui.BaseActivity;
import wzp.com.texturemusic.customview.CarouselTextView;
import wzp.com.texturemusic.customview.CustomPath;
import wzp.com.texturemusic.customview.MusicVerticalFFTView;
import wzp.com.texturemusic.dbmodule.util.DbCollectUtil;
import wzp.com.texturemusic.interf.OnDialogListener;
import wzp.com.texturemusic.interf.OnImageLoadListener;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.DownloadUtil;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;
import wzp.com.texturemusic.util.SPSetingUtil;
import wzp.com.texturemusic.util.StringUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Wang on 2017/9/14.
 * 主要的播放界面
 */
public class MainPlayActivity extends BaseActivity {
    //Visualizer 最大采样精度的几分之1
    //1 为最大 往后递减 但是值必须为 2的倍数
    private static final int MAX_SAMPLING_MULTIPE = 1;
    //Visualizer 最大采样速率的几分之1
    //1 为最大 往后递减 但是值必须为 2的倍数
    private static final int MAX_SAMPLING_SPEED = 2;
    //点击事件的间隔时间 单位毫秒
    private static final int CLICK_TIME = 300;
    @BindView(R.id.top_status_bar)
    View topStatusBar;
    @BindView(R.id.return_img)
    ImageView mReturnImg;
    @BindView(R.id.music_tv)
    TextView topMusicTv;
    @BindView(R.id.m_OperationImg)
    ImageView mOperationImg;
    @BindView(R.id.m_comment_Img)
    ImageView mCommentImg;
    @BindView(R.id.m_Music_cover_img)
    ImageView mMusicCoverImg;
    @BindView(R.id.m_Progress_bar)
    CircularSeekBar mProgressBar;
    @BindView(R.id.start_time_tv)
    TextView startTimeTv;
    @BindView(R.id.end_time_tv)
    TextView endTimeTv;
    @BindView(R.id.m_Music_Name_Tv)
    TextView mMusicNameTv;
    @BindView(R.id.m_Music_Info_tv)
    CarouselTextView mMusicInfoTv;
    @BindView(R.id.m_play_button)
    PlayPauseView mPlayButton;
    @BindView(R.id.m_Last_Button)
    ImageView mLastButton;
    @BindView(R.id.m_Next_Button)
    ImageView mNextButton;
    @BindView(R.id.m_playqueue_Button)
    ImageView mPlayqueueImg;
    @BindView(R.id.m_Play_Type_Button)
    ImageView mPlayTypeButton;
    @BindView(R.id.music_FFT_view)
    MusicVerticalFFTView mVerticalFFTView2;
    @BindView(R.id.m_constrain_layout)
    ConstraintLayout mConstrainLayout;
    @BindView(R.id.artist_name_tv)
    TextView artistNameTv;
    @BindView(R.id.v_guideline)
    Guideline vGuideline;
    @BindView(R.id.img_collect)
    ImageView imgCollect;
    @BindView(R.id.img_switcher)
    ImageSwitcher imageSwitcher;
    @BindView(R.id.m_path_view)
    CustomPath mPathView;
    //音乐可视化
    private int mAudioSessionId = 0;
    //用于捕捉音频FFT数据
    private Visualizer mVisualizer;
    private VisualizerListener visualizerListener;
    private ObjectAnimator mAnimator;
    private AudioManager mAudioManager;
    private boolean openFFTView = true;//是否打开音乐可是化界面
    private boolean openAnimation = true;//是否打开动画
    /////////用于优化进度条跟新消耗大量的View
    private int maxProgress = 0;
    private int currentProgress = 0;


    class VisualizerListener implements Visualizer.OnDataCaptureListener {

        @Override
        public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
        }

        @Override
        public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
            if (openFFTView) {
                int length = fft.length;
                if (length>256){
                    length=256;
                }
                byte[] result = new byte[length / 2 + 1];
                result[0] = (byte) Math.abs(fft[1]);
                int j = 1;
                for (int i = 2; i < length-1; i = i + 2) {
                    result[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
                    j++;
                }
                if (result.length>2){
                    mVerticalFFTView2.setFftData(result);
                    mPathView.setData(result);
                }else {
                    mVerticalFFTView2.setFftData(null);
                    mPathView.setData(null);
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_play);
        setStatusBarHeight(0);
        ButterKnife.bind(this);
        mContext = this;
        openAnimation = SPSetingUtil.getBooleanValue(AppConstant.SP_KEY_OPEN_ANIMATION, true);
        openFFTView = SPSetingUtil.getBooleanValue(AppConstant.SP_KEY_OPEN_FFT_VIEW, true);
        if (openFFTView && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //请求录音权限
            RxPermissions permissions = new RxPermissions(this);
            permissions.request(Manifest.permission.RECORD_AUDIO)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            openFFTView = aBoolean;
                            init();
                        }
                    });
        } else {
            init();
        }
    }

    @SuppressLint("CheckResult")
    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int statckbarHight = BaseUtil.getStackBarHight();
            ViewGroup.LayoutParams params = topStatusBar.getLayoutParams();
            params.height = statckbarHight;
            topStatusBar.setLayoutParams(params);
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mPathView.setPointColor(getMainColor());
        RxView.clicks(mPlayButton)
                .throttleFirst(CLICK_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        int state = getNowPlayState();
                        if (state == EngineEvent.STATUS_PLAYING) {
                            if (mPlayButton.isPlaying()) {
                                mPlayButton.pause();
                            }
                            pauseMusic();
                        } else if (state == EngineEvent.STATUS_PAUSE) {
                            if (!mPlayButton.isPlaying()) {
                                mPlayButton.play();
                            }
                            resumeMusic();
                        } else {
                            if (!mPlayButton.isPlaying()) {
                                mPlayButton.play();
                            }
                            playMusic();
                        }
                    }
                });
        RxView.clicks(mLastButton)
                .throttleFirst(CLICK_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        lastMusic();
                    }
                });
        RxView.clicks(mNextButton)
                .throttleFirst(CLICK_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        nextMusic();
                    }
                });
        visualizerListener = new VisualizerListener();
        mProgressBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {

            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                seekToPosition((long) seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });
        int nowMode = getPlayMode();
        if (nowMode == AppConstant.PLAY_MODE_SINGLE) {
            mPlayTypeButton.setImageResource(R.drawable.ic_play_type_single);
        } else if (nowMode == AppConstant.PLAY_MODE_LOOP) {
            mPlayTypeButton.setImageResource(R.drawable.ic_play_type_list);
        } else if (nowMode == AppConstant.PLAY_MODE_RANDOM) {
            mPlayTypeButton.setImageResource(R.drawable.ic_play_type_random);
        } else {
            ToastUtil.showNormalMsg("播放模式错误");
        }
        mMusicCoverImg
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mMusicCoverImg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int circle_r = mMusicCoverImg.getHeight() / 2 > mMusicCoverImg.getWidth() / 2
                                ? mMusicCoverImg.getHeight() / 2 : mMusicCoverImg.getWidth() / 2;
                        Animator animator = ViewAnimationUtils.createCircularReveal(mMusicCoverImg, mMusicCoverImg.getWidth() / 2,
                                mMusicCoverImg.getHeight() / 2, 0, circle_r);
                        animator.setInterpolator(new LinearInterpolator());
                        animator.setDuration(600);
                        animator.start();
                    }
                });
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
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

    @Override
    protected void onResume() {
        super.onResume();
        resumeAnimator();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseAnimator();
    }

    @Override
    protected void onStop() {
        stopAnimator();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopAnimator();
        if (mVisualizer != null) {
            mVisualizer.release();
        }
        if (mAudioManager != null) {
            mAudioManager = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    @OnClick({R.id.return_img, R.id.m_OperationImg, R.id.m_comment_Img, R.id.m_playqueue_Button, R.id.m_Play_Type_Button,
            R.id.artist_name_tv, R.id.img_download, R.id.img_similer, R.id.img_album_info, R.id.img_collect})
    public void onViewClicked(View view) {
        final MusicBean bean;
        switch (view.getId()) {
            case R.id.return_img:
                supportFinishAfterTransition();
                break;
            case R.id.m_OperationImg:
                //右上角的操作按钮
                shareMusic(mConstrainLayout, getPlayMusic(false));
                break;
            case R.id.m_comment_Img:
                //跳转到评论界面
                bean = getPlayMusic(false);
                toCommentActivity(bean);
                break;
            case R.id.m_playqueue_Button:
                final List<MusicBean> dataList = getPlayListQueue(false);
                showMusicListPopwindow(mConstrainLayout, dataList, new PlayQueuePopwindow.OnPopClickListener() {
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
                break;
            case R.id.m_Play_Type_Button:
                //调整播放模式
                int nowMode = getPlayMode();
                nowMode++;
                nowMode = nowMode % 3;
                setPlayMode(nowMode);
                if (nowMode == AppConstant.PLAY_MODE_SINGLE) {
                    mPlayTypeButton.setImageResource(R.drawable.ic_play_type_single);
                    ToastUtil.showNormalMsg("单曲循环");
                } else if (nowMode == AppConstant.PLAY_MODE_LOOP) {
                    mPlayTypeButton.setImageResource(R.drawable.ic_play_type_list);
                    ToastUtil.showNormalMsg("列表循环");
                } else if (nowMode == AppConstant.PLAY_MODE_RANDOM) {
                    mPlayTypeButton.setImageResource(R.drawable.ic_play_type_random);
                    ToastUtil.showNormalMsg("随机播放");
                } else {
                    ToastUtil.showNormalMsg("播放模式错误");
                }
                break;
            case R.id.artist_name_tv:
                //跳转到歌手界面
                bean = getPlayMusic(false);
                toArtistDetailActivity(bean);
                break;
            case R.id.img_download:
                bean = getPlayMusic(false);
                if (bean != null && bean.getLocalMusic() != null && !bean.getLocalMusic()) {
                    DownloadUtil.downloadMusic(mContext, bean);
                    ToastUtil.showNormalMsg("开始下载音乐");
                } else {
                    ToastUtil.showNormalMsg("本地音乐无需下载");
                }
                break;
            case R.id.img_similer:
                //跳转到相似歌曲推荐界面
                bean = getPlayMusic(false);
                if (bean != null && !StringUtil.isEmpty(bean.getMusicId())) {
                    Intent intent = new Intent(mContext, SimilarMusicActivity.class);
                    intent.putExtra("musicId", bean.getMusicId());
                    startActivity(intent);
                } else {
                    ToastUtil.showNormalMsg("暂无数据");
                }

                break;
            case R.id.img_album_info:
                //跳转到专辑详情界面去
                bean = getPlayMusic(false);
                toAlbumDetailActivity(bean);
                break;
            case R.id.img_collect:
                //收藏
                bean = getPlayMusic(false);
                if (bean != null) {
                    if (bean.getLiked() != null && bean.getLiked()) {
                        DbCollectUtil.deleteLikedMusic(bean);
                        ToastUtil.showNormalMsg("取消收藏成功");
                        imgCollect.setImageResource(R.drawable.ic_nav_item_collect);
                        updateCurrentDataForLiked(false);
                    } else {
                        bean.setLiked(true);
                        DbCollectUtil.addLikedMusic(bean);
                        ToastUtil.showNormalMsg("收藏成功");
                        imgCollect.setImageResource(R.drawable.ic_collect_like);
                        updateCurrentDataForLiked(true);
                    }
                }
                break;
        }
    }

    @Override
    public void supportFinishAfterTransition() {
        stopAnimator();
        super.supportFinishAfterTransition();
    }

    @Override
    public void finish() {
        stopAnimator();
        super.finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //必须手动调用该方法设置当前Intent
        //否则会出现隐式Intent打开APP时无法播放音乐的BUG
        setIntent(intent);
    }

    @Override
    protected void onMediaDataChange(@NonNull MusicBean bean) {
        if (bean.getLiked() != null && bean.getLiked()) {
            imgCollect.setImageResource(R.drawable.ic_collect_like);
        } else {
            imgCollect.setImageResource(R.drawable.ic_nav_item_collect);
        }
        mMusicInfoTv.setText((bean.getAlbumName() + "-" + bean.getArtistName()).trim());
        mMusicNameTv.setText(bean.getMusicName().trim());
        topMusicTv.setText(bean.getMusicName().trim());
        artistNameTv.setText(bean.getArtistName().trim());
        updateImg(bean);
        startAnimator();
    }

    @Override
    protected void onPlayStatusChange(int playbackState) {
        if (playbackState != EngineEvent.STATUS_PLAYING) {
            mVerticalFFTView2.setFftData(null);
        }
        switch (playbackState) {
            case EngineEvent.STATUS_PLAYING:
                if (!mPlayButton.isPlaying()) {
                    mPlayButton.play();
                }
                initVisualSize(getAudioSessionId());
                startAnimator();
                break;
            case EngineEvent.STATUS_PAUSE:
                if (mVisualizer != null && mVisualizer.getEnabled()) {
                    mVisualizer.setEnabled(false);
                }
                if (mPlayButton.isPlaying()) {
                    mPlayButton.pause();
                }
                pauseAnimator();
                break;
            case EngineEvent.STATUS_END:
                if (mPlayButton.isPlaying()) {
                    mPlayButton.pause();
                }
                releseVisualzer();
                mProgressBar.setProgress(0);
                startTimeTv.setText("00:00");
                stopAnimator();
                break;
            default:
                if (mPlayButton.isPlaying()) {
                    mPlayButton.pause();
                }
                releseVisualzer();
                stopAnimator();
                break;
        }
    }

    @Override
    protected void onMediaSessionConnect(MusicBean bean, int playStatus) {
        super.onMediaSessionConnect(bean, playStatus);
        startAnimator();
        updateImg(bean);
        if (bean != null) {
            if (!StringUtil.isEmpty(bean.getArtistName())) {
                artistNameTv.setVisibility(View.VISIBLE);
                artistNameTv.setText(bean.getArtistName());
                mMusicInfoTv.setText((bean.getAlbumName() + "-" + bean.getArtistName()));
            } else {
                artistNameTv.setText("");
                artistNameTv.setVisibility(View.GONE);
                mMusicInfoTv.setText(bean.getAlbumName());
            }
            mMusicNameTv.setText(bean.getMusicName());
            topMusicTv.setText(bean.getMusicName());
            endTimeTv.setText(FormatData.timeValueToString(bean.getAllTime()));
            startTimeTv.setText("00:00");
            if (bean.getLiked() != null && bean.getLiked()) {
                imgCollect.setImageResource(R.drawable.ic_collect_like);
            } else {
                imgCollect.setImageResource(R.drawable.ic_nav_item_collect);
            }
        }
        if (playStatus == EngineEvent.STATUS_PLAYING) {
            if (!mPlayButton.isPlaying()) {
                mPlayButton.play();
            }
            initVisualSize(getAudioSessionId());
        } else {
            if (mPlayButton.isPlaying()) {
                mPlayButton.pause();
            }
            releseVisualzer();
        }
        //接收歌曲的信息
        Intent intent = getIntent();
        if (intent != null) {
            Uri uri = intent.getData();
            if (uri != null) {
                playMusic(uri);
                getIntent().setData(null);
            }
        }
    }

    @Override
    protected void onMediaSessionDestroyed() {
        releseVisualzer();
    }

    @Override
    protected void onServiceDestroyed() {
        if (mPlayButton.isPlaying()) {
            mPlayButton.pause();
        }
        mAudioSessionId = 0;
        releseVisualzer();
    }

    @Override
    protected void onBufferPercentChange(int percent) {
    }

    @Override
    protected void onAudioSessionId(int audioSession) {
        initVisualSize(audioSession);
    }

    @Override
    protected void onLyricChange(MusicBean bean, String lyricChange) {
        if (!mMusicInfoTv.getText().toString().equals(lyricChange))
            mMusicInfoTv.setText(lyricChange);
    }

    @Override
    protected void onUpdateProgress(int currentTime, int durationTime) {
        //在更新进度条时会消耗大量资源
        if (durationTime <= 0) {
            durationTime = 0;
        }
        if (currentTime <= 0) {
            currentTime = 0;
        }
        if (durationTime != maxProgress) {
            maxProgress = durationTime;
            mProgressBar.setMax(maxProgress);
            endTimeTv.setText(FormatData.timeValueToString(durationTime));
        }
        if (currentTime != currentProgress) {
            currentProgress = currentTime;
            mProgressBar.setProgress(currentTime);
            startTimeTv.setText(FormatData.timeValueToString(currentTime));
        }

    }

    @Override
    protected void onPlayWarn(String warnType, String warnHint) {
        int playStatus = getNowPlayState();
        if (playStatus == EngineEvent.STATUS_PLAYING) {
            if (mPlayButton != null && !mPlayButton.isPlaying()) {
                mPlayButton.play();
            }
        } else {
            mPlayButton.pause();
        }
    }

    /**
     * 初始化音频采集器
     *
     * @param audioSessionId
     */
    private void initVisualSize(int audioSessionId) {
        if (openFFTView) {
            mVerticalFFTView2.setVisibility(View.VISIBLE);
            if (audioSessionId != 0) {
                if (audioSessionId == mAudioSessionId) {
                    //同一首歌
                    if (mVisualizer != null && !mVisualizer.getEnabled()) {
                        mVisualizer.setEnabled(true);
                    } else {
                        if (mVisualizer != null) {
                            mVisualizer.release();
                            mVisualizer = null;
                        }
                        mVisualizer = new Visualizer(audioSessionId);
                        mVisualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
                        //设置采样率
                        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1] / MAX_SAMPLING_MULTIPE);
                        //第三个控制采样速率
                        mVisualizer.setDataCaptureListener(visualizerListener, Visualizer.getMaxCaptureRate() / MAX_SAMPLING_SPEED, false, true);
                        mVisualizer.setEnabled(true);
                    }
                } else {
                    mAudioSessionId = audioSessionId;
                    if (mVisualizer != null) {
                        mVisualizer.release();
                        mVisualizer = null;
                    }
                    mVisualizer = new Visualizer(audioSessionId);
                    mVisualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
                    //设置采样率
                    mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1] / MAX_SAMPLING_MULTIPE);
                    //第三个控制采样速率
                    mVisualizer.setDataCaptureListener(visualizerListener, Visualizer.getMaxCaptureRate() / MAX_SAMPLING_SPEED, false, true);
                    mVisualizer.setEnabled(true);
                }
            }
        } else {
            mVerticalFFTView2.setVisibility(View.INVISIBLE);
        }
    }

    private void releseVisualzer() {
        if (mVisualizer != null) {
            mVisualizer.release();
            mVisualizer = null;
        }
        mAudioSessionId = 0;
        mVerticalFFTView2.setFftData(null);
    }

    private void startAnimator() {
        if (openAnimation) {
            int status = getNowPlayState();
            if (status == EngineEvent.STATUS_PLAYING) {
                if (mAnimator != null) {
                    if (mAnimator.isPaused()) {
                        mAnimator.resume();
                    } else if (mAnimator.isRunning()) {

                    } else {
                        initAnimator();
                    }
                } else {
                    initAnimator();
                }
            } else if (status == EngineEvent.STATUS_PAUSE) {
                pauseAnimator();
            } else {
                if (mAnimator != null) {
                    mAnimator.cancel();
                    mAnimator = null;
                }
            }
        }
    }

    private void initAnimator() {
        if (openAnimation) {
            mAnimator = ObjectAnimator.ofFloat(mMusicCoverImg, View.ROTATION, 0, 359.9f);
            mAnimator.setDuration(20 * 1000);
            mAnimator.setRepeatMode(ValueAnimator.RESTART);
            mAnimator.setInterpolator(new LinearInterpolator());
            MusicBean bean = getPlayMusic(false);
            if (bean != null) {
                int count = (int) ((bean.getAllTime() / (20 * 1000)) + 1);
                mAnimator.setRepeatCount(count);
            } else {
                mAnimator.setRepeatCount(1);
            }
            mAnimator.start();
        }
    }

    private void stopAnimator() {
        if (mAnimator != null) {
            mAnimator.removeAllListeners();
            mAnimator.end();
            mAnimator = null;
        }
    }

    private void pauseAnimator() {
        if (mAnimator != null && mAnimator.isRunning() && openAnimation) {
            mAnimator.pause();
        }
    }

    private void resumeAnimator() {
        if (mAnimator != null && mAnimator.isPaused() && openAnimation) {
            mAnimator.resume();
        }
    }

    private void updateImg(MusicBean bean) {
        if (bean == null) {
            return;
        }
        if (bean.getLocalMusic() == null) {
            bean.setLocalMusic(true);
        }

        if (bean.getLocalMusic()) {
            ImageUtil.loadImage(mContext, bean.getCoverImgUrl(), mMusicCoverImg, 500, 500,
                    new CircleCrop(), R.mipmap.logo);
            ImageUtil.loadImage(mContext, bean.getCoverImgUrl(), 100, 100, R.mipmap.main_play_bk,
                    new OnImageLoadListener() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            setUIColor(bitmap);
                        }
                    });
        }
        if (!bean.getLocalMusic()) {
            ImageUtil.loadImage(mContext, bean.getCoverImgUrl() + AppConstant.WY_IMG_400_400,
                    mMusicCoverImg, new CircleCrop(), R.mipmap.logo);
            ImageUtil.loadImage(mContext, bean.getCoverImgUrl() + AppConstant.WY_IMG_100_100, R.mipmap.main_play_bk,
                    new OnImageLoadListener() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            setUIColor(bitmap);
                        }
                    });
        }
    }

    private void setUIColor(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();
        int[] colorDatas = new int[3];
        colorDatas[0] = palette.getLightMutedColor(Color.parseColor("#F44336"));
        colorDatas[1] = palette.getMutedColor(Color.parseColor("#e91e63"));
        colorDatas[2] = palette.getDarkMutedColor(Color.parseColor("#673ab7"));
        mProgressBar.setPointerColor(colorDatas[1]);
        mProgressBar.setCircleProgressColor(colorDatas[1]);
        mProgressBar.setPointerColor(colorDatas[1]);
        mVerticalFFTView2.setColorData(colorDatas);
        mPathView.setPointColor(colorDatas[1]);
        imageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), NativeStackBlur.process(bitmap, 50)));
    }


}
