package wzp.com.texturemusic.usermodule.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.WYApiUtil;
import wzp.com.texturemusic.bean.KeyValueBean;
import wzp.com.texturemusic.bean.UserBean;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.customview.CommonNavView;

/**
 * Created by Go_oG
 * Description:关于用户
 * on 2017/11/27.
 */

public class UserDetailAboutFragment extends BaseFragment {
    @BindView(R.id.nav_certifition)
    CommonNavView navCertifition;
    @BindView(R.id.certifition_tv)
    TextView certifitionTv;
    @BindView(R.id.nav_person_info)
    CommonNavView navPersonInfo;
    @BindView(R.id.level_tv)
    TextView levelTv;
    @BindView(R.id.gender_tv)
    TextView genderTv;
    @BindView(R.id.area_tv)
    TextView areaTv;
    @BindView(R.id.nav_introduction)
    CommonNavView navIntroduction;
    @BindView(R.id.person_introduce_tv)
    TextView personIntroduceTv;
    Unbinder unbinder;
    private UserBean bean;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bean = getArguments().getParcelable("bundle");
        Observable.create(new ObservableOnSubscribe<List<KeyValueBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<KeyValueBean>> e) throws Exception {
                String json = "";
                InputStream inputStream = getResources().getAssets().open("area.json");
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                json = new String(buffer, "UTF-8");
                inputStream.close();
                if (!TextUtils.isEmpty(json)) {
                    JSONArray jsonArray = JSONArray.parseArray(json);
                    int size = jsonArray.size();
                    for (int i = 0; i > size; i++) {
                        KeyValueBean bean = new KeyValueBean();
                        bean.setKey(jsonArray.getJSONObject(i).getString("code"));
                    }
                }
                e.onNext(new ArrayList<KeyValueBean>());
            }
        }).compose(this.<List<KeyValueBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<KeyValueBean>>() {
                    @Override
                    public void accept(List<KeyValueBean> keyValueBeans) throws Exception {

                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_user_detail_info, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        navCertifition.hindRightImg();
        navIntroduction.hindRightImg();
        navPersonInfo.hindRightImg();
    }

    @Override
    public void lazyLoading() {
        if (!hasLoadData) {
            loadData(bean.getUserId());
        }
    }


    @SuppressLint("CheckResult")
    private void loadData(long userId) {
        WYApiUtil.getInstance().buildUserService()
                .getUserDetail(userId)
                .map(new Function<String, UserBean>() {
                    @Override
                    public UserBean apply(String s) throws Exception {
                        return jsonToEntiy(s);
                    }
                }).subscribeOn(Schedulers.io())
                .compose(this.<UserBean>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserBean>() {
                    @Override
                    public void accept(UserBean bean) throws Exception {
                        if (bean.getUserId() != 0) {
                            hasLoadData = true;
                            certifitionTv.setText(bean.getDescription());
                            personIntroduceTv.setText(bean.getSignnature());
                            if (bean.getGender() == 1) {
                                genderTv.setText("性别: 男");
                            } else {
                                genderTv.setText("性别: 女");
                            }
                            levelTv.setText("等级:" + bean.getLevel());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private UserBean jsonToEntiy(String json) {
        UserBean bean = new UserBean();
        JSONObject rootObject = JSONObject.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            bean.setCreateDays(rootObject.getIntValue("createDays"));
            bean.setListenSongsCount(rootObject.getIntValue("listenSongs"));
            JSONObject profileObject = rootObject.getJSONObject("profile");
            bean.setLevel(rootObject.getIntValue("level"));
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
                int userType = profileObject.getIntValue("userType");
            }

        }
        return bean;
    }

}
