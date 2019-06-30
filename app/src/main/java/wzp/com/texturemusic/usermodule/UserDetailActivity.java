package wzp.com.texturemusic.usermodule;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.WYApiUtil;
import wzp.com.texturemusic.bean.UserBean;
import wzp.com.texturemusic.common.adapter.FragmentViewpagerAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.usermodule.ui.UserDetailAboutFragment;
import wzp.com.texturemusic.usermodule.ui.UserDetailEventFragment;
import wzp.com.texturemusic.usermodule.ui.UserDetailMusicFragment;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:网络用户详情界面
 * on 2017/11/27.
 */

public class UserDetailActivity extends BaseActivityWrapper {
    @BindView(R.id.user_backimg)
    ImageView userBackimg;
    @BindView(R.id.user_head_img)
    CircleImageView userHeadImg;
    @BindView(R.id.user_nickname_tv)
    TextView userNicknameTv;
    @BindView(R.id.user_msg_tv)
    TextView userMsgTv;
    @BindView(R.id.ac_user_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.ac_user_appbarlayout)
    AppBarLayout acUserAppbarlayout;
    @BindView(R.id.m_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.m_viewpager)
    ViewPager mViewpager;
    @BindView(R.id.ac_user_coordinaTorlayout)
    CoordinatorLayout mCoordinaTorlayout;
    @BindView(R.id.username_tv)
    TextView usernameTv;
    private UserBean userBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        setStatusBarHeight(0);
        userBean = getIntent().getParcelableExtra(AppConstant.UI_INTENT_KEY_USER);
        FragmentViewpagerAdapter   adapter = new FragmentViewpagerAdapter(getSupportFragmentManager());
        List<Fragment> fragmentList = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putParcelable("bundle", userBean);

        UserDetailMusicFragment musicFragment = new UserDetailMusicFragment();
        musicFragment.setArguments(bundle);
        fragmentList.add(musicFragment);

        UserDetailEventFragment    eventFragment = new UserDetailEventFragment();
        eventFragment.setArguments(bundle);
        fragmentList.add(eventFragment);

        UserDetailAboutFragment   aboutFragment = new UserDetailAboutFragment();
        aboutFragment.setArguments(bundle);
        fragmentList.add(aboutFragment);

        adapter.setFragmentList(fragmentList);
        adapter.setTitle(new String[]{"音乐", "动态", "关于"});
        mViewpager.setAdapter(adapter);
        mTablayout.setupWithViewPager(mViewpager);
        loadData(userBean);
    }

    @SuppressLint("CheckResult")
    private void loadData(UserBean bean) {
        if (bean != null) {
            WYApiUtil.getInstance().buildUserService()
                    .getUserDetail(bean.getUserId())
                    .map(new Function<String, UserBean>() {
                        @Override
                        public UserBean apply(String s) throws Exception {
                            return jsonToEntiy(s);
                        }
                    }).subscribeOn(Schedulers.io())
                    .compose(this.<UserBean>bindUntilEvent(ActivityEvent.DESTROY))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<UserBean>() {
                        @Override
                        public void accept(UserBean bean) throws Exception {
                            if (bean.getUserId() != 0) {
                                ImageUtil.loadImage(mContext,
                                        bean.getUserCoverImgUrl() + AppConstant.WY_IMG_200_200,
                                        userHeadImg,
                                        R.drawable.ic_user_head, R.drawable.ic_user_head);
                                ImageUtil.loadImage(mContext,
                                        bean.getBackgroundUrl() + AppConstant.WY_IMG_500_300,
                                        userBackimg,
                                        R.drawable.drawable_error_background);
                                userNicknameTv.setText(bean.getNickName());
                                usernameTv.setText(bean.getNickName());
                                String info = "关注 " + bean.getFollows() + " | 粉丝 " + bean.getFolloweds();
                                userMsgTv.setText(info);
                                if (mTablayout.getTabAt(0) != null && mTablayout.getTabAt(1) != null) {
                                    mTablayout.getTabAt(0).setText("音乐(" + bean.getPlaylistCount() + ")");
                                    mTablayout.getTabAt(1).setText("动态(" + bean.getEventCount() + ")");
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
        }
    }

    private UserBean jsonToEntiy(String json) {
        UserBean bean = new UserBean();
        JSONObject rootObject = JSONObject.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            bean.setCreateDays(rootObject.getIntValue("createDays"));
            bean.setListenSongsCount(rootObject.getIntValue("listenSongs"));
            JSONObject profileObject = rootObject.getJSONObject("profile");
            if (profileObject != null) {
                bean.setBackgroundUrl(profileObject.getString("backgroundUrl"));
                bean.setNickName(profileObject.getString("nickname"));
                bean.setUserCoverImgUrl(profileObject.getString("avatarUrl"));
                bean.setGender(profileObject.getIntValue("gender"));//2为女 1为男
                bean.setUserId(profileObject.getLongValue("userId"));
                bean.setDescription(profileObject.getString("description"));
                bean.setSignnature(profileObject.getString("signature"));
                bean.setFollows(profileObject.getIntValue("follows"));
                bean.setFolloweds(profileObject.getIntValue("followeds"));
                bean.setEventCount(profileObject.getIntValue("eventCount"));
                bean.setPlaylistCount(profileObject.getIntValue("playlistCount"));
                bean.setPlaylistSubscribedCount(profileObject.getIntValue("playlistBeSubscribedCount"));
            }

        }
        return bean;
    }

    @OnClick(R.id.toolbar_break_img)
    public void onViewClicked() {
        finish();
    }
}
