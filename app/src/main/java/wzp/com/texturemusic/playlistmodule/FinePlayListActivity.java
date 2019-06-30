package wzp.com.texturemusic.playlistmodule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.PlaylistApiManager;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.PlayListBean;
import wzp.com.texturemusic.bean.UserBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.core.customui.OnLoadMoreForLinearLayoutManager;
import wzp.com.texturemusic.playlistmodule.adapter.FinePlaylistItemAdapter;
import wzp.com.texturemusic.playlistmodule.ui.PlayListTypeActivity;
import wzp.com.texturemusic.usermodule.UserDetailActivity;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:精品歌单列表展示界面
 * on 2017/10/3.
 */

public class FinePlayListActivity extends BaseActivityWrapper {
    @BindView(R.id.ac_fine_recycleview)
    RecyclerView mRecycleview;
    @BindView(R.id.ac_fine_refresh)
    SwipeRefreshLayout mFineRefresh;
    private Context context;
    private boolean hasMoreData = true;
    private String cateType = "全部";//筛选
    private int offset = 0;
    private int limit = 50;
    private FinePlaylistItemAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBottomView(false);
        setContentView(R.layout.activity_fine_playlist);
        ButterKnife.bind(this);
        context = this;
        init();
        getPlaylist(null, offset * limit, limit, false);
    }

    @SuppressLint("CheckResult")
    private void init() {
        mFineRefresh.setOnRefreshListener(() -> {
            offset = 0;
            getPlaylist(cateType, offset, limit, false);
        });
        int mainColor;
        TypedArray array = this.obtainStyledAttributes(new int[]{
                R.attr.main_color
        });
        mainColor = array.getColor(0, Color.parseColor("#6e75a4"));
        array.recycle();
        mFineRefresh.setColorSchemeColors(mainColor);
        SlideInUpAnimator animator = new SlideInUpAnimator();
        mRecycleview.setItemAnimator(animator);
        mRecycleview.getItemAnimator().setAddDuration(600);
        mRecycleview.getItemAnimator().setChangeDuration(600);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        adapter = new FinePlaylistItemAdapter(context);

        adapter.getItemClickSubject().compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(itemBean -> {
                    View view=itemBean.getView();
                    int position=itemBean.getPosition();
                    Intent intent;
                    switch (view.getId()) {
                        case R.id.item_fine_user_linear:
                            intent = new Intent(mContext, UserDetailActivity.class);
                            intent.putExtra(AppConstant.UI_INTENT_KEY_USER, adapter.getDataList().get(position).getCreater());
                            startActivity(intent);
                            break;
                        case R.id.item_fine2_relative:
                            intent = new Intent(mContext, PlaylistDetailActivity.class);
                            intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST, adapter.getDataList().get(position).getPlaylistId());
                            intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST_IMG, adapter.getDataList().get(position).getCoverImgUr());
                            startActivity(intent);
                            break;
                    }
                });
        mRecycleview.setAdapter(adapter);
        mRecycleview.addOnScrollListener(new OnLoadMoreForLinearLayoutManager(layoutManager) {
            @Override
            public void onLoadMore() {
                if (hasMoreData) {
                    ++offset;
                    int off = offset * limit;
                    getPlaylist(cateType, off, limit, true);
                } else {
                    ToastUtil.showNormalMsg("已经没有更多数据了");
                }
            }
        });
        mRecycleview.setLayoutManager(layoutManager);

    }

    @OnClick({R.id.ac_fine_toolbar_break, R.id.ac_fine_toolbar_operation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ac_fine_toolbar_break:
                finish();
                break;
            case R.id.ac_fine_toolbar_operation:
                //筛选
                Intent intent = new Intent(mContext, PlayListTypeActivity.class);
                startActivityForResult(intent, 100);
                break;
        }

    }

    /**
     * 获得歌单
     *
     * @param offset
     * @param limit
     * @param isLoadMore
     * @param cateType   标签ID
     */
    @SuppressLint("CheckResult")
    private void getPlaylist(String cateType, int offset, final int limit, final boolean isLoadMore) {
        PlaylistApiManager
                .getHighQualityPlayList(cateType, offset, limit)
                .map(s -> jsonToEntiy(s)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listBeen -> {
                    hasLoadData=true;
                    if (mFineRefresh.isRefreshing()) {
                        mFineRefresh.setRefreshing(false);
                    }
                    if (!isLoadMore) {
                        int size = adapter.getDataList().size();
                        if (size == 0) {
                            adapter.addDataList(listBeen);
                            adapter.notifyItemRangeInserted(0, listBeen.size());
                        } else {
                            adapter.clearDataList();
                            adapter.addDataList(listBeen);
                            adapter.notifyItemRangeRemoved(0, size);
                        }
                    } else {
                        int size = adapter.getDataList().size();
                        adapter.addDataList(listBeen);
                        adapter.notifyItemRangeInserted(size - 1, listBeen.size());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mFineRefresh.isRefreshing()) {
                            mFineRefresh.setRefreshing(false);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            String val = data.getStringExtra("id");
            if (TextUtils.isEmpty(val)) {
                //不管
            } else {
                cateType = val;
                offset = 0;
                getPlaylist(cateType, offset, limit, false);
            }
        }
    }

    private List<PlayListBean> jsonToEntiy(String json) {
        List<PlayListBean> dataList = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(json);
        if (jsonObject.getIntValue("code") == 200) {
            JSONArray playlists = jsonObject.getJSONArray("playlists");
            hasMoreData = jsonObject.getBoolean("more");
            int size = playlists.size();
            if (size > 0) {
                JSONObject itemdata;
                for (int i = 0; i < size; i++) {
                    itemdata = playlists.getJSONObject(i);
                    PlayListBean playListBean = new PlayListBean();
                    playListBean.setPlayCount(itemdata.getInteger("playCount"));
                    playListBean.setPlaylistName(itemdata.getString("name"));
                    playListBean.setCoverImgUr(itemdata.getString("coverImgUrl"));
                    playListBean.setPlaylistId(itemdata.getLong("id"));
                    playListBean.setMusicCount(itemdata.getIntValue("trackCount"));
                    playListBean.setSubDescription(itemdata.getString("copywriter"));
                    playListBean.setCommentId(itemdata.getString("commentThreadId"));
                    playListBean.setCommentCount(itemdata.getInteger("commentCount"));
                    playListBean.setShareCount(itemdata.getIntValue("shareCount"));
                    playListBean.setDescription(itemdata.getString("description"));
                    UserBean userBean = new UserBean();
                    userBean.setGender(itemdata.getJSONObject("creator").getIntValue("gender"));
                    userBean.setUserId(itemdata.getLong("userId"));
                    userBean.setNickName(itemdata.getJSONObject("creator").getString("nickname"));
                    userBean.setUserCoverImgUrl(itemdata.getJSONObject("creator").getString("avatarUrl"));
                    userBean.setSignnature(itemdata.getJSONObject("creator").getString("signature"));
                    playListBean.setCreater(userBean);
                    dataList.add(playListBean);
                }
            }
        }
        return dataList;
    }


}
