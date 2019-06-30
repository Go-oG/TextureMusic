package wzp.com.texturemusic.djmodule.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.common.adapter.FragmentViewpagerAdapter;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;

/**
 * Created by Go_oG
 * Description:电台排行界面
 * on 2017/10/28.
 */

public class DjRankActivity extends BaseActivityWrapper {
    @BindView(R.id.dj_rank_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.dj_rank_viewpager)
    ViewPager mViewpager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dj_rank);
        ButterKnife.bind(this);
        FragmentViewpagerAdapter  viewpagerAdapter = new FragmentViewpagerAdapter(getSupportFragmentManager());
        String[] title = new String[]{"最热节目", "新晋电台", "最热电台"};
        viewpagerAdapter.setTitle(title);
        List<Fragment> fragments = new ArrayList<>();

        DjRankHotProgrameFragment fragment = new DjRankHotProgrameFragment();
        fragments.add(fragment);

        DjRankHotOrNewDjFragment newDjFragment = new DjRankHotOrNewDjFragment();
        newDjFragment.setIshotRadio(0);
        fragments.add(newDjFragment);

        DjRankHotOrNewDjFragment hotDjFragment = new DjRankHotOrNewDjFragment();
        hotDjFragment.setIshotRadio(1);
        fragments.add(hotDjFragment);


        viewpagerAdapter.setFragmentList(fragments);
        mViewpager.setAdapter(viewpagerAdapter);
        mTablayout.setupWithViewPager(mViewpager);
    }

    @OnClick(R.id.dj_rank_break)
    public void onViewClicked() {
        finish();
    }

}
