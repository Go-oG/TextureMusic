package wzp.com.texturemusic.common.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.CommentApiManager;
import wzp.com.texturemusic.bean.CommentBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.PlayListBean;
import wzp.com.texturemusic.bean.SubCommentBean;
import wzp.com.texturemusic.bean.UserBean;
import wzp.com.texturemusic.common.adapter.CommentItemAdapter;
import wzp.com.texturemusic.common.adapter.CommentSingleAdapter;
import wzp.com.texturemusic.common.adapter.CommentTitleAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.interf.OnDialogListener;
import wzp.com.texturemusic.interf.OnOperationListener;
import wzp.com.texturemusic.core.customui.OnLoadMoreForLinearLayoutManager;
import wzp.com.texturemusic.playlistmodule.PlaylistDetailActivity;
import wzp.com.texturemusic.usermodule.UserDetailActivity;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:显示某一个类别的评论
 * on 2018/1/6.
 */

public class CommentActivity extends BaseActivityWrapper {
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;
    @BindView(R.id.m_recyclerview)
    RecyclerView mRecyclerview;
    private SubCommentBean subCommentBean;
    private int offset = 0;
    private int limit = 50;
    private CommentSingleAdapter singleAdapter;
    private CommentTitleAdapter titleAdapter;
    private CommentItemAdapter itemAdapter;
    private boolean hasMoreData = true;
    private boolean isFirstLoad = true;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBottomView(false);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        toolbarTitleTv.setText("评论");
        subCommentBean = getIntent().getParcelableExtra(AppConstant.UI_INTENT_KEY_COMMENT);
        VirtualLayoutManager  layoutManager = new VirtualLayoutManager(mContext, LinearLayoutManager.VERTICAL);
        List<DelegateAdapter.Adapter> adapterList = new ArrayList<>();
        singleAdapter = new CommentSingleAdapter(mContext, subCommentBean, new OnOperationListener() {
            @Override
            public void onPlayAll() {
                if (subCommentBean.getCommentType() == AppConstant.COMMENT_TYPE_PLAYLIST) {
                    PlayListBean bean = subCommentBean.getPlayList();
                    if (bean != null) {
                        Intent intent = new Intent(mContext, PlaylistDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST, bean.getPlaylistId());
                        intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST_IMG, bean.getCoverImgUr());
                        startActivity(intent);
                    }
                } else if (subCommentBean.getCommentType() == AppConstant.COMMENT_TYPE_MUSIC) {
                    //歌曲
                    final MusicBean bean = subCommentBean.getMusicBean();
                    if (bean != null) {
                        ToastUtil.showCustomDialog(mContext, "是否播放" + bean.getMusicName(), new OnDialogListener() {
                            @Override
                            public void onResult(boolean success) {
                                if (success) {
                                    playMusic(bean);
                                }
                            }
                        });
                    }
                } else if (subCommentBean.getCommentType() == AppConstant.COMMENT_TYPE_MV) {
                    //MV
                } else if (subCommentBean.getCommentType() == AppConstant.COMMENT_TYPE_DJ) {
                    //电台节目的评论
                }
            }

            @Override
            public void onMultipleChoice() {

            }
        });
        titleAdapter = new CommentTitleAdapter(mContext);
        itemAdapter = new CommentItemAdapter(mContext);
        itemAdapter.getItemClickSubject()
                .compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(itemBean -> {
                    int position = itemBean.getPosition();
                    Intent intent = new Intent(mContext, UserDetailActivity.class);
                    intent.putExtra(AppConstant.UI_INTENT_KEY_USER, itemAdapter.getDataList().get(position).getUserBean());
                    startActivity(intent);
                });
        adapterList.add(singleAdapter);
        adapterList.add(titleAdapter);
        adapterList.add(itemAdapter);
        DelegateAdapter adapter = new DelegateAdapter(layoutManager);
        adapter.setAdapters(adapterList);
        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.addOnScrollListener(new OnLoadMoreForLinearLayoutManager(layoutManager) {
            @Override
            public void onLoadMore() {
                if (hasMoreData) {
                    offset++;
                    getCommentData(subCommentBean.getCommentId(), offset * limit, limit, true);
                } else {
                    ToastUtil.showNormalMsg("已经没有数据了");
                }
            }
        });
        if (subCommentBean != null) {
            getCommentData(subCommentBean.getCommentId(), offset, limit, false);
        }else {
            ToastUtil.showNormalMsg("暂无评论信息");
            finish();
        }
    }

    @OnClick(R.id.toolar_return_img)
    public void onViewClicked() {
        finish();
    }

    @SuppressLint("CheckResult")
    private void getCommentData(String commentId, int offset, int limit, final boolean isLoadMore) {
        CommentApiManager
                .getComments(commentId, offset, limit, false)
                .map(new Function<String, List<CommentBean>>() {
                    @Override
                    public List<CommentBean> apply(String s) throws Exception {
                        return jsonToEntiy(s);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CommentBean>>() {
                    @Override
                    public void accept(List<CommentBean> commentBeans) throws Exception {
                        if (!isLoadMore) {
                            itemAdapter.clearDataList();
                        }
                        itemAdapter.addDataList(commentBeans);
                        itemAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showNormalMsg(throwable.getMessage());
                    }
                });


    }

    /**
     * 歌单的评论
     *
     * @param json
     * @return
     */
    private List<CommentBean> jsonToEntiy(String json) {
        List<CommentBean> list = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(json);
        if (jsonObject.getIntValue("code") == 200) {
            int total = jsonObject.getIntValue("total");
            hasMoreData = jsonObject.getBoolean("more");
            JSONArray hotCommets = jsonObject.getJSONArray("hotComments");
            JSONArray comments = jsonObject.getJSONArray("comments");
            if (hotCommets != null && isFirstLoad) {
                isFirstLoad = false;
                int size = hotCommets.size();
                for (int i = 0; i < size; i++) {
                    CommentBean commentBean = new CommentBean();
                    JSONObject itemObject = hotCommets.getJSONObject(i);
                    UserBean userBean = new UserBean();
                    userBean.setUserCoverImgUrl(itemObject.getJSONObject("user").getString("avatarUrl"));
                    userBean.setNickName(itemObject.getJSONObject("user").getString("nickname"));
                    userBean.setUserId(itemObject.getJSONObject("user").getLongValue("userId"));
                    commentBean.setUserBean(userBean);
                    commentBean.setLikeCount(itemObject.getIntValue("likedCount"));
                    commentBean.setCreateTime(FormatData.unixTimeTostring(itemObject.getLongValue("time")));
                    commentBean.setCommentId(String.valueOf(itemObject.getLongValue("commentId")));
                    commentBean.setDescription(itemObject.getString("content"));
                    list.add(commentBean);
                }
            }
            if (comments != null) {
                int size = comments.size();
                for (int i = 0; i < size; i++) {
                    CommentBean commentBean = new CommentBean();
                    JSONObject itemObject = comments.getJSONObject(i);
                    UserBean userBean = new UserBean();
                    userBean.setUserCoverImgUrl(itemObject.getJSONObject("user").getString("avatarUrl"));
                    userBean.setNickName(itemObject.getJSONObject("user").getString("nickname"));
                    userBean.setUserId(itemObject.getJSONObject("user").getLongValue("userId"));
                    commentBean.setUserBean(userBean);
                    commentBean.setLikeCount(itemObject.getIntValue("likedCount"));
                    commentBean.setCreateTime(FormatData.unixTimeTostring(itemObject.getLongValue("time")));
                    commentBean.setCommentId(String.valueOf(itemObject.getLongValue("commentId")));
                    commentBean.setDescription(itemObject.getString("content"));
                    list.add(commentBean);
                }
            }
        }
        return list;
    }

    @Override
    protected void onNetworkChange(boolean isConnected, boolean isVpn) {
        super.onNetworkChange(isConnected, isVpn);
        if (isConnected && !hasLoadData && subCommentBean != null) {
            getCommentData(subCommentBean.getCommentId(), offset, limit, false);
        }
    }
}
