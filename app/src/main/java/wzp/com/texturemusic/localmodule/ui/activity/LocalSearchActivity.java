package wzp.com.texturemusic.localmodule.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.dbmodule.DbUtil;
import wzp.com.texturemusic.dbmodule.bean.DbMusicEntiy;
import wzp.com.texturemusic.dbmodule.util.DbMusicUtil;
import wzp.com.texturemusic.interf.OnDeleteListener;
import wzp.com.texturemusic.localmodule.adapter.LocalSearchAdapter;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:本地搜索界面
 * on 2017/12/25.
 */

public class LocalSearchActivity extends BaseActivityWrapper {
    @BindView(R.id.m_editText)
    EditText mEditText;
    @BindView(R.id.m_recyclerview)
    RecyclerView mRecyclerview;
    private String searchStr = "";
    private LocalSearchAdapter adapter;
    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBottomView(false);
        setContentView(R.layout.activity_local_search);
        ButterKnife.bind(this);
        adapter = new LocalSearchAdapter(mContext);
        adapter.getItemClickSubject().compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                       final int position=itemBean.getPosition();
                        MusicBean bean = DbUtil.dbMusicEntiyToMusicBean(adapter.getDataList().get(position));
                        if (itemBean.getView().getId() == R.id.item_operation_img) {
                            showMusicInfoPop(bean, new OnDeleteListener() {
                                @Override
                                public void onDelete() {
                                    adapter.getDataList().remove(position);
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyItemRangeChanged(position,adapter.getDataList().size());
                                }
                            });
                        } else {
                            playMusic(bean);
                        }
                    }
                });
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String str = v.getText().toString().trim();
                    if (TextUtils.isEmpty(str) || str.equals("null")) {
                        ToastUtil.showNormalMsg("搜索条件不能为空");
                    } else {
                        if (!str.equals(searchStr)) {
                            searchStr = str;
                            loadData(str);
                        }else {
                            ToastUtil.showNormalMsg("数据已加载");
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        SlideInDownAnimator animator = new SlideInDownAnimator(new OvershootInterpolator(0f));
        mRecyclerview.setItemAnimator(animator);
        mRecyclerview.getItemAnimator().setAddDuration(400);
        mRecyclerview.getItemAnimator().setChangeDuration(400);

    }

    @OnClick(R.id.m_return_img)
    public void onViewClicked() {
        finish();
    }
    @SuppressLint("CheckResult")
    private void loadData(final String searchStr) {
        Observable.create(new ObservableOnSubscribe<List<DbMusicEntiy>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DbMusicEntiy>> e) throws Exception {
                //模糊查询 greendao 需要加匹配字符%
                e.onNext(DbMusicUtil.queryBlurData("%" + searchStr + "%"));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<DbMusicEntiy>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<DbMusicEntiy>>() {
                    @Override
                    public void accept(List<DbMusicEntiy> list) throws Exception {
                        int oldSize = adapter.getDataList().size();
                        if (list.isEmpty()) {
                            ToastUtil.showNormalMsg("没有搜索到数据");
                            adapter.clearDataList();
                            adapter.notifyItemRangeRemoved(0, oldSize);
                        } else {
                            adapter.clearDataList();
                            adapter.addDataList(list);
                            adapter.notifyItemRangeRemoved(0, oldSize);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showNormalMsg("数据错误");
                    }
                });
    }


}
