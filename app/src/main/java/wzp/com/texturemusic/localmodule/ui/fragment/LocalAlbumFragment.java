package wzp.com.texturemusic.localmodule.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.albummodule.LocalAlbumDetailActivity;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.dbmodule.util.DbMusicUtil;
import wzp.com.texturemusic.localmodule.adapter.LocalAlbumAdapter;

/**
 * Created by Go_oG
 * Description:
 * on 2017/9/26.
 */

public class LocalAlbumFragment extends BaseFragment {
    @BindView(R.id.local_fragment_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private Context mContext;
    private boolean dataIsLoaded = false;
    private GridLayoutManager layoutManager;
    private LocalAlbumAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }
    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new LocalAlbumAdapter(mContext);
        mAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view=itemBean.getView();
                        int position=itemBean.getPosition();
                        AlbumBean albumBean = mAdapter.getDataList().get(position);
                        albumBean.setLocalAlbum(true);
                        Intent intent = new Intent(mContext, LocalAlbumDetailActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_ALBUM, albumBean);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view.findViewById(R.id.local_album_img), getString(R.string.sharealbumdetail));
                        startActivity(intent, optionsCompat.toBundle());
                    }
                });
        layoutManager = new GridLayoutManager(mContext, 2);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancleLoadingView(false);
        unbinder.unbind();
    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_local, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecycleview.setAdapter(mAdapter);
        mRecycleview.setLayoutManager(layoutManager);
    }
    @SuppressLint("CheckResult")
    @Override
    public void lazyLoading() {
        if (!dataIsLoaded) {
            dataIsLoaded = true;
            //开始加载数据
            showLoadingView(true);
            Observable.create(new ObservableOnSubscribe<List<AlbumBean>>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<List<AlbumBean>> e) throws Exception {
                    e.onNext(DbMusicUtil.getLocalAllAlbum());
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(this.<List<AlbumBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribe(new Consumer<List<AlbumBean>>() {
                        @Override
                        public void accept(List<AlbumBean> albumBeen) throws Exception {
                            cancleLoadingView(true);
                            mAdapter.clearDataList();
                            mAdapter.addDataList(albumBeen);
                            mAdapter.notifyDataSetChanged();
                        }
                    });

        }
    }


}
