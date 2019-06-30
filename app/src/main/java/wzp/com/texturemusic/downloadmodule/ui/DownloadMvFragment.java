package wzp.com.texturemusic.downloadmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
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
import wzp.com.texturemusic.BuildConfig;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.dbmodule.bean.DbDownloadEntiy;
import wzp.com.texturemusic.dbmodule.util.DbDownloadUtil;
import wzp.com.texturemusic.downloadmodule.adapter.DownloadMvAdapter;
import wzp.com.texturemusic.interf.OnDialogListener;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:下载歌曲的界面
 * on 2017/11/8.
 */

public class DownloadMvFragment extends BaseFragment {
    @BindView(R.id.m_recyclerView)
    RecyclerView mRecyclerView;
    Unbinder unbinder;
    private DownloadMvAdapter mAdapter;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new DownloadMvAdapter(mContext);
        mAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        final int position = itemBean.getPosition();
                        View view = itemBean.getView();
                        String path = mAdapter.getDataList().get(position).getFilePath();
                        final File file = new File(path);
                        if (view.getId() == R.id.item_operation_img) {
                            ToastUtil.showCustomDialog(mContext, "是否删除该MV", new OnDialogListener() {
                                @Override
                                public void onResult(boolean success) {
                                    if (success) {
                                        if (file.exists() && file.isFile()) {
                                            Observable.create(new ObservableOnSubscribe<Boolean>() {
                                                @Override
                                                public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                                                    DbDownloadUtil.deleteDownloadEntiy(mAdapter.getDataList().get(position));
                                                    emitter.onNext(file.delete());
                                                }
                                            }).subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new Consumer<Boolean>() {
                                                        @Override
                                                        public void accept(Boolean aBoolean) throws Exception {
                                                            mAdapter.getDataList().remove(position);
                                                            mAdapter.notifyDataSetChanged();
                                                            toast("删除成功");
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });
                        } else {
                            if (file.exists() && file.isFile()) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                Uri uri;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                                } else {
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    uri = Uri.fromFile(file);
                                }
                                intent.setDataAndType(uri, "video/*");
                                startActivity(intent);
                            }
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
                e.onNext(DbDownloadUtil.getAllDownloadEntiyForMv());
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
