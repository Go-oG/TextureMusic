package wzp.com.texturemusic.usermodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.WYApiUtil;
import wzp.com.texturemusic.bean.CommentBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.UserBean;
import wzp.com.texturemusic.common.ui.ShowImgActivity;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.core.customui.OnLoadMoreForLinearLayoutManager;
import wzp.com.texturemusic.usermodule.adapter.UserDetailEventAdapter;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:用户动态
 * on 2017/11/27.
 */

public class UserDetailEventFragment extends BaseFragment {
    @BindView(R.id.m_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private UserBean bean;
    private UserDetailEventAdapter adapter;
    private int offset = 0;
    private int limit = 20;
    private boolean hasMoreData = true;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bean = getArguments().getParcelable("bundle");
        adapter = new UserDetailEventAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int imgIndex = (int) itemBean.getView().getTag();//点击的图片位置
                        int clickPosition = itemBean.getPosition();
                        List<String> urls = adapter.getDataList().get(clickPosition).getImgUrls();
                        Intent intent = new Intent(mContext, ShowImgActivity.class);
                        intent.putStringArrayListExtra(ShowImgActivity.INTENT_KEY_DATA, (ArrayList<String>) urls);
                        intent.putExtra(ShowImgActivity.INTENT_KEY_INDEX, imgIndex);
                        itemBean.getView().setTransitionName(getString(R.string.sharemusicimg));
                        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                getActivity(),
                                 itemBean.getView().findViewById(R.id.item_child_img),
                                getString(R.string.sharemusicimg));
                        startActivity(intent, compat.toBundle());

                    }
                });
    }

    @Override
    public void lazyLoading() {
        if (!hasLoadData && hasMoreData) {
            loadData(bean.getUserId(), offset, limit, false);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_user_detail, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecycleview.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycleview.setLayoutManager(layoutManager);
        mRecycleview.addOnScrollListener(new OnLoadMoreForLinearLayoutManager(layoutManager) {
            @Override
            public void onLoadMore() {
                if (hasMoreData) {
                    offset++;
                    loadData(bean.getUserId(), offset * limit, limit, true);
                } else {
                    ToastUtil.showNormalMsg("没有更多数据了");
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void loadData(long userId, int offset, int limit, final boolean isLoadMore) {
        WYApiUtil.getInstance().buildUserService()
                .getUserEvent(userId, offset, limit)
                .map(new Function<String, List<CommentBean>>() {
                    @Override
                    public List<CommentBean> apply(String s) throws Exception {
                        return jsonToevent(s);
                    }
                }).subscribeOn(Schedulers.io())
                .compose(this.<List<CommentBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CommentBean>>() {
                    @Override
                    public void accept(List<CommentBean> commentBeans) throws Exception {
                        hasLoadData = true;
                        if (isLoadMore) {
                            int posution = adapter.getItemCount();
                            adapter.addDataList(commentBeans);
                            adapter.notifyItemRangeInserted(posution, commentBeans.size());
                        } else {
                            adapter.clearDataList();
                            adapter.addDataList(commentBeans);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {


                    }
                });
    }


    private List<CommentBean> jsonToevent(String json) {
        List<CommentBean> list = new ArrayList<>();
        JSONObject rootObject = JSONObject.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            hasMoreData = rootObject.getBoolean("more");
            int size = rootObject.getIntValue("size");
            JSONArray eventObject = rootObject.getJSONArray("events");
            if (eventObject != null && eventObject.size() > 0) {
                int siz = eventObject.size();
                JSONObject itemObject;
                for (int i = 0; i < siz; i++) {
                    itemObject = eventObject.getJSONObject(i);
                    CommentBean bean = new CommentBean();
                    bean.setLikeCount(itemObject.getJSONObject("info").getInteger("likedCount"));
                    bean.setCommentCount(itemObject.getJSONObject("info").getIntValue("commentCount"));
                    bean.setShareCount(itemObject.getJSONObject("info").getIntValue("shareCount"));
                    bean.setCommentId(itemObject.getJSONObject("info").getString("threadId"));
                    List<String> imgList = new ArrayList<>();
                    JSONArray imgArray = itemObject.getJSONArray("pics");
                    if (imgArray != null && imgArray.size() > 0) {
                        for (int j = 0; j < imgArray.size(); j++) {
                            imgList.add(imgArray.getJSONObject(j).getString("rectangleUrl"));
                        }
                    }
                    bean.setImgUrls(imgList);
                    bean.setCreateTime(String.valueOf(itemObject.getLongValue("eventTime")));
                    UserBean userBean = new UserBean();
                    userBean.setUserId(itemObject.getJSONObject("user").getLongValue("userId"));
                    userBean.setUserCoverImgUrl(itemObject.getJSONObject("user").getString("avatarUrl"));
                    userBean.setNickName(itemObject.getJSONObject("user").getString("nickname"));
                    userBean.setSignnature(itemObject.getJSONObject("user").getString("signture"));
                    bean.setUserBean(userBean);
                    String msg = itemObject.getString("json");
                    String contentMsg;
                    if (TextUtils.isEmpty(msg)) {
                        contentMsg = "";
                    } else {
                        contentMsg = JSONObject.parseObject(msg).getString("msg");
                        JSONObject resourceObject = JSONObject.parseObject(msg).getJSONObject("resource");
                        if (resourceObject != null) {
                            String s2 = resourceObject.getString("content");
                            if (!TextUtils.isEmpty(s2)) {
                                contentMsg = s2;
                            }
                            JSONObject resourceInfoObject = resourceObject.getJSONObject("resourceInfo");
                            if (resourceInfoObject != null) {
                                String musicName = resourceInfoObject.getString("name");
                                long musicId = resourceInfoObject.getLongValue("id");
                                String imgUrl = resourceInfoObject.getString("imgurl");
                                String artistName = resourceInfoObject.getJSONObject("artist")
                                        .getString("name");
                                long artistId = resourceInfoObject.getJSONObject("artist")
                                        .getLongValue("id");
                                String artistImgUrl = resourceInfoObject.getJSONObject("artist")
                                        .getString("picUrl");
                                MusicBean musicBean = new MusicBean();
                                musicBean.setMusicBitrate(AppConstant.MUSIC_BITRATE_NORMAL);
                                musicBean.setLocalMusic(false);
                                musicBean.setMusicId(String.valueOf(musicId));
                                musicBean.setMusicName(musicName);
                                musicBean.setCoverImgUrl(imgUrl);
                                musicBean.setArtistName(artistName);
                                musicBean.setArtistId(String.valueOf(artistId));
                                musicBean.setArtistImgUrl(artistImgUrl);
                                bean.setMusicBean(musicBean);
                            }
                        }
                    }
                    bean.setDescription(contentMsg);
                    list.add(bean);
                }
            }
        }
        return list;
    }

}
