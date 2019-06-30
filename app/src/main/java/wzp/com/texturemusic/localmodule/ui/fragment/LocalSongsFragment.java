package wzp.com.texturemusic.localmodule.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.dbmodule.DbUtil;
import wzp.com.texturemusic.dbmodule.bean.DbMusicEntiy;
import wzp.com.texturemusic.dbmodule.util.DbMusicUtil;
import wzp.com.texturemusic.interf.OnDeleteListener;
import wzp.com.texturemusic.localmodule.adapter.LocalSongsAdapter;
import wzp.com.texturemusic.localmodule.ui.LocalActivity;
import wzp.com.texturemusic.util.MusicUtil;
import wzp.com.texturemusic.util.SPSetingUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/9/26.
 */

public class LocalSongsFragment extends BaseFragment {
    @BindView(R.id.local_fragment_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private LocalSongsAdapter adapter;
    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new LocalSongsAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        final int position = itemBean.getPosition();
                        View view = itemBean.getView();
                        DbMusicEntiy bean = adapter.getDataList().get(position);
                        MusicBean musicBean = DbUtil.dbMusicEntiyToMusicBean(bean);
                        if (view.getId() == R.id.local_songs_item) {
                            ((LocalActivity) getActivity()).playMusic(musicBean);
                        } else {
                            ((LocalActivity) getActivity()).showMusicInfoPop(musicBean, new OnDeleteListener() {
                                @Override
                                public void onDelete() {
                                    adapter.getDataList().remove(position);
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyItemRangeChanged(position,adapter.getDataList().size());
                                }
                            });
                        }
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
            mContentView = inflater.inflate(R.layout.fragment_local, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecycleview.setAdapter(adapter);
        mRecycleview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecycleview.setItemAnimator(new SlideInUpAnimator());
        mRecycleview.getItemAnimator().setChangeDuration(600);
        mRecycleview.getItemAnimator().setAddDuration(600);
        mRecycleview.getItemAnimator().setRemoveDuration(600);
    }

    @Override
    public void onDestroy() {
        hasLoadData = false;
        super.onDestroy();
    }

    @Override
    public void lazyLoading() {
        if (!hasLoadData) {
            //加载数据
            getLocalMusicData(true);
        }
    }

    /**
     * 获取本地音乐数据
     */
    @SuppressLint("CheckResult")
    public void getLocalMusicData(final boolean showLoading) {
        if (showLoading) {
            showLoadingView(true);
        }
        Observable.create(new ObservableOnSubscribe<List<DbMusicEntiy>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DbMusicEntiy>> e)  {
                if (SPSetingUtil.getBooleanValue(AppConstant.SP_KEY_IS_FIRST_INSERT_DB_DATA, true)) {
                    //是第一次初始化数据库
                    List<MusicBean> dataList = MusicUtil.getLocalMusicData(mContext);
                    DbMusicUtil.insertMusicData(dataList);
                    SPSetingUtil.setBooleanValue(AppConstant.SP_KEY_IS_FIRST_INSERT_DB_DATA, false);
                }
                List<DbMusicEntiy> datList = DbMusicUtil.queryAllLocalMusic();
                e.onNext(datList);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<DbMusicEntiy>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<List<DbMusicEntiy>>() {
                               @Override
                               public void accept(List<DbMusicEntiy> list) {
                                   if (showLoading) {
                                       cancleLoadingView(true);
                                   }
                                   if (list.isEmpty()) {
                                       ToastUtil.showNormalMsg("没有数据了");
                                       hasLoadData = false;
                                   } else {
                                       hasLoadData = true;
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
                               public void accept(Throwable throwable){
                                  showErrorView();
                                   hasLoadData = false;
                                   ToastUtil.showNormalMsg("没有数据了");
                               }
                           }
                );

    }


}
