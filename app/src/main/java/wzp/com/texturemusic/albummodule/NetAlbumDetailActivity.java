package wzp.com.texturemusic.albummodule;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.albummodule.ui.NetAlbumDetailFragment;
import wzp.com.texturemusic.albummodule.ui.NetAlbumInfoFragment;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.common.adapter.FragmentViewpagerAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.util.ImageUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:网络专辑详情界面
 * on 2017/10/12.
 */
public class NetAlbumDetailActivity extends BaseActivityWrapper {
    @BindView(R.id.album_detail_break)
    ImageView mBreakImg;
    @BindView(R.id.album_detail_operation)
    ImageView mOperationImg;
    @BindView(R.id.album_detail_sort)
    ImageView mSortImg;
    @BindView(R.id.ac_album_detail_toolbar)
    RelativeLayout mToolbar;
    @BindView(R.id.album_detail_bk_img)
    ImageView mBkImg;
    @BindView(R.id.toolbar_album_name_tv)
    TextView toolbarAlbumNameTv;
    @BindView(R.id.fab_btn)
    FloatingActionButton mFabBtn;
    @BindView(R.id.m_tablayout)
    public TabLayout mTablayout;
    @BindView(R.id.mv_viewpager)
    ViewPager mViewpager;
    private AlbumBean albumBean;

    private NetAlbumDetailFragment detailFragment;
    private NetAlbumInfoFragment infoFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_album_detail);
        setStatusBarHeight(0);
        ButterKnife.bind(this);
        mToolbar.setBackgroundColor(Color.parseColor("#00000000"));
        albumBean = getIntent().getParcelableExtra(AppConstant.UI_INTENT_KEY_ALBUM);
        toolbarAlbumNameTv.setText("专辑:" + albumBean.getAlbumName());
        if (!albumBean.getLocalAlbum()) {
            String imgurl = albumBean.getAlbumImgUrl() + AppConstant.WY_IMG_500_300;
            if (!TextUtils.isEmpty(imgurl)) {
                ImageUtil.loadImage(mContext, imgurl, mBkImg, R.drawable.drawable_error_background, R.drawable.drawable_error_background);
            } else {
                ImageUtil.loadImage(mContext, R.drawable.drawable_error_background, mBkImg, R.drawable.drawable_error_background);
            }
            hasLoadData = true;
        } else {
            ToastUtil.showNormalMsg("该专辑不是网络专辑");
        }
        init();
    }

    private void init() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("album", albumBean);
        infoFragment = new NetAlbumInfoFragment();
        infoFragment.setArguments(bundle);
        detailFragment = new NetAlbumDetailFragment();
        detailFragment.setArguments(bundle);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(infoFragment);
        fragmentList.add(detailFragment);
        FragmentViewpagerAdapter viewpagerAdapter = new FragmentViewpagerAdapter(getSupportFragmentManager());
        String[] TITLE = new String[]{"详情", "音乐"};
        viewpagerAdapter.setTitle(TITLE);
        viewpagerAdapter.setFragmentList(fragmentList);
        mViewpager.setAdapter(viewpagerAdapter);
        mTablayout.setupWithViewPager(mViewpager);
        mViewpager.setCurrentItem(1);
    }

    @OnClick({R.id.album_detail_break, R.id.album_detail_operation, R.id.album_detail_sort,
            R.id.fab_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.album_detail_break:
                supportFinishAfterTransition();
                break;
            case R.id.album_detail_operation:
                break;
            case R.id.album_detail_sort:
                break;
            case R.id.fab_btn:
                //播放全部
                List<MusicBean> list = detailFragment.getMusicList();
                if (list != null && list.size() > 0) {
                    addPlayQueueData((ArrayList<MusicBean>) list);
                    playMusic(list.get(0));
                } else {
                    ToastUtil.showNormalMsg("数据为空,无法播放空数据");
                }
                break;
        }
    }

    @Override
    protected void onNetworkChange(boolean isConnected, boolean isVpn) {
        super.onNetworkChange(isConnected, isVpn);
        if (!hasLoadData && !albumBean.getLocalAlbum()) {
            String imgurl = albumBean.getAlbumImgUrl() + AppConstant.WY_IMG_500_300;
            if (!TextUtils.isEmpty(imgurl)) {
                ImageUtil.loadImage(mContext, imgurl, mBkImg, R.drawable.drawable_error_background, R.drawable.drawable_error_background);
            } else {
                ImageUtil.loadImage(mContext, R.drawable.drawable_error_background, mBkImg, R.drawable.drawable_error_background);
            }
            hasLoadData = true;
        }
    }
}