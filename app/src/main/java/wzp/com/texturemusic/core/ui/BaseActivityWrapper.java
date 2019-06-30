package wzp.com.texturemusic.core.ui;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.freedom.lauzy.playpauseviewlib.PlayPauseView;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.ShareBean;
import wzp.com.texturemusic.common.popwindow.PlayQueuePopwindow;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.eventlistener.EngineEvent;
import wzp.com.texturemusic.customview.CarouselTextView;
import wzp.com.texturemusic.interf.OnDeleteListener;
import wzp.com.texturemusic.localmodule.ui.MainPlayActivity;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.ImageUtil;


/**
 * Created by Wang on 2018/4/1.
 * BaseActivity的再次封装
 */

public abstract class BaseActivityWrapper extends BaseActivity {
    private View mStackView;
    private LinearLayout mContentLinear;
    private ImageView mBottomMusicImg;
    private CarouselTextView mBottomAlbumTv;
    private TextView mBottomMusicNameTv;
    private ImageView mBottomNextImg;
    private PlayPauseView mBottomPlayImg;
    private FrameLayout mBottomFrame;
    private ViewStub mBottomViewStub;
    //标识是否显示底部Bottom控制按钮
    protected boolean isShowBottomView = true;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_base_wrapper);
        mStackView = findViewById(R.id.m_stack_view);
        mContentLinear = findViewById(R.id.m_content_Linear);
        mBottomFrame = findViewById(R.id.m_bottom_frame);
        initStatckbar();
        //中间实际的view
        LayoutInflater.from(this).inflate(layoutResID, mContentLinear);
        if (isShowBottomView) {
            initBottomView();
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBottomFrame.getLayoutParams();
            params.height = 0;
            mBottomFrame.setLayoutParams(params);
            mBottomFrame.setVisibility(View.GONE);
        }
    }

    /**
     * 该方法只在显示底部布局时才会调用
     */
    @SuppressLint("CheckResult")
    private void initBottomView() {
        mBottomFrame.setVisibility(View.VISIBLE);
        mBottomViewStub = findViewById(R.id.m_bottom_viewStub);
        mBottomViewStub.setVisibility(View.VISIBLE);
        mBottomMusicImg = findViewById(R.id.comment_bottom_music_img);
        mBottomAlbumTv = findViewById(R.id.comment_bottom_album_tv);
        mBottomMusicNameTv = findViewById(R.id.comment_bottom_music_name_tv);
        mBottomNextImg = findViewById(R.id.comment_bottom_next_img);
        mBottomPlayImg = findViewById(R.id.comment_bottom_play_img);
        RxView.clicks(mBottomMusicImg)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (isShowBottomView) {
                            //开启动画
                            Pair<View, String> pair1 = new Pair<View, String>(mBottomMusicImg, getString(R.string.sharemusicimg));
                            Pair<View, String> pair4 = new Pair<View, String>(mBottomPlayImg, getString(R.string.shareplaybtn));
                            Pair<View, String> pair5 = new Pair<View, String>(mBottomNextImg, getString(R.string.sharenextbtn));
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(BaseActivityWrapper.this,
                                    pair1, pair4, pair5);
                            Intent intent = new Intent(mContext, MainPlayActivity.class);
                            startActivity(intent, options.toBundle());
                        } else {
                            Intent intent = new Intent(mContext, MainPlayActivity.class);
                            startActivity(intent);
                        }
                    }
                });
        RxView.clicks(mBottomNextImg)
                .throttleFirst(300, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        nextMusic();
                    }
                });
        RxView.clicks(mBottomPlayImg)
                .throttleFirst(300, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        int state = getNowPlayState();
                        if (state == EngineEvent.STATUS_PLAYING) {
                            pauseMusic();
                            if (mBottomPlayImg.isPlaying()) {
                                mBottomPlayImg.pause();
                            }
                        } else if (state == EngineEvent.STATUS_PAUSE) {
                            resumeMusic();
                            if (!mBottomPlayImg.isPlaying()) {
                                mBottomPlayImg.play();
                            }
                        } else {
                            playMusic();
                            if (!mBottomPlayImg.isPlaying()) {
                                mBottomPlayImg.play();
                            }
                        }
                    }
                });
    }

    private void initStatckbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int statckbarHight = BaseUtil.getStackBarHight();
            ViewGroup.LayoutParams params = mStackView.getLayoutParams();
            params.height = statckbarHight;
            mStackView.setLayoutParams(params);
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onPlayWarn(String warnType, String warnHint) {
        int playStatus = getNowPlayState();
        if (playStatus == EngineEvent.STATUS_PLAYING) {
            if (isShowBottomView && mBottomPlayImg != null && !mBottomPlayImg.isPlaying()) {
                mBottomPlayImg.play();
            }
        } else {
            mBottomPlayImg.pause();
        }
    }

    @Override
    protected void onServiceDestroyed() {
        super.onServiceDestroyed();
        if (mBottomPlayImg.isPlaying()) {
            mBottomPlayImg.pause();
        }
    }

    @Override
    protected void onLyricChange(MusicBean bean, String lyrics) {
        super.onLyricChange(bean, lyrics);
        if (isShowBottomView && (!mBottomAlbumTv.getText().toString().equals(lyrics))) {
            mBottomAlbumTv.setText(lyrics);
        }
    }

    public void shareMusic(final MusicBean bean) {
        ShareBean shareBean = new ShareBean();
        shareBean.setTitle(bean.getMusicName());
        shareBean.setText(bean.getArtistName() + "-" + bean.getAlbumName());
        if (bean.getLocalMusic()) {
            shareBean.setImgpath(bean.getCoverImgUrl());
            shareBean.setFileUrl(bean.getPlayPath());
        } else {
            shareBean.setImgUrl(bean.getCoverImgUrl());
            shareBean.setWebUrl("http://music.163.com/#/song?id=" + bean.getMusicId());
        }
        shareMusic(shareBean);
    }

    @Override
    public void setStatusBarHeight(int height) {
        if (mStackView != null) {
            ViewGroup.LayoutParams params = mStackView.getLayoutParams();
            params.height = height;
            mStackView.setLayoutParams(params);
        }
    }

    @Override
    protected void updateBottomInfo(MusicBean bean) {
        if (bean == null) {
            if (mBottomViewStub == null) {
                return;
            }
            if (mBottomViewStub.getVisibility() == View.VISIBLE || isShowBottomView) {
                mBottomMusicNameTv.setText("未知");
                mBottomAlbumTv.setText("未知");
                ImageUtil.loadImage(mContext, R.mipmap.logo, mBottomMusicImg,
                        200, 200, new CircleCrop(), R.mipmap.logo);
            }
            return;
        }
        if (isShowBottomView) {
            mBottomMusicNameTv.setText(bean.getMusicName());
            mBottomAlbumTv.setText(bean.getAlbumName());
            if (bean.getLocalMusic()) {
                ImageUtil.loadImage(mContext, bean.getCoverImgUrl(), mBottomMusicImg,
                        200, 200, new CircleCrop(), R.mipmap.logo);
            } else {
                ImageUtil.loadImage(mContext, bean.getCoverImgUrl() + AppConstant.WY_IMG_200_200,
                        mBottomMusicImg, new CircleCrop(), R.mipmap.logo);
            }
        }
    }

    @Override
    protected void onPlayStatusChange(int playbackState) {
        if (isShowBottomView) {
            showBottomViewAnim(playbackState, null);
            if (playbackState == EngineEvent.STATUS_PLAYING) {
                if (!mBottomPlayImg.isPlaying()) {
                    mBottomPlayImg.play();
                }
            } else {
                if (mBottomPlayImg.isPlaying()) {
                    mBottomPlayImg.pause();
                }
            }
        }
    }

    @Override
    protected void onMediaSessionConnect(MusicBean bean, int playStatus) {
        super.onMediaSessionConnect(bean, playStatus);
        if (!isShowBottomView) {
            return;
        }
        showBottomViewAnim(playStatus, bean);
        if (playStatus == EngineEvent.STATUS_PLAYING) {
            if (!mBottomPlayImg.isPlaying()) {
                mBottomPlayImg.play();
            }
        } else {
            if (mBottomPlayImg.isPlaying()) {
                mBottomPlayImg.pause();
            }
        }
    }

    @Override
    public void setStatusBarColor(int color) {
        if (mStackView != null) {
            mStackView.setBackgroundColor(color);
        }
    }

    public void shareMusic(final ShareBean bean) {
        if (bean != null) {
            shareMusic(mContentLinear, bean);
        }
    }

    private void showBottomViewAnim(int status, @Nullable MusicBean bean) {
        if (isShowBottomView) {
            if (status == EngineEvent.STATUS_PLAYING) {
                if (!mBottomPlayImg.isPlaying()) {
                    mBottomPlayImg.play();
                }
            } else {
                if (mBottomPlayImg.isPlaying()) {
                    mBottomPlayImg.pause();
                }
            }
            if (mBottomViewStub.getVisibility() != View.VISIBLE) {
                mBottomViewStub.setAlpha(0F);
                mBottomViewStub.setVisibility(View.VISIBLE);
                ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(mBottomViewStub, "alpha", 0F, 1F);
                alphaAnim.setDuration(800);
                alphaAnim.start();
            }
            if (bean != null) {
                mBottomMusicNameTv.setText(bean.getMusicName());
                mBottomAlbumTv.setText(bean.getAlbumName());
            }
        }
    }

    public void showMusicInfoPop(MusicBean bean, OnDeleteListener listener) {
        showMusicInfoPop(mContentLinear, bean, listener);
    }

    public void showBottomView(boolean showBottomView) {
        this.isShowBottomView = showBottomView;
    }

    /**
     * 显示播放列表
     */
    public void showMusicListPopwindow(List<MusicBean> list, PlayQueuePopwindow.OnPopClickListener listener) {
        showMusicListPopwindow(mContentLinear, list, listener);
    }

    //显示错误view
    protected void showErrorView() {
    }

}
