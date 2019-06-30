package wzp.com.texturemusic.localmodule.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
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
import wzp.com.texturemusic.common.popwindow.CollectPopwindow;
import wzp.com.texturemusic.core.ui.BaseActivity;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.dbmodule.DbUtil;
import wzp.com.texturemusic.dbmodule.bean.DbCollectMusicEntiy;
import wzp.com.texturemusic.dbmodule.bean.DbMusicEntiy;
import wzp.com.texturemusic.dbmodule.util.DbCollectUtil;
import wzp.com.texturemusic.interf.OnPopItemClick;
import wzp.com.texturemusic.localmodule.adapter.LocalSongsAdapter;
import wzp.com.texturemusic.localmodule.ui.activity.LocalCollectActivity;
import wzp.com.texturemusic.util.LogUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Wang on 2018/3/9.
 */

public class CollectMusicFragment extends BaseFragment {
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
                            ((LocalCollectActivity) getActivity()).playMusic(musicBean);
                        } else {
                            showCollectPopwindow(musicBean);
                        }
                    }
                });
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
    public void lazyLoading() {
        if (!hasLoadData) {
            //加载数据
            getLocalMusicData();
        }
    }

    /**
     * 获取本地音乐数据
     */
    @SuppressLint("CheckResult")
    public void getLocalMusicData() {
        Observable.create(new ObservableOnSubscribe<List<DbMusicEntiy>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DbMusicEntiy>> e) throws Exception {
                List<DbCollectMusicEntiy> datList = DbCollectUtil.getAllLikedMusic();
                List<DbMusicEntiy> list=new ArrayList<>();
                for (DbCollectMusicEntiy entiy:datList){
                    DbMusicEntiy musicEntiy=new DbMusicEntiy();
                    musicEntiy.setAlbumId(entiy.getAlbumId());
                    musicEntiy.setAlbumName(entiy.getAlbumName());
                    musicEntiy.setArtistId(entiy.getArtistId());
                    musicEntiy.setArtistName(entiy.getArtistName());
                    musicEntiy.setIsLocalMusic(entiy.getLocalMusic());
                    musicEntiy.setCoverImgUrl(entiy.getCoverImgUrl());
                    musicEntiy.setMusicName(entiy.getMusicName());
                    musicEntiy.setMusicId(entiy.getMusicId());
                    musicEntiy.setMusicDbId(entiy.getDbId());
                    list.add(musicEntiy);
                }
                e.onNext(list);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<DbMusicEntiy>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<List<DbMusicEntiy>>() {
                               @Override
                               public void accept(List<DbMusicEntiy> list) throws Exception {
                                   LogUtil.d(TAG, "SIZE=" + list.size());
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
                               public void accept(Throwable throwable) throws Exception {
                               showErrorView();
                                   hasLoadData = false;
                                   ToastUtil.showNormalMsg("没有数据了");
                               }
                           }
                );

    }

    private void showCollectPopwindow(final MusicBean bean) {
        CollectPopwindow collectPopwindow = new CollectPopwindow(mContext, bean);
        collectPopwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ((BaseActivity) getActivity()).setBackgroundAlpha(0.5f, 1f, 200);
            }
        });
        collectPopwindow.setPopItemClick(new OnPopItemClick() {
            @Override
            public void popItemClick(int position) {
                if (position == 0) {
                    //分享
                    ((BaseActivityWrapper) getActivity()).shareMusic(bean);
                } else {
                    //删除
                    DbCollectUtil.deleteLikedMusic(bean);
                    ToastUtil.showNormalMsg("删除成功");
                    adapter.getDataList().remove(bean);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        ((BaseActivity) getActivity()).setBackgroundAlpha(1f, 0.5f, 200);
        collectPopwindow.showAtLocation(mRecycleview, Gravity.BOTTOM, 0, 0);
    }
}
