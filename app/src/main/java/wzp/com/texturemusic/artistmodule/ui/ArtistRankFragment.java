package wzp.com.texturemusic.artistmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.ArtistApiManager;
import wzp.com.texturemusic.artistmodule.adapter.ArtistRankAdapter;
import wzp.com.texturemusic.artistmodule.bean.RankArtistBean;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.customui.BaseLinearLayoutManager;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.util.FormatData;

/**
 * author:Go_oG
 * date: on 2018/5/10
 * packageName: wzp.com.texturemusic.artistmodule.ui
 */
public class ArtistRankFragment extends BaseFragment {
    public static final String BUNDLE_KEY = "apiKey";
    @BindView(R.id.update_time_tv)
    TextView updateTimeTv;
    @BindView(R.id.m_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private ArtistRankAdapter mAdapter;
    private long updateTime = 0;
    private BaseLinearLayoutManager layoutManager;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ArtistRankAdapter(mContext);
        mAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        ArtistBean bean = mAdapter.getDataList().get(itemBean.getPosition()).getArtist();
                        if (view.getId() == R.id.item_artist_topic_tv) {
//                            String url = "http:music.163.com/m/activity/" + bean.getArtistId();
//                            Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                            it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
//                            getContext().startActivity(it);
                            toast("该功能正在开发中");
                        } else {
                            Intent intent = new Intent(mContext, ArtistDetailActivity.class);
                            intent.putExtra(AppConstant.UI_INTENT_KEY_ARTIST, bean);
                            startActivity(intent);
                        }
                    }
                });
        layoutManager=new BaseLinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);

    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_artist_rank, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecycleview.setItemAnimator(new SlideInUpAnimator());
        mRecycleview.getItemAnimator().setRemoveDuration(400);
        mRecycleview.getItemAnimator().setAddDuration(400);
        mRecycleview.getItemAnimator().setChangeDuration(400);
        mRecycleview.getItemAnimator().setMoveDuration(400);
        mRecycleview.setAdapter(mAdapter);
        mRecycleview.setLayoutManager(layoutManager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void lazyLoading() {
        if (!hasLoadData) {
            loadData(getArguments().getInt(BUNDLE_KEY));
        }
    }

    @SuppressLint("CheckResult")
    private void loadData(int type) {
        showLoadingView(false);
        ArtistApiManager
                .getArtistTopList(type, false)
                .map(s -> jsonToAlbumEntiy(s))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(list -> {
                    cancleLoadingView(true);
                    hasLoadData = true;
                    mAdapter.clearDataList();
                    mAdapter.addDataList(list);
                    mAdapter.notifyItemRangeInserted(0, list.size());
                    updateTimeTv.setText("更新时间:"+FormatData.unixTimeTostring(updateTime));
                }, throwable -> cancleLoadingView(false));
    }


    private List<RankArtistBean> jsonToAlbumEntiy(String json) {
        List<RankArtistBean> list = new ArrayList<>();
        JSONObject rootObject = JSON.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            JSONObject listObject = rootObject.getJSONObject("list");
            updateTime = listObject.getLongValue("updateTime");
            JSONArray artistsArray = listObject.getJSONArray("artists");
            int size = artistsArray.size();
            if (size > 0) {
                JSONObject itemObject;
                for (int i = 0; i < size; i++) {
                    itemObject = artistsArray.getJSONObject(i);
                    RankArtistBean bean = new RankArtistBean();
                    ArtistBean artistBean = new ArtistBean();
                    artistBean.setArtistName(itemObject.getString("name"));
                    artistBean.setArtistId(String.valueOf(itemObject.getLongValue("id")));
                    artistBean.setArtistImgUrl(itemObject.getString("picUrl"));
                    artistBean.setMusicCount(itemObject.getIntValue("size"));
                    artistBean.setLocalArtist(false);
                    bean.setArtist(artistBean);
                    bean.setLastRank(itemObject.getInteger("lastRank"));
                    bean.setScore(itemObject.getInteger("score"));
                    bean.setTopicPerson(itemObject.getInteger("topicPerson"));
                    if (itemObject.containsKey("transNames")) {
                        JSONArray names = itemObject.getJSONArray("transNames");
                        if (names != null && names.size() > 0) {
                            bean.setTransName(names.getString(0));
                        }
                    }
                    list.add(bean);
                }
            }
        }
        return list;
    }
}
