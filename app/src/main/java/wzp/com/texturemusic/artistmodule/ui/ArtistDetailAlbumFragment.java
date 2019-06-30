package wzp.com.texturemusic.artistmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

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
import wzp.com.texturemusic.albummodule.NetAlbumDetailActivity;
import wzp.com.texturemusic.api.ArtistApiManager;
import wzp.com.texturemusic.artistmodule.adapter.ArtistDetailAlbumAdapter;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.core.customui.OnLoadMoreForLinearLayoutManager;

/**
 * Created by Go_oG
 * Description:艺术家-》专辑
 * on 2017/11/13.
 */

public class ArtistDetailAlbumFragment extends BaseFragment {
    @BindView(R.id.m_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private ArtistDetailAlbumAdapter adapter;
    private boolean hasMoreData = true;
    private boolean isFirstLoad = true;
    private int offset = 0;
    private int limit = 20;
    private ArtistBean bean;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArtistDetailAlbumAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        Intent intent = new Intent(mContext, NetAlbumDetailActivity.class);
                        AlbumBean bean = adapter.getDataList().get(itemBean.getPosition());
                        bean.setLocalAlbum(false);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_ALBUM, bean);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                itemBean.getView().findViewById(R.id.album_img), getString(R.string.sharealbumdetail));
                        startActivity(intent, optionsCompat.toBundle());
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
            mContentView = inflater.inflate(R.layout.fragment_artist_detail_music, container, true);
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
                    }
                }
            }
        });
        SlideInLeftAnimator animator = new SlideInLeftAnimator(new OvershootInterpolator(0f));
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
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private void loadData(String artistID, int offset, final int limit, final boolean isLoadMore) {
        if (isFirstLoad) {
            showLoadingView(false);
            isFirstLoad = false;
        }
        ArtistApiManager
                .getArtistAlbum(artistID, offset, limit)
                .map(s -> jsonToAlbumEntiy(s))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<AlbumBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(list -> {
                    cancleLoadingView(true);
                    hasLoadData = true;
                    if (!isLoadMore) {
                        adapter.clearDataList();
                        adapter.addDataList(list);
                        adapter.notifyItemRangeInserted(0, list.size());
                    } else {
                        int size = adapter.getItemCount();
                        adapter.addDataList(list);
                        adapter.notifyItemRangeInserted(size - 1, list.size());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        cancleLoadingView(false);
                    }
                });

    }


    private List<AlbumBean> jsonToAlbumEntiy(String json) {
        List<AlbumBean> list = new ArrayList<>();
        JSONObject rootObject = JSON.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            hasMoreData = rootObject.getBoolean("more");
            JSONArray songsObject = rootObject.getJSONArray("hotAlbums");
            int size = songsObject.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    JSONObject itemObject = songsObject.getJSONObject(i);
                    AlbumBean bean = new AlbumBean();
                    bean.setAlbumName(itemObject.getString("name"));
                    bean.setAlbumId(String.valueOf(itemObject.getLongValue("id")));
                    bean.setPublishTime(itemObject.getLong("publishTime"));
                    bean.setAlbumImgUrl(itemObject.getString("picUrl"));
                    bean.setMusicCount(itemObject.getIntValue("size"));
                    bean.setLocalAlbum(false);
                    list.add(bean);
                }
            }
        }
        return list;
    }

}
