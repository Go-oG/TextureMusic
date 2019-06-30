package wzp.com.texturemusic.localmodule.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.dbmodule.DbUtil;
import wzp.com.texturemusic.dbmodule.bean.DbMusicEntiy;
import wzp.com.texturemusic.dbmodule.util.DbMusicUtil;
import wzp.com.texturemusic.localmodule.adapter.SetAlarmRingAdapter;
import wzp.com.texturemusic.util.ToastUtil;


/**
 * Created by Go_oG
 * Description:设置闹钟铃声的界面
 * on 2017/12/25.
 */

public class SetAlarmClockRingActivity extends BaseActivityWrapper {
    @BindView(R.id.m_recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_tv)
    TextView toolbarRightTv;
    private SetAlarmRingAdapter adapter;


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initService(false);
        super.onCreate(savedInstanceState);
        showBottomView(false);
        setContentView(R.layout.activity_set_alarm_clock_ring);
        ButterKnife.bind(this);
        toolbarRightTv.setText("完成");
        toolbarRightTv.setVisibility(View.VISIBLE);
        toolbarTitleTv.setText("设置闹钟");
        mContext = this;
        adapter = new SetAlarmRingAdapter(this);
        adapter.getItemClickSubject().compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int size = adapter.getDataList().size();
                        int position = itemBean.getPosition();
                        for (int i = 0; i < size; i++) {
                            if (i == position) {
                                adapter.getDataList().get(position)
                                        .setHasCheck(!adapter.getDataList().get(position).getHasCheck());
                            } else {
                                adapter.getDataList().get(i).setHasCheck(false);
                            }
                        }
                        adapter.notifyItemRangeChanged(0, size);
                    }
                });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.setLayoutManager(layoutManager);
        loadData();
    }

    @OnClick({R.id.toolar_return_img, R.id.toolbar_right_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolar_return_img:
                finish();
                break;
            case R.id.toolbar_right_tv:
                int index = -1;
                for (int i = 0; i < adapter.getDataList().size(); i++) {
                    if (adapter.getDataList().get(i).getHasCheck()) {
                        index = i;
                        break;
                    }
                }
                if (index == -1) {
                    ToastUtil.showNormalMsg("请选择音乐");
                } else {
                    final MusicBean bean = adapter.getDataList().get(index);
                    setRing(bean.getPlayPath(), bean.getMusicName() + "-" + bean.getArtistName());
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext = null;
    }

    @SuppressLint("CheckResult")
    private void loadData() {
        showLoadingView(false);
        Observable.create(new ObservableOnSubscribe<List<DbMusicEntiy>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DbMusicEntiy>> e) throws Exception {
                e.onNext(DbMusicUtil.queryAllLocalMusic());
            }
        }).map(new Function<List<DbMusicEntiy>, List<MusicBean>>() {
            @Override
            public List<MusicBean> apply(List<DbMusicEntiy> list) throws Exception {
                List<MusicBean> musicBeanList = new ArrayList<>();
                for (DbMusicEntiy entiy : list) {
                    musicBeanList.add(DbUtil.dbMusicEntiyToMusicBean(entiy));
                }
                return musicBeanList;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MusicBean>>() {
                    @Override
                    public void accept(List<MusicBean> list) throws Exception {
                        cancleLoadingView();
                        if (list.isEmpty()) {
                            ToastUtil.showNormalMsg("没有数据了");
                        } else {
                            int size = adapter.getDataList().size();
                            if (size == 0) {
                                adapter.addDataList(list);
                                adapter.notifyItemRangeInserted(0, list.size());
                            } else {
                                adapter.clearDataList();
                                adapter.addDataList(list);
                                adapter.notifyItemRangeRemoved(0, size);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        cancleLoadingView();
                        ToastUtil.showNormalMsg("没有数据了");
                    }
                });

    }
}
