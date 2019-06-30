package wzp.com.texturemusic.mvmodule.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.mvmodule.adapter.TopMvViewpagerAdapater;

/**
 * Created by Wang on 2017/6/8.
 * mv频道中的排行榜
 * WYApiForAll.getTopMv()
 */


public class TopMvFragment extends BaseFragment {
    @BindView(R.id.fr_mv_top_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.fr_mv_top_viewpager)
    ViewPager mViewpager;
    Unbinder unbinder;
    private TopMvViewpagerAdapater mViewpagerAdapater;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewpagerAdapater = new TopMvViewpagerAdapater(getChildFragmentManager());
        TopMvChildFragment  zhFragment = new TopMvChildFragment();
        TopMvChildFragment hkFragment = new TopMvChildFragment();
        TopMvChildFragment eaFragment = new TopMvChildFragment();
        TopMvChildFragment krFragment = new TopMvChildFragment();
        TopMvChildFragment jpFragment = new TopMvChildFragment();
        List<Fragment>  fragmentList = new ArrayList<>();
        fragmentList.add(zhFragment);
        fragmentList.add(hkFragment);
        fragmentList.add(eaFragment);
        fragmentList.add(krFragment);
        fragmentList.add(jpFragment);
        mViewpagerAdapater.setList(fragmentList);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_mv_top, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mViewpager.setAdapter(mViewpagerAdapater);
        mTablayout.setupWithViewPager(mViewpager);
    }

}
