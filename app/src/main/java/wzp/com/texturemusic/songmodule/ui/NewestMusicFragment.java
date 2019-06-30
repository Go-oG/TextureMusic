package wzp.com.texturemusic.songmodule.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

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
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.WYApiUtil;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.songmodule.NewestMusicActivity;
import wzp.com.texturemusic.songmodule.adapter.NewestAdapter;
import wzp.com.texturemusic.util.SPSetingUtil;

/**
 * Created by Go_oG
 * Description:最新音乐
 * on 2017/10/30.
 */
public class NewestMusicFragment extends BaseFragment {
    @BindView(R.id.newwest_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    @BindView(R.id.newest_refreshlayout)
    SwipeRefreshLayout mRefreshlayout;
    private static final String AREA_IDS[] = new String[]{"0", "96", "16", "8"};
    private static final int[] headImgResouce = new int[]{R.drawable.newsongs_zh, R.drawable.newsongs_ea, R.drawable.newsongs_kr, R.drawable.newsongs_jp};
    private int fragTag = 0;
    private LinearLayoutManager layoutManager;

    private NewestAdapter mAdapter;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragTag = getArguments().getInt("types", 0);
        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mAdapter=new NewestAdapter(mContext);
        mAdapter.setImgResouceId(headImgResouce[fragTag]);
        mAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        int position = itemBean.getPosition();
                        MusicBean bean = mAdapter.getDataList().get(position);
                        if (view.getId() == R.id.item_operation_img) {
                            ((NewestMusicActivity) getActivity()).showMusicInfoPop(bean, null);
                        } else {
                            ((NewestMusicActivity) getActivity()).playMusic(bean);
                        }
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
            mContentView = inflater.inflate(R.layout.fragment_new_west, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateDataWithUI();
            }
        });
        mRefreshlayout.setColorSchemeColors(getMainColor());
        mRecycleview.setLayoutManager(layoutManager);
        mRecycleview.setAdapter(mAdapter);
        mRecycleview.setItemAnimator(new SlideInUpAnimator(new LinearInterpolator()));
        mRecycleview.getItemAnimator().setAddDuration(600);
        mRecycleview.getItemAnimator().setChangeDuration(600);
    }

    @Override
    public void lazyLoading() {
        if (!hasLoadData) {
            updateDataWithUI();
        }
    }

    @SuppressLint("CheckResult")
    private void updateDataWithUI() {
        WYApiUtil.getInstance().buildSongService()
                .getFirstNewMusic(AREA_IDS[fragTag])
                .map(new Function<String, List<MusicBean>>() {
                    @Override
                    public List<MusicBean> apply(String s) throws Exception {
                        return jsonToEntiy(s);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<MusicBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<List<MusicBean>>() {
                    @Override
                    public void accept(List<MusicBean> musicBeans) throws Exception {
                        hasLoadData = true;
                        if (mRefreshlayout.isRefreshing()) {
                            mRefreshlayout.setRefreshing(false);
                        }
                        mAdapter.clearDataList();
                        mAdapter.addDataList(musicBeans);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }


    private List<MusicBean> jsonToEntiy(String json) {
        int bit = SPSetingUtil.getIntValue(AppConstant.SP_KEY_PLAY_QUALITY, AppConstant.MUSIC_BITRATE_NORMAL);
        List<MusicBean> list = new ArrayList<>();
        JSONObject root = JSONObject.parseObject(json);
        if (root != null && root.getIntValue("code") == 200) {
            JSONArray datas = root.getJSONArray("data");
            int length = datas.size();
            JSONObject result;
            for (int i = 0; i < length; i++) {
                result = datas.getJSONObject(i);
                MusicBean bean = new MusicBean();
                bean.setMusicBitrate(bit);
                bean.setMusicId(String.valueOf(result.getLong("id")));
                bean.setLocalMusic(false);
                bean.setCommentId(result.getString("commentThreadId"));
                bean.setMusicName(result.getString("name"));
                bean.setAllTime(result.getLong("duration"));
                String albumImgUrl = result.getJSONObject("album").getString("picUrl");
                bean.setCoverImgUrl(albumImgUrl);
                bean.setAlbumImgUrl(albumImgUrl);
                bean.setArtistName(result.getJSONArray("artists").getJSONObject(0).getString("name"));
                bean.setArtistId(String.valueOf(result.getJSONArray("artists").getJSONObject(0).getLong("id")));
                bean.setArtistImgUrl(result.getJSONArray("artists").getJSONObject(0).getString("img1v1Url"));
                bean.setAlbumName(result.getJSONObject("album").getString("name"));
                bean.setAlbumId(String.valueOf(result.getJSONObject("album").getLong("id")));
                long mvId = result.getLong("mvid");
                if (mvId != 0L) {
                    bean.setHasMV(true);
                    MvBean mvBean = new MvBean();
                    mvBean.setMvId(mvId);
                    bean.setMvBean(mvBean);
                } else {
                    bean.setHasMV(false);
                }
                list.add(bean);
            }
        }
        return list;
    }

}
