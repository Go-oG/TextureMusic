package wzp.com.texturemusic.artistmodule.ui;

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
import wzp.com.texturemusic.common.adapter.FragmentViewpagerAdapter;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;

/**
 * author:Go_oG
 * date: on 2018/5/10
 * packageName: wzp.com.texturemusic.artistmodule.ui
 * 歌手排行榜界面
 */
public class ArtistRankActivity extends BaseActivityWrapper {
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;
    @BindView(R.id.m_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.m_viewpager)
    ViewPager mViewpager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_rank);
        ButterKnife.bind(this);
        toolbarTitleTv.setText("歌手排行榜");
        FragmentViewpagerAdapter viewpagerAdapter;
        viewpagerAdapter = new FragmentViewpagerAdapter(getSupportFragmentManager());
        viewpagerAdapter.setTitle(new String[]{"华语", "欧美", "韩国", "日本"});
        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt(ArtistRankFragment.BUNDLE_KEY, i + 1);
            ArtistRankFragment rankFragment = new ArtistRankFragment();
            rankFragment.setArguments(bundle);
            fragmentList.add(rankFragment);
        }
        viewpagerAdapter.setFragmentList(fragmentList);
        mViewpager.setAdapter(viewpagerAdapter);
        mTablayout.setupWithViewPager(mViewpager);
    }

    @OnClick(R.id.toolar_return_img)
    public void onViewClicked() {
        finish();
    }
}
