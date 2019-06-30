package wzp.com.texturemusic.searchmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.WYApiUtil;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.PlayListBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.customui.BaseGridLayoutManager;
import wzp.com.texturemusic.core.customui.BaseRecyclerView;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.core.customui.OnLoadMoreForLinearLayoutManager;
import wzp.com.texturemusic.playlistmodule.PlaylistDetailActivity;
import wzp.com.texturemusic.searchmodule.adapter.SearchPlayListAdapter;
import wzp.com.texturemusic.searchmodule.listener.OnSearchListener;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/9/17.
 */

public class SearchPlayListFragment extends BaseFragment implements OnSearchListener {
    @BindView(R.id.fr_search_recycleview)
    BaseRecyclerView mRecycleview;
    @BindView(R.id.fr_search_refresh)
    SwipeRefreshLayout mRefreshLayout;
    Unbinder unbinder;
    private String lastSearchStr = "";//标识上一次搜索的数据
    private SearchPlayListAdapter adapter;
    private int offset = 0;
    private int limit = 30;
    private boolean lazyLoading = false;
    private BaseGridLayoutManager layoutManager;


    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SearchPlayListAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position=itemBean.getPosition();
                        PlayListBean bean = adapter.getDataList().get(position);
                        Intent intent = new Intent(mContext, PlaylistDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST, bean.getPlaylistId());
                        intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST_IMG, bean.getCoverImgUr());
                        startActivity(intent);
                    }
                });
        layoutManager=new BaseGridLayoutManager(mContext,2);
        layoutManager.setRecycleChildrenOnDetach(true);
        layoutManager.setExtraLayoutSpace(200);
        layoutManager.setSlideSpeedRatio(3);
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
        mRecycleview.setFlingScale(1.5);
        mRecycleview.setHasFixedSize(true);
        mRecycleview.setAdapter(adapter);
        mRecycleview.setLayoutManager(layoutManager);
        mRecycleview.addOnScrollListener(new OnLoadMoreForLinearLayoutManager(layoutManager) {
            @Override
            public void onLoadMore() {
                offset++;
                loadSearchDataWithRefreshUI(lastSearchStr, offset * limit, limit, true);
            }
        });
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

    @SuppressLint("CheckResult")
    private void loadSearchDataWithRefreshUI(String searchStr, int offset, int limit, final boolean isLoadMore) {
        WYApiUtil.getInstance().buildSearchService()
                .getSearchData(searchStr, 1000, offset, true, limit)
                .map(new Function<String, List<PlayListBean>>() {
                    @Override
                    public List<PlayListBean> apply(@NonNull String s) throws Exception {
                        return jsonToPlayListBean(s);
                    }
                }).compose(this.<List<PlayListBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<PlayListBean>>() {
                    @Override
                    public void accept(List<PlayListBean> list) throws Exception {
                        if (mRefreshLayout.isRefreshing()) {
                            mRefreshLayout.setRefreshing(false);
                        }
                        if (isLoadMore) {
                            adapter.addDataList(list);
                        } else {
                            adapter.clearDataList();
                            adapter.addDataList(list);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showNormalMsg("出错啦");
                    }
                });
    }

    private List<PlayListBean> jsonToPlayListBean(String searchJson) {
        List<PlayListBean> list = new ArrayList<>();
        JSONObject rootObject = JSONObject.parseObject(searchJson);
        int requestCode = rootObject.getIntValue("code");
        if (requestCode == 200) {
            JSONObject results = rootObject.getJSONObject("result");
            JSONArray playlists = results.getJSONArray("playlists");
            if (playlists != null) {
                int size = playlists.size();
                JSONObject jsonObject;
                for (int i = 0; i < size; i++) {
                    jsonObject = playlists.getJSONObject(i);
                    PlayListBean bean = new PlayListBean();
                    bean.setCoverImgUr(jsonObject.getString("coverImgUrl"));
                    bean.setCreaterId(jsonObject.getJSONObject("creator").getLongValue("userId"));
                    bean.setCreaterName(jsonObject.getJSONObject("creator").getString("nickname"));
                    bean.setPlaylistId(jsonObject.getLongValue("id"));
                    bean.setPlaylistName(jsonObject.getString("name"));
                    bean.setPlayCount(jsonObject.getIntValue("playCount"));
                    list.add(bean);
                }
            }
        }
        return list;
    }
}
