package wzp.com.texturemusic.playlistmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
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
import wzp.com.texturemusic.api.PlaylistApiManager;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.KeyValueBean;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.playlistmodule.adapter.PlaylistTypeAdapter;

/**
 * Created by Go_oG
 * Description:歌单筛选界面
 * on 2017/11/22.
 */

public class PlayListTypeActivity extends BaseActivityWrapper {
    @BindView(R.id.m_recycleview)
    RecyclerView mRecycleview;
    private PlaylistTypeAdapter adapter;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_type);
        ButterKnife.bind(this);
        adapter = new PlaylistTypeAdapter(mContext);
        adapter.getItemClickSubject().compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        Intent intent = new Intent();
                        intent.putExtra("id", adapter.getDataList().get(itemBean.getPosition()).getValue());
                        setResult(101, intent);
                        finish();
                    }
                });
        mRecycleview.setAdapter(adapter);
        mRecycleview.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        getCateList();
    }

    @SuppressLint("CheckResult")
    private void getCateList() {
        PlaylistApiManager
                .getHighQualityPlayListType()
                .map(new Function<String, List<KeyValueBean>>() {
                    @Override
                    public List<KeyValueBean> apply(String s) throws Exception {
                        List<KeyValueBean> list = new ArrayList<>();
                        JSONObject rootObject = JSONObject.parseObject(s);
                        if (rootObject.getIntValue("code") == 200) {
                            JSONArray tagArray = rootObject.getJSONArray("tags");
                            int size = tagArray.size();
                            if (size > 0) {
                                for (int i = 0; i < size; i++) {
                                    JSONObject itemObject = tagArray.getJSONObject(i);
                                    KeyValueBean bean = new KeyValueBean();
                                    bean.setKey(String.valueOf(itemObject.getLongValue("id")));
                                    bean.setValue(itemObject.getString("name"));
                                    list.add(bean);
                                }
                            }
                        }
                        return list;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<KeyValueBean>>() {
                    @Override
                    public void accept(List<KeyValueBean> keyValueBeans) throws Exception {
                        hasLoadData=true;
                        adapter.clearDataList();
                        adapter.addDataList(keyValueBeans);
                        adapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });

    }

    @OnClick({R.id.return_img, R.id.all_btn})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.return_img:
                intent = new Intent();
                intent.putExtra("id", "");
                setResult(101, intent);
                finish();
                break;
            case R.id.all_btn:
                intent = new Intent();
                intent.putExtra("id", "全部");
                setResult(101, intent);
                finish();
                break;
        }
    }



}
