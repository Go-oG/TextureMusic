package wzp.com.texturemusic.djmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
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
import wzp.com.texturemusic.api.DJApiManager;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.RadioBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.djmodule.adapter.DjTypeListAdapter;
import wzp.com.texturemusic.core.customui.OnLoadMoreForLinearLayoutManager;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:电台分类数据列表界面
 * on 2017/11/29.
 */

public class DjTypeListActivity extends BaseActivityWrapper {
    @BindView(R.id.dj_type_tv)
    TextView djTypeTv;
    @BindView(R.id.m_recycleview)
    RecyclerView mRecycleview;
    private DjTypeListAdapter adapter;
    private int limit = 30;
    private int offset = 0;
    private String catdId = "";
    private boolean hasMoredata = true;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dj_type_list);
        ButterKnife.bind(this);
        catdId = getIntent().getStringExtra(AppConstant.UI_INTENT_KEY_DJ_TYPE);
        adapter = new DjTypeListAdapter(mContext);
        adapter.getItemClickSubject().compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position=itemBean.getPosition();
                        RadioBean radioBean = new RadioBean();
                        radioBean.setRadioId(adapter.getDataList().get(position).getAlbumId());
                        radioBean.setCoverImgUrl(adapter.getDataList().get(position).getAlbumImgUrl());
                        radioBean.setRadioName(adapter.getDataList().get(position).getAlbumName());
                        Intent intent = new Intent(mContext, DjDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_DJ, radioBean);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(DjTypeListActivity.this,
                                itemBean.getView().findViewById(R.id.item_img), getString(R.string.sharedjdetail));
                        startActivity(intent, optionsCompat.toBundle());
                    }
                });
        mRecycleview.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycleview.setLayoutManager(layoutManager);
        mRecycleview.addOnScrollListener(new OnLoadMoreForLinearLayoutManager(layoutManager) {
            @Override
            public void onLoadMore() {
                offset++;
                loadData(catdId, offset * limit, limit, true);
            }
        });
        if (!TextUtils.isEmpty(catdId)) {
            if (hasMoredata) {
                loadData(catdId, offset, limit, false);
            } else {
                ToastUtil.showNormalMsg("没有更多数据");
            }
        }
    }


    @SuppressLint("CheckResult")
    private void loadData(String cateId, int offset, int limit, final boolean isLoadMore) {
        DJApiManager
                .getDjDiffentTypeHotData(cateId, offset, limit, 0)
                .map(new Function<String, List<AlbumBean>>() {
                    @Override
                    public List<AlbumBean> apply(String s) throws Exception {
                        return jsonToEntiy(s);
                    }
                }).subscribeOn(Schedulers.io())
                .compose(this.<List<AlbumBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AlbumBean>>() {
                    @Override
                    public void accept(List<AlbumBean> albumBeans) throws Exception {
                        if (albumBeans.size() > 0) {
                            djTypeTv.setText(albumBeans.get(0).getPublishCompany());
                        } else {
//                            djTypeTv.setText("");
                        }
                        if (!isLoadMore) {
                            adapter.clearDataList();
                        }
                        adapter.addDataList(albumBeans);
                        adapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showNormalMsg("网络访问出错");
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
            hasMoredata = root.getBoolean("hasMore");
            if (jsonArray != null) {
                int length = jsonArray.size();
                JSONObject itemObject;
                for (int i = 0; i < length; i++) {
                    itemObject = jsonArray.getJSONObject(i);
                    AlbumBean bean = new AlbumBean();
                    String categoryStr = itemObject.getString("category");
                    Long djId = itemObject.getLong("id");
                    bean.setAlbumImgUrl(itemObject.getString("picUrl"));
                    bean.setAlbumName(itemObject.getString("name"));
                    bean.setAlbumId(String.valueOf(djId));
                    bean.setMusicCount(itemObject.getInteger("programCount"));
                    bean.setLikedCount(itemObject.getIntValue("subCount"));
                    bean.setDescription(itemObject.getString("rcmdtext"));
                    bean.setPublishCompany(categoryStr);
                    dataList.add(bean);
                }
            }
        }
        return dataList;
    }

    @OnClick(R.id.m_return_img)
    public void onViewClicked() {
        finish();
    }
}
