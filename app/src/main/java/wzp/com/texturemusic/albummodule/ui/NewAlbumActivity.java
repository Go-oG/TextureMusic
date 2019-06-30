package wzp.com.texturemusic.albummodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.azoft.carousellayoutmanager.CarouselLayoutManager;
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
import wzp.com.texturemusic.albummodule.NetAlbumDetailActivity;
import wzp.com.texturemusic.albummodule.adapter.NewAlbumShelvesAdapter;
import wzp.com.texturemusic.albummodule.interf.OnAlbumScroollListener;
import wzp.com.texturemusic.api.AlbumApiManager;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.common.adapter.FragmentViewpagerAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;

/**
 * Created by Go_oG
 * Description:新碟上架界面
 * on 2018/1/25.
 */

public class NewAlbumActivity extends BaseActivityWrapper {
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;
    @BindView(R.id.ac_album_recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.m_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.m_viewpager)
    ViewPager mViewpager;
    @BindView(R.id.m_top_frame)
    FrameLayout mTopFrame;
    private NewAlbumShelvesAdapter recycleAdapter;
    private CarouselLayoutManager layoutManager;

    private static final String[] TYPES = new String[]{"ZH", "EA", "KR", "JP"};
    private static final String[] TITLES = new String[]{"内地", "欧美", "韩国", "日本"};
    private boolean isFirstLoadData = true;
    private boolean hasMoreData = true;
    private int offset = 0;
    private int limit = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBottomView(false);
        initService(false);
        setContentView(R.layout.activity_new_album);
        ButterKnife.bind(this);
        init();
        loadDataWithUI(offset, limit, false);
    }

    @SuppressLint("CheckResult")
    private void init() {
        recycleAdapter = new NewAlbumShelvesAdapter(mContext);
        recycleAdapter.getItemClickSubject()
                .compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(itemBean -> {
                    Intent intent = new Intent(mContext, NetAlbumDetailActivity.class);
                    AlbumBean bean = recycleAdapter.getDataList().get(itemBean.getPosition());
                    bean.setLocalAlbum(false);
                    intent.putExtra(AppConstant.UI_INTENT_KEY_ALBUM, bean);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(NewAlbumActivity.this,
                            itemBean.getView(), getString(R.string.sharealbumdetail));
                    startActivity(intent, optionsCompat.toBundle());
                });
        layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, false);
        mRecycleView.setAdapter(recycleAdapter);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.addOnScrollListener(new OnAlbumScroollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                offset++;
                if (hasMoreData) {
                    loadDataWithUI(offset * limit, limit, true);
                }
            }

            @Override
            public void onScrolledCenterPosition(int position) {
            }
        });
        FragmentViewpagerAdapter pagerAdapter = new FragmentViewpagerAdapter(getSupportFragmentManager());
        List<Fragment> fragmentList = new ArrayList<>();
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        for (int i = 0; i < TYPES.length; i++) {
            NewAlbumFragment fragment = new NewAlbumFragment();
            fragment.viewPool = viewPool;
            Bundle bundle = new Bundle();
            bundle.putString("type", TYPES[i]);
            fragment.setArguments(bundle);
            fragmentList.add(fragment);
        }
        pagerAdapter.setFragmentList(fragmentList);
        pagerAdapter.setTitle(TITLES);
        mViewpager.setAdapter(pagerAdapter);
        mTablayout.setupWithViewPager(mViewpager);
        toolbarTitleTv.setText("新碟上架");
    }

    @OnClick(R.id.toolar_return_img)
    public void onViewClicked() {
        finish();
    }

    @SuppressLint("CheckResult")
    private void loadDataWithUI(int offset, int limit, final boolean isLoadmore) {
        AlbumApiManager.getNewAlbum("All", limit, offset)
                .map(s -> jsonToAlbumBean(s)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<AlbumBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<AlbumBean>>() {
                    @Override
                    public void accept(List<AlbumBean> list) throws Exception {
                        if (list.isEmpty()) {
                            hasMoreData = false;
                        } else {
                            hasMoreData = true;
                        }

                        if (!isLoadmore) {
                            recycleAdapter.clearDataList();
                        }
                        recycleAdapter.addDataList(list);
                        recycleAdapter.notifyDataSetChanged();
                        if (isFirstLoadData) {
                            isFirstLoadData = false;
                            layoutManager.scrollToPosition(list.size() / 2);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

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
