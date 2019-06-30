package wzp.com.texturemusic.songmodule;

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
import wzp.com.texturemusic.songmodule.ui.NewestMusicFragment;

/**
 * Created by Go_oG
 * Description:最新音乐
 * on 2017/10/12.
 */
public class NewestMusicActivity extends BaseActivityWrapper {
    @BindView(R.id.newwest_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.newwest_viewpager)
    ViewPager mViewpager;
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newest_music);
        ButterKnife.bind(this);
        toolbarTitleTv.setText("最新音乐");
        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            NewestMusicFragment fragment = new NewestMusicFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("types", i);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        FragmentViewpagerAdapter viewpagerAdapter = new FragmentViewpagerAdapter(getSupportFragmentManager());
        viewpagerAdapter.setFragmentList(fragments);
        viewpagerAdapter.setTitle(new String[]{"华语", "欧美", "韩国", "日本"});
        mViewpager.setAdapter(viewpagerAdapter);
        mTablayout.setupWithViewPager(mViewpager);
    }

    @OnClick(R.id.toolar_return_img)
    public void onViewClicked() {
        finish();
    }
}
