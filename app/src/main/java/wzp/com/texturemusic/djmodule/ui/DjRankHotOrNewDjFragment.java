package wzp.com.texturemusic.djmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

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
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.DJApiManager;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.RadioBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.djmodule.adapter.DjRankRadioAdapter;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:电台排行榜中的新晋电台和最热电台的界面
 * on 2017/10/28.
 */

public class DjRankHotOrNewDjFragment extends BaseFragment {
    @BindView(R.id.update_time_tv)
    TextView mUpdateTimeTv;
    @BindView(R.id.dj_rank_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private int ishotRadio = 0;
    private DjRankRadioAdapter adapter;
    private int offset = 0;
    private int limit = 100;

    public void setIshotRadio(int ishotRadio) {
        this.ishotRadio = ishotRadio;
    }


    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new DjRankRadioAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position=itemBean.getPosition();
                        RadioBean bean = adapter.getDataList().get(position);
                        Intent intent = new Intent(mContext, DjDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_DJ, bean);
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
            mContentView = inflater.inflate(R.layout.fragment_dj_rank, container,true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycleview.setLayoutManager(layoutManager);
        mRecycleview.setAdapter(adapter);
        mRecycleview.setItemAnimator(new SlideInUpAnimator());
        mRecycleview.getItemAnimator().setRemoveDuration(400);
        mRecycleview.getItemAnimator().setAddDuration(400);
        mRecycleview.getItemAnimator().setChangeDuration(400);
        mRecycleview.getItemAnimator().setMoveDuration(400);
    }


    @Override
    public void lazyLoading() {
        if (!hasLoadData) {
            loadDataAndUpdataUI(offset, limit);
        }
    }

    @SuppressLint("CheckResult")
    public void loadDataAndUpdataUI(int offset, int limit) {
        showLoadingView(true);
        DJApiManager
                .getTopDjradio(offset, limit, ishotRadio)
                .map(new Function<String, List<RadioBean>>() {
                    @Override
                    public List<RadioBean> apply(@NonNull String s) throws Exception {
                        return jsonTolistData(s);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<RadioBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<List<RadioBean>>() {
                    @Override
                    public void accept(List<RadioBean> listDatas) throws Exception {
                        hasLoadData=true;
                        cancleLoadingView(true);
                        if (listDatas != null) {
                            String updateTime = listDatas.get(0).getUpdateTime();
                            mUpdateTimeTv.setText("更新时间:" + FormatData.unixTimeTostring(Long.valueOf
                                    (updateTime)).substring(5));
                            adapter.clearDataList();
                            adapter.addDataList(listDatas);
                            adapter.notifyItemRangeInserted(0, listDatas.size());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    showErrorView();
                        ToastUtil.showNormalMsg("网络错误");
                    }
                });
    }

    public static List<RadioBean> jsonTolistData(String json) {
        List<RadioBean> list = new ArrayList<>();
        JSONObject root;
        try {
            root = new JSONObject(json);
        } catch (JSONException e) {
            root = null;
        }
        if (root != null && root.optInt("code") == 200) {
            String updateTime = String.valueOf(root.optLong("updateTime"));
            JSONArray jsArrays = root.optJSONArray("toplist");
            int length = jsArrays.length();
            JSONObject reObject;
            for (int i = 0; i < length; i++) {
                reObject = jsArrays.optJSONObject(i);
                RadioBean bean = new RadioBean();
                bean.setRadioId(String.valueOf(reObject.optLong("id")));
                bean.setRadioName(reObject.optString("name"));
                bean.setCoverImgUrl(reObject.optString("picUrl"));
                bean.setHotScore(reObject.optInt("score"));
                bean.setRank(reObject.optInt("rank"));
                bean.setLastRank(reObject.optInt("lastRank"));
                bean.setDjErName(reObject.optString("creatorName"));
                bean.setSubCount(reObject.optInt("subCount"));
                bean.setUpdateTime(updateTime);
                list.add(bean);
            }
        }
        return list;
    }

}
