package wzp.com.texturemusic.downloadmodule.ui;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.DownloadBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.service.DownloadService;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.downloadmodule.adapter.DownloadingAdapter;
import wzp.com.texturemusic.util.LogUtil;

/**
 * Created by Go_oG
 * Description:显示已经下载完了的数据
 * on 2017/11/8.
 */

public class DownloadingFragment extends BaseFragment {
    @BindView(R.id.m_recyclerview)
    RecyclerView mRecyclerview;
    Unbinder unbinder;
    private DownloadingAdapter adapter;
    private DownloadService.DownloadBinder mBinder;
    private Disposable disposable;
    private final ServiceConnection mServiceConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //连接成功
            mBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            cancleThreed();
        }

    };

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new DownloadingAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        DownloadBean bean = adapter.getDataList().get(itemBean.getPosition());
                        View view = itemBean.getView();
                        if (view.getId() == R.id.item_download_delete) {
                            if (mBinder != null) {
                                mBinder.cancleDownload(bean);
                            }
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(mContext, DownloadService.class);
        getActivity().bindService(intent, mServiceConnect, Context.BIND_AUTO_CREATE);
        initThred();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (disposable!=null&&!disposable.isDisposed()){
            disposable.dispose();
            disposable=null;
        }
        getActivity().unbindService(mServiceConnect);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_downloading, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    private void initThred() {
        disposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (mBinder != null) {
                            adapter.clearDataList();
                            List<DownloadBean> list = mBinder.getAllDownloadData();
                            LogUtil.d(TAG, "下载数据大小=" + list.size());
                            adapter.addDataList(list);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

    }

    private void cancleThreed() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

}
