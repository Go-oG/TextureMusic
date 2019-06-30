package wzp.com.texturemusic.searchmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.WYApiUtil;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.core.customui.OnLoadMoreForLinearLayoutManager;
import wzp.com.texturemusic.mvmodule.MvDetailActivity;
import wzp.com.texturemusic.searchmodule.adapter.SearchMVAdapter;
import wzp.com.texturemusic.searchmodule.listener.OnSearchListener;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/9/17.
 */

public class SearchMvFragment extends BaseFragment implements OnSearchListener {
    @BindView(R.id.fr_search_recycleview)
    RecyclerView mRecycleview;
    @BindView(R.id.fr_search_refresh)
    SwipeRefreshLayout mRefreshLayout;
    Unbinder unbinder;
    private String lastSearchStr = "";//标识上一次搜索的数据
    private SearchMVAdapter adapter;
    private int offset = 0;
    private int limit = 30;
    private boolean lazyLoading = false;
    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SearchMVAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position=itemBean.getPosition();
                        Intent intent = new Intent(mContext, MvDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_MV, adapter.getDataList().get(position).getMvId());
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
            mContentView = inflater.inflate(R.layout.fragment_search, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                loadSearchDataWithRefreshUI(lastSearchStr, offset, limit, false);
            }
        });
        mRecycleview.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycleview.setLayoutManager(layoutManager);
        mRecycleview.addOnScrollListener(new OnLoadMoreForLinearLayoutManager(layoutManager) {
            @Override
            public void onLoadMore() {
                offset++;
                loadSearchDataWithRefreshUI(lastSearchStr, offset * limit, limit, true);
            }
        });
        SlideInUpAnimator animator = new SlideInUpAnimator();
        mRecycleview.setItemAnimator(animator);
        mRecycleview.getItemAnimator().setAddDuration(600);
        mRecycleview.getItemAnimator().setChangeDuration(600);
    }

    @Override
    public void lazyLoading() {
        if (!lazyLoading) {
            //已经加载过同样的数据
            lazyLoading = true;
            offset = 0;
            loadSearchDataWithRefreshUI(lastSearchStr, offset, limit, false);
        }
    }


    /**
     * 准备开始搜索数据
     *
     * @param s
     */
    @Override
    public void onSearch(String s) {
        if (TextUtils.isEmpty(s)) {
            //空数据
            lastSearchStr = "";
        } else {
            if (!s.equals(lastSearchStr)) {
                //搜索数据 开始搜索
                lazyLoading = false;
                lastSearchStr = s;
                if (getUserVisibleHint() && isInitView) {
                    loadSearchDataWithRefreshUI(s, offset, limit, false);
                }
            }
        }

    }

    private List<MvBean> jsonToSearChMvBean(String searchJson) {
        List<MvBean> list = new ArrayList<>();
        JSONObject rootObject = JSONObject.parseObject(searchJson);
        int requestCode = rootObject.getIntValue("code");
        if (requestCode == 200) {
            JSONObject results = rootObject.getJSONObject("result");
            JSONArray mvs = results.getJSONArray("mvs");
            if (mvs != null) {
                int size = mvs.size();
                JSONObject jsonObject;
                for (int i = 0; i < size; i++) {
                    jsonObject = mvs.getJSONObject(i);
                    Long artistId = 0L;
                    if (jsonObject.getJSONArray("artists").getJSONObject(0) != null) {
                        artistId = jsonObject.getJSONArray("artists").getJSONObject(0).getLongValue("id");
                    }
                    MvBean bean = new MvBean();
                    bean.setPlayCount(jsonObject.getInteger("playCount"));
                    bean.setMvId(jsonObject.getLong("id"));
                    bean.setMvName(jsonObject.getString("name"));
                    bean.setCoverImgUrl(jsonObject.getString("cover"));
                    bean.setMvTime(jsonObject.getLong("duration"));
                    bean.setArtistId(artistId);
                    bean.setArtistName(jsonObject.getString("artistName"));
                    list.add(bean);
                }
            }
        }
        return list;
    }

    @SuppressLint("CheckResult")
    private void loadSearchDataWithRefreshUI(String searchStr, int offset, int limit, final boolean isLoadMore) {
        WYApiUtil.getInstance().buildSearchService()
                .getSearchData(searchStr, 1004, offset, true, limit)
                .map(new Function<String, List<MvBean>>() {
                    @Override
                    public List<MvBean> apply(@NonNull String s) throws Exception {
                        return jsonToSearChMvBean(s);
                    }
                }).compose(this.<List<MvBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MvBean>>() {
                    @Override
                    public void accept(List<MvBean> list) throws Exception {
                        if (mRefreshLayout.isRefreshing()) {
                            mRefreshLayout.setRefreshing(false);
                        }
                        if (!isLoadMore) {
                            int size = adapter.getDataList().size();
                            if (size == 0) {
                                adapter.addDataList(list);
                                adapter.notifyItemRangeInserted(0, list.size());
                            } else {
                                adapter.clearDataList();
                                adapter.addDataList(list);
                                adapter.notifyItemRangeRemoved(0, size);
                            }
                        } else {
                            int size = adapter.getDataList().size();
                            adapter.addDataList(list);
                            adapter.notifyItemRangeInserted(size, list.size());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showNormalMsg("出错啦");
                    }
                });
    }
}
