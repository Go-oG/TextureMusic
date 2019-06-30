package wzp.com.texturemusic.mvmodule.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.MvApiManager;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.common.adapter.RecomnentSingleAdapater;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.mvmodule.MvDetailActivity;
import wzp.com.texturemusic.mvmodule.adapter.FeaturedMvRecycleAdapter;
import wzp.com.texturemusic.mvmodule.bean.MvContentBean;

/**
 * Created by Wang on 2017/6/8.
 * mv频道中的精选Mv
 */

public class FeaturedMvFragment extends BaseFragment {
    public static final String MV_MAP_KEY_WY = "mvmapkey_wy";
    public static final String MV_MAP_KEY_ZH = "mvmapkey_zh";
    public static final String MV_MAP_KEY_HG = "mvmapkey_hk";
    public static final String MV_MAP_KEY_EA = "mvmapkey_ea";
    public static final String MV_MAP_KEY_KR = "mvmapkey_kr";
    public static final String MV_MAP_KEY_JP = "mvmapkey_jp";
    @BindView(R.id.fr_fraturedmv_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private FeaturedMvRecycleAdapter wyCreateAdapter, ZHAdapter, HKAdapter, EAAdapter, KRAdapter, JPAdapter;
    private DelegateAdapter mAdapter;
    private VirtualLayoutManager layoutManager;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecomnentSingleAdapater wyAdapter = new RecomnentSingleAdapater(mContext, "网易出品");
        wyAdapter.showRightImg(false);
        RecomnentSingleAdapater zhAdapter = new RecomnentSingleAdapater(mContext, "内地");
        zhAdapter.showRightImg(false);
        RecomnentSingleAdapater hkAdapter = new RecomnentSingleAdapater(mContext, "港台");
        hkAdapter.showRightImg(false);
        RecomnentSingleAdapater eaAdapter = new RecomnentSingleAdapater(mContext, "欧美");
        eaAdapter.showRightImg(false);
        RecomnentSingleAdapater krAdapter = new RecomnentSingleAdapater(mContext, "韩国");
        krAdapter.showRightImg(false);
        RecomnentSingleAdapater jpAdapter = new RecomnentSingleAdapater(mContext, "日本");
        jpAdapter.showRightImg(false);

        wyCreateAdapter = new FeaturedMvRecycleAdapter(mContext);
        wyCreateAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        MvContentBean bean = wyCreateAdapter.getDataList().get(position);
                        Intent intent = new Intent(mContext, MvDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_MV, bean.getMvId());
                        startActivity(intent);
                    }
                });
        ZHAdapter = new FeaturedMvRecycleAdapter(mContext);

        ZHAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        MvContentBean bean = ZHAdapter.getDataList().get(position);
                        Intent intent = new Intent(mContext, MvDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_MV, bean.getMvId());
                        startActivity(intent);
                    }
                });
        HKAdapter = new FeaturedMvRecycleAdapter(mContext);
        HKAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        MvContentBean bean = HKAdapter.getDataList().get(position);
                        Intent intent = new Intent(mContext, MvDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_MV, bean.getMvId());
                        startActivity(intent);
                    }
                });
        EAAdapter = new FeaturedMvRecycleAdapter(mContext);
        EAAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        MvContentBean bean = EAAdapter.getDataList().get(position);
                        Intent intent = new Intent(mContext, MvDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_MV, bean.getMvId());
                        startActivity(intent);
                    }
                });
        KRAdapter = new FeaturedMvRecycleAdapter(mContext);
        KRAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        MvContentBean bean = KRAdapter.getDataList().get(position);
                        Intent intent = new Intent(mContext, MvDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_MV, bean.getMvId());
                        startActivity(intent);
                    }
                });
        JPAdapter = new FeaturedMvRecycleAdapter(mContext);
        JPAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        MvContentBean bean = JPAdapter.getDataList().get(position);
                        Intent intent = new Intent(mContext, MvDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_MV, bean.getMvId());
                        startActivity(intent);
                    }
                });
        List<DelegateAdapter.Adapter> adapterList = new ArrayList<>();
        adapterList.add(wyAdapter);
        adapterList.add(wyCreateAdapter);
        adapterList.add(zhAdapter);
        adapterList.add(ZHAdapter);
        adapterList.add(hkAdapter);
        adapterList.add(HKAdapter);
        adapterList.add(eaAdapter);
        adapterList.add(EAAdapter);
        adapterList.add(krAdapter);
        adapterList.add(KRAdapter);
        adapterList.add(jpAdapter);
        adapterList.add(JPAdapter);
        layoutManager = new VirtualLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mAdapter = new DelegateAdapter(layoutManager);
        mAdapter.setAdapters(adapterList);
    }


    @Override
    public void lazyLoading() {
        if (!hasLoadData) {
            loadDataAndUpdateUI();
        }

    }

    @SuppressLint("CheckResult")
    private void loadDataAndUpdateUI() {
        Observable.zip(MvApiManager.getMvChannelsCoverData(),
                MvApiManager.getCoverForWY(0, 2),
                new BiFunction<String, String, Map<String, List<MvContentBean>>>() {
                    @Override
                    public Map<String, List<MvContentBean>> apply(@NonNull String s, @NonNull String s2) throws Exception {
                        Map<String, List<MvContentBean>> map = jsonToEntiy(s);
                        map.put(MV_MAP_KEY_WY, jsonToWyEntiy(s2));
                        return map;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Map<String, List<MvContentBean>>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<Map<String, List<MvContentBean>>>() {
                    @Override
                    public void accept(@NonNull Map<String, List<MvContentBean>> stringListMap) throws Exception {
                        hasLoadData = true;
                        updateUI(stringListMap);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private Map<String, List<MvContentBean>> jsonToEntiy(String json) {
        Map<String, List<MvContentBean>> map = new HashMap<>();
        JSONObject root;
        try {
            root = new JSONObject(json);
        } catch (JSONException e) {
            root = null;
        }
        if (root != null && root.optInt("code") == 200) {
            JSONArray data = root.optJSONArray("data");
            int length = data.length();
            JSONObject result;
            JSONArray reArray;
            for (int i = 0; i < length; i++) {
                result = data.optJSONObject(i);
                List<MvContentBean> list = new ArrayList<>();
                reArray = result.optJSONArray("mvs");
                int reLength = reArray.length();
                String area = result.optString("area");
                JSONObject resultObject;
                for (int j = 0; j < reLength; j++) {
                    resultObject = reArray.optJSONObject(j);
                    MvContentBean bean = new MvContentBean();
                    bean.setCoverImgUrl(resultObject.optString("cover"));
                    bean.setMvName(resultObject.optString("name"));
                    bean.setMvId(resultObject.optLong("id"));
                    bean.setPlayCount(resultObject.optInt("playCount"));
                    bean.setArtistName(resultObject.optJSONArray("artists").optJSONObject(0).optString("name"));
                    bean.setArtistId(resultObject.optJSONArray("artists").optJSONObject(0).optLong("id"));
                    bean.setDescription(resultObject.optString("briefDesc"));
                    list.add(bean);
                }
                if (area.equals("内地")) {
                    map.put(MV_MAP_KEY_ZH, list);
                } else if (area.equals("港台")) {
                    map.put(MV_MAP_KEY_HG, list);
                } else if (area.equals("欧美")) {
                    map.put(MV_MAP_KEY_EA, list);
                } else if (area.equals("韩国")) {
                    map.put(MV_MAP_KEY_KR, list);
                } else if (area.equals("日本")) {
                    map.put(MV_MAP_KEY_JP, list);
                }
            }
        }
        return map;
    }

    private List<MvContentBean> jsonToWyEntiy(String json) {
        List<MvContentBean> list = new ArrayList<>();
        JSONObject root;
        try {
            root = new JSONObject(json);
        } catch (JSONException e) {
            root = null;
        }
        if (root != null && root.optInt("code") == 200) {
            JSONArray array = root.optJSONArray("data");
            int length = array.length();
            for (int i = 0; i < length; i++) {
                JSONObject resultObject = array.optJSONObject(i);
                MvContentBean bean = new MvContentBean();
                bean.setCoverImgUrl(resultObject.optString("cover"));
                bean.setMvName(resultObject.optString("name"));
                bean.setMvId(resultObject.optLong("id"));
                bean.setPlayCount(resultObject.optInt("playCount"));
                bean.setArtistName(resultObject.optJSONArray("artists").optJSONObject(0).optString("name"));
                bean.setArtistId(resultObject.optJSONArray("artists").optJSONObject(0).optLong("id"));
                bean.setDescription(resultObject.optString("briefDesc"));
                list.add(bean);
            }
        }
        return list;
    }

    private void updateUI(Map<String, List<MvContentBean>> map) {
        wyCreateAdapter.addDataList(map.get(MV_MAP_KEY_WY));
        ZHAdapter.addDataList(map.get(MV_MAP_KEY_ZH));
        HKAdapter.addDataList(map.get(MV_MAP_KEY_HG));
        EAAdapter.addDataList(map.get(MV_MAP_KEY_EA));
        KRAdapter.addDataList(map.get(MV_MAP_KEY_KR));
        JPAdapter.addDataList(map.get(MV_MAP_KEY_JP));
        wyCreateAdapter.notifyDataSetChanged();
        ZHAdapter.notifyDataSetChanged();
        HKAdapter.notifyDataSetChanged();
        EAAdapter.notifyDataSetChanged();
        KRAdapter.notifyDataSetChanged();
        JPAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        hasLoadData = true;
    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_featuredmv, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecycleview.setAdapter(mAdapter);
        mRecycleview.setLayoutManager(layoutManager);
    }
}
