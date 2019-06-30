package wzp.com.texturemusic.artistmodule.ui;

import android.graphics.Color;
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
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.common.adapter.FragmentViewpagerAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.util.ImageUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:歌手详情界面
 * on 2017/10/12.
 */

public class ArtistDetailActivity extends BaseActivityWrapper {
    public static final String BUNDLE_KEY = "bundle";
    @BindView(R.id.app_bar_image)
    ImageView appBarImage;
    @BindView(R.id.m_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.m_appbar)
    AppBarLayout mAppbar;
    @BindView(R.id.mv_viewpager)
    public ViewPager mvViewpager;
    @BindView(R.id.artist_name_tv)
    TextView artistNameTv;
    @BindView(R.id.m_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.m_collapsingtoolbar)
    CollapsingToolbarLayout mCollapsingtoolbar;
    protected ArtistBean artistBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initService(true);
        super.onCreate(savedInstanceState);
        artistBean = getIntent().getParcelableExtra(AppConstant.UI_INTENT_KEY_ARTIST);
        showBottomView(true);
        setContentView(R.layout.activity_artist_detail);
        ButterKnife.bind(this);
        setStatusBarHeight(0);
        init();
        if (artistBean != null) {
            ImageUtil.loadImage(mContext, artistBean.getArtistImgUrl(), appBarImage, R.drawable.png_artist_cover, R.drawable.png_artist_cover);
            artistNameTv.setText(artistBean.getArtistName());
        } else {
            ToastUtil.showNormalMsg("暂无该歌手信息");
        }
        mCollapsingtoolbar.setBackgroundColor(Color.TRANSPARENT);
    }

    private void init() {
        List<Fragment> list = new ArrayList<>();
        ArtistDetailMusicFragment musicFragment = new ArtistDetailMusicFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putParcelable(BUNDLE_KEY, artistBean);
        musicFragment.setArguments(bundle1);
        list.add(musicFragment);

        ArtistDetailAlbumFragment albumFragment = new ArtistDetailAlbumFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putParcelable(BUNDLE_KEY, artistBean);
        albumFragment.setArguments(bundle2);
        list.add(albumFragment);

        ArtistDetailMvFragment mvFragment = new ArtistDetailMvFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putParcelable(BUNDLE_KEY, artistBean);
        mvFragment.setArguments(bundle3);
        list.add(mvFragment);

        ArtistDetailIntroductionFragment infoFragment = new ArtistDetailIntroductionFragment();
        Bundle bundle4 = new Bundle();
        bundle4.putParcelable(BUNDLE_KEY, artistBean);
        infoFragment.setArguments(bundle4);
        list.add(infoFragment);
        FragmentViewpagerAdapter viewpagerAdapter = new FragmentViewpagerAdapter(getSupportFragmentManager());
        viewpagerAdapter.setTitle(new String[]{"歌曲", "专辑", "MV", "详情"});
        viewpagerAdapter.setFragmentList(list);
        mvViewpager.setAdapter(viewpagerAdapter);
        mTablayout.setupWithViewPager(mvViewpager);
    }

    @OnClick(R.id.toolbar_return_img)
    public void onViewClicked() {
        supportFinishAfterTransition();
    }

    @Override
    protected void onNetworkChange(boolean isConnected, boolean isVpn) {
        super.onNetworkChange(isConnected, isVpn);
        if (isConnected && !hasLoadData) {
            if (artistBean != null) {
                ImageUtil.loadImage(mContext, artistBean.getArtistImgUrl(), appBarImage, R.drawable.png_artist_cover, R.drawable.png_artist_cover);
                artistNameTv.setText(artistBean.getArtistName());
            }
        }
    }

}
