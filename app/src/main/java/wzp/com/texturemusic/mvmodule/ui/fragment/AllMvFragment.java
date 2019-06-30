package wzp.com.texturemusic.mvmodule.ui.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.PopupWindow;
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
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.MvApiManager;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.core.customui.OnLoadMoreForLinearLayoutManager;
import wzp.com.texturemusic.mvmodule.MvActivity;
import wzp.com.texturemusic.mvmodule.MvDetailActivity;
import wzp.com.texturemusic.mvmodule.adapter.AllMvRecycleAdapter;
import wzp.com.texturemusic.mvmodule.bean.MvContentBean;

/**
 * Created by Wang on 2017/6/8.
 * MV频道中的全部MV
 */

public class AllMvFragment extends BaseFragment {
    private static final String[] areas = new String[]{"全部", "内地", "港台", "欧美", "韩国", "日本"};
    private static final String[] types = new String[]{"全部", "官方版", "原声", "现场版", "网易出品"};
    private static final String[] aescs = new String[]{"上升最快", "最热", "最新"};
    private static final int TYPE_LOADMORE = 0;
    private static final int TYPE_LOADNEW = 1;
    @BindView(R.id.fr_mv_all_text)
    TextView mTagText;
    @BindView(R.id.fr_mv_all_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    PopupWindow popupWindow;
    private volatile String area = "全部", type = "全部", aesc = "上升最快";
    private AllMvRecycleAdapter mAdapter;
    private volatile int offset = 0;
    private volatile int limit = 20;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new AllMvRecycleAdapter(mContext);
        mAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        MvContentBean bean = mAdapter.getDataList().get(position);
                        Intent intent = new Intent(mContext, MvDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_MV, bean.getMvId());
                        startActivity(intent);
                    }
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void lazyLoading() {
        if (!hasLoadData) {
            showLoadingView(false);
            MvApiManager
                    .getAllMvData(area, 0, offset, limit)
                    .map(new Function<String, List<MvContentBean>>() {
                        @Override
                        public List<MvContentBean> apply(@NonNull String s) throws Exception {
                            return jsonToEntiy(s);
                        }
                    }).compose(this.<List<MvContentBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<MvContentBean>>() {
                        @Override
                        public void accept(@NonNull List<MvContentBean> list) throws Exception {
                            cancleLoadingView(true);
                            hasLoadData=true;
                            updateUI(list, TYPE_LOADNEW);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            showErrorView();
                            toast("网络错误");
                        }
                    });
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
            mContentView = inflater.inflate(R.layout.fragment_mv_all, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        mRecycleview.addOnScrollListener(new OnLoadMoreForLinearLayoutManager(layoutManager) {
            @SuppressLint("CheckResult")
            @Override
            public void onLoadMore() {
                offset++;
                int index = offset * limit;
                MvApiManager
                        .getAllMvData(area, 0, index, limit)
                        .map(new Function<String, List<MvContentBean>>() {
                            @Override
                            public List<MvContentBean> apply(@NonNull String s) throws Exception {
                                return jsonToEntiy(s);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<MvContentBean>>() {
                            @Override
                            public void accept(@NonNull List<MvContentBean> list) throws Exception {
                                updateUI(list, TYPE_LOADMORE);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                toast("网络错误");
                            }
                        });
            }
        });
        mRecycleview.setAdapter(mAdapter);
        mRecycleview.setLayoutManager(layoutManager);
        mTagText.setText(area + "." + type + "." + aesc);
        mTagText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopuWindow();
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
                bean.setMvId(result.optLong("id"));
                bean.setCoverImgUrl(result.optString("cover"));
                bean.setMvName(result.optString("name"));
                bean.setPlayCount(result.optInt("playCount"));
                bean.setDurationTime(result.optLong("duration"));
                bean.setArtistId(result.optLong("artistId"));
                bean.setArtistName(result.optString("artistName"));
                list.add(bean);
            }
        }
        return list;
    }

    private void showPopuWindow() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_allmv_tag_view, null);
        final TabLayout areaTab, typeTab, aescTab;
        areaTab = view.findViewById(R.id.allmv_tag_area);
        typeTab = view.findViewById(R.id.allmv_tag_type);
        aescTab = view.findViewById(R.id.allmv_tag_aesc);
        Button comit = view.findViewById(R.id.allmv_tag_commit_btn);

        areaTab.addTab(areaTab.newTab(), 0);
        areaTab.addTab(areaTab.newTab(), 1);
        areaTab.addTab(areaTab.newTab(), 2);
        areaTab.addTab(areaTab.newTab(), 3);
        areaTab.addTab(areaTab.newTab(), 4);
        areaTab.addTab(areaTab.newTab(), 5);
        for (int i = 0; i < 6; i++) {
            areaTab.getTabAt(i).setText(areas[i]);
        }
        typeTab.addTab(typeTab.newTab(), 0);
        typeTab.addTab(typeTab.newTab(), 1);
        typeTab.addTab(typeTab.newTab(), 2);
        typeTab.addTab(typeTab.newTab(), 3);
        typeTab.addTab(typeTab.newTab(), 4);
        for (int i = 0; i < 5; i++) {
            typeTab.getTabAt(i).setText(types[i]);
        }

        aescTab.addTab(aescTab.newTab(), 0);
        aescTab.addTab(aescTab.newTab(), 1);
        aescTab.addTab(aescTab.newTab(), 2);
        for (int i = 0; i < 3; i++) {
            aescTab.getTabAt(i).setText(aescs[i]);
        }

        comit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                offset = 0;
                area = areas[areaTab.getSelectedTabPosition()];
                type = types[typeTab.getSelectedTabPosition()];
                aesc = aescs[aescTab.getSelectedTabPosition()];

//隐藏window 开始加载数据
                MvApiManager
                        .getAllMvData(area, 0, offset, limit)
                        .map(new Function<String, List<MvContentBean>>() {
                            @Override
                            public List<MvContentBean> apply(@NonNull String s) throws Exception {
                                return jsonToEntiy(s);
                            }
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<MvContentBean>>() {
                            @Override
                            public void accept(@NonNull List<MvContentBean> list) throws Exception {
                                popupWindow.dismiss();
                                updateUI(list, TYPE_LOADNEW);
                            }
                        });
            }
        });
        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        //设置出现动画
        //  popupWindow.setAnimationStyle(R.style.localItemAnnimation);
        //监听消失事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(0.3f, 1f, 200);
            }
        });
        popupWindow.setBackgroundDrawable(mContext.getDrawable(R.drawable.time_window_back));
        popupWindow.setClippingEnabled(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        MvActivity ac = ((MvActivity) getActivity());
        // int y = ac.mToolbar.getHeight() + ac.mTablayout.getHeight() - BaseUtil.dp2px(8);
        int y = 0;
        popupWindow.showAtLocation(mRecycleview, Gravity.TOP, 0, y);
        setBackgroundAlpha(1f, 0.3f, 200);
    }


    private void setBackgroundAlpha(float startalpha, float endAlpha, long duration) {
        View view = getActivity().getWindow().getDecorView();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", startalpha, endAlpha);
        alpha.setDuration(duration); //设置动画时间
        alpha.setInterpolator(new DecelerateInterpolator());//设置动画插入器减速
        alpha.start();//启动动画。
    }

    private void updateUI(List<MvContentBean> list, int type) {
        if (type == TYPE_LOADNEW) {
            mAdapter.clearDataList();
            mAdapter.addDataList(list);
            mAdapter.notifyDataSetChanged();
        } else if (type == TYPE_LOADMORE) {
            mAdapter.addDataList(list);
            mAdapter.notifyDataSetChanged();
        }

    }

}
