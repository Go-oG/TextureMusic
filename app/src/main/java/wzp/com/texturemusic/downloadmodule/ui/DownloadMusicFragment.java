package wzp.com.texturemusic.downloadmodule.ui;

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
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.dbmodule.bean.DbDownloadEntiy;
import wzp.com.texturemusic.dbmodule.util.DbDownloadUtil;
import wzp.com.texturemusic.downloadmodule.DownloadActivity;
import wzp.com.texturemusic.downloadmodule.adapter.DownloadMusicAdapter;
import wzp.com.texturemusic.interf.OnDeleteListener;

public class DownloadMusicFragment extends BaseFragment {
    @BindView(R.id.m_recyclerView)
    RecyclerView mRecyclerView;
    Unbinder unbinder;
    private DownloadMusicAdapter mAdapter;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new DownloadMusicAdapter(mContext);
        mAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        final int position = itemBean.getPosition();
                       // final MusicBean bean = DbMusicUtil.queryMusic(mAdapter.getDataList().get(position));
                        final MusicBean bean = DbDownloadUtil.queryMusic(mAdapter.getDataList().get(position));
                        if (bean == null) {
                            toast("暂无该歌曲信息");
                            mAdapter.getDataList().remove(position);
                            mAdapter.notifyDataSetChanged();
                            return;
                        }
                        if (view.getId() == R.id.item_operation_img) {
                            ((DownloadActivity) getActivity()).showMusicInfoPop(bean, new OnDeleteListener() {
                                @Override
                                public void onDelete() {
                                    mAdapter.getDataList().remove(position);
                                    mAdapter.notifyDataSetChanged();
                                    DbDownloadUtil.deleteDownloadMusic(bean);
                                }
                            });
                        } else {
                            ((DownloadActivity) getActivity()).playMusic(bean);
                        }
                    }
                });
    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_download_song, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @SuppressLint("CheckResult")
    @Override
    public void lazyLoading() {
        super.lazyLoading();
        Observable.create(new ObservableOnSubscribe<List<DbDownloadEntiy>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DbDownloadEntiy>> e) throws Exception {
                e.onNext(DbDownloadUtil.getAllDownloadEntiyForMusic());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<DbDownloadEntiy>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<List<DbDownloadEntiy>>() {
                    @Override
                    public void accept(List<DbDownloadEntiy> dbDownloadEntiys) throws Exception {
                        mAdapter.clearDataList();
                        mAdapter.addDataList(dbDownloadEntiys);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    public void updateUi() {
        if (mAdapter != null) {
            mAdapter.clearDataList();
            mAdapter.notifyDataSetChanged();
        }

    }
}
