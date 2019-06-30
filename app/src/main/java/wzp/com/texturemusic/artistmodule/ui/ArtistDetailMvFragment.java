package wzp.com.texturemusic.artistmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
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
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.ArtistApiManager;
import wzp.com.texturemusic.artistmodule.adapter.ArtistDetailMvAdapter;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.core.customui.OnLoadMoreForLinearLayoutManager;
import wzp.com.texturemusic.mvmodule.MvDetailActivity;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/11/13.
 */

public class ArtistDetailMvFragment extends BaseFragment {
    @BindView(R.id.m_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private ArtistDetailMvAdapter adapter;
    private boolean hasMoreData = true;
    private boolean isFirstLoad = true;
    private int offset = 0;
    private int limit = 20;
    private ArtistBean bean;
    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArtistDetailMvAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        long mvId = adapter.getDataList().get(itemBean.getPosition()).getMvId();
                        Intent intent = new Intent(mContext, MvDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_MV, mvId);
                        startActivity(intent);
                    }
                });
        bean = getArguments().getParcelable(ArtistDetailActivity.BUNDLE_KEY);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_artist_detail_music, container,true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecycleview.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycleview.setLayoutManager(layoutManager);
        mRecycleview.addOnScrollListener(new OnLoadMoreForLinearLayoutManager(layoutManager) {
            @Override
            public void onLoadMore() {
                offset++;
                if (!bean.getLocalArtist()) {
                    if (hasMoreData) {
                        offset++;
                        loadData(bean.getArtistId(), offset * limit, limit, true);
                    } else {
                        toast("没有更多数据了");
                    }
                }
            }
        });
        SlideInLeftAnimator animator = new SlideInLeftAnimator();
        mRecycleview.setItemAnimator(animator);
        mRecycleview.getItemAnimator().setAddDuration(600);
        mRecycleview.getItemAnimator().setChangeDuration(600);
    }


    @Override
    public void lazyLoading() {
        if (!hasLoadData) {
            if (!bean.getLocalArtist()) {
                if (hasMoreData) {
                    loadData(bean.getArtistId(), offset, limit, false);
                } else {
                    toast("没有更多数据了");
                }
            }
        }

    }
    @SuppressLint("CheckResult")
    private void loadData(String artistID, int offset, int limit, final boolean isLoadMore) {
        if (isFirstLoad) {
            showLoadingView(true);
            isFirstLoad = false;
        }
        ArtistApiManager
                .getArtistMv(artistID, offset, limit)
                .map(new Function<String, List<MvBean>>() {
                    @Override
                    public List<MvBean> apply(String s) throws Exception {
                        return jsonToMvEntiy(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<MvBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<List<MvBean>>() {
                    @Override
                    public void accept(List<MvBean> list) throws Exception {
                        cancleLoadingView(true);
                        hasLoadData=true;
                        if (list.isEmpty()){
                            ToastUtil.showNormalMsg("暂无数据");
                            return;
                        }
                        if (!isLoadMore) {
                            adapter.clearDataList();
                            adapter.addDataList(list);
                            adapter.notifyItemRangeChanged(0, list.size());
                        } else {
                            int size = adapter.getItemCount();
                            adapter.addDataList(list);
                            adapter.notifyItemRangeInserted(size - 1, list.size());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                       showErrorView();
                    }
                });

    }

    private List<MvBean> jsonToMvEntiy(String json) {
        List<MvBean> list = new ArrayList<>();
        JSONObject rootObject = JSON.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            hasMoreData = rootObject.getBoolean("hasMore");
            JSONArray songsObject = rootObject.getJSONArray("mvs");
            int size = songsObject.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    JSONObject itemObject = songsObject.getJSONObject(i);
                    MvBean bean = new MvBean();
                    bean.setPublishTime(itemObject.getString("publishTime"));
                    bean.setCoverImgUrl(itemObject.getString("imgurl"));
                    bean.setPlayCount(itemObject.getIntValue("playCount"));
                    bean.setMvName(itemObject.getString("name"));
                    bean.setMvId(itemObject.getLongValue("id"));
                    bean.setArtistName(itemObject.getString("artistName"));
                    bean.setLocalMv(false);
                    list.add(bean);
                }
            }
        }
        return list;
    }

}
