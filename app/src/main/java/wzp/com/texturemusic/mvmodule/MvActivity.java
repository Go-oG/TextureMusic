package wzp.com.texturemusic.mvmodule;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.mvmodule.adapter.MvChannelViewpagerAdapter;
import wzp.com.texturemusic.mvmodule.ui.fragment.AllMvFragment;
import wzp.com.texturemusic.mvmodule.ui.fragment.FeaturedMvFragment;
import wzp.com.texturemusic.mvmodule.ui.fragment.TopMvFragment;

/**
 * Created by Go_oG
 * Description:Mv分类数据界面
 * on 2017/9/17.
 */

public class MvActivity extends BaseActivityWrapper {
    @BindView(R.id.mv_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.mv_viewpager)
    ViewPager mViewpager;
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBottomView(false);
        setContentView(R.layout.activity_mv);
        ButterKnife.bind(this);
        toolbarTitleTv.setText("MV频道");
        MvChannelViewpagerAdapter viewpagerAdapter = new MvChannelViewpagerAdapter(getSupportFragmentManager());

        AllMvFragment allMvFragment = new AllMvFragment();
        FeaturedMvFragment featuredMvFragment = new FeaturedMvFragment();
        TopMvFragment topMvFragment = new TopMvFragment();
        List<Fragment> list = new ArrayList<>();
        list.add(featuredMvFragment);
        list.add(topMvFragment);
        list.add(allMvFragment);
        viewpagerAdapter.setList(list);
        mViewpager.setAdapter(viewpagerAdapter);
        mTabLayout.setupWithViewPager(mViewpager, true);
    }

    @OnClick(R.id.toolar_return_img)
    public void onViewClicked() {
        finish();
    }
}
