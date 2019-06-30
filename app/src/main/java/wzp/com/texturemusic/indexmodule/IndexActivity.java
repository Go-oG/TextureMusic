package wzp.com.texturemusic.indexmodule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.core.util.Pair;
import androidx.viewpager.widget.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.freedom.lauzy.playpauseviewlib.PlayPauseView;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.functions.Consumer;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.anim.LocalViewpagerAnim;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.TimeBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.eventlistener.EngineEvent;
import wzp.com.texturemusic.core.reciver.AlarmReciver;
import wzp.com.texturemusic.core.service.MediaService;
import wzp.com.texturemusic.core.ui.BaseActivity;
import wzp.com.texturemusic.customview.CarouselTextView;
import wzp.com.texturemusic.customview.DrawerLayout;
import wzp.com.texturemusic.customview.SpringScrollView;
import wzp.com.texturemusic.djmodule.DjFragment;
import wzp.com.texturemusic.downloadmodule.DownloadActivity;
import wzp.com.texturemusic.indexmodule.adapter.IndexViewpageAdapter;
import wzp.com.texturemusic.localmodule.ui.LocalActivity;
import wzp.com.texturemusic.localmodule.ui.MainPlayActivity;
import wzp.com.texturemusic.localmodule.ui.activity.AppAboutActivity;
import wzp.com.texturemusic.localmodule.ui.activity.LocalCollectActivity;
import wzp.com.texturemusic.localmodule.ui.activity.LocalThemeActivity;
import wzp.com.texturemusic.localmodule.ui.activity.PlayHistoryActivity;
import wzp.com.texturemusic.localmodule.ui.activity.SetAlarmClockRingActivity;
import wzp.com.texturemusic.localmodule.ui.activity.SettingActivity;
import wzp.com.texturemusic.personalmodule.PersonalFragment;
import wzp.com.texturemusic.playlistmodule.ui.PlayListFragment;
import wzp.com.texturemusic.playlistmodule.ui.PlaylistRankFragment;
import wzp.com.texturemusic.searchmodule.SearchActivity;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.ImageUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:主页activity
 * on 2017/9/17.
 */

public class IndexActivity extends BaseActivity {
    private static final int NAV_ITEM_NO_CLICK = -1;
    private static final int NAV_ITEM_FRIENDS = 0;
    private static final int NAV_ITEM_WIFI = 1;
    private static final int NAV_ITEM_THEME = 2;
    private static final int NAV_ITEM_TIMER = 3;
    private static final int NAV_ITEM_ALARM = 4;
    private static final int NAV_ITEM_ABOUT = 6;
    private static final int NAV_ITEM_SETTING = 7;
    private static final int NAV_ITEM_EXIT = 8;
    private static final int NAV_ITEM_MUSIC = 9;
    private static final int NAV_ITEM_HISTORY = 10;
    private static final int NAV_ITEM_DOWNLOAD = 11;
    private static final int NAV_ITEM_COLLECT = 12;
    @BindView(R.id.ac_base_stack_view)
    View mStackView;
    @BindView(R.id.ac_base_drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.item_user_imageView)
    CircleImageView itemUserImageView;
    private int navItemIndex = -1;
    @BindView(R.id.ac_index_nav_return)
    ImageView mNavReturnImg;
    @BindView(R.id.m_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.ac_index_nav_search)
    ImageView mNavSearchImg;
    @BindView(R.id.m_viewpager)
    public ViewPager mViewpager;
    //////////////////////////////////////////////
    private SpringScrollView springScrollView;
    private ImageView mBottomMusicImg;
    private CarouselTextView mBottomAlbumTv;
    private TextView mBottomMusicNameTv;
    private ImageView mBottomNextImg;
    private PlayPauseView mBottomPlayImg;
    //////////////////////////////////////////////
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);
        springScrollView = findViewById(R.id.spring_scrollview);
        init();
    }

    private void init() {
        initStatckbar();
        initNavView();
        initBottomView();
        fragmentList = new ArrayList<>();
        IndexViewpageAdapter viewpageAdapter = new IndexViewpageAdapter(getSupportFragmentManager());
        PersonalFragment personalFragment = new PersonalFragment();
        PlayListFragment playListFragment = new PlayListFragment();
        DjFragment djFragment = new DjFragment();
        PlaylistRankFragment playlistRankFragment = new PlaylistRankFragment();
        fragmentList.add(personalFragment);
        fragmentList.add(playListFragment);
        fragmentList.add(djFragment);
        fragmentList.add(playlistRankFragment);
        viewpageAdapter.setFragmentList(fragmentList);
        mViewpager.setAdapter(viewpageAdapter);
        mViewpager.setPageTransformer(true, new LocalViewpagerAnim());
        mTablayout.setupWithViewPager(mViewpager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fragmentList.clear();
    }

    @SuppressLint("WrongConstant")
    @OnClick({R.id.ac_index_nav_return, R.id.ac_index_nav_search, R.id.nav_item_local_music, R.id.nav_item_collect,
            R.id.nav_item_history, R.id.nav_item_download_manage, R.id.nav_item_theme,
            R.id.nav_item_timing, R.id.nav_item_alarm, R.id.nav_item_about, R.id.nav_item_exit, R.id.nav_item_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ac_index_nav_return:
                if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
                } else {
                    mDrawerLayout.openDrawer(Gravity.START, true);
                }
                break;
            case R.id.ac_index_nav_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_item_theme:
                navItemIndex = NAV_ITEM_THEME;
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.nav_item_timing:
                navItemIndex = NAV_ITEM_TIMER;
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.nav_item_alarm:
                navItemIndex = NAV_ITEM_ALARM;
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.nav_item_about:
                navItemIndex = NAV_ITEM_ABOUT;
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.nav_item_setting:
                navItemIndex = NAV_ITEM_SETTING;
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.nav_item_exit:
                navItemIndex = NAV_ITEM_EXIT;
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.nav_item_local_music:
                navItemIndex = NAV_ITEM_MUSIC;
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.nav_item_history:
                navItemIndex = NAV_ITEM_HISTORY;
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.nav_item_download_manage:
                navItemIndex = NAV_ITEM_DOWNLOAD;
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.nav_item_collect:
                navItemIndex = NAV_ITEM_COLLECT;
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
                mDrawerLayout.closeDrawer(Gravity.START, true);
            } else {
                moveTaskToBack(true);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 初始化侧边view
     */
    private void initNavView() {
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                navItemIndex = NAV_ITEM_NO_CLICK;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (springScrollView != null) {
                    springScrollView.scrollTo(0, 0);
                }
                Intent intent;
                switch (navItemIndex) {
                    case NAV_ITEM_FRIENDS:
                        ToastUtil.showNormalMsg("朋友功能正在开发中");
                        break;
                    case NAV_ITEM_WIFI:
                        ToastUtil.showNormalMsg("WiFi传输功能正在开发中");
                        break;
                    case NAV_ITEM_THEME:
                        //更换皮肤
                        intent = new Intent(mContext, LocalThemeActivity.class);
                        startActivity(intent);
                        break;
                    case NAV_ITEM_TIMER:
                        showTimeDialog();
                        break;
                    case NAV_ITEM_ALARM:
                        //设置闹钟铃声
                        intent = new Intent(mContext, SetAlarmClockRingActivity.class);
                        startActivity(intent);
                        break;
                    case NAV_ITEM_ABOUT:
                        intent = new Intent(mContext, AppAboutActivity.class);
                        startActivity(intent);
                        break;
                    case NAV_ITEM_SETTING:
                        intent = new Intent(mContext, SettingActivity.class);
                        startActivity(intent);
                        break;
                    case NAV_ITEM_EXIT:
                        intent = new Intent(mContext, MediaService.class);
                        stopService(intent);
                        finish();
                        break;
                    case NAV_ITEM_MUSIC:
                        intent = new Intent(mContext, LocalActivity.class);
                        startActivity(intent);
                        break;
                    case NAV_ITEM_HISTORY:
                        intent = new Intent(mContext, PlayHistoryActivity.class);
                        startActivity(intent);
                        break;
                    case NAV_ITEM_DOWNLOAD:
                        intent = new Intent(mContext, DownloadActivity.class);
                        startActivity(intent);
                        break;
                    case NAV_ITEM_COLLECT:
                        intent = new Intent(mContext, LocalCollectActivity.class);
                        startActivity(intent);
                        break;
                }
                navItemIndex = NAV_ITEM_NO_CLICK;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }

        });
    }

    /**
     * 该方法只在显示底部布局时才会调用
     */
    @SuppressLint("CheckResult")
    private void initBottomView() {
        mBottomMusicImg = findViewById(R.id.comment_bottom_music_img);
        mBottomAlbumTv = findViewById(R.id.comment_bottom_album_tv);
        mBottomMusicNameTv = findViewById(R.id.comment_bottom_music_name_tv);
        mBottomNextImg = findViewById(R.id.comment_bottom_next_img);
        mBottomPlayImg = findViewById(R.id.comment_bottom_play_img);
        RxView.clicks(mBottomMusicImg)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Pair<View, String> pair1 = new Pair<View, String>(mBottomMusicImg, getString(R.string.sharemusicimg));
                        Pair<View, String> pair4 = new Pair<View, String>(mBottomPlayImg, getString(R.string.shareplaybtn));
                        Pair<View, String> pair5 = new Pair<View, String>(mBottomNextImg, getString(R.string.sharenextbtn));
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(IndexActivity.this,
                                pair1, pair4, pair5);
                        Intent intent = new Intent(mContext, MainPlayActivity.class);
                        startActivity(intent, options.toBundle());
                    }
                });
        RxView.clicks(mBottomNextImg)
                .throttleFirst(700, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        nextMusic();
                    }
                });
        RxView.clicks(mBottomPlayImg)
                .throttleFirst(700, TimeUnit.MILLISECONDS)
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
    public void setStatusBarHeight(int height) {
        ViewGroup.LayoutParams params = mStackView.getLayoutParams();
        params.height = height;
        mStackView.setLayoutParams(params);
    }

    private void showTimeDialog() {
        //显示时间window
        new MaterialDialog.Builder(this)
                .theme(Theme.LIGHT)
                .title("定时停止播放")
                .items(R.array.timewindow)
                .itemsIds(R.array.timewindowIds)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        switch (position) {
                            case 0:
                                cancleTimeing();
                                break;
                            case 1:
                                setTimeing(10 * 60 * 1000);
                                break;
                            case 2:
                                setTimeing(20 * 60 * 1000);
                                break;
                            case 3:
                                setTimeing(30 * 60 * 1000);
                                break;
                            case 4:
                                setTimeing(45 * 60 * 1000);
                                break;
                            case 5:
                                setTimeing(60 * 60 * 1000);
                                break;
                            case 6:
                                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                                        .setThemeLight()
                                        .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                                if (hourOfDay == 0 && minute == 0) {
                                                    ToastUtil.showNormalMsg("时间不能都是0");
                                                } else {
                                                    long alltime = (hourOfDay * 60 * 60 * 1000) + minute * 60 * 1000;
                                                    setTimeing(alltime);
                                                }
                                            }
                                        })
                                        .setForced24hFormat()
                                        .setStartTime(12, 0)
                                        .setDoneText("确定")
                                        .setCancelText("取消")
                                        .setThemeDark();
                                rtpd.show(getSupportFragmentManager(), "BaseActivity");
                                break;
                        }
                    }
                })
                .checkBoxPrompt("计时结束播放完当前歌曲再结束", false, new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            ToastUtil.showNormalMsg("将在歌曲播放完后再停止");
                        }
                    }
                })
                .show();
    }

    private void setTimeing(long time) {
        TimeBean bean = new TimeBean();
        bean.setRequestCode(AlarmReciver.START_ALARM);
        bean.setTimes(time);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstant.UI_BUNDLE_KEY_TIME, bean);
        Intent intent = new Intent();
        intent.setAction(AlarmReciver.ACTION_TIME);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        ToastUtil.showNormalMsg("定时成功：将在" + ((time / 60) / 1000) + "分钟后停止播放");
    }

    private void cancleTimeing() {
        //取消定时
        Intent intent = new Intent();
        intent.setAction(AlarmReciver.ACTION_TIME);
        TimeBean bean = new TimeBean();
        bean.setRequestCode(AlarmReciver.CANCLE_ALARM);
        Bundle bundle = new Bundle();
        bundle.putParcelable(null, bean);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        ToastUtil.showNormalMsg("已经取消定时");

    }

    @Override
    protected void updateBottomInfo(MusicBean bean) {
        if (bean == null) {
            mBottomMusicNameTv.setText("未知");
            mBottomAlbumTv.setText("未知");
            ImageUtil.loadImage(mContext, R.mipmap.logo, mBottomMusicImg,
                    200, 200, new CircleCrop(), R.mipmap.logo);
            return;
        }
        mBottomMusicNameTv.setText(bean.getMusicName());
        mBottomAlbumTv.setText(bean.getAlbumName());
        if (bean.getLocalMusic()!=null&&bean.getLocalMusic()) {
            ImageUtil.loadImage(mContext, bean.getCoverImgUrl(), mBottomMusicImg,
                    200, 200, new CircleCrop(), R.mipmap.logo);
        } else {
            ImageUtil.loadImage(mContext, bean.getCoverImgUrl() + AppConstant.WY_IMG_200_200,
                    mBottomMusicImg, new CircleCrop(), R.mipmap.logo);
        }
    }

    @Override
    public void setStatusBarColor(int color) {
        mStackView.setBackgroundColor(color);
    }

    @Override
    protected void onPlayStatusChange(int playbackState) {
        super.onPlayStatusChange(playbackState);
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

    @Override
    protected void onMediaSessionConnect(MusicBean bean, int playStatus) {
        super.onMediaSessionConnect(bean, playStatus);
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
    protected void onLyricChange(MusicBean bean, String lyrics) {
        super.onLyricChange(bean, lyrics);
        if ((!mBottomAlbumTv.getText().toString().equals(lyrics))) {
            mBottomAlbumTv.setText(lyrics);
        }
    }

    @Override
    protected void onPlayWarn(String warnType, String warnHint) {
        super.onPlayWarn(warnType, warnHint);
        int playStatus = getNowPlayState();
        if (playStatus == EngineEvent.STATUS_PLAYING) {
            if ( mBottomPlayImg != null && !mBottomPlayImg.isPlaying()) {
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
    protected void onMediaSessionDestroyed() {
        super.onMediaSessionDestroyed();
        if (mBottomPlayImg.isPlaying()) {
            mBottomPlayImg.pause();
        }
    }
}
