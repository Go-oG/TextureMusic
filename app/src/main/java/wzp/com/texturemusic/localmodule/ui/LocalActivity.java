package wzp.com.texturemusic.localmodule.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.anim.LocalViewpagerAnim;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.dbmodule.DbUtil;
import wzp.com.texturemusic.dbmodule.bean.DbMusicEntiy;
import wzp.com.texturemusic.dbmodule.util.DbMusicUtil;
import wzp.com.texturemusic.interf.OnDialogListener;
import wzp.com.texturemusic.localmodule.adapter.LocalViewpagerAdapter;
import wzp.com.texturemusic.localmodule.ui.activity.LocalSearchActivity;
import wzp.com.texturemusic.localmodule.ui.fragment.LocalAlbumFragment;
import wzp.com.texturemusic.localmodule.ui.fragment.LocalArtistFragment;
import wzp.com.texturemusic.localmodule.ui.fragment.LocalSongsFragment;
import wzp.com.texturemusic.util.MusicUtil;
import wzp.com.texturemusic.util.SPSetingUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:本地音乐主界面
 * on 2017/9/26.
 */

public class LocalActivity extends BaseActivityWrapper {
    @BindView(R.id.local_break_img)
    ImageView mBreakImg;
    @BindView(R.id.local_title_tv)
    TextView mTitleTv;
    @BindView(R.id.local_operation_img)
    ImageView mOperationImg;
    @BindView(R.id.local_search_img)
    ImageView mSearchImg;
    @BindView(R.id.local_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.local_viewpager)
    ViewPager mViewpager;
    private LocalSongsFragment songsFragment;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBottomView(true);
        setContentView(R.layout.activity_local);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            initView();
                            if (!aBoolean) {
                                ToastUtil.showErrorMsg("权限请求失败请重新进入");
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            initView();
                        }
                    });
        } else {
            initView();
        }
    }

    @OnClick({R.id.local_break_img, R.id.local_operation_img, R.id.local_search_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.local_break_img:
                finish();
                break;
            case R.id.local_operation_img:
                ToastUtil.showCustomDialog(mContext, "是否重新获取本地音乐,该功能需要时间较长", new OnDialogListener() {
                    @Override
                    public void onResult(boolean success) {
                        if (success) {
                            refreshDb();
                        }
                    }
                });
                break;
            case R.id.local_search_img:
                Intent intent = new Intent(mContext, LocalSearchActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 刷新数据库
     */
    @SuppressLint("CheckResult")
    private void refreshDb() {
        if (mViewpager.getCurrentItem() != 0) {
            mViewpager.setCurrentItem(0, true);
        }
        showLoadingView(false);
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                DbUtil.getMusicDao().deleteAll();
                List<MusicBean> dataList = MusicUtil.getLocalMusicData(mContext);
                DbMusicUtil.insertMusicData(dataList);
                SPSetingUtil.setBooleanValue(AppConstant.SP_KEY_IS_FIRST_INSERT_DB_DATA, false);
                e.onNext(true);
            }
        }).map(new Function<Boolean, List<DbMusicEntiy>>() {
            @Override
            public List<DbMusicEntiy> apply(Boolean aBoolean) throws Exception {
                return DbMusicUtil.queryAllLocalMusic();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<DbMusicEntiy>>() {
                    @Override
                    public void accept(List<DbMusicEntiy> list) throws Exception {
                        songsFragment.getLocalMusicData(false);
                        cancleLoadingView();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        cancleLoadingView();
                    }
                });


    }


    private void initView() {
        List<Fragment> fragmentList = new ArrayList<>();
        LocalViewpagerAdapter viewpagerAdapter = new LocalViewpagerAdapter(getSupportFragmentManager());
        songsFragment = new LocalSongsFragment();
        LocalAlbumFragment albumFragment = new LocalAlbumFragment();
        LocalArtistFragment artistFragment = new LocalArtistFragment();
        fragmentList.add(songsFragment);
        fragmentList.add(albumFragment);
        fragmentList.add(artistFragment);
        viewpagerAdapter.setFragmentList(fragmentList);
        mViewpager.setAdapter(viewpagerAdapter);
        mViewpager.setPageTransformer(true, new LocalViewpagerAnim());
        mTablayout.setupWithViewPager(mViewpager);
    }
}
