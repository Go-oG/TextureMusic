package wzp.com.texturemusic.albummodule;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.albummodule.adapter.AlbumItemAdapter;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.dbmodule.util.DbMusicUtil;
import wzp.com.texturemusic.interf.OnDeleteListener;
import wzp.com.texturemusic.mvmodule.MvDetailActivity;
import wzp.com.texturemusic.util.ImageUtil;
import wzp.com.texturemusic.util.LogUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:本地专辑的详情页面
 * on 2018/1/26.
 */

public class LocalAlbumDetailActivity extends BaseActivityWrapper {
    @BindView(R.id.album_detail_break)
    ImageView mBreakImg;
    @BindView(R.id.album_detail_operation)
    ImageView mOperationImg;
    @BindView(R.id.album_detail_sort)
    ImageView mSortImg;
    @BindView(R.id.ac_album_detail_toolbar)
    RelativeLayout mToolbar;
    @BindView(R.id.album_detail_bk_img)
    ImageView mBkImg;
    @BindView(R.id.album_detail_recycle)
    RecyclerView mRecycleview;
    @BindView(R.id.toolbar_album_name_tv)
    TextView toolbarAlbumNameTv;
    @BindView(R.id.fab_btn)
    FloatingActionButton mFabBtn;
    private AlbumItemAdapter adapter;
    private AlbumBean albumBean;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initService(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_album_detail);
        setStatusBarHeight(0);
        ButterKnife.bind(this);
        mToolbar.setBackgroundColor(Color.parseColor("#00000000"));
        albumBean = getIntent().getParcelableExtra(AppConstant.UI_INTENT_KEY_ALBUM);
        adapter = new AlbumItemAdapter(this);
        adapter.getItemClickSubject()
                .compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(itemBean -> {
                    View view = itemBean.getView();
                    final int position = itemBean.getPosition();
                    MusicBean bean = adapter.getDataList().get(position);
                    if (view.getId() == R.id.item_operation_img) {
                        if (bean.getLocalMusic()) {
                            showMusicInfoPop(bean, new OnDeleteListener() {
                                @Override
                                public void onDelete() {
                                    adapter.getDataList().remove(position);
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyItemRangeChanged(position,adapter.getDataList().size());
                                }
                            });
                        } else {
                            showMusicInfoPop(bean, null);
                        }
                    } else if (view.getId() == R.id.item_show_mv_img) {
                        if (bean.getHasMV() != null && bean.getHasMV()) {
                            //跳转到Mv中去
                            ToastUtil.showNormalMsg("跳转Mv");
                            long mvId = bean.getMvBean().getMvId();
                            Intent intent = new Intent(mContext, MvDetailActivity.class);
                            intent.putExtra(AppConstant.UI_INTENT_KEY_MV, mvId);
                            startActivity(intent);
                        } else {
                            LogUtil.d(TAG, "播放音乐1");
                            playMusic(bean);
                        }
                    } else {
                        LogUtil.d(TAG, "播放音乐2");
                        playMusic(bean);
                    }
                });
        mRecycleview.setAdapter(adapter);
        mRecycleview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        SlideInDownAnimator animator = new SlideInDownAnimator(new OvershootInterpolator(0f));
        mRecycleview.setItemAnimator(animator);
        mRecycleview.getItemAnimator().setAddDuration(600);
        mRecycleview.getItemAnimator().setChangeDuration(600);
        toolbarAlbumNameTv.setText("专辑:" + albumBean.getAlbumName());
        if (albumBean.getLocalAlbum()) {
            String imgUrl = albumBean.getAlbumImgUrl();
            if (!TextUtils.isEmpty(imgUrl)) {
                ImageUtil.loadImage(mContext, imgUrl, mBkImg, R.drawable.png_artist_cover);
            } else {
                ImageUtil.loadImage(mContext, R.drawable.png_artist_cover, mBkImg, R.drawable.png_artist_cover);
            }
            loadLocalAlbumDetail(albumBean);
            startAnnim();
        } else {
            ToastUtil.showNormalMsg("当前专辑不是本地专辑");
        }
    }

    @OnClick({R.id.album_detail_break, R.id.album_detail_operation, R.id.album_detail_sort,
            R.id.fab_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.album_detail_break:
                supportFinishAfterTransition();
                break;
            case R.id.album_detail_operation:
                break;
            case R.id.album_detail_sort:
                break;
            case R.id.fab_btn:
                //播放全部
                addPlayQueueData((ArrayList<MusicBean>) adapter.getDataList());
                playMusic(0);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.clearDataList();
        adapter = null;

    }
    @SuppressLint("CheckResult")
    private void loadLocalAlbumDetail(final AlbumBean albumBean) {
        if (albumBean.getLocalAlbum()) {
            Observable.create(new ObservableOnSubscribe<List<MusicBean>>() {
                @Override
                public void subscribe(ObservableEmitter<List<MusicBean>> e) throws Exception {
                    e.onNext(DbMusicUtil.queryLocalAlbumMusic(albumBean));
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(this.<List<MusicBean>>bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(new Consumer<List<MusicBean>>() {
                        @Override
                        public void accept(List<MusicBean> list) throws Exception {
                            adapter.clearDataList();
                            adapter.addDataList(list);
                            adapter.notifyItemRangeChanged(0, list.size());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ToastUtil.showNormalMsg("数据错误");
                        }
                    });
        }
    }

    private void startAnnim() {
        startPostponedEnterTransition();
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 0, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 0, 1f);
        ObjectAnimator.ofPropertyValuesHolder(mFabBtn, pvhX, pvhY, pvhZ).setDuration(600).start();
    }

}
