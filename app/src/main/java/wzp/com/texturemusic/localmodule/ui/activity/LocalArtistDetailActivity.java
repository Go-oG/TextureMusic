package wzp.com.texturemusic.localmodule.ui.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
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
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.dbmodule.util.DbMusicUtil;
import wzp.com.texturemusic.interf.OnDeleteListener;
import wzp.com.texturemusic.localmodule.adapter.LocalArtistDetailAdapter;
import wzp.com.texturemusic.util.ImageUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description: 本地歌手详情界面
 * on 2017/12/25.
 */

public class LocalArtistDetailActivity extends BaseActivityWrapper {
    @BindView(R.id.app_bar_image)
    ImageView appBarImage;
    @BindView(R.id.toolbar_return_img)
    ImageView mReturnImg;
    @BindView(R.id.artist_name_tv)
    TextView mArtistNameTv;
    @BindView(R.id.m_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.m_collapsingtoolbar)
    CollapsingToolbarLayout mCollapsingtoolbar;
    @BindView(R.id.m_appbar)
    AppBarLayout mAppbar;
    @BindView(R.id.m_recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.fab_btn)
    FloatingActionButton mFabBtn;
    private ArtistBean artistBean;
    private LocalArtistDetailAdapter adapter;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_artist_detail);
        setStatusBarHeight(0);
        ButterKnife.bind(this);
        adapter = new LocalArtistDetailAdapter(mContext);
        adapter.getItemClickSubject().compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        final int position = itemBean.getPosition();
                        MusicBean bean = adapter.getDataList().get(position);
                        if (view.getId() == R.id.item_operation_img) {
                            showMusicInfoPop(bean, new OnDeleteListener() {
                                @Override
                                public void onDelete() {
                                    adapter.getDataList().remove(position);
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyItemRangeChanged(position, adapter.getDataList().size());
                                }
                            });
                        } else {
                            playMusic(bean);
                        }

                    }
                });
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.setAdapter(adapter);
        artistBean = getIntent().getParcelableExtra(AppConstant.UI_INTENT_KEY_ARTIST);
        if (artistBean != null && artistBean.getLocalArtist()) {
            ImageUtil.loadImage(mContext,
                    artistBean.getArtistImgUrl(),
                    appBarImage);
            mArtistNameTv.setText(artistBean.getArtistName());
            loadData(artistBean);
        } else {
            ToastUtil.showErrorMsg("数据参数错误");
        }
        startAnnim();

    }

    @SuppressLint("CheckResult")
    private void loadData(final ArtistBean bean) {
        Observable.create(new ObservableOnSubscribe<List<MusicBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<MusicBean>> e) throws Exception {
                e.onNext(DbMusicUtil.queryLocalArtistMusic(bean));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<MusicBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<MusicBean>>() {
                    @Override
                    public void accept(List<MusicBean> list) throws Exception {
                        adapter.addDataList(list);
                        adapter.notifyItemRangeInserted(0, list.size());
                        mArtistNameTv.setText(artistBean.getArtistName() + "(" + list.size() + ")");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showNormalMsg("暂无数据");
                    }
                });
    }

    @OnClick({R.id.toolbar_return_img, R.id.fab_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_return_img:
                supportFinishAfterTransition();
                break;
            case R.id.fab_btn:
                //播放全部
                if (adapter.getDataList().size() > 0) {
                    addPlayQueueData((ArrayList<MusicBean>) adapter.getDataList());
                    playMusic(0);
                } else {
                    ToastUtil.showNormalMsg("暂无可播放数据");
                }
                break;
        }
    }

    private void startAnnim() {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 0, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 0, 1f);
        ObjectAnimator.ofPropertyValuesHolder(mFabBtn, pvhX, pvhY, pvhZ).setDuration(600).start();
    }
}
