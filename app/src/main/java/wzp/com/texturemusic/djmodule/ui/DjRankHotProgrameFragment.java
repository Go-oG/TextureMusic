package wzp.com.texturemusic.djmodule.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.DJApiManager;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.RadioProgramBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.djmodule.adapter.DjRankListAdapter;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.SPSetingUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:电台排行榜中的最热节目
 * on 2017/10/28.
 */

public class DjRankHotProgrameFragment extends BaseFragment {
    @BindView(R.id.update_time_tv)
    TextView mUpdateTimeTv;
    @BindView(R.id.dj_rank_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private DjRankListAdapter adapter;
    private int limit = 100;
    private int offset = 0;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new DjRankListAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        RadioProgramBean bean = adapter.getDataList().get(position);
                        MusicBean musicBean = new MusicBean();
                        musicBean.setLocalMusic(false);
                        musicBean.setMusicOrigin(AppConstant.MUSIC_ORIGIN_DJ);
                        musicBean.setMusicName(bean.getProgramName());
                        musicBean.setCoverImgUrl(bean.getCoverImgUrl());
                        musicBean.setMusicId(bean.getMusicId());
                        musicBean.setDjProgramId(bean.getProgramId());
                        musicBean.setAlbumName(bean.getDjName());
                        musicBean.setAlbumId(String.valueOf(bean.getDjId()));
                        musicBean.setCommentId(bean.getCommentId());
                        musicBean.setSubCommentId(String.valueOf(bean.getProgramId()));
                        musicBean.setArtistName(bean.getDjErName());
                        musicBean.setArtistId(String.valueOf(bean.getDjErId()));
                        musicBean.setAllTime(Long.valueOf(bean.getDurationTime()));
                        musicBean.setMusicBitrate(SPSetingUtil.getIntValue(AppConstant.SP_KEY_PLAY_QUALITY, AppConstant.MUSIC_BITRATE_NORMAL));
                        ((DjRankActivity) getActivity()).playMusic(musicBean);
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
            mContentView = inflater.inflate(R.layout.fragment_dj_rank, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycleview.setItemAnimator(new SlideInUpAnimator());
        mRecycleview.getItemAnimator().setRemoveDuration(400);
        mRecycleview.getItemAnimator().setAddDuration(400);
        mRecycleview.getItemAnimator().setChangeDuration(400);
        mRecycleview.getItemAnimator().setMoveDuration(400);
        mRecycleview.setAdapter(adapter);
        mRecycleview.setLayoutManager(layoutManager);
    }

    @Override
    public void lazyLoading() {
        if (!hasLoadData) {
            loadHotProgrameData(offset, limit);
        }
    }

    @SuppressLint("CheckResult")
    private void loadHotProgrameData(int offset, int limit) {
        showLoadingView(false);
        DJApiManager
                .getTopDjPrograme(offset, limit)
                .map(new Function<String, List<RadioProgramBean>>() {
                    @Override
                    public List<RadioProgramBean> apply(@NonNull String s) throws Exception {
                        return jsonToHotProgremeData(s);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<RadioProgramBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<List<RadioProgramBean>>() {
                    @Override
                    public void accept(List<RadioProgramBean> listDatas) throws Exception {
                        cancleLoadingView(true);
                        hasLoadData = true;
                        if (listDatas != null) {
                            String updateTime = listDatas.get(0).getUpdateTime();
                            mUpdateTimeTv.setText("更新时间:" + FormatData.unixTimeTostring(Long.valueOf
                                    (updateTime)).substring(5));
                            adapter.clearDataList();
                            adapter.addDataList(listDatas);
                            adapter.notifyItemRangeInserted(0, listDatas.size());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        showErrorView();
                        ToastUtil.showNormalMsg("网络错误");
                    }
                });
    }

    private List<RadioProgramBean> jsonToHotProgremeData(String json) {
        List<RadioProgramBean> list = new ArrayList<>();
        JSONObject root = JSONObject.parseObject(json);
        if (root != null && root.getIntValue("code") == 200) {
            String updateTime = String.valueOf(root.getLongValue("updateTime"));
            JSONArray jsArrays = root.getJSONArray("toplist");
            int length = jsArrays.size();
            JSONObject reObject;
            JSONObject programObject;
            for (int i = 0; i < length; i++) {
                reObject = jsArrays.getJSONObject(i);
                programObject = reObject.getJSONObject("program");
                RadioProgramBean bean = new RadioProgramBean();
                bean.setRank(reObject.getIntValue("rank"));
                bean.setLastRank(reObject.getIntValue("lastRank"));
                bean.setHotScord(reObject.getIntValue("score"));
                bean.setMusicId(String.valueOf(programObject.getLongValue("mainTrackId")));
                bean.setProgramName(programObject.getString("name"));
                bean.setCommentCount(programObject.getIntValue("commentCount"));
                bean.setLikeCount(programObject.getIntValue("likedCount"));
                bean.setCommentId(programObject.getString("commentThreadId"));
                bean.setCoverImgUrl(programObject.getString("coverUrl"));
                bean.setDurationTime(programObject.getIntValue("duration"));
                bean.setPlayCount(programObject.getIntValue("listenerCount"));
                bean.setTrackCount(programObject.getIntValue("trackCount"));
                bean.setDjId(programObject.getJSONObject("radio").getLongValue("id"));
                bean.setDjName(programObject.getJSONObject("radio").getString("name"));
                bean.setUserName(programObject.getJSONObject("dj").getString("nickname"));
                bean.setUserId(programObject.getJSONObject("dj").getLongValue("userId"));
                bean.setUpdateTime(updateTime);
                bean.setProgramId(programObject.getLongValue("id"));
                list.add(bean);
            }
        }
        return list;
    }

}
