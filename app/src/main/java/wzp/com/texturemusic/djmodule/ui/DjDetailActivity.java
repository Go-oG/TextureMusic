package wzp.com.texturemusic.djmodule.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.RadioBean;
import wzp.com.texturemusic.common.adapter.FragmentViewpagerAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.interf.OnImageLoadListener;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:电台详情界面
 * on 2017/10/12.
 */

public class DjDetailActivity extends BaseActivityWrapper {
    @BindView(R.id.app_bar_image)
    ImageView appBarImage;
    @BindView(R.id.dj_name_tv)
    TextView djNameTv;
    @BindView(R.id.m_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.m_collapsingtoolbar)
    CollapsingToolbarLayout mCollapsingtoolbar;
    @BindView(R.id.m_appbar)
    AppBarLayout mAppbar;
    @BindView(R.id.m_tablayout)
    public TabLayout mTablayout;
    @BindView(R.id.mv_viewpager)
    ViewPager mViewpager;
    private RadioBean radioBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dj_detail);
        setStatusBarHeight(0);
        ButterKnife.bind(this);
        supportPostponeEnterTransition();
        radioBean = getIntent().getParcelableExtra(AppConstant.UI_INTENT_KEY_DJ);
        if (radioBean != null) {
            ImageUtil.loadImageSkipMemory(mContext, radioBean.getCoverImgUrl(), R.drawable.ic_large_album,
                    new OnImageLoadListener() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            appBarImage.setImageBitmap(bitmap);
                        }
                    });
            djNameTv.setText(radioBean.getRadioName());
        }
        FragmentViewpagerAdapter adapter = new FragmentViewpagerAdapter(getSupportFragmentManager());
        adapter.setTitle(new String[]{"详情", "节目"});
        List<Fragment> list = new ArrayList<>();
        DjDetailInfoFragment infoFragment = new DjDetailInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstant.UI_INTENT_KEY_DJ, radioBean);
        infoFragment.setArguments(bundle);
        DjDetailProgrameFragment programeFragment = new DjDetailProgrameFragment();
        programeFragment.setArguments(bundle);
        list.add(infoFragment);
        list.add(programeFragment);
        adapter.setFragmentList(list);
        mViewpager.setAdapter(adapter);
        mTablayout.setupWithViewPager(mViewpager);
        mViewpager.setCurrentItem(1);
        startPostponedEnterTransition();
    }


    @OnClick(R.id.toolbar_return_img)
    public void onViewClicked() {
        supportFinishAfterTransition();
    }

}
