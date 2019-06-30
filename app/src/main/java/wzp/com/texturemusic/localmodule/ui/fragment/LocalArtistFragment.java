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
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.dbmodule.util.DbMusicUtil;
import wzp.com.texturemusic.localmodule.adapter.LocalArtistAdapter;
import wzp.com.texturemusic.localmodule.ui.activity.LocalArtistDetailActivity;


/**
 * Created by Go_oG
 * Description:
 * on 2017/9/26.
 */

public class LocalArtistFragment extends BaseFragment {
    @BindView(R.id.local_fragment_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private Context mContext;
    private LocalArtistAdapter artistAdapter;
    private boolean dataHasLoaded = false;
    private GridLayoutManager layoutManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artistAdapter = new LocalArtistAdapter(mContext);
        artistAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        int position = itemBean.getPosition();
                        Intent intent = new Intent(mContext, LocalArtistDetailActivity.class);
                        ArtistBean bean = artistAdapter.getDataList().get(position);
                        bean.setLocalArtist(true);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_ARTIST, bean);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view.findViewById(R.id.local_artist_img), getString(R.string.shareartistdetail));
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
        mRecycleview.setAdapter(artistAdapter);
        mRecycleview.setLayoutManager(layoutManager);
    }

    @SuppressLint("CheckResult")
    @Override
    public void lazyLoading() {
        if (!dataHasLoaded) {
            dataHasLoaded = true;
            showLoadingView(true);
            Observable.create(new ObservableOnSubscribe<List<ArtistBean>>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<List<ArtistBean>> e) throws Exception {
                    e.onNext(DbMusicUtil.getLocalAllArtist());
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(this.<List<ArtistBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribe(new Consumer<List<ArtistBean>>() {
                        @Override
                        public void accept(List<ArtistBean> list) throws Exception {
                            cancleLoadingView(true);
                            artistAdapter.clearDataList();
                            artistAdapter.addDataList(list);
                            artistAdapter.notifyDataSetChanged();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            cancleLoadingView(false);
                        }
                    });
        }
    }
}
