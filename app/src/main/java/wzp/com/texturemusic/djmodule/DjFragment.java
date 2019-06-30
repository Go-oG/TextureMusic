package wzp.com.texturemusic.djmodule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function7;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.DJApiManager;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.RadioBean;
import wzp.com.texturemusic.common.adapter.RecomnentSingleAdapater;
import wzp.com.texturemusic.common.bean.RadioCoverBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.djmodule.adapter.DjFragmentAdapter;
import wzp.com.texturemusic.djmodule.ui.DjDetailActivity;
import wzp.com.texturemusic.djmodule.ui.DjRankActivity;
import wzp.com.texturemusic.djmodule.ui.DjTypeListActivity;
import wzp.com.texturemusic.interf.OnRecycleItemClickListener;
import wzp.com.texturemusic.util.DataCacheUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description: 主播电台界面
 * on 2017/9/17.
 */

public class DjFragment extends BaseFragment {
    //主动更新间隔 3小时
    private static final long UPDATE_TIME = 3 * 3600000;
    @BindView(R.id.fr_index_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private DjFragmentAdapter tjadapter, starAdapter, creatAdapter, talkAdapter, emotionAdapter, storyAdapter, secondAdapter;
    private DelegateAdapter delegateAdapter;
    private VirtualLayoutManager virtualLayoutManager;
    public static final String RADIO_TYPE_STAR = "star";
    public static final String RADIO_TYPE_TUIJIAN = "tuijain";
    public static final String RADIO_TYPE_CREAT = "creat";
    public static final String RADIO_TYPE_TALKSHOW = "talkshow";
    public static final String RADIO_TYPE_EMOTION = "emotion";
    public static final String RADIO_TYPE_MUSICSTORY = "musicstory";
    public static final String RADIO_TYPE_SECONDYUAN = "secondyuan";
    private RecyclerView.RecycledViewPool viewPool;
    //上次更新时间戳
    private long lastUpdateTime = 0;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewPool = new RecyclerView.RecycledViewPool();
        List<DelegateAdapter.Adapter> adapterList = new ArrayList<>();
        virtualLayoutManager = new VirtualLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        virtualLayoutManager.setRecycleChildrenOnDetach(true);
        RecomnentSingleAdapater tjDj = new RecomnentSingleAdapater(mContext, "推荐电台排行");
        adapterList.add(tjDj);
        tjadapter = new DjFragmentAdapter(mContext);
        tjadapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        int position = itemBean.getPosition();
                        Intent intent = new Intent(mContext, DjDetailActivity.class);
                        RadioBean radioBean = new RadioBean();
                        radioBean.setRadioId(tjadapter.getDataList().get(position).getAlbumId());
                        radioBean.setCoverImgUrl(tjadapter.getDataList().get(position).getAlbumImgUrl());
                        radioBean.setRadioName(tjadapter.getDataList().get(position).getAlbumName());
                        intent.putExtra(AppConstant.UI_INTENT_KEY_DJ, radioBean);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view.findViewById(R.id.comment_index_img), getString(R.string.sharedjdetail));
                        startActivity(intent, optionsCompat.toBundle());
                    }
                });
        adapterList.add(tjadapter);
        RecomnentSingleAdapater starDj = new RecomnentSingleAdapater(mContext, "明星电台");
        starDj.setClickListener(new OnRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, DjTypeListActivity.class);
                intent.putExtra(AppConstant.UI_INTENT_KEY_DJ_TYPE, "1");
                startActivity(intent);
            }
        });
        adapterList.add(starDj);
        starAdapter = new DjFragmentAdapter(mContext);

        starAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        int position = itemBean.getPosition();
                        Intent intent = new Intent(mContext, DjDetailActivity.class);
                        RadioBean radioBean = new RadioBean();
                        radioBean.setRadioId(starAdapter.getDataList().get(position).getAlbumId());
                        radioBean.setCoverImgUrl(starAdapter.getDataList().get(position).getAlbumImgUrl());
                        radioBean.setRadioName(starAdapter.getDataList().get(position).getAlbumName());
                        intent.putExtra(AppConstant.UI_INTENT_KEY_DJ, radioBean);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view.findViewById(R.id.comment_index_img), getString(R.string.sharedjdetail));
                        startActivity(intent, optionsCompat.toBundle());
                    }
                });
        adapterList.add(starAdapter);
        RecomnentSingleAdapater creatDj = new RecomnentSingleAdapater(mContext, "创作|翻唱");

        creatDj.setClickListener(new OnRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, DjTypeListActivity.class);
                intent.putExtra(AppConstant.UI_INTENT_KEY_DJ_TYPE, "2001");
                startActivity(intent);
            }
        });
        adapterList.add(creatDj);
        creatAdapter = new DjFragmentAdapter(mContext);

        creatAdapter.getItemClickSubject().compose(this.<ItemBean>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        int position = itemBean.getPosition();
                        Intent intent = new Intent(mContext, DjDetailActivity.class);
                        RadioBean radioBean = new RadioBean();
                        radioBean.setRadioId(creatAdapter.getDataList().get(position).getAlbumId());
                        radioBean.setCoverImgUrl(creatAdapter.getDataList().get(position).getAlbumImgUrl());
                        radioBean.setRadioName(creatAdapter.getDataList().get(position).getAlbumName());
                        intent.putExtra(AppConstant.UI_INTENT_KEY_DJ, radioBean);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view.findViewById(R.id.comment_index_img), getString(R.string.sharedjdetail));
                        startActivity(intent, optionsCompat.toBundle());
                    }
                });
        adapterList.add(creatAdapter);

        RecomnentSingleAdapater talkShowDj = new RecomnentSingleAdapater(mContext, "脱口秀");
        talkShowDj.setClickListener(new OnRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, DjTypeListActivity.class);
                intent.putExtra(AppConstant.UI_INTENT_KEY_DJ_TYPE, "5");
                startActivity(intent);
            }
        });
        adapterList.add(talkShowDj);
        talkAdapter = new DjFragmentAdapter(mContext);

        talkAdapter.getItemClickSubject().compose(this.<ItemBean>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        int position = itemBean.getPosition();
                        Intent intent = new Intent(mContext, DjDetailActivity.class);
                        RadioBean radioBean = new RadioBean();
                        radioBean.setRadioId(talkAdapter.getDataList().get(position).getAlbumId());
                        radioBean.setCoverImgUrl(talkAdapter.getDataList().get(position).getAlbumImgUrl());
                        radioBean.setRadioName(talkAdapter.getDataList().get(position).getAlbumName());
                        intent.putExtra(AppConstant.UI_INTENT_KEY_DJ, radioBean);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view.findViewById(R.id.comment_index_img), getString(R.string.sharedjdetail));
                        startActivity(intent, optionsCompat.toBundle());
                    }
                });
        adapterList.add(talkAdapter);

        RecomnentSingleAdapater emotionDj = new RecomnentSingleAdapater(mContext, "情感频道");
        emotionDj.setClickListener(new OnRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, DjTypeListActivity.class);
                intent.putExtra(AppConstant.UI_INTENT_KEY_DJ_TYPE, "3");
                startActivity(intent);
            }
        });
        adapterList.add(emotionDj);
        emotionAdapter = new DjFragmentAdapter(mContext);
        emotionAdapter.getItemClickSubject().compose(this.<ItemBean>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        int position = itemBean.getPosition();
                        Intent intent = new Intent(mContext, DjDetailActivity.class);
                        RadioBean radioBean = new RadioBean();
                        radioBean.setRadioId(emotionAdapter.getDataList().get(position).getAlbumId());
                        radioBean.setCoverImgUrl(emotionAdapter.getDataList().get(position).getAlbumImgUrl());
                        radioBean.setRadioName(emotionAdapter.getDataList().get(position).getAlbumName());
                        intent.putExtra(AppConstant.UI_INTENT_KEY_DJ, radioBean);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view.findViewById(R.id.comment_index_img), getString(R.string.sharedjdetail));
                        startActivity(intent, optionsCompat.toBundle());
                    }
                });
        adapterList.add(emotionAdapter);

        RecomnentSingleAdapater storyDj = new RecomnentSingleAdapater(mContext, "音乐故事");
        storyDj.setClickListener(new OnRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, DjTypeListActivity.class);
                intent.putExtra(AppConstant.UI_INTENT_KEY_DJ_TYPE, "2");
                startActivity(intent);
            }
        });
        adapterList.add(storyDj);
        storyAdapter = new DjFragmentAdapter(mContext);
        storyAdapter.getItemClickSubject().compose(this.<ItemBean>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        int position = itemBean.getPosition();
                        Intent intent = new Intent(mContext, DjDetailActivity.class);
                        RadioBean radioBean = new RadioBean();
                        radioBean.setRadioId(storyAdapter.getDataList().get(position).getAlbumId());
                        radioBean.setCoverImgUrl(storyAdapter.getDataList().get(position).getAlbumImgUrl());
                        radioBean.setRadioName(storyAdapter.getDataList().get(position).getAlbumName());
                        intent.putExtra(AppConstant.UI_INTENT_KEY_DJ, radioBean);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view.findViewById(R.id.comment_index_img), getString(R.string.sharedjdetail));
                        startActivity(intent, optionsCompat.toBundle());
                    }
                });
        adapterList.add(storyAdapter);

        RecomnentSingleAdapater secondDj = new RecomnentSingleAdapater(mContext, "二次元");
        secondDj.setClickListener(new OnRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, DjTypeListActivity.class);
                intent.putExtra(AppConstant.UI_INTENT_KEY_DJ_TYPE, "3001");
                startActivity(intent);
            }
        });
        adapterList.add(secondDj);

        secondAdapter = new DjFragmentAdapter(mContext);

        secondAdapter.getItemClickSubject().compose(this.<ItemBean>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        int position = itemBean.getPosition();
                        Intent intent = new Intent(mContext, DjDetailActivity.class);
                        RadioBean radioBean = new RadioBean();
                        radioBean.setRadioId(secondAdapter.getDataList().get(position).getAlbumId());
                        radioBean.setCoverImgUrl(secondAdapter.getDataList().get(position).getAlbumImgUrl());
                        radioBean.setRadioName(secondAdapter.getDataList().get(position).getAlbumName());
                        intent.putExtra(AppConstant.UI_INTENT_KEY_DJ, radioBean);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view.findViewById(R.id.comment_index_img), getString(R.string.sharedjdetail));
                        startActivity(intent, optionsCompat.toBundle());
                    }
                });
        adapterList.add(secondAdapter);
        delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.setAdapters(adapterList);

    }


    @Override
    public void lazyLoading() {
        long currentTime = System.currentTimeMillis();
        if (!hasLoadData) {
            lastUpdateTime = currentTime;
            loadDataFromNet();
        } else {
            if (currentTime - lastUpdateTime >= UPDATE_TIME) {
                lastUpdateTime = currentTime;
                loadDataFromNet();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_dj, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecycleview.setRecycledViewPool(viewPool);
        mRecycleview.setItemViewCacheSize(30);
        mRecycleview.setDrawingCacheEnabled(true);
        mRecycleview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecycleview.setAdapter(delegateAdapter);
        mRecycleview.setLayoutManager(virtualLayoutManager);
    }

    @SuppressLint("CheckResult")
    private void loadDataFromNet() {
        if (tjadapter.getDataList().isEmpty()) {
            mRecycleview.setVisibility(View.GONE);
            showLoadingView(true);
        }
        Observable.zip(
                DJApiManager.getDjHotDatas(0, 6),
                DJApiManager.getDjDiffentTypeRecomment("1", 0, 3),
                DJApiManager.getDjDiffentTypeRecomment("2001", 0, 3),
                DJApiManager.getDjDiffentTypeRecomment("5", 0, 3),
                DJApiManager.getDjDiffentTypeRecomment("3", 0, 3),
                DJApiManager.getDjDiffentTypeRecomment("2", 0, 3),
                DJApiManager.getDjDiffentTypeRecomment("3001", 0, 3),
                new Function7<String, String, String, String, String, String, String, Map<String, List<AlbumBean>>>() {
                    @Override
                    public Map<String, List<AlbumBean>> apply(@NonNull String tuijian, @NonNull String star, @NonNull String creat,
                                                              @NonNull String talkshow, @NonNull String emotion, @NonNull String story,
                                                              @NonNull String erciyuan) throws Exception {
                        Map<String, List<AlbumBean>> map = new ArrayMap<>();
                        map.put(RADIO_TYPE_TUIJIAN, jsonToEntiy(tuijian));
                        map.put(RADIO_TYPE_STAR, jsonToEntiy(star));
                        map.put(RADIO_TYPE_CREAT, jsonToEntiy(creat));
                        map.put(RADIO_TYPE_TALKSHOW, jsonToEntiy(talkshow));
                        map.put(RADIO_TYPE_EMOTION, jsonToEntiy(emotion));
                        map.put(RADIO_TYPE_MUSICSTORY, jsonToEntiy(story));
                        map.put(RADIO_TYPE_SECONDYUAN, jsonToEntiy(erciyuan));
                        return map;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Map<String, List<AlbumBean>>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<Map<String, List<AlbumBean>>>() {
                    @Override
                    public void accept(Map<String, List<AlbumBean>> map) throws Exception {
                        hasLoadData = true;
                        RadioCoverBean coverBean = new RadioCoverBean();
                        coverBean.setMap(map);
                        DataCacheUtil.saveRadioCoverData(JSONObject.toJSONString(coverBean));
                        tjadapter.clearDataList();
                        tjadapter.addDataList(map.get(RADIO_TYPE_TUIJIAN));
                        tjadapter.notifyDataSetChanged();

                        starAdapter.clearDataList();
                        starAdapter.addDataList(map.get(RADIO_TYPE_STAR));
                        starAdapter.notifyDataSetChanged();

                        creatAdapter.clearDataList();
                        creatAdapter.addDataList(map.get(RADIO_TYPE_CREAT));
                        creatAdapter.notifyDataSetChanged();

                        talkAdapter.clearDataList();
                        talkAdapter.addDataList(map.get(RADIO_TYPE_TALKSHOW));
                        talkAdapter.notifyDataSetChanged();

                        emotionAdapter.clearDataList();
                        emotionAdapter.addDataList(map.get(RADIO_TYPE_EMOTION));
                        emotionAdapter.notifyDataSetChanged();

                        storyAdapter.clearDataList();
                        storyAdapter.addDataList(map.get(RADIO_TYPE_MUSICSTORY));
                        storyAdapter.notifyDataSetChanged();

                        secondAdapter.clearDataList();
                        secondAdapter.addDataList(map.get(RADIO_TYPE_SECONDYUAN));
                        secondAdapter.notifyDataSetChanged();
                        mRecycleview.setVisibility(View.VISIBLE);
                        showContentView();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        cancleLoadingView(true);
                        mRecycleview.setVisibility(View.VISIBLE);
                        loadDataFromLocal();
                    }
                });
    }

    private List<AlbumBean> jsonToEntiy(String json) {
        List<AlbumBean> dataList = new ArrayList<>();
        JSONObject root;
        try {
            root = JSON.parseObject(json);
        } catch (JSONException e) {
            root = null;
        }
        if (root != null && root.getIntValue("code") == 200) {
            JSONArray jsonArray = root.getJSONArray("djRadios");
            if (jsonArray != null) {
                int length = jsonArray.size();
                JSONObject reObject;
                for (int i = 0; i < length; i++) {
                    reObject = jsonArray.getJSONObject(i);
                    AlbumBean bean = new AlbumBean();
                    String coverImgUrl = reObject.getString("picUrl");
                    Long djId = reObject.getLong("id");
                    String djName = reObject.getString("name");
                    bean.setAlbumImgUrl(coverImgUrl);
                    bean.setAlbumName(djName);
                    bean.setAlbumId(String.valueOf(djId));
                    dataList.add(bean);
                }
            }
        }

        return dataList;
    }

    private void loadDataFromLocal() {
        RadioCoverBean bean = JSONObject.parseObject(DataCacheUtil.getRadioCoverData(), RadioCoverBean.class);
        if (bean != null && bean.getMap() != null) {
            Map<String, List<AlbumBean>> map = bean.getMap();
            tjadapter.clearDataList();
            tjadapter.addDataList(map.get(RADIO_TYPE_TUIJIAN));
            tjadapter.notifyDataSetChanged();
            starAdapter.clearDataList();
            starAdapter.addDataList(map.get(RADIO_TYPE_STAR));
            starAdapter.notifyDataSetChanged();
            creatAdapter.clearDataList();
            creatAdapter.addDataList(map.get(RADIO_TYPE_CREAT));
            creatAdapter.notifyDataSetChanged();
            talkAdapter.clearDataList();
            talkAdapter.addDataList(map.get(RADIO_TYPE_TALKSHOW));
            talkAdapter.notifyDataSetChanged();
            emotionAdapter.clearDataList();
            emotionAdapter.addDataList(map.get(RADIO_TYPE_EMOTION));
            emotionAdapter.notifyDataSetChanged();
            storyAdapter.clearDataList();
            storyAdapter.addDataList(map.get(RADIO_TYPE_MUSICSTORY));
            storyAdapter.notifyDataSetChanged();
            secondAdapter.clearDataList();
            secondAdapter.addDataList(map.get(RADIO_TYPE_SECONDYUAN));
            secondAdapter.notifyDataSetChanged();
            showContentView();
        } else {
            showErrorView();
            ToastUtil.showNormalMsg("暂无网络");
        }

    }

    private void showTypeDialog() {
        new MaterialDialog.Builder(mContext)
                .title("选择分类")
                .autoDismiss(true)
                .theme(Theme.LIGHT)
                .items(R.array.radiolistname)
                .itemsIds(R.array.radiolistIds)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        Intent intent = new Intent(mContext, DjTypeListActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_DJ_TYPE, getResources().getIntArray(R.array.radiolistIds)[position] + "");
                        startActivity(intent);
                    }
                })
                .show();
    }

    /**
     * 跳转到电台排行界面
     */
    private void gotoDjTopActivity() {
        Intent intent = new Intent(mContext, DjRankActivity.class);
        startActivity(intent);
    }

    @OnClick({R.id.fr_radio_fenleiRelative, R.id.fr_radio_topRelative})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fr_radio_fenleiRelative:
                showTypeDialog();
                break;
            case R.id.fr_radio_topRelative:
                gotoDjTopActivity();
                break;
        }
    }

}
