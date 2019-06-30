package wzp.com.texturemusic.djmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.DJApiManager;
import wzp.com.texturemusic.bean.DjSongBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.KeyValueBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.RadioBean;
import wzp.com.texturemusic.bean.SubCommentBean;
import wzp.com.texturemusic.common.popwindow.MusicOperationPopwindow;
import wzp.com.texturemusic.common.ui.CommentActivity;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.djmodule.adapter.DjDetailProgramAdapter;
import wzp.com.texturemusic.interf.OnMusicPopItemListener;
import wzp.com.texturemusic.core.customui.OnLoadMoreForLinearLayoutManager;
import wzp.com.texturemusic.util.DownloadUtil;
import wzp.com.texturemusic.util.LogUtil;
import wzp.com.texturemusic.util.SPSetingUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description: 电台界面列表
 * on 2017/11/16.
 */

public class DjDetailProgrameFragment extends BaseFragment {
    @BindView(R.id.m_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private RadioBean djBean;
    private DjDetailProgramAdapter adapter;
    private int offset = 0;
    private int limit = 30;
    private boolean hasMoreDate = true;
    private MusicOperationPopwindow popwindow;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        djBean = getArguments().getParcelable(AppConstant.UI_INTENT_KEY_DJ);
        adapter = new DjDetailProgramAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        MusicBean bean = adapter.getDataList().get(itemBean.getPosition()).getMusicBean();
                        View view = itemBean.getView();
                        if (view.getId() == R.id.item_operation_img) {
                            showDjDeailInfo(bean);
                        } else {
                            ((DjDetailActivity) getActivity()).playMusic(bean);
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
            mContentView = inflater.inflate(R.layout.fragment_dj_detail_programe, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycleview.setAdapter(adapter);
        mRecycleview.setLayoutManager(layoutManager);
        mRecycleview.addOnScrollListener(new OnLoadMoreForLinearLayoutManager(layoutManager) {
            @Override
            public void onLoadMore() {
                if (hasMoreDate) {
                    offset++;
                    getDjData(djBean.getRadioId(), offset * limit, limit, true);
                } else {
                    toast("没有更多数据");
                }
            }
        });
        mRecycleview.setItemAnimator(new SlideInUpAnimator(new LinearInterpolator()));
        mRecycleview.getItemAnimator().setAddDuration(600);
        mRecycleview.getItemAnimator().setChangeDuration(600);
    }

    @Override
    public void lazyLoading() {
        if (djBean != null && !hasLoadData) {
            getDjData(djBean.getRadioId(), offset, limit, false);
        }
    }

    @SuppressLint("CheckResult")
    private void getDjData(String djId, int offset, final int limit, final boolean isLoadMore) {
        DJApiManager
                .getDjPrograme(djId, offset, limit, 0)
                .map(new Function<String, List<DjSongBean>>() {
                    @Override
                    public List<DjSongBean> apply(String s) throws Exception {
                        return jsonToEntiy(s);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<DjSongBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<List<DjSongBean>>() {
                    @Override
                    public void accept(List<DjSongBean> list) throws Exception {
                        hasLoadData = true;
                        DjDetailActivity activity = (DjDetailActivity) getActivity();
                        if (activity != null && !list.isEmpty()) {
                            activity.mTablayout.getTabAt(1).setText("节目(" + list.get(0).getProgramCount() + ")");
                        }
                        if (!isLoadMore) {
                            int size = adapter.getDataList().size();
                            if (size == 0) {
                                adapter.addDataList(list);
                                adapter.notifyItemRangeInserted(0, list.size());
                            } else {
                                adapter.clearDataList();
                                adapter.addDataList(list);
                                adapter.notifyItemRangeRemoved(0, size);
                            }
                        } else {
                            int size = adapter.getDataList().size();
                            adapter.addDataList(list);
                            adapter.notifyItemRangeInserted(size, list.size());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable)  {
                    }
                });
    }

    private List<DjSongBean> jsonToEntiy(String json) {
        int bit = SPSetingUtil.getIntValue(AppConstant.SP_KEY_PLAY_QUALITY, AppConstant.MUSIC_BITRATE_NORMAL);
        List<DjSongBean> list = new ArrayList<>();
        JSONObject rootObject = JSON.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            int count = rootObject.getIntValue("count");
            hasMoreDate = rootObject.getBoolean("more");
            JSONArray programArray = rootObject.getJSONArray("programs");
            int size = programArray.size();
            if (size > 0) {
                JSONObject itemObject;
                for (int i = 0; i < size; i++) {
                    itemObject = programArray.getJSONObject(i);
                    DjSongBean bean = new DjSongBean();
                    JSONObject songObject = itemObject.getJSONObject("mainSong");
                    JSONObject albumObject = itemObject.getJSONObject("radio");
                    MusicBean musicBean = new MusicBean();
                    musicBean.setArtistName(itemObject.getJSONObject("dj").getString("nickname"));
                    musicBean.setArtistId(String.valueOf(itemObject.getJSONObject("dj").getLongValue("userId")));
                    musicBean.setArtistImgUrl(itemObject.getJSONObject("dj").getString("avatarUrl"));
                    musicBean.setCommentCount(itemObject.getIntValue("commentCount"));
                    musicBean.setCoverImgUrl(itemObject.getString("coverUrl"));
                    musicBean.setCommentId(itemObject.getString("commentThreadId"));
                    musicBean.setAllTime(itemObject.getLongValue("duration"));
                    musicBean.setMusicOrigin(AppConstant.MUSIC_ORIGIN_DJ);
                    musicBean.setLocalMusic(false);
                    musicBean.setLocalAlbum(false);
                    musicBean.setLocalArtist(false);
                    musicBean.setHasMV(false);
                    musicBean.setMusicName(songObject.getString("name"));
                    musicBean.setMusicId(String.valueOf(songObject.getLongValue("id")));
                    musicBean.setDjProgramId(itemObject.getLongValue("id"));
                    musicBean.setMusicBitrate(bit);
                    if (albumObject != null) {
                        musicBean.setAlbumName(albumObject.getString("name"));
                        musicBean.setAlbumId(String.valueOf(albumObject.getLongValue("id")));
                        musicBean.setAlbumImgUrl(albumObject.getString("picUrl"));
                    }
                    bean.setMusicBean(musicBean);
                    bean.setPlayCount(itemObject.getIntValue("listenerCount"));
                    bean.setCreatTime(itemObject.getLongValue("createTime"));
                    bean.setProgramCount(count);
                    bean.setShareCount(itemObject.getIntValue("shareCount"));
                    bean.setLikedCount(itemObject.getIntValue("likedCount"));
                    list.add(bean);
                }
            }
        }
        return list;
    }

    private void showDjDeailInfo(MusicBean bean) {
        List<KeyValueBean> daList = new ArrayList<>();
        KeyValueBean nextBean = new KeyValueBean();
        nextBean.setTag(MusicOperationPopwindow.ITEM_TAG_NEXT_PLAY);
        nextBean.setValue("下一首播放");
        nextBean.setIntVal(R.drawable.ic_pop_item_next_play);
        daList.add(nextBean);
        Boolean localMusic = bean.getLocalMusic();
        if (localMusic != null && !localMusic) {
            KeyValueBean downlaodBean = new KeyValueBean();
            downlaodBean.setTag(MusicOperationPopwindow.ITEM_TAG_DOWNLOAD);
            downlaodBean.setValue("下载");
            downlaodBean.setIntVal(R.drawable.ic_pop_item_download);
            daList.add(downlaodBean);
            if (bean.getCommentCount() != null) {
                KeyValueBean commentBean = new KeyValueBean();
                commentBean.setTag(MusicOperationPopwindow.ITEM_TAG_COMMENT);
                commentBean.setValue("评论(" + bean.getCommentCount() + ")");
                commentBean.setIntVal(R.drawable.ic_pop_item_commont);
                daList.add(commentBean);
            } else {
                if (!TextUtils.isEmpty(bean.getMusicId())) {
                    bean.setCommentId("R_SO_4_" + bean.getMusicId());
                    KeyValueBean commentBean = new KeyValueBean();
                    commentBean.setTag(MusicOperationPopwindow.ITEM_TAG_COMMENT);
                    commentBean.setValue("查看评论");
                    commentBean.setIntVal(R.drawable.ic_pop_item_commont);
                    daList.add(commentBean);
                }
            }
        }
        KeyValueBean shareBean = new KeyValueBean();
        shareBean.setTag(MusicOperationPopwindow.ITEM_TAG_SHARE);
        shareBean.setValue("分享");
        shareBean.setIntVal(R.drawable.ic_pop_item_share);
        daList.add(shareBean);
        popwindow = new MusicOperationPopwindow(mContext, daList, bean,
                new OnMusicPopItemListener() {
                    @Override
                    public void onShare(MusicBean bean) {
                        if (popwindow != null && popwindow.isShowing()) {
                            popwindow.dismiss();
                        }
                        ((DjDetailActivity) getActivity()).shareMusic(bean);
                    }

                    @Override
                    public void onDownload(MusicBean bean) {
                        if (popwindow != null && popwindow.isShowing()) {
                            popwindow.dismiss();
                        }
                        if (!bean.getLocalMusic()) {
                            DownloadUtil.downloadMusic(mContext, bean);
                            ToastUtil.showNormalMsg("开始下赞:" + bean.getMusicName() + "-" + bean.getArtistName());
                        }
                    }

                    @Override
                    public void onDelete(MusicBean bean) {

                    }

                    @Override
                    public void onSetAlarm(MusicBean bean) {

                    }

                    @Override
                    public void onNextPlay(MusicBean bean) {
                        if (popwindow != null && popwindow.isShowing()) {
                            popwindow.dismiss();
                        }
                        ArrayList<MusicBean> data = new ArrayList<>();
                        data.add(bean);
                        ((DjDetailActivity) getActivity()).addNextMusic(data);
                        ToastUtil.showNormalMsg("添加成功");
                    }

                    @Override
                    public void onCommont(MusicBean bean) {
                        if (popwindow != null && popwindow.isShowing()) {
                            popwindow.dismiss();
                        }
                        String commentId = bean.getCommentId();
                        LogUtil.d(TAG, "CommentId=" + commentId + " musicId=" + bean.getMusicId());
                        if (TextUtils.isEmpty(commentId)) {
                            commentId = "A_DJ_1_" + bean.getMusicId();
                        }
                        SubCommentBean sbean = new SubCommentBean();
                        sbean.setCommentId(commentId);
                        sbean.setCoverImgUrl(bean.getCoverImgUrl());
                        sbean.setTitle(bean.getMusicName());
                        sbean.setSubTitle(bean.getAlbumName() + "-" + bean.getArtistName());
                        sbean.setMusicBean(bean);
                        sbean.setCommentType(AppConstant.COMMENT_TYPE_MUSIC);
                        Intent intent = new Intent(mContext, CommentActivity.class);
                        intent.putExtra(AppConstant.UI_INTENT_KEY_COMMENT, sbean);
                        startActivity(intent);
                    }

                    @Override
                    public void onArtist(MusicBean bean) {

                    }

                    @Override
                    public void onAlbum(MusicBean bean) {

                    }

                    @Override
                    public void onMv(MusicBean bean) {

                    }

                    @Override
                    public void onMusicInfo(MusicBean bean) {

                    }
                });
        popwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ((DjDetailActivity) getActivity()).setBackgroundAlpha(0.5f, 1f, 200);
            }
        });
        ((DjDetailActivity) getActivity()).setBackgroundAlpha(1, 0.5f, 200);
        popwindow.showAtLocation(((DjDetailActivity) getActivity()).mTablayout, Gravity.BOTTOM, 0, 0);
    }

}
