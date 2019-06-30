package wzp.com.texturemusic.localmodule.ui.activity;

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
import wzp.com.texturemusic.localmodule.ui.fragment.CollectMusicFragment;
import wzp.com.texturemusic.localmodule.ui.fragment.CollectPlaylistFagment;

/**
 * Created by Wang on 2018/3/9.
 * 本地我的收藏界面
 */

public class LocalCollectActivity extends BaseActivityWrapper {
    @BindView(R.id.m_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.m_viewpager)
    ViewPager mViewpager;
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_collectt);
        ButterKnife.bind(this);
        FragmentViewpagerAdapter  viewpagerAdapter = new FragmentViewpagerAdapter(getSupportFragmentManager());
        viewpagerAdapter.setTitle(new String[]{"音乐", "歌单"});
        CollectMusicFragment musicFragment = new CollectMusicFragment();
        CollectPlaylistFagment playlistFagment = new CollectPlaylistFagment();
        List<Fragment> list = new ArrayList<>();
        list.add(musicFragment);
        list.add(playlistFagment);
        viewpagerAdapter.setFragmentList(list);
        mViewpager.setAdapter(viewpagerAdapter);
        mTablayout.setupWithViewPager(mViewpager);
        toolbarTitleTv.setText("我的收藏");

    }

    @OnClick(R.id.toolar_return_img)
    public void onViewClicked() {
        finish();
    }
}
