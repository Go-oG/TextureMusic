package wzp.com.texturemusic.djmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.DJApiManager;
import wzp.com.texturemusic.bean.CommentBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.RadioBean;
import wzp.com.texturemusic.bean.UserBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.djmodule.adapter.DjDetailInfoAdapter;
import wzp.com.texturemusic.djmodule.bean.DjInfoBean;
import wzp.com.texturemusic.core.customui.OnLoadMoreForLinearLayoutManager;
import wzp.com.texturemusic.usermodule.UserDetailActivity;
import wzp.com.texturemusic.util.ImageUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description: 电台详情
 * on 2017/11/16.
 */

public class DjDetailInfoFragment extends BaseFragment {
    @BindView(R.id.djer_img)
    CircleImageView djerImg;
    @BindView(R.id.djer_name_tv)
    TextView djerNameTv;
    @BindView(R.id.djer_short_info_tv)
    TextView djerShortInfoTv;
    @BindView(R.id.dj_info_tv)
    TextView djInfoTv;
    @BindView(R.id.m_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    @BindView(R.id.dj_type_tv)
    TextView djTypeTv;
    @BindView(R.id.dj_uname_tv)
    TextView djUnameTv;
    private RadioBean djBean;
    private DjDetailInfoAdapter adapter;
    private DjInfoBean dataBean;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        djBean = getArguments().getParcelable(AppConstant.UI_INTENT_KEY_DJ);
        adapter = new DjDetailInfoAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        //点击了用户头像
                        CommentBean commentBean = adapter.getDataList().get(itemBean.getPosition());
                        UserBean userBean = new UserBean();
                        userBean.setUserId(commentBean.getCommentCreaterId());
                        userBean.setNickName(commentBean.getCommnetCreaterName());
                        userBean.setUserCoverImgUrl(commentBean.getCommentCreaterImgUrl());
                        Intent intent = new Intent(mContext, UserDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_USER, userBean);
                        startActivity(intent);
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
            mContentView = inflater.inflate(R.layout.fragment_dj_detail_info,container,true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecycleview.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycleview.setLayoutManager(layoutManager);
        mRecycleview.addOnScrollListener(new OnLoadMoreForLinearLayoutManager(layoutManager) {
            @Override
            public void onLoadMore() {

            }
        });
    }

    @Override
    public void lazyLoading() {
        if (!hasLoadData) {
            if (djBean != null) {
                loadDJDetailInfo(djBean.getRadioId());
            }
        }
    }

    @SuppressLint("CheckResult")
    private void loadDJDetailInfo(String djId) {
        DJApiManager
                .getDjInfo(djId)
                .map(new Function<String, DjInfoBean>() {
                    @Override
                    public DjInfoBean apply(String s) throws Exception {
                        return jsonToEntiy(s);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<DjInfoBean>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<DjInfoBean>() {
                    @Override
                    public void accept(DjInfoBean bean) throws Exception {
                        dataBean = bean;
                        hasLoadData = true;
                        adapter.clearDataList();
                        adapter.addDataList(bean.getCommentList());
                        adapter.notifyDataSetChanged();
                        ImageUtil.loadImage(mContext,
                                bean.getUserImgUrl() + AppConstant.WY_IMG_200_200,
                                djerImg, R.drawable.ic_user_head, R.drawable.ic_user_head);
                        djerNameTv.setText(bean.getUserName());
                        djerShortInfoTv.setText(bean.getUserSignature());
                        djTypeTv.setText("分类: " + bean.getCategory());
                        djUnameTv.setText("作者: " + bean.getDjName());
                        djInfoTv.setText("简介: " + bean.getDescription());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showErrorMsg("网络错误");
                    }
                });

    }


    private DjInfoBean jsonToEntiy(String json) {
        DjInfoBean bean = new DjInfoBean();
        JSONObject rootObject = JSONObject.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            JSONObject djRadioObject = rootObject.getJSONObject("djRadio");
            bean.setDjId(djRadioObject.getLongValue("id"));
            bean.setDjName(djRadioObject.getString("name"));
            bean.setDjImgUrl(djRadioObject.getString("picUrl"));
            bean.setDescription(djRadioObject.getString("desc"));
            bean.setSubCount(djRadioObject.getIntValue("subCount"));
            bean.setProgramCount(djRadioObject.getIntValue("programCount"));
            bean.setCreateTime(djRadioObject.getLongValue("createTime"));
            bean.setCategoryId(djRadioObject.getLongValue("categoryId"));
            bean.setCategory(djRadioObject.getString("category"));
            bean.setShortInfo(djRadioObject.getString("rcmdText"));
            bean.setShareCount(djRadioObject.getIntValue("shareCount"));
            JSONObject djObject = djRadioObject.getJSONObject("dj");
            bean.setUserId(djObject.getLongValue("userId"));
            bean.setUserName(djObject.getString("nickname"));
            bean.setUserImgUrl(djObject.getString("avatarUrl"));
            bean.setUserSignature(djObject.getString("signature"));
            JSONArray commentObject = djRadioObject.getJSONArray("commentDatas");
            int size = commentObject.size();
            List<CommentBean> commentList = new ArrayList<>();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    JSONObject itemObject = commentObject.getJSONObject(i);
                    CommentBean commentBean = new CommentBean();
                    commentBean.setVal(itemObject.getString("programName"));
                    commentBean.setCommentId(String.valueOf(itemObject.getLongValue("commentId")));
                    commentBean.setDescription(itemObject.getString("content"));
                    commentBean.setCommentCreaterId(itemObject.getJSONObject("userProfile").getLong("userId"));
                    commentBean.setCommnetCreaterName(itemObject.getJSONObject("userProfile").getString("nickname"));
                    commentBean.setCommentCreaterImgUrl(itemObject.getJSONObject("userProfile").getString("avatarUrl"));
                    commentList.add(commentBean);
                }
            }
            bean.setCommentList(commentList);
        }
        return bean;
    }

    @OnClick(R.id.dj_er_item)
    public void onViewClicked() {
        if (dataBean != null) {
            UserBean userBean = new UserBean();
            userBean.setUserId(dataBean.getUserId());
            userBean.setNickName(dataBean.getUserName());
            userBean.setUserCoverImgUrl(dataBean.getUserImgUrl());
            Intent intent = new Intent(mContext, UserDetailActivity.class);
            intent.putExtra(AppConstant.UI_INTENT_KEY_USER, userBean);
            startActivity(intent);
        } else {
            ToastUtil.showNormalMsg("请稍后再试");
        }

    }
}
