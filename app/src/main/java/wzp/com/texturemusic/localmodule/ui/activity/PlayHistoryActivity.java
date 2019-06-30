package wzp.com.texturemusic.localmodule.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.dbmodule.DbUtil;
import wzp.com.texturemusic.dbmodule.bean.DbHistoryEntiy;
import wzp.com.texturemusic.dbmodule.util.DbHistoryUtil;
import wzp.com.texturemusic.interf.OnDeleteListener;
import wzp.com.texturemusic.interf.OnDialogListener;
import wzp.com.texturemusic.localmodule.adapter.PlayHistoryAdapter;
import wzp.com.texturemusic.mvmodule.MvDetailActivity;
import wzp.com.texturemusic.util.StringUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:播放历史Activity
 * on 2017/9/27.
 */

public class PlayHistoryActivity extends BaseActivityWrapper {
    @BindView(R.id.m_recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;
    @BindView(R.id.toolbar_right_tv)
    TextView toolbarRightTv;
    private PlayHistoryAdapter adapter;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_history);
        ButterKnife.bind(this);
        toolbarTitleTv.setText("播放记录");
        toolbarRightTv.setText("清空");
        toolbarRightTv.setVisibility(View.VISIBLE);
        adapter = new PlayHistoryAdapter(mContext);
        adapter.getItemClickSubject()
                .compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        final int position = itemBean.getPosition();
                        DbHistoryEntiy entiy = adapter.getDataList().get(position);
                        MusicBean musicBean = DbUtil.dbHistoryEntiyToMusicBean(entiy);
                        if (itemBean.getView().getId() == R.id.item_operation_img) {
                            if (musicBean.getLocalMusic()) {
                                showMusicInfoPop(musicBean, new OnDeleteListener() {
                                    @Override
                                    public void onDelete() {
                                        adapter.getDataList().remove(position);
                                        adapter.notifyItemRemoved(position);
                                        adapter.notifyItemRangeChanged(position, adapter.getDataList().size());
                                    }
                                });
                            } else {
                                showMusicInfoPop(musicBean, null);
                            }
                        } else if (itemBean.getView().getId() == R.id.item_show_mv_img && musicBean.getHasMV()) {
                            Long mvId = musicBean.getMvBean().getMvId();
                            if (mvId != null && mvId != 0) {
                                Intent intent = new Intent(mContext, MvDetailActivity.class);
                                intent.putExtra(AppConstant.UI_INTENT_KEY_MV, mvId);
                                startActivity(intent);
                            } else {
                                playMusic();
                            }
                        } else {
                            playMusic(musicBean);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerview.setItemAnimator(new SlideInUpAnimator());
        mRecyclerview.getItemAnimator().setChangeDuration(400);
        mRecyclerview.getItemAnimator().setAddDuration(400);
        mRecyclerview.getItemAnimator().setRemoveDuration(400);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDate();
            }
        });
        swipeLayout.setColorSchemeColors(getMainColor());
        loadDate();
    }

    @SuppressLint("CheckResult")
    private void loadDate() {
        Observable.create(new ObservableOnSubscribe<List<DbHistoryEntiy>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DbHistoryEntiy>> e) throws Exception {
                e.onNext(DbHistoryUtil.getAllHistoryData());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<DbHistoryEntiy>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<DbHistoryEntiy>>() {
                    @Override
                    public void accept(List<DbHistoryEntiy> list) throws Exception {
                        if (swipeLayout.isRefreshing()) {
                            swipeLayout.setRefreshing(false);
                        }
                        if (!list.isEmpty()) {
                            adapter.clearDataList();
                            adapter.addDataList(list);
                            adapter.notifyItemRangeChanged(0, list.size());
                            String content = "播放记录(" + list.size() + ")";
                            toolbarTitleTv.setText(StringUtil.builderStringSize(content, 13, 5, content.length()));
                        } else {
                            ToastUtil.showNormalMsg("暂无数据");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showNormalMsg("暂无数据");
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void clearHistory() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                DbHistoryUtil.clearHistoryMusicData();
                e.onNext(true);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Boolean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        int size = adapter.getDataList().size();
                        adapter.clearDataList();
                        adapter.notifyItemRangeRemoved(0, size);
                        toolbarTitleTv.setText("播放记录");
                        ToastUtil.showNormalMsg("清除数据成功");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showNormalMsg("清除失败请重新尝试");
                    }
                });
    }


    @OnClick({R.id.toolar_return_img, R.id.toolbar_right_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolar_return_img:
                finish();
                break;
            case R.id.toolbar_right_tv:
                if (adapter != null && !adapter.getDataList().isEmpty()) {
                    ToastUtil.showCustomDialog(mContext, "清空所有最近的播放记录?", new OnDialogListener() {
                        @Override
                        public void onResult(boolean success) {
                            if (success) {
                                clearHistory();
                            }
                        }
                    });
                } else {
                    ToastUtil.showCustomDialog(mContext, "暂无数据是否立即播放", new OnDialogListener() {
                        @Override
                        public void onResult(boolean success) {
                            if (success) {
                                playMusic();
                            }
                        }
                    });
                }
                break;
        }
    }
}
