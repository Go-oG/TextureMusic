package wzp.com.texturemusic.localmodule.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.util.SPSetingUtil;

/**
 * Created by Go_oG
 * Description:设置界面
 * on 2017/9/24.
 */
public class SettingActivity extends BaseActivityWrapper {
    @BindView(R.id.set_wifi_switch)
    Switch mWifiSwitch;
    @BindView(R.id.set_notwifi_play_switch)
    Switch mPlaySwitch;
    @BindView(R.id.set_notwifi_download_switch)
    Switch mDownloadSwitch;
    @BindView(R.id.set_playQuality_tv)
    TextView mPlayQualityTv;
    @BindView(R.id.set_item_playquality)
    RelativeLayout mPlayqualityRelative;
    @BindView(R.id.set_downlaodQuality_tv)
    TextView mDownlaodQualityTv;
    @BindView(R.id.set_item_download)
    RelativeLayout mItemDownload;
    @BindView(R.id.set_item_cache)
    RelativeLayout mItemCache;
    @BindView(R.id.set_deskty_lrc_switch)
    Switch mDesktyLrcSwitch;
    @BindView(R.id.set_item_lookshow)
    RelativeLayout mItemLookshow;
    @BindView(R.id.set_erji_switch)
    Switch mErjiSwitch;
    @BindView(R.id.set_lookshow_tv)
    TextView lockScreenTv;
    @BindView(R.id.open_fft_switch)
    Switch openFftSwitch;
    @BindView(R.id.open_animation)
    Switch openAnimationSwitch;
    //用于设置锁屏
    boolean isLocakScreen = true;
    int itemIndex = 0;
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;
    @BindView(R.id.setone_tv)
    TextView setoneTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initService(false);
        super.onCreate(savedInstanceState);
        showBottomView(false);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        toolbarTitleTv.setText("设置");
        initView();
    }

    /**
     * 获取全局配置
     */
    private void initView() {
        SharedPreferences sp = SPSetingUtil.getSettingSP();
        boolean wifi = sp.getBoolean(AppConstant.SP_KEY_WIFI, true);
        boolean g2play = sp.getBoolean(AppConstant.SP_KEY_2G_PLAY, true);
        boolean g2downlaod = sp.getBoolean(AppConstant.SP_KEY_2G_DOWNLOAD, true);
        int playQuality = sp.getInt(AppConstant.SP_KEY_PLAY_QUALITY, AppConstant.MUSIC_BITRATE_NORMAL);
        int downloadQuaity = sp.getInt(AppConstant.SP_KEY_DOWNLOAD_QUALITY, AppConstant.MUSIC_BITRATE_NORMAL);
        boolean showLrc = sp.getBoolean(AppConstant.SP_KEY_DESKTY_LRC, true);
        boolean erji = sp.getBoolean(AppConstant.SP_KEY_ERJI_CONTROL, true);
        boolean lockScreen = sp.getBoolean(AppConstant.SP_KEY_IS_LOCK_SCREEN, true);

        mWifiSwitch.setChecked(wifi);
        mPlaySwitch.setChecked(g2play);
        mDownloadSwitch.setChecked(g2downlaod);
        if (wifi) {
            //仅WiFi连接
            mPlaySwitch.setClickable(false);
            mDownloadSwitch.setClickable(false);
        } else {
            mPlaySwitch.setClickable(true);
            mDownloadSwitch.setClickable(true);
        }
        switch (playQuality) {
            case AppConstant.MUSIC_BITRATE_LOW:
                mPlayQualityTv.setText("标准(128kbit/s)");
                break;
            case AppConstant.MUSIC_BITRATE_NORMAL:
                mPlayQualityTv.setText("较高(192kbit/s)");
                break;
            case AppConstant.MUSIC_BITRATE_HIGHT:
                mPlayQualityTv.setText("极高(320kbit/s)");
                break;
            case AppConstant.MUSIC_BITRATE_SUPER_HIGHT:
                mPlayQualityTv.setText("无损品质");
                break;
        }
        switch (downloadQuaity) {
            case AppConstant.MUSIC_BITRATE_LOW:
                mDownlaodQualityTv.setText("标准(128kbit/s) 每首约4M");
                break;
            case AppConstant.MUSIC_BITRATE_NORMAL:
                mDownlaodQualityTv.setText("较高(192kbit/s) 每首约6M");
                break;
            case AppConstant.MUSIC_BITRATE_HIGHT:
                mDownlaodQualityTv.setText("极高(320kbit/s) 每首约10M");
                break;
            case AppConstant.MUSIC_BITRATE_SUPER_HIGHT:
                mDownlaodQualityTv.setText("无损品质 每首约30M");
                break;
        }
        mDesktyLrcSwitch.setChecked(showLrc);
        mErjiSwitch.setChecked(erji);
        if (lockScreen) {
            String te = SPSetingUtil.getIntValue(AppConstant.SP_KEY_LOCK_SCREEN_TYPE, AppConstant.LOCAL_SCREEN_TYPE_SYSTEM)
                    == AppConstant.LOCAL_SCREEN_TYPE_SYSTEM ? "系统锁屏" : "自定义锁屏";
            lockScreenTv.setText(te);
        } else {
            lockScreenTv.setText("不显示锁屏");
        }
        //是否打开音乐可是化界面默认false
        boolean openFFTView = sp.getBoolean(AppConstant.SP_KEY_OPEN_FFT_VIEW, true);
        openFftSwitch.setChecked(openFFTView);
        openFftSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ab = openFftSwitch.isChecked();
                openFftSwitch.setChecked(ab);
                SPSetingUtil.setBooleanValue(AppConstant.SP_KEY_OPEN_FFT_VIEW, ab);
            }
        });

        boolean openAnimation = sp.getBoolean(AppConstant.SP_KEY_OPEN_ANIMATION, true);
        openAnimationSwitch.setChecked(openAnimation);
        openAnimationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ab = openAnimationSwitch.isChecked();
                openAnimationSwitch.setChecked(ab);
                SPSetingUtil.setBooleanValue(AppConstant.SP_KEY_OPEN_ANIMATION, ab);
            }
        });

    }

    @OnClick({R.id.set_wifi_switch, R.id.set_notwifi_play_switch, R.id.set_notwifi_download_switch,
            R.id.set_item_playquality, R.id.set_item_download, R.id.set_item_cache, R.id.set_deskty_lrc_switch,
            R.id.set_erji_switch, R.id.set_item_lookshow, R.id.toolar_return_img})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.set_wifi_switch:
                //wifi
                boolean w = mWifiSwitch.isChecked();
                SPSetingUtil.setBooleanValue(AppConstant.SP_KEY_WIFI, w);
                mPlaySwitch.setClickable(!w);
                mDownloadSwitch.setClickable(!w);
                intent = new Intent();
                intent.setAction(AppConstant.RECIVER_ACTION_NETWORK_SET_CHANGE);
                sendBroadcast(intent);
                break;
            case R.id.set_notwifi_play_switch:
                //网络播放
                SPSetingUtil.setBooleanValue(AppConstant.SP_KEY_2G_PLAY, mPlaySwitch.isChecked());
                intent = new Intent();
                intent.setAction(AppConstant.RECIVER_ACTION_NETWORK_SET_CHANGE);
                sendBroadcast(intent);
                break;
            case R.id.set_notwifi_download_switch:
                SPSetingUtil.setBooleanValue(AppConstant.SP_KEY_2G_DOWNLOAD, mDownloadSwitch.isChecked());
                break;
            case R.id.set_item_playquality:
                //播放质量
                showMusicQualityDialog(false);
                break;
            case R.id.set_item_download:
                //下载质量
                showMusicQualityDialog(true);
                break;
            case R.id.set_item_cache:
                intent = new Intent(mContext, CacheSetActivity.class);
                startActivity(intent);
                break;
            case R.id.set_deskty_lrc_switch:
                SPSetingUtil.setBooleanValue(AppConstant.SP_KEY_DESKTY_LRC, mDesktyLrcSwitch.isChecked());
                break;
            case R.id.set_erji_switch:
                //耳机线控
                boolean controlB = mErjiSwitch.isChecked();
                mErjiSwitch.setChecked(controlB);
                SPSetingUtil.setBooleanValue(AppConstant.SP_KEY_ERJI_CONTROL, controlB);
                break;
            case R.id.set_item_lookshow:
                //锁屏
                showLockScreenDialog();
                break;
            case R.id.toolar_return_img:
                finish();
                break;
        }
    }

    private void showLockScreenDialog() {
        itemIndex = 0;
        boolean lockScreen = SPSetingUtil.getBooleanValue(AppConstant.SP_KEY_IS_LOCK_SCREEN, true);
        isLocakScreen=lockScreen;
        int index = SPSetingUtil.getIntValue(AppConstant.SP_KEY_LOCK_SCREEN_TYPE, AppConstant.LOCAL_SCREEN_TYPE_SYSTEM);
        index = (index == AppConstant.LOCAL_SCREEN_TYPE_SYSTEM ? 0 : 1);
        itemIndex=index;
        TypedArray array = getTheme().obtainStyledAttributes(new int[]{
                R.attr.main_bk_color,
                R.attr.item_text_sub_color,
                R.attr.main_color
        });
        int backColor = array.getColor(0, 0xffffff);
        int textColor = array.getColor(1, Color.parseColor("#666666"));
        int mainColor = array.getColor(2, Color.parseColor("#6e75a4"));
        array.recycle();
        new MaterialDialog.Builder(mContext)
                .autoDismiss(false)
                .cancelable(false)
                .titleColor(textColor)
                .contentColor(textColor)
                .backgroundColor(backColor)
                .itemsColor(textColor)
                .widgetColor(mainColor)
                .negativeColor(mainColor)
                .positiveColorRes(R.color.general_lowgray_night)
                .title("选择锁屏方式")
                .items("系统锁屏", "自定义锁屏")
                .alwaysCallSingleChoiceCallback()
                .itemsCallbackSingleChoice(index, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        itemIndex = position;
                        return true;
                    }
                })
                .checkBoxPrompt("锁屏显示", lockScreen, new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        isLocakScreen = isChecked;
                    }
                })
                .negativeText("确定")
                .positiveText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //取消
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //确定
                        SPSetingUtil.setBooleanValue(AppConstant.SP_KEY_IS_LOCK_SCREEN, isLocakScreen);
                        if (isLocakScreen) {
                            if (itemIndex == 0) {
                                SPSetingUtil.setIntValue(AppConstant.SP_KEY_LOCK_SCREEN_TYPE, AppConstant.LOCAL_SCREEN_TYPE_SYSTEM);
                                lockScreenTv.setText("系统锁屏");
                            } else {
                                SPSetingUtil.setIntValue(AppConstant.SP_KEY_LOCK_SCREEN_TYPE, AppConstant.LOCAL_SCREEN_TYPE_CUSTOM);
                                lockScreenTv.setText("自定义锁屏");
                            }
                        } else {
                            lockScreenTv.setText("不显示锁屏");
                        }
                        dialog.dismiss();
                    }
                })
                .build()
                .show();
    }

    private void showMusicQualityDialog(final boolean isDownloadQuality) {
        itemIndex = -1;
        String title = isDownloadQuality ? "下载音质" : "播放音质";
        TypedArray array = getTheme().obtainStyledAttributes(new int[]{
                R.attr.main_bk_color,
                R.attr.item_text_sub_color,
                R.attr.main_color
        });
        int backColor = array.getColor(0, 0xffffff);
        int textColor = array.getColor(1, Color.parseColor("#666666"));
        int mainColor = array.getColor(2, Color.parseColor("#6e75a4"));
        new MaterialDialog.Builder(mContext)
                .autoDismiss(false)
                .cancelable(false)
                .titleColor(textColor)
                .contentColor(textColor)
                .backgroundColor(backColor)
                .itemsColor(textColor)
                .widgetColor(mainColor)
                .negativeColor(mainColor)
                .positiveColorRes(R.color.general_lowgray_night)
                .title(title)
                .items("标准(128kbit/s)", "较高(192kbit/s)", "极高(320kbit/s)", "无损品质")
                .alwaysCallSingleChoiceCallback()
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        itemIndex = which;
                        return true;
                    }
                })
                .negativeText("确定")
                .positiveText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (!(itemIndex == -1)) {
                            int[] bitArray = new int[]{AppConstant.MUSIC_BITRATE_LOW, AppConstant.MUSIC_BITRATE_NORMAL,
                                    AppConstant.MUSIC_BITRATE_HIGHT, AppConstant.MUSIC_BITRATE_SUPER_HIGHT};
                            if (isDownloadQuality) {
                                SPSetingUtil.setIntValue(AppConstant.SP_KEY_DOWNLOAD_QUALITY, bitArray[itemIndex]);
                                switch (bitArray[itemIndex]) {
                                    case AppConstant.MUSIC_BITRATE_LOW:
                                        mDownlaodQualityTv.setText("标准(128kbit/s) 每首约4M");
                                        break;
                                    case AppConstant.MUSIC_BITRATE_NORMAL:
                                        mDownlaodQualityTv.setText("较高(192kbit/s) 每首约6M");
                                        break;
                                    case AppConstant.MUSIC_BITRATE_HIGHT:
                                        mDownlaodQualityTv.setText("极高(320kbit/s) 每首约10M");
                                        break;
                                    case AppConstant.MUSIC_BITRATE_SUPER_HIGHT:
                                        mDownlaodQualityTv.setText("无损品质 每首约30M");
                                        break;
                                }
                            } else {
                                SPSetingUtil.setIntValue(AppConstant.SP_KEY_PLAY_QUALITY, bitArray[itemIndex]);
                                switch (bitArray[itemIndex]) {
                                    case AppConstant.MUSIC_BITRATE_LOW:
                                        mPlayQualityTv.setText("标准(128kbit/s)");
                                        break;
                                    case AppConstant.MUSIC_BITRATE_NORMAL:
                                        mPlayQualityTv.setText("较高(192kbit/s)");
                                        break;
                                    case AppConstant.MUSIC_BITRATE_HIGHT:
                                        mPlayQualityTv.setText("极高(320kbit/s)");
                                        break;
                                    case AppConstant.MUSIC_BITRATE_SUPER_HIGHT:
                                        mPlayQualityTv.setText("无损品质");
                                        break;
                                }
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

}
