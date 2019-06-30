package wzp.com.texturemusic.localmodule.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import wzp.com.texturemusic.bean.ShareBean;
import wzp.com.texturemusic.common.popwindow.CollectPopwindow;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseActivity;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.dbmodule.bean.DbCollectPlaylistEntiy;
import wzp.com.texturemusic.dbmodule.util.DbCollectUtil;
import wzp.com.texturemusic.interf.OnPopItemClick;
import wzp.com.texturemusic.localmodule.adapter.CollectPlaylistAdapter;
import wzp.com.texturemusic.playlistmodule.PlaylistDetailActivity;
import wzp.com.texturemusic.util.LogUtil;
import wzp.com.texturemusic.util.ToastUtil;


/**
 * Created by Wang on 2018/3/9.
 */

public class CollectPlaylistFagment extends BaseFragment {
    private CollectPlaylistAdapter adapter;
    @BindView(R.id.local_fragment_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new CollectPlaylistAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        View view = itemBean.getView();
                        if (view.getId() == R.id.item_playlist_operation) {
                            showCollectPopwindow(adapter.getDataList().get(position));
                        } else {
                            Long playListId = adapter.getDataList().get(position).getPlaylistId();
                            if (playListId != null && playListId != 0L) {
                                Intent intent = new Intent(mContext, PlaylistDetailActivity.class);
                                intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST, playListId);
                                intent.putExtra(AppConstant.UI_INTENT_KEY_PLAYLIST_IMG, adapter.getDataList().get(position).getCoverImgUr());
                                startActivity(intent);
                            } else {
                                ToastUtil.showNormalMsg("暂无该歌单信息");
                            }
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
            getLocalPlaylistData();
        }
    }

    /**
     * 获取本地音乐数据
     */
    @SuppressLint("CheckResult")
    public void getLocalPlaylistData() {
        Observable.create(new ObservableOnSubscribe<List<DbCollectPlaylistEntiy>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DbCollectPlaylistEntiy>> e) throws Exception {
                List<DbCollectPlaylistEntiy> datList = DbCollectUtil.getAllLikedPlaylist();
                e.onNext(datList);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<DbCollectPlaylistEntiy>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<List<DbCollectPlaylistEntiy>>() {
                               @Override
                               public void accept(List<DbCollectPlaylistEntiy> list) throws Exception {
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


    private void showCollectPopwindow(final DbCollectPlaylistEntiy entiy) {
        CollectPopwindow collectPopwindow = new CollectPopwindow(mContext, entiy);
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
                    ShareBean shareBean = new ShareBean();
                    shareBean.setTitle(entiy.getPlaylistName());
                    shareBean.setImgUrl(entiy.getCoverImgUr());
                    shareBean.setText(entiy.getSubDescription());
                    shareBean.setWebUrl("https://music.163.com/#/playlist?id=" + entiy.getPlaylistId());
                    ((BaseActivityWrapper) getActivity()).shareMusic(shareBean);
                } else {
                    //删除
                    DbCollectUtil.deleteLikedPlaylist(entiy);
                    ToastUtil.showNormalMsg("删除成功");
                    adapter.getDataList().remove(entiy);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        ((BaseActivity) getActivity()).setBackgroundAlpha(1f, 0.5f, 200);
        collectPopwindow.showAtLocation(mRecycleview, Gravity.BOTTOM, 0, 0);
    }
}
