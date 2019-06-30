package wzp.com.texturemusic.searchmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
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
import wzp.com.texturemusic.artistmodule.ui.ArtistDetailActivity;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.core.customui.OnLoadMoreForLinearLayoutManager;
import wzp.com.texturemusic.searchmodule.adapter.SearchArtistAdapter;
import wzp.com.texturemusic.searchmodule.listener.OnSearchListener;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/9/17.
 */

public class SearchArtistFragment extends BaseFragment implements OnSearchListener {
    @BindView(R.id.fr_search_recycleview)
    RecyclerView mRecycleview;
    @BindView(R.id.fr_search_refresh)
    SwipeRefreshLayout mRefreshLayout;
    Unbinder unbinder;
    private String lastSearchStr = "";//标识上一次搜索的数据
    private int limit = 20, offset = 0;
    private boolean lazyLoading = false;
    private SearchArtistAdapter adapter;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SearchArtistAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        ArtistBean bean = adapter.getDataList().get(itemBean.getPosition());
                        bean.setLocalArtist(false);
                        Intent intent = new Intent(mContext, ArtistDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_ARTIST, bean);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                itemBean.getView().findViewById(R.id.item_artist_img),
                                getString(R.string.shareartistdetail));
                        startActivity(intent, optionsCompat.toBundle());
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
                loadSearchDataWithRefreshUI(lastSearchStr, offset, limit, true);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycleview.addOnScrollListener(new OnLoadMoreForLinearLayoutManager(layoutManager) {
            @Override
            public void onLoadMore() {
                offset++;
                loadSearchDataWithRefreshUI(lastSearchStr, offset * limit, limit, false);
            }
        });
        mRecycleview.setAdapter(adapter);
        mRecycleview.setLayoutManager(layoutManager);
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
            loadSearchDataWithRefreshUI(lastSearchStr, offset, limit, true);
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
                    loadSearchDataWithRefreshUI(s, offset, limit, true);
                }
            }
        }
    }


    /**
     * 加载数据并刷新
     * 加载数据的类型 直接加载 或者 加载更多
     */
    @SuppressLint("CheckResult")
    private void loadSearchDataWithRefreshUI(String searchStr, int offset, int limit, final boolean isRefresh) {
        WYApiUtil.getInstance().buildSearchService()
                .getSearchData(searchStr, 100, offset, true, limit)
                .map(new Function<String, List<ArtistBean>>() {
                    @Override
                    public List<ArtistBean> apply(@NonNull String s) throws Exception {
                        return jsonToArtistBean(s);
                    }
                }).compose(this.<List<ArtistBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ArtistBean>>() {
                    @Override
                    public void accept(List<ArtistBean> list) throws Exception {
                        if (mRefreshLayout.isRefreshing()) {
                            mRefreshLayout.setRefreshing(false);
                        }
                        if (isRefresh) {
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


    private List<ArtistBean> jsonToArtistBean(String searchJson) {
        List<ArtistBean> list = new ArrayList<>();
        JSONObject rootObject = JSONObject.parseObject(searchJson);
        int requestCode = rootObject.getIntValue("code");
        if (requestCode == 200) {
            JSONObject results = rootObject.getJSONObject("result");
            JSONArray artistss = results.getJSONArray("artists");
            if (artistss != null) {
                int size = artistss.size();
                JSONObject jsonObject;
                for (int i = 0; i < size; i++) {
                    jsonObject = artistss.getJSONObject(i);
                    ArtistBean bean = new ArtistBean();
                    bean.setArtistImgUrl(jsonObject.getString("img1v1Url"));
                    bean.setArtistId(String.valueOf(jsonObject.getLongValue("id")));
                    bean.setArtistName(jsonObject.getString("name"));
                    bean.setLocalArtist(false);
                    list.add(bean);
                }
            }
        }
        return list;
    }


}
