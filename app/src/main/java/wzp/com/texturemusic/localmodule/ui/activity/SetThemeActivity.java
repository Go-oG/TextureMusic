package wzp.com.texturemusic.localmodule.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import wzp.com.texturemusic.AppManager;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.ThemeBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.indexmodule.IndexActivity;
import wzp.com.texturemusic.localmodule.adapter.LocalSetThemeAdapter;
import wzp.com.texturemusic.util.ImageUtil;
import wzp.com.texturemusic.util.SPSetingUtil;

/**
 * Created by Go_oG
 * Description:主题设置界面
 * on 2017/12/26.
 */

public class SetThemeActivity extends BaseActivityWrapper {
    @BindView(R.id.toolar_return_img)
    ImageView toolarReturnImg;
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;
    @BindView(R.id.img_theme)
    ImageView imgTheme;
    @BindView(R.id.m_recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.toolbar_right_tv)
    TextView toolbarRightTv;
    @BindView(R.id.common_toolabr)
    RelativeLayout commonToolabr;
    private LocalSetThemeAdapter adapter;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initService(false);
        addActivityToCustomStack(false);
        super.onCreate(savedInstanceState);
        showBottomView(false);
        setContentView(R.layout.activity_set_theme);
        ButterKnife.bind(this);
        toolbarTitleTv.setText("个性换肤");
        toolbarRightTv.setText("确定");
        toolbarRightTv.setVisibility(View.VISIBLE);
        adapter = new LocalSetThemeAdapter(mContext);
        adapter.setClickTime(300);
        adapter.getItemClickSubject().compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        setTheme(adapter.getDataList().get(position).getThemeIndex());
                        updateColor();
                        int size = adapter.getDataList().size();
                        for (int i = 0; i < size; i++) {
                            adapter.getDataList().get(i).setCheck(i == position);
                        }
                        adapter.notifyDataSetChanged();
                        ImageUtil.loadImage(mContext, adapter.getDataList().get(position).getShowThemeImgResId(), imgTheme,
                                DrawableTransitionOptions.withCrossFade(400), R.drawable.ic_large_album);
                    }
                });
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        loadData();
    }

    @OnClick({R.id.toolar_return_img, R.id.toolbar_right_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolar_return_img:
                finish();
                break;
            case R.id.toolbar_right_tv:
                setAppTheme();
                break;
        }
    }

    private void loadData() {
        List<ThemeBean> themeBeanList = new ArrayList<>();
        int length = AppConstant.THEME_NAMES.length;
        String themeName = SPSetingUtil.getStringValue(AppConstant.SP_KEY_THEME_NAME, AppConstant.THEME_NAMES[0]);
        int index = -1;
        for (int i = 0; i < length; i++) {
            ThemeBean bean = new ThemeBean();
            bean.setThemeImgResId(AppConstant.THEME_IMG_RES_ID[i]);
            bean.setThemeIndex(AppConstant.THEME_ID[i]);
            bean.setShowThemeImgResId(AppConstant.THEME_SHOW_IMG_RES_ID[i]);
            bean.setThemeName(AppConstant.THEME_NAMES[i]);
            if (themeName.equals(bean.getThemeName())) {
                bean.setCheck(true);
                index = i;
            } else {
                bean.setCheck(false);
            }
            themeBeanList.add(bean);
        }
        adapter.clearDataList();
        adapter.addDataList(themeBeanList);
        adapter.notifyDataSetChanged();
        if (index != -1) {
            ImageUtil.loadImage(mContext, adapter.getDataList().get(index).getShowThemeImgResId(), imgTheme,
                    DrawableTransitionOptions.withCrossFade(400), R.drawable.ic_large_album);
        }
    }

    private void setAppTheme() {
        String themeName = "";
        for (int i = 0; i < adapter.getDataList().size(); i++) {
            if (adapter.getDataList().get(i).getCheck()) {
                themeName = adapter.getDataList().get(i).getThemeName();
                break;
            }
        }
        if (!TextUtils.isEmpty(themeName)) {
            SPSetingUtil.setStringValue(AppConstant.SP_KEY_THEME_NAME, themeName);
            AppManager.getAppManager().finishAllActivity();
            Intent intent = new Intent(mContext, IndexActivity.class);
            Intent intent1 = new Intent(mContext, LocalThemeActivity.class);
            Intent[] intents = new Intent[]{intent, intent1};
            startActivities(intents);
            finish();
        }
    }

    private void updateColor() {
        TypedArray array = getTheme().obtainStyledAttributes(new int[]{
                R.attr.main_color,
                R.attr.img_tint_color,
        });
        int mainColor = array.getColor(0, Color.parseColor("#6e75a4"));
        int imgTintColor = array.getColor(1, Color.parseColor("#ffffff"));
        array.recycle();
        setStatusBarColor(mainColor);
        commonToolabr.setBackgroundColor(mainColor);
        toolarReturnImg.setImageTintList(ColorStateList.valueOf(imgTintColor));
        toolbarTitleTv.setTextColor(imgTintColor);
        toolbarRightTv.setTextColor(imgTintColor);
    }

}
