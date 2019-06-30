package wzp.com.texturemusic.mvmodule.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.android.FragmentEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import wzp.com.texturemusic.api.MvApiManager;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.customview.RecycleItemStytle;
import wzp.com.texturemusic.mvmodule.MvDetailActivity;
import wzp.com.texturemusic.mvmodule.adapter.TopMvChildRecycleAdapter;
import wzp.com.texturemusic.mvmodule.bean.MvContentBean;

/**
 * Created by Wang on 2017/6/10.
 * topmv viewpager子frag
 */

public class TopMvChildFragment extends BaseFragment {
    private static final String[] title = new String[]{"内地", "港台", "欧美", "韩国", "日本"};
    @BindView(R.id.fr_person_recycle)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private TopMvChildRecycleAdapter mAdapter;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new TopMvChildRecycleAdapter(mContext);
        mAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        MvContentBean bean = mAdapter.getDataList().get(itemBean.getPosition());
                        Intent intent = new Intent(mContext, MvDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_MV, bean.getMvId());
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
            mContentView = inflater.inflate(R.layout.fragment_person_recomment, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecycleview.addItemDecoration(new RecycleItemStytle(mContext, LinearLayoutManager.VERTICAL, 4, 0xffffff));
        mRecycleview.setAdapter(mAdapter);
        mRecycleview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void lazyLoading() {
        if (!hasLoadData) {
            TopMvFragment parentFragment = (TopMvFragment) getParentFragment();
            int positon = parentFragment.mTablayout.getSelectedTabPosition();
            loadDataAndUpdataUI(title[positon]);
        }
    }

    @SuppressLint("CheckResult")
    private void loadDataAndUpdataUI(final String type) {
        MvApiManager.getTopMv(0, 50, type)
                .map(new Function<String, List<MvContentBean>>() {
                    @Override
                    public List<MvContentBean> apply(@NonNull String s) throws Exception {
                        return jsonToEntiy(s);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<MvContentBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<List<MvContentBean>>() {
                    @Override
                    public void accept(@NonNull List<MvContentBean> list) throws Exception {
                        hasLoadData=true;
                        updateUI(list);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        toast("网络错误");
                    }
                });
    }

    private List<MvContentBean> jsonToEntiy(String json) {
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
            JSONObject result;
            for (int i = 0; i < length; i++) {
                result = array.optJSONObject(i);
                MvContentBean bean = new MvContentBean();
                bean.setCoverImgUrl(result.optString("cover"));
                bean.setMvName(result.optString("name"));
                bean.setMvId(result.optLong("id"));
                bean.setPlayCount(result.optInt("playCount"));
                bean.setDescription(result.optString("briefDesc"));
                bean.setArtistName(result.optString("artistName"));
                bean.setArtistId(result.optLong("artistId"));
                bean.setMvIndex(result.optInt("mark"));
                int lastMark = result.optInt("lastRank");
                list.add(bean);
            }
        }
        return list;
    }

    private void updateUI(List<MvContentBean> list) {
        mAdapter.clearDataList();
        mAdapter.addDataList(list);
        mAdapter.notifyDataSetChanged();
    }

}
