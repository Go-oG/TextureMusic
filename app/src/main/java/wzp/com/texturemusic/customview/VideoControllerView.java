package wzp.com.texturemusic.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import app.minimize.com.seek_bar_compat.SeekBarCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.util.BitmapUtil;
import wzp.com.texturemusic.util.LogUtil;

/**
 * Created by Wang on 2018/3/1.
 * 自定义的video播放控制按钮
 */
public class VideoControllerView extends ConstraintLayout {
    private static final String TAG = "VideoControllerView";
    public static final int PLAYSTATUS_PLAYING = 0;
    public static final int PLAYSTATUS_PAUSE = 1;
    @BindView(R.id.wzp_video_texture)
    TextureView mTextureView;
    @BindView(R.id.wzp_video_unexpand_nav_return_img)
    ImageView mUnexpandNavReturnImg;
    @BindView(R.id.wzp_video_unexpand_nav_tv)
    TextView mUnexpandNavTv;
    @BindView(R.id.wzp_video_unexpand_nav_operation_img)
    ImageView mUnexpandNavOperationImg;
    @BindView(R.id.wzp_video_unexpand_top_relative)
    RelativeLayout mUnexpandTopRelative;
    @BindView(R.id.wzp_video_expand_nav_return_img)
    ImageView mExpandNavReturnImg;
    @BindView(R.id.wzp_video_expand_nav_operation_img)
    ImageView mExpandNavOperationImg;
    @BindView(R.id.wzp_video_expand_nav_share_img)
    ImageView mExpandNavShareImg;
    @BindView(R.id.wzp_video_expand_nav_barrage_img)
    ImageView mExpandNavBarrageImg;
    @BindView(R.id.wzp_video_expand_top_relative)
    RelativeLayout mExpandTopRelative;
    @BindView(R.id.wzp_video_lock_screen)
    ImageView mLockScreenImg;
    @BindView(R.id.wzp_video_camera_img)
    ImageView mCameraImg;
    @BindView(R.id.wzp_video_gif_img)
    ImageView mGifImg;
    @BindView(R.id.wzp_video_expand_play_img)
    ImageView mExpandPlayImg;
    @BindView(R.id.wzp_video_expand_next_img)
    ImageView mExpandNextImg;
    @BindView(R.id.wzp_video_expand_unexpand_img)
    ImageView mExpandUnexpandImg;
    @BindView(R.id.wzp_video_expand_resolution_tv)
    TextView mExpandResolutionTv;
    @BindView(R.id.wzp_video_expand_starttime_tv)
    TextView mExpandStarttimeTv;
    @BindView(R.id.wzp_video_expand_endtime_tv)
    TextView mExpandEndtimeTv;
    @BindView(R.id.wzp_video_expand_seekbar)
    SeekBarCompat mExpandSeekbar;
    @BindView(R.id.wzp_video_expand_bottom_relative)
    RelativeLayout mExpandBottomRelative;
    @BindView(R.id.wzp_video_unexpand_play_img)
    ImageView mUnexpandPlayImg;
    @BindView(R.id.wzp_video_unexpand_starttime_endtime_tv)
    TextView mUnexpandStarttimeEndtimeTv;
    @BindView(R.id.wzp_video_unexpand_expand_img)
    ImageView mUnexpandExpandImg;
    @BindView(R.id.wzp_video_unexpand_seekbar)
    SeekBarCompat mUnexpandSeekbar;
    @BindView(R.id.wzp_video_unexpand_bottom_relative)
    RelativeLayout mUnexpandBottomRelative;
    @BindView(R.id.wzp_video_root_view)
    ConstraintLayout mRootView;
    @BindView(R.id.wzp_video_expand_nav_tv)
    TextView mExpandNavTv;
    @BindView(R.id.wzp_video_unexpand_next_img)
    ImageView mUnexpandNextImg;
    private OnVideoControlListener controlListener;
    private MyGestureListener gestureListener;
    private GestureDetector gestureDetector;
    /////////////////////////////////////////
    private Timer timer;

    private TimerTask timerTask;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null && msg.what == 1) {
                //隐藏view
                hintOrShowView(false, PLAYSTATUS_PAUSE,true);
            }
        }
    };
    //标识view是否被锁住了
    private boolean viewIsLock = false;
    private int playStatus = PLAYSTATUS_PAUSE;
    ////////////////////////////////////
    private boolean showLockScreenImg = true;
    private boolean showCameraImg = true;
    private boolean showGifImg = true;
    private boolean showBarrageImg = true;
    private boolean showShareImg = true;
    private boolean showExpandOperationImg = true;
    private boolean showUnExpandOperationImg = true;
    private boolean showUnExpandImg = true;
    private boolean showExpandImg = true;
    private boolean showUnExpandTimeTv = true;
    private boolean showExpandResolutionTv = true;
    private boolean showExpandStartTimeTv = true;
    private boolean showExpandEndTimeTv = true;
    private boolean showExpandNextImg = true;
    private boolean showUnExpandNextImg = true;

    //文本字体的颜色必须为AARRGGBB格式的
    private int expandNavTitleColor = 0xffffffff;
    private int expandStartTimeTvColor = 0xffffffff;
    private int expandEndTimeTvColor = 0xffffffff;
    private int expandResolutionTvColor = 0xffffffff;
    private int unExpandNavTitleColor = 0xffffffff;
    private int unExpandTimeTvColor = 0xffffffff;

    private int expandNavTitleSize = 15;
    private int expandStartTimeTvSize = 11;
    private int expandEndTimeTvSize = 11;
    private int expandResolutionTvSize = 15;
    private int unExpandNavTitleSize = 15;
    private int unExpandTimeTvSize = 11;

    private int expandNavReturnImgSrc = R.drawable.ic_nav_break;
    private int expandShareImgSrc = R.drawable.ic_share;
    private int expandOperationImgSrc = R.drawable.ic_video_more;
    private int expandBarrageImgSrc = R.drawable.ic_barrage_close;
    private int expandPlayImgSrc = R.drawable.exo_controls_play;
    private int expandNextImgSrc = R.drawable.exo_controls_next;
    private int lockScreenImgSrc = R.drawable.ic_video_open_lock;
    private int cameraImgSrc = R.drawable.ic_video_camera;
    private int gifImgSrc = R.drawable.ic_video_gif;
    private int unExpandNavReturnImgSrc = R.drawable.ic_nav_break;
    private int unExpandOperationImgSrc = R.drawable.ic_video_more;
    private int unExpandPlayImgSrc = R.drawable.exo_controls_play;
    private int unExpandNextImgSrc = R.drawable.exo_controls_next;
    private int expandImgSrc = R.drawable.ic_expand;
    private int unExpandImgSrc = R.drawable.ic_un_expand;
    //占位图
    private int coverPlaceholderImgSrc = 0;
    ///////////////////////////////////////
    //标识是展开还是缩小状态
    private boolean viewIsExpand = false;
    //多少秒后自动隐藏 单位毫秒
    private long autoHintTime = 5000;

    private float lastX, lastY;


    public VideoControllerView(Context context) {
        super(context);
        initView(context);
    }

    public VideoControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTypedArray(context, attrs);
        initView(context);
    }

    public VideoControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypedArray(context, attrs);
        initView(context);
    }

    private void initTypedArray(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.VideoControllerView);
        showLockScreenImg = mTypedArray.getBoolean(R.styleable.VideoControllerView_showLockScreenImg, true);
        showCameraImg = mTypedArray.getBoolean(R.styleable.VideoControllerView_showCameraImg, true);
        showGifImg = mTypedArray.getBoolean(R.styleable.VideoControllerView_showGifImg, true);
        showBarrageImg = mTypedArray.getBoolean(R.styleable.VideoControllerView_showBarrageImg, true);
        showShareImg = mTypedArray.getBoolean(R.styleable.VideoControllerView_showShareImg, true);
        showExpandOperationImg = mTypedArray.getBoolean(R.styleable.VideoControllerView_showExpandOperationImg, true);
        showUnExpandImg = mTypedArray.getBoolean(R.styleable.VideoControllerView_showUnExpandImg, true);
        showUnExpandTimeTv = mTypedArray.getBoolean(R.styleable.VideoControllerView_showUnExpandTimeTv, true);
        showExpandResolutionTv = mTypedArray.getBoolean(R.styleable.VideoControllerView_showExpandResolutionTv, true);
        showExpandStartTimeTv = mTypedArray.getBoolean(R.styleable.VideoControllerView_showExpandStartTimeTv, true);
        showExpandEndTimeTv = mTypedArray.getBoolean(R.styleable.VideoControllerView_showExpandEndTimeTv, true);
        showUnExpandNextImg = mTypedArray.getBoolean(R.styleable.VideoControllerView_showUnExpandNextImg, true);
        showExpandNextImg = mTypedArray.getBoolean(R.styleable.VideoControllerView_showExpandNextImg, true);

        expandNavTitleColor = mTypedArray.getColor(R.styleable.VideoControllerView_expandNavTitleColor, 0xffffffff);
        expandStartTimeTvColor = mTypedArray.getColor(R.styleable.VideoControllerView_expandStartTimeTvColor, 0xffffffff);
        expandEndTimeTvColor = mTypedArray.getColor(R.styleable.VideoControllerView_expandEndTimeTvColor, 0xffffffff);
        expandResolutionTvColor = mTypedArray.getColor(R.styleable.VideoControllerView_expandResolutionTvColor, 0xffffffff);
        unExpandNavTitleColor = mTypedArray.getColor(R.styleable.VideoControllerView_unExpandNavTitleColor, 0xffffffff);
        unExpandTimeTvColor = mTypedArray.getColor(R.styleable.VideoControllerView_unExpandTimeTvColor, 0xffffffff);

        expandNavTitleSize = mTypedArray.getDimensionPixelSize(R.styleable.VideoControllerView_expandNavTitleSize, 15);
        expandStartTimeTvSize = mTypedArray.getDimensionPixelSize(R.styleable.VideoControllerView_expandStartTimeTvSize, 11);
        expandEndTimeTvSize = mTypedArray.getDimensionPixelSize(R.styleable.VideoControllerView_expandEndTimeTvSize, 11);
        expandResolutionTvSize = mTypedArray.getDimensionPixelSize(R.styleable.VideoControllerView_expandResolutionTvSize, 15);
        unExpandNavTitleSize = mTypedArray.getDimensionPixelSize(R.styleable.VideoControllerView_unExpandNavTitleSize, 15);
        unExpandTimeTvSize = mTypedArray.getDimensionPixelSize(R.styleable.VideoControllerView_unExpandTimeTvSize, 11);

        expandNavReturnImgSrc = mTypedArray.getResourceId(R.styleable.VideoControllerView_expandNavReturnImgSrc, R.drawable.ic_nav_break);
        expandShareImgSrc = mTypedArray.getResourceId(R.styleable.VideoControllerView_expandShareImgSrc, R.drawable.ic_share);
        expandOperationImgSrc = mTypedArray.getResourceId(R.styleable.VideoControllerView_expandOperationImgSrc, R.drawable.ic_video_more);
        expandBarrageImgSrc = mTypedArray.getResourceId(R.styleable.VideoControllerView_expandBarrageImgSrc, R.drawable.ic_barrage_close);
        expandPlayImgSrc = mTypedArray.getResourceId(R.styleable.VideoControllerView_expandPlayImgSrc, R.drawable.exo_controls_play);
        expandNextImgSrc = mTypedArray.getResourceId(R.styleable.VideoControllerView_expandNextImgSrc, R.drawable.exo_controls_next);
        lockScreenImgSrc = mTypedArray.getResourceId(R.styleable.VideoControllerView_lockScreenImgSrc, R.drawable.ic_video_open_lock);
        cameraImgSrc = mTypedArray.getResourceId(R.styleable.VideoControllerView_cameraImgSrc, R.drawable.ic_video_camera);
        gifImgSrc = mTypedArray.getResourceId(R.styleable.VideoControllerView_gifImgSrc, R.drawable.ic_video_gif);
        unExpandNavReturnImgSrc = mTypedArray.getResourceId(R.styleable.VideoControllerView_unExpandNavReturnImgSrc, R.drawable.ic_nav_break);
        unExpandOperationImgSrc = mTypedArray.getResourceId(R.styleable.VideoControllerView_unExpandOperationImgSrc, R.drawable.ic_video_more);
        unExpandPlayImgSrc = mTypedArray.getResourceId(R.styleable.VideoControllerView_unExpandPlayImgSrc, R.drawable.exo_controls_play);
        unExpandNextImgSrc = mTypedArray.getResourceId(R.styleable.VideoControllerView_unExpandNextImgSrc, R.drawable.exo_controls_next);
        expandImgSrc = mTypedArray.getResourceId(R.styleable.VideoControllerView_expandImgSrc, R.drawable.ic_expand);
        unExpandImgSrc = mTypedArray.getResourceId(R.styleable.VideoControllerView_unExpandImgSrc, R.drawable.ic_un_expand);
        autoHintTime = mTypedArray.getInt(R.styleable.VideoControllerView_autoHintTime, 5000);
        coverPlaceholderImgSrc = mTypedArray.getResourceId(R.styleable.VideoControllerView_coverPlaceholderImgSrc, 0);
        mTypedArray.recycle();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(Context context) {
        View.inflate(context, R.layout.custom_video_control_view, this);
        ButterKnife.bind(this);
        mLockScreenImg.setVisibility(showLockScreenImg ? VISIBLE : GONE);
        mCameraImg.setVisibility(showCameraImg ? VISIBLE : GONE);
        mGifImg.setVisibility(showGifImg ? VISIBLE : GONE);
        mExpandNavBarrageImg.setVisibility(showBarrageImg ? VISIBLE : GONE);
        mExpandNavShareImg.setVisibility(showShareImg ? VISIBLE : GONE);
        mExpandNavOperationImg.setVisibility(showExpandOperationImg ? VISIBLE : GONE);
        mUnexpandNavOperationImg.setVisibility(showUnExpandOperationImg ? VISIBLE : GONE);
        mExpandUnexpandImg.setVisibility(showUnExpandImg ? VISIBLE : GONE);
        mUnexpandExpandImg.setVisibility(showExpandImg ? VISIBLE : GONE);
        mUnexpandStarttimeEndtimeTv.setVisibility(showUnExpandTimeTv ? VISIBLE : GONE);
        mExpandResolutionTv.setVisibility(showExpandResolutionTv ? VISIBLE : GONE);
        mExpandStarttimeTv.setVisibility(showExpandStartTimeTv ? VISIBLE : GONE);
        mExpandEndtimeTv.setVisibility(showExpandEndTimeTv ? VISIBLE : GONE);
        mExpandNextImg.setVisibility(showExpandNextImg ? VISIBLE : GONE);
        mUnexpandNextImg.setVisibility(showUnExpandNextImg ? VISIBLE : GONE);
        mExpandNavTv.setTextColor(expandNavTitleColor);
        mExpandStarttimeTv.setTextColor(expandStartTimeTvColor);
        mExpandEndtimeTv.setTextColor(expandEndTimeTvColor);
        mExpandResolutionTv.setTextColor(expandResolutionTvColor);
        mUnexpandNavTv.setTextColor(unExpandNavTitleColor);
        mUnexpandStarttimeEndtimeTv.setTextColor(unExpandTimeTvColor);
        mExpandNavTv.setTextSize(expandNavTitleSize);
        mExpandStarttimeTv.setTextSize(expandStartTimeTvSize);
        mExpandEndtimeTv.setTextSize(expandEndTimeTvSize);
        mExpandResolutionTv.setTextSize(expandResolutionTvSize);
        mUnexpandNavTv.setTextSize(unExpandNavTitleSize);
        mUnexpandStarttimeEndtimeTv.setTextSize(unExpandTimeTvSize);
        mExpandNavReturnImg.setImageResource(expandNavReturnImgSrc);
        mExpandNavShareImg.setImageResource(expandShareImgSrc);
        mExpandNavOperationImg.setImageResource(expandOperationImgSrc);
        mExpandNavBarrageImg.setImageResource(expandBarrageImgSrc);
        mExpandPlayImg.setImageResource(expandPlayImgSrc);
        mExpandNextImg.setImageResource(expandNextImgSrc);
        mLockScreenImg.setImageResource(lockScreenImgSrc);
        mCameraImg.setImageResource(cameraImgSrc);
        mGifImg.setImageResource(gifImgSrc);
        mUnexpandNavReturnImg.setImageResource(unExpandNavReturnImgSrc);
        mUnexpandNavOperationImg.setImageResource(unExpandOperationImgSrc);
        mUnexpandPlayImg.setImageResource(unExpandPlayImgSrc);
        mUnexpandNextImg.setImageResource(unExpandNextImgSrc);
        mUnexpandExpandImg.setImageResource(expandImgSrc);
        mExpandUnexpandImg.setImageResource(unExpandImgSrc);
        if (coverPlaceholderImgSrc != 0) {
            mRootView.setBackgroundResource(coverPlaceholderImgSrc);
        }
        gestureListener = new MyGestureListener();
        gestureDetector = new GestureDetector(context, gestureListener);
        gestureDetector.setIsLongpressEnabled(true);
        mExpandSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && controlListener != null) {
                    controlListener.onProgressChange(true, false);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (controlListener != null) {
                    controlListener.onProgressChange(true, true);
                }
            }
        });

        mUnexpandSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && controlListener != null) {
                    controlListener.onProgressChange(false, false);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (controlListener != null) {
                    controlListener.onProgressChange(true, true);
                }
            }
        });
        mRootView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        if (mUnexpandTopRelative.getVisibility() == VISIBLE) {
            initTimeThread();
        }
    }

    public boolean isViewIsLock() {
        return viewIsLock;
    }

    public void setViewIsLock(boolean viewIsLock) {
        this.viewIsLock = viewIsLock;
    }

    public boolean isViewIsExpand() {
        return viewIsExpand;
    }

    public void setViewIsExpand(boolean viewIsExpand) {
        this.viewIsExpand = viewIsExpand;
        hintOrShowView(true, playStatus,true);
    }

    /**
     * 设置好久自动隐藏
     *
     * @param autoHintTime 单位毫秒
     */
    public void setAutoHintTime(long autoHintTime) {
        this.autoHintTime = autoHintTime;
    }

    public void setCoverPlaceholderImgSrc(int resouceId) {
        this.coverPlaceholderImgSrc = resouceId;
        mRootView.setBackgroundResource(resouceId);
    }

    public void setCoverPlaceholderImgSrc(Drawable drawable) {
        mRootView.setBackground(drawable);
    }

    public void setCoverPlaceholderImgSrc(Context c, Bitmap bitmap) {
        mRootView.setBackground(BitmapUtil.bitmapToDrawable(c, bitmap));
    }

    public void setControlListener(OnVideoControlListener controlListener) {
        this.controlListener = controlListener;
    }

    public void setShowLockScreenImg(boolean showLockScreenImg) {
        this.showLockScreenImg = showLockScreenImg;
        mLockScreenImg.setVisibility(showLockScreenImg ? VISIBLE : GONE);
    }

    public void setShowCameraImg(boolean showCameraImg) {
        this.showCameraImg = showCameraImg;
        mCameraImg.setVisibility(showCameraImg ? VISIBLE : GONE);
    }

    public void setShowGifImg(boolean showGifImg) {
        this.showGifImg = showGifImg;
        mGifImg.setVisibility(showGifImg ? VISIBLE : GONE);
    }

    public void setShowBarrageImg(boolean showBarrageImg) {
        this.showBarrageImg = showBarrageImg;
        mExpandNavBarrageImg.setVisibility(showBarrageImg ? VISIBLE : GONE);
    }

    public void setShowShareImg(boolean showShareImg) {
        this.showShareImg = showShareImg;
        mExpandNavShareImg.setVisibility(showShareImg ? VISIBLE : GONE);
    }

    public void setShowExpandOperationImg(boolean showExpandOperationImg) {
        this.showExpandOperationImg = showExpandOperationImg;
        mExpandNavOperationImg.setVisibility(showExpandOperationImg ? VISIBLE : GONE);
    }

    public void setShowUnExpandOperationImg(boolean showUnExpandOperationImg) {
        this.showUnExpandOperationImg = showUnExpandOperationImg;
        mUnexpandNavOperationImg.setVisibility(showUnExpandOperationImg ? VISIBLE : GONE);
    }

    public void setShowUnExpandImg(boolean showUnExpandImg) {
        this.showUnExpandImg = showUnExpandImg;
        mExpandUnexpandImg.setVisibility(showUnExpandImg ? VISIBLE : GONE);
    }

    public void setShowExpandImg(boolean showExpandImg) {
        this.showExpandImg = showExpandImg;
        mUnexpandExpandImg.setVisibility(showExpandImg ? VISIBLE : GONE);
    }

    public void setShowUnExpandTimeTv(boolean showUnExpandTimeTv) {
        this.showUnExpandTimeTv = showUnExpandTimeTv;
        mUnexpandStarttimeEndtimeTv.setVisibility(showUnExpandTimeTv ? VISIBLE : GONE);
    }

    public void setShowExpandResolutionTv(boolean showExpandResolutionTv) {
        this.showExpandResolutionTv = showExpandResolutionTv;
        mExpandResolutionTv.setVisibility(showExpandResolutionTv ? VISIBLE : GONE);
    }

    public void setShowExpandStartTimeTv(boolean showExpandStartTimeTv) {
        this.showExpandStartTimeTv = showExpandStartTimeTv;
        mExpandStarttimeTv.setVisibility(showExpandStartTimeTv ? VISIBLE : GONE);
    }

    public void setShowExpandEndTimeTv(boolean showExpandEndTimeTv) {
        this.showExpandEndTimeTv = showExpandEndTimeTv;
        mExpandEndtimeTv.setVisibility(showExpandEndTimeTv ? VISIBLE : GONE);
    }

    public void setExpandNavTitleColor(int expandNavTitleColor) {
        this.expandNavTitleColor = expandNavTitleColor;
        mExpandNavTv.setTextColor(expandNavTitleColor);

    }

    public void setExpandStartTimeTvColor(int expandStartTimeTvColor) {
        this.expandStartTimeTvColor = expandStartTimeTvColor;
        mExpandStarttimeTv.setTextColor(expandStartTimeTvColor);
    }

    public void setExpandEndTimeTvColor(int expandEndTimeTvColor) {
        this.expandEndTimeTvColor = expandEndTimeTvColor;
        mExpandEndtimeTv.setTextColor(expandEndTimeTvColor);
    }

    public void setExpandResolutionTvColor(int expandResolutionTvColor) {
        this.expandResolutionTvColor = expandResolutionTvColor;
        mExpandResolutionTv.setTextColor(expandResolutionTvColor);
    }

    public void setUnExpandNavTitleColor(int unExpandNavTitleColor) {
        this.unExpandNavTitleColor = unExpandNavTitleColor;
        mUnexpandNavTv.setTextColor(unExpandNavTitleColor);
    }

    public void setUnExpandTimeTvColor(int unExpandTimeTvColor) {
        this.unExpandTimeTvColor = unExpandTimeTvColor;
        mUnexpandStarttimeEndtimeTv.setTextColor(unExpandTimeTvColor);
    }

    public void setExpandNavTitleSize(int expandNavTitleSize) {
        this.expandNavTitleSize = expandNavTitleSize;
        mExpandNavTv.setTextSize(expandNavTitleSize);
    }

    public void setExpandStartTimeTvSize(int expandStartTimeTvSize) {
        this.expandStartTimeTvSize = expandStartTimeTvSize;
        mExpandStarttimeTv.setTextSize(expandStartTimeTvSize);
    }

    public void setExpandEndTimeTvSize(int expandEndTimeTvSize) {
        this.expandEndTimeTvSize = expandEndTimeTvSize;
        mExpandEndtimeTv.setTextSize(expandEndTimeTvSize);
    }

    public void setExpandResolutionTvSize(int expandResolutionTvSize) {
        this.expandResolutionTvSize = expandResolutionTvSize;
        mExpandResolutionTv.setTextSize(expandResolutionTvSize);
    }

    public void setUnExpandNavTitleSize(int unExpandNavTitleSize) {
        this.unExpandNavTitleSize = unExpandNavTitleSize;
        mUnexpandNavTv.setTextSize(unExpandNavTitleSize);
    }

    public void setUnExpandTimeTvSize(int unExpandTimeTvSize) {
        this.unExpandTimeTvSize = unExpandTimeTvSize;
        mUnexpandStarttimeEndtimeTv.setTextSize(unExpandTimeTvSize);
    }

    public void setExpandNavReturnImgSrc(int expandNavReturnImgSrc) {
        this.expandNavReturnImgSrc = expandNavReturnImgSrc;
        mExpandNavReturnImg.setImageResource(expandNavReturnImgSrc);
    }

    public void setExpandShareImgSrc(int expandShareImgSrc) {
        this.expandShareImgSrc = expandShareImgSrc;
        mExpandNavShareImg.setImageResource(expandShareImgSrc);
    }

    public void setOperationImg(int imgsouceId){
        setExpandOperationImgSrc(imgsouceId);
        setUnExpandOperationImgSrc(imgsouceId);
    }
    public void setExpandOperationImgSrc(int expandOperationImgSrc) {
        this.expandOperationImgSrc = expandOperationImgSrc;
        mExpandNavOperationImg.setImageResource(expandOperationImgSrc);
    }

    public void setExpandBarrageImgSrc(int expandBarrageImgSrc) {
        this.expandBarrageImgSrc = expandBarrageImgSrc;
        mExpandNavBarrageImg.setImageResource(expandBarrageImgSrc);
    }

    public void setExpandPlayImgSrc(int expandPlayImgSrc) {
        this.expandPlayImgSrc = expandPlayImgSrc;
        mExpandPlayImg.setImageResource(expandPlayImgSrc);
    }

    public void setExpandNextImgSrc(int expandNextImgSrc) {
        this.expandNextImgSrc = expandNextImgSrc;
        mExpandNextImg.setImageResource(expandNextImgSrc);
    }

    public void setLockScreenImgSrc(int lockScreenImgSrc) {
        this.lockScreenImgSrc = lockScreenImgSrc;
        mLockScreenImg.setImageResource(lockScreenImgSrc);
    }

    public void setCameraImgSrc(int cameraImgSrc) {
        this.cameraImgSrc = cameraImgSrc;
        mCameraImg.setImageResource(cameraImgSrc);
    }

    public void setGifImgSrc(int gifImgSrc) {
        this.gifImgSrc = gifImgSrc;
        mGifImg.setImageResource(gifImgSrc);
    }

    public void setUnExpandNavReturnImgSrc(int unExpandNavReturnImgSrc) {
        this.unExpandNavReturnImgSrc = unExpandNavReturnImgSrc;
        mUnexpandNavReturnImg.setImageResource(unExpandNavReturnImgSrc);
    }

    public void setUnExpandOperationImgSrc(int unExpandOperationImgSrc) {
        this.unExpandOperationImgSrc = unExpandOperationImgSrc;
        mUnexpandNavOperationImg.setImageResource(unExpandOperationImgSrc);
    }

    public void setUnExpandPlayImgSrc(int unExpandPlayImgSrc) {
        this.unExpandPlayImgSrc = unExpandPlayImgSrc;
        mUnexpandPlayImg.setImageResource(unExpandPlayImgSrc);
    }

    public void setUnExpandNextImgSrc(int unExpandNextImgSrc) {
        this.unExpandNextImgSrc = unExpandNextImgSrc;
        mUnexpandNextImg.setImageResource(unExpandNextImgSrc);
    }

    public void setExpandImgSrc(int expandImgSrc) {
        this.expandImgSrc = expandImgSrc;
        mUnexpandExpandImg.setImageResource(expandImgSrc);
    }

    public void setUnExpandImgSrc(int unExpandImgSrc) {
        this.unExpandImgSrc = unExpandImgSrc;
        mExpandUnexpandImg.setImageResource(unExpandImgSrc);
    }

    public void setUnexpandNavTvText(String text) {
        mUnexpandNavTv.setText(text);
    }

    public void setExpandNavTvText(String text) {
        mExpandNavTv.setText(text);
    }

    public void setmUnexpandTimeTvText(String text) {
        mUnexpandStarttimeEndtimeTv.setText(text);
    }

    public void setmExpandStarttimeTvText(String text) {
        mExpandStarttimeTv.setText(text);
    }

    public void setmExpandEndtimeTvText(String text) {
        mExpandEndtimeTv.setText(text);
    }

    public void setProgress(int val, int max) {
        mExpandSeekbar.setMax(max);
        mUnexpandSeekbar.setMax(max);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mExpandSeekbar.setProgress(val, true);
            mUnexpandSeekbar.setProgress(val, true);
        } else {
            mExpandSeekbar.setProgress(val);
            mUnexpandSeekbar.setProgress(val);
        }
    }

    public TextureView getTextureView() {
        return mTextureView;
    }

    public SeekBarCompat getSeekBarCompat() {
        if (mUnexpandBottomRelative.getVisibility() == VISIBLE) {
            return mUnexpandSeekbar;
        } else {
            return mExpandSeekbar;
        }
    }

    public void setPlayImgSrc(int src) {
        mExpandPlayImg.setImageResource(src);
        mUnexpandPlayImg.setImageResource(src);
    }

    public void setTimeTv(String startTime, String endTime) {
        mUnexpandStarttimeEndtimeTv.setText(startTime + "/" + endTime);
        mExpandStarttimeTv.setText(startTime);
        mExpandEndtimeTv.setText(endTime);

    }

    public void setNavTitle(String title) {
        mExpandNavTv.setText(title);
        mUnexpandNavTv.setText(title);
    }

    public void setResolutionTv(String tv) {
        mExpandResolutionTv.setText(tv);
    }

    public TextView getResolutionTv() {
        return mExpandResolutionTv;
    }

    @OnClick({R.id.wzp_video_unexpand_nav_return_img, R.id.wzp_video_unexpand_nav_operation_img, R.id.wzp_video_unexpand_next_img,
            R.id.wzp_video_expand_nav_return_img, R.id.wzp_video_expand_nav_operation_img, R.id.wzp_video_expand_nav_share_img,
            R.id.wzp_video_expand_nav_barrage_img, R.id.wzp_video_lock_screen, R.id.wzp_video_camera_img, R.id.wzp_video_gif_img,
            R.id.wzp_video_expand_play_img, R.id.wzp_video_expand_next_img, R.id.wzp_video_expand_unexpand_img, R.id.wzp_video_unexpand_play_img,
            R.id.wzp_video_unexpand_expand_img, R.id.wzp_video_expand_resolution_tv})
    public void onViewClicked(View view) {
        if (controlListener != null) {
            switch (view.getId()) {
                case R.id.wzp_video_unexpand_nav_return_img:
                    controlListener.onReturn(false);
                    break;
                case R.id.wzp_video_unexpand_nav_operation_img:
                    controlListener.onOperation(false);
                    break;
                case R.id.wzp_video_expand_nav_return_img:
                    controlListener.onReturn(true);
                    break;
                case R.id.wzp_video_expand_nav_operation_img:
                    controlListener.onOperation(true);
                    break;
                case R.id.wzp_video_expand_nav_share_img:
                    controlListener.onShare();
                    break;
                case R.id.wzp_video_expand_nav_barrage_img:
                    controlListener.onBarrage();
                    break;
                case R.id.wzp_video_lock_screen:
                    controlListener.onLockScreen();
                    break;
                case R.id.wzp_video_camera_img:
                    controlListener.onCamera();
                    break;
                case R.id.wzp_video_gif_img:
                    controlListener.onGif();
                    break;
                case R.id.wzp_video_expand_play_img:
                    controlListener.onVideoPlay(true);
                    break;
                case R.id.wzp_video_expand_next_img:
                    controlListener.onVideoNext(true);
                    break;
                case R.id.wzp_video_expand_unexpand_img:
                    viewIsExpand = false;
                    controlListener.onUnExpand();
                    break;
                case R.id.wzp_video_unexpand_play_img:
                    controlListener.onVideoPlay(false);
                    break;
                case R.id.wzp_video_unexpand_expand_img:
                    viewIsExpand = true;
                    controlListener.onExpand();
                    break;
                case R.id.wzp_video_unexpand_next_img:
                    controlListener.onVideoNext(false);
                    break;
                case R.id.wzp_video_expand_resolution_tv:
                    controlListener.onResolution();
                    break;
            }
        }
    }

    public interface OnVideoControlListener {
        void onReturn(boolean expand);

        void onLockScreen();

        void onCamera();

        void onGif();

        void onVideoPlay(boolean expand);

        void onVideoNext(boolean expand);

        void onBarrage();

        void onShare();

        void onOperation(boolean expand);

        void onResolution();

        /**
         * @param isUp                是不是向上滑动
         * @param distance            表示相对于上一个滑动点的滑动距离 有正负之分
         * @param distancePercent     表示滑动距离站整个View高度或者宽度的百分比
         * @param isContinueDirection 表示滑动过程中是不是相对上一个方向为同一个方向
         */
        void onLeftVerticalScroll(boolean isUp, float distance, float distancePercent, boolean isContinueDirection);

        void onRightVerticalScroll(boolean isUp, float distance, float distancePercent, boolean isContinueDirection);

        /**
         * 水平方向滑动回调该方法
         *
         * @param distance 表示相对于上一个滑动点的滑动距离 有正负之分
         * @param isScroll 表示是不是在OnScroll中回调还是在onFling中回调
         */
        void onLeftRight(float distance, boolean isScroll);

        void onRightLeft(float distance, boolean isScroll);

        void onExpand();

        void onUnExpand();

        void onDoubleClick();

        //长按
        void onLongPress();

        void onProgressChange(boolean expand, boolean isTouchStop);

        void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        void onDown();

        void onViewVisibilityChange(boolean visibility);

        void onDownUp();

    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (controlListener == null) {
                return false;
            }
            float y1 = e1.getY();//按下的点
            float y2 = e2.getY();//释放后的点
            float x1 = e1.getX();
            float x2 = e2.getX();
            boolean isY = (Math.abs(y1 - y2) >= Math.abs(x1 - x2));
            float distanceX = x2 - x1;
            if (!isY) {
                if (x2 > x1) {
                    //向右划
                    controlListener.onLeftRight(distanceX, false);
                } else {
                    //向左划
                    controlListener.onRightLeft(distanceX, false);
                }
            }
            controlListener.onDownUp();
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (controlListener == null) {
                return false;
            }
            controlListener.onScroll(e1, e2, distanceX, distanceY);
            int textureHeight = mTextureView.getHeight();
            float y1 = e1.getY();//按下的点
            float y2 = e2.getY();// 滚动中的点
            float x1 = e1.getX();
            float x2 = e2.getX();
            boolean isY = (Math.abs(y1 - y2) >= Math.abs(e1.getX() - e2.getX()));
            //是不是在左边滑动
            int rootWidth = mRootView.getWidth() / 2;
            boolean isLeftScroll = e1.getX() < rootWidth && e2.getX() < rootWidth;
            //同方向上划
            boolean twoUp = y1 > lastY && lastY > y2;//y1>lastY>y2
            boolean downUp = lastY > y1 && y1 > y2;//lastY>y1>y2
            boolean upDown = y2 > y1 && y1 > lastY;//y2>y1>lastY
            boolean twoDown = y2 > lastY && lastY > y1;//y2>lastY>y1
            //有正负之分 正的表示在按压的点的下方
            float yDistance = y2 - y1;
            float yPercent = Math.abs(yDistance) / (float) textureHeight;
            if (isY) {
                if (isLeftScroll) {
                    if (y2 != lastY) {
                        lastY = y2;
                        if (twoUp) {
                            //同时上划
                            controlListener.onLeftVerticalScroll(true, distanceY, yPercent, true);
                        }
                        if (downUp) {
                            //先下划在上划
                            controlListener.onLeftVerticalScroll(true, distanceY, yPercent, false);
                        }
                        if (upDown) {
                            //先上划在下划
                            controlListener.onLeftVerticalScroll(false, distanceY, yPercent, false);
                        }
                        if (twoDown) {
                            //同方向下划
                            controlListener.onLeftVerticalScroll(false, distanceY, yPercent, true);
                        }

                    }
                }
                if (!isLeftScroll) {
                    if (y2 != lastY) {
                        lastY = y2;
                        if (twoUp) {
                            //同时上划
                            controlListener.onRightVerticalScroll(true, distanceY, yPercent, true);
                        }
                        if (downUp) {
                            //先下划在上划
                            controlListener.onRightVerticalScroll(true, distanceY, yPercent, false);
                        }
                        if (upDown) {
                            //先上划在下划
                            controlListener.onRightVerticalScroll(false, distanceY, yPercent, false);
                        }
                        if (twoDown) {
                            //同方向下划
                            controlListener.onRightVerticalScroll(false, distanceY, yPercent, false);
                        }
                    }
                }
            }
            if (!isY) {
                //X轴方向的操作
                float XVal = e2.getX() - e1.getX();
                if (x2 > x1) {
                    //向右划
                    controlListener.onLeftRight(XVal, true);
                } else {
                    //向左划
                    controlListener.onRightLeft(XVal, true);
                }
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (controlListener != null) {
                controlListener.onDoubleClick();
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (controlListener != null) {
                controlListener.onLongPress();
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //为点按操作
            if (viewIsLock) {
                if ((mUnexpandSeekbar.getVisibility() == VISIBLE || mExpandSeekbar.getVisibility() == VISIBLE)) {
                    hintOrShowView(false, playStatus,true);
                } else {
                    hintOrShowView(true, playStatus,true);
                }
            } else {
                if ((mExpandTopRelative.getVisibility() == VISIBLE || mUnexpandTopRelative.getVisibility() == VISIBLE)) {
                    hintOrShowView(false, playStatus,true);
                } else {
                    hintOrShowView(true, playStatus,true);
                }
            }
            controlListener.onDown();
            return false;
        }


    }

    /**
     * 用于减少接口数量
     */
    public static class AbstractVideoControlAdapter implements OnVideoControlListener {

        @Override
        public void onLeftVerticalScroll(boolean isUp, float distance, float distancePercent, boolean isContinueDirection) {

        }

        @Override
        public void onRightVerticalScroll(boolean isUp, float distance, float distancePercent, boolean isContinueDirection) {

        }

        @Override
        public void onReturn(boolean expand) {
            LogUtil.d(TAG, "onReturn()");
        }

        @Override
        public void onLockScreen() {
            LogUtil.d(TAG, "onLockScreen()");
        }

        @Override
        public void onCamera() {
            LogUtil.d(TAG, "onCamera()");
        }

        @Override
        public void onGif() {
            LogUtil.d(TAG, "onGif()");
        }

        @Override
        public void onVideoPlay(boolean expand) {
            LogUtil.d(TAG, "onVideoPlay()");
        }

        @Override
        public void onVideoNext(boolean expand) {
            LogUtil.d(TAG, "onVideoNext()");
        }

        @Override
        public void onBarrage() {
            LogUtil.d(TAG, "onBarrage()");
        }

        @Override
        public void onShare() {
            LogUtil.d(TAG, "onShare()");
        }

        @Override
        public void onOperation(boolean expand) {
            LogUtil.d(TAG, "onOperation()");
        }

        @Override
        public void onResolution() {
            LogUtil.d(TAG, "onResolution()");
        }


        @Override
        public void onLeftRight(float distance, boolean isScroll) {
            LogUtil.d(TAG, "onLeftRight()");
        }

        @Override
        public void onRightLeft(float distance, boolean isScroll) {
            LogUtil.d(TAG, "onRightLeft()");
        }

        @Override
        public void onExpand() {
            LogUtil.d(TAG, "onExpand()");
        }

        @Override
        public void onUnExpand() {
            LogUtil.d(TAG, "onUnExpand()");
        }

        @Override
        public void onDoubleClick() {
            LogUtil.d(TAG, "onDoubleClick()");
        }

        @Override
        public void onLongPress() {
            LogUtil.d(TAG, "onLongPress()");
        }

        @Override
        public void onProgressChange(boolean expand, boolean isTouchStop) {
            LogUtil.d(TAG, "onProgressChange()");
        }

        @Override
        public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            LogUtil.d(TAG, "onScroll()");
        }

        @Override
        public void onDown() {
        }

        @Override
        public void onViewVisibilityChange(boolean visibility) {

        }

        @Override
        public void onDownUp() {

        }
    }

    /**
     * 用于控制展开和关闭时view的可见性
     */
    public void hintOrShowView(boolean show, int playState,boolean audioHind) {
        this.playStatus = playState;
        if (show) {
            if (viewIsLock) {
                //锁住屏幕
                mExpandBottomRelative.setVisibility(VISIBLE);
                mUnexpandBottomRelative.setVisibility(VISIBLE);
                mExpandTopRelative.setVisibility(GONE);
                mUnexpandTopRelative.setVisibility(GONE);
                mExpandNavTv.setVisibility(GONE);
                mExpandPlayImg.setVisibility(GONE);
                mExpandNextImg.setVisibility(GONE);
                mExpandStarttimeTv.setVisibility(GONE);
                mExpandEndtimeTv.setVisibility(GONE);
                mExpandResolutionTv.setVisibility(GONE);
                if (viewIsExpand) {
                    mExpandSeekbar.setVisibility(VISIBLE);
                    mUnexpandSeekbar.setVisibility(GONE);
                } else {
                    mExpandSeekbar.setVisibility(GONE);
                    mUnexpandSeekbar.setVisibility(VISIBLE);
                }
                mCameraImg.setVisibility(GONE);
                mGifImg.setVisibility(GONE);
            } else {
                //没有锁住屏幕
                mExpandBottomRelative.setVisibility(viewIsExpand ? VISIBLE : GONE);
                mExpandTopRelative.setVisibility(viewIsExpand ? VISIBLE : GONE);
                mUnexpandBottomRelative.setVisibility(viewIsExpand ? GONE : VISIBLE);
                mUnexpandTopRelative.setVisibility(viewIsExpand ? GONE : VISIBLE);
                mExpandNavTv.setVisibility(viewIsExpand ? VISIBLE : GONE);
                mExpandPlayImg.setVisibility(viewIsExpand ? VISIBLE : GONE);
                mExpandNextImg.setVisibility(viewIsExpand && showExpandNextImg ? VISIBLE : GONE);
                mExpandStarttimeTv.setVisibility(viewIsExpand && showExpandStartTimeTv ? VISIBLE : GONE);
                mExpandEndtimeTv.setVisibility(viewIsExpand && showExpandEndTimeTv ? VISIBLE : GONE);
                mExpandResolutionTv.setVisibility(viewIsExpand && showExpandResolutionTv ? VISIBLE : GONE);
                mUnexpandStarttimeEndtimeTv.setVisibility((!viewIsExpand) ? VISIBLE : GONE);
                mUnexpandNextImg.setVisibility((!viewIsExpand) && showUnExpandNextImg ? VISIBLE : GONE);
                mUnexpandExpandImg.setVisibility(!(viewIsExpand) && showExpandImg ? VISIBLE : GONE);
                if (playState == PLAYSTATUS_PLAYING) {
                    setPlayImgSrc(R.drawable.exo_controls_pause);
                } else {
                    setPlayImgSrc(R.drawable.exo_controls_play);
                }
            }
            cancleTimeThread();
            if (audioHind){
                initTimeThread();
            }
            if (controlListener != null) {
                controlListener.onViewVisibilityChange(true);
            }
        } else {
            mExpandBottomRelative.setVisibility(GONE);
            mExpandTopRelative.setVisibility(GONE);
            mUnexpandBottomRelative.setVisibility(GONE);
            mUnexpandTopRelative.setVisibility(GONE);
            if (controlListener != null) {
                controlListener.onViewVisibilityChange(false);
            }
        }
    }

    /**
     * 初始化定时线程
     */
    private void initTimeThread() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, autoHintTime);
    }

    private void cancleTimeThread() {
        if (timer != null) {
            timer.cancel();
        }
    }

}
