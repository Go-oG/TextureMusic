package wzp.com.texturemusic.downloadmodule;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.anim.LocalViewpagerAnim;
import wzp.com.texturemusic.common.adapter.FragmentViewpagerAdapter;
import wzp.com.texturemusic.core.service.DownloadService;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.dbmodule.util.DbDownloadUtil;
import wzp.com.texturemusic.downloadmodule.ui.DownloadMusicFragment;
import wzp.com.texturemusic.downloadmodule.ui.DownloadMvFragment;
import wzp.com.texturemusic.downloadmodule.ui.DownloadingFragment;
import wzp.com.texturemusic.interf.OnDialogListener;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:下载管理Activity
 * on 2017/9/27.
 * 只需要 添加下载中和下载完成的fragment界面
 */

public class DownloadActivity extends BaseActivityWrapper {
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;
    @BindView(R.id.downlaod_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.download_viewpager)
    ViewPager mViewpager;
    @BindView(R.id.toolbar_right_tv)
    TextView toolbarRightTv;
    private DownloadMvFragment mvFragment;
    private DownloadMusicFragment musicFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initService(true);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);
        toolbarTitleTv.setText("下载管理");
        toolbarRightTv.setText("清空");
        toolbarRightTv.setVisibility(View.VISIBLE);
        List<Fragment> fragmentList = new ArrayList<>();
        musicFragment = new DownloadMusicFragment();
        mvFragment = new DownloadMvFragment();
        DownloadingFragment downloadingFragment = new DownloadingFragment();
        fragmentList.add(musicFragment);
        fragmentList.add(mvFragment);
        fragmentList.add(downloadingFragment);
        FragmentViewpagerAdapter viewpagerAdapter = new FragmentViewpagerAdapter(getSupportFragmentManager());
        viewpagerAdapter.setTitle(new String[]{"单曲", "MV", "下载中"});
        viewpagerAdapter.setFragmentList(fragmentList);
        mViewpager.setAdapter(viewpagerAdapter);
        mViewpager.setPageTransformer(true, new LocalViewpagerAnim());
        mTablayout.setupWithViewPager(mViewpager, true);
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0 || position == 1) {
                    toolbarRightTv.setVisibility(View.VISIBLE);
                } else {
                    toolbarRightTv.setVisibility(View.GONE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        Intent intent = new Intent(mContext, DownloadService.class);
        startService(intent);
    }

    @OnClick({R.id.toolar_return_img, R.id.toolbar_right_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolar_return_img:
                finish();
                break;
            case R.id.toolbar_right_tv:
                ToastUtil.showCustomDialog(mContext, "是否清空下载历史记录", new OnDialogListener() {
                    @Override
                    public void onResult(boolean success) {
                        if (success) {
                            if (mViewpager.getCurrentItem() == 0) {
                                DbDownloadUtil.clearDownloadHisoryForMusic();
                                if (musicFragment != null) {
                                    musicFragment.updateUi();
                                }
                                ToastUtil.showNormalMsg("清除成功");
                            } else if (mViewpager.getCurrentItem() == 1) {
                                DbDownloadUtil.clearDownloadHisoryForMv();
                                if (mvFragment != null) {
                                    mvFragment.updateUi();
                                }
                                ToastUtil.showNormalMsg("清除成功");
                            }
                        }
                    }
                });
                break;
        }
    }

}
