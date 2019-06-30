package wzp.com.texturemusic.albummodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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
import wzp.com.texturemusic.albummodule.NetAlbumDetailActivity;
import wzp.com.texturemusic.albummodule.adapter.NewAlbumAdapter;
import wzp.com.texturemusic.api.AlbumApiManager;
import wzp.com.texturemusic.artistmodule.ui.ArtistDetailActivity;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.customui.BaseRecyclerView;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.core.customui.OnLoadMoreForLinearLayoutManager;

/**
 * Created by Go_oG
 * Description:用于展示不同分类的专辑列表
 */

public class NewAlbumFragment extends BaseFragment {
    @BindView(R.id.m_recyclerview)
    BaseRecyclerView mRecyclerview;
    @BindView(R.id.m_swiperefresh)
    SwipeRefreshLayout mSwiperefresh;
    Unbinder unbinder;
    private String TYPE = "ALL";
    private boolean hasMoreData = true;
    private boolean hasLoadData = false;
    private int offset = 0;
    private int limit = 20;
    private NewAlbumAdapter adapter;
    private GridLayoutManager layoutManager;
    RecyclerView.RecycledViewPool viewPool;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TYPE = getArguments().getString("type", "ALl");
        adapter = new NewAlbumAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        View view = itemBean.getView();
                        if (view.getId() == R.id.item_img) {
                            //点击的专辑封面
                            Intent intent = new Intent(mContext, NetAlbumDetailActivity.class);
                            AlbumBean bean = adapter.getDataList().get(position);
                            bean.setLocalAlbum(false);
                            intent.putExtra(AppConstant.UI_INTENT_KEY_ALBUM, bean);
                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                    view, getString(R.string.sharealbumdetail));
                            startActivity(intent);
                        } else if (view.getId() == R.id.item_artist_linear) {
                            //点击了歌手界面
                            Intent intent = new Intent(mContext, ArtistDetailActivity.class);
                            ArtistBean bean = adapter.getDataList().get(position).getArtistBean();
                            bean.setLocalArtist(false);
                            intent.putExtra(AppConstant.UI_INTENT_KEY_ARTIST, bean);
                            startActivity(intent);
                        }
                    }
                });
        layoutManager = new GridLayoutManager(mContext, 2);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_new_album, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecyclerview.setFlingScale(1.35);
        if (viewPool != null) {
            mRecyclerview.setRecycledViewPool(viewPool);
        }
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.addOnScrollListener(new OnLoadMoreForLinearLayoutManager(layoutManager) {
            @Override
            public void onLoadMore() {
                if (hasMoreData) {
                    offset++;
                    loadDataWithUI(offset * limit, limit, true);
                }
            }
        });
        mSwiperefresh.setEnabled(false);
    }

    @Override
    public void lazyLoading() {
        if (!hasLoadData) {
            loadDataWithUI(offset, limit, false);
        }

    }

    @SuppressLint("CheckResult")
    private void loadDataWithUI(int offset, int limit, final boolean isLoadmore) {
        AlbumApiManager.getNewAlbum(TYPE, limit, offset)
                .map(s -> jsonToAlbumBean(s)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<AlbumBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<List<AlbumBean>>() {
                    @Override
                    public void accept(List<AlbumBean> list) throws Exception {
                        hasLoadData = true;
                        if (list.isEmpty()) {
                            hasMoreData = false;
                        } else {
                            hasMoreData = true;
                        }
                        if (!isLoadmore) {
                            adapter.clearDataList();
                            adapter.addDataList(list);
                            adapter.notifyDataSetChanged();
                        } else {
                            //加载更多
                            int endIndex = adapter.getDataList().size();
                            adapter.addDataList(list);
                            adapter.notifyItemRangeInserted(endIndex, list.size());
                        }
                    }
                });

    }

    public List<AlbumBean> jsonToAlbumBean(String json) {
        List<AlbumBean> list = new ArrayList<>();
        JSONObject root;
        root = JSONObject.parseObject(json);
        if (root != null && root.getIntValue("code") == 200) {
            JSONArray jsonArrays = root.getJSONArray("albums");
            int length = jsonArrays.size();
            JSONObject reObject;
            for (int i = 0; i < length; i++) {
                reObject = jsonArrays.getJSONObject(i);
                JSONObject artistObject = reObject.getJSONObject("artist");
                AlbumBean bean = new AlbumBean();
                bean.setAlbumId(String.valueOf(reObject.getLongValue("id")));
                bean.setAlbumName(reObject.getString("name"));
                bean.setAlbumImgUrl(reObject.getString("picUrl"));
                bean.setCommentId(reObject.getString("commentThreadId"));
                bean.setPublishTime(reObject.getLong("publishTime"));
                bean.setPublishCompany(reObject.getString("company"));
                bean.setDescription(reObject.getString("description"));
                ArtistBean artistBean = new ArtistBean();
                artistBean.setArtistId(String.valueOf(artistObject.getLongValue("id")));
                artistBean.setArtistName(artistObject.getString("name"));
                artistBean.setArtistImgUrl(artistObject.getString("picUrl"));
                artistBean.setAlbumCount(artistObject.getIntValue("albumSize"));
                artistBean.setMusicCount(artistObject.getIntValue("musicSize"));
                bean.setArtistBean(artistBean);
                bean.setHasLoadData(false);
                list.add(bean);
            }
        }
        return list;
    }

}
