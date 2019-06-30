package wzp.com.texturemusic.common.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.WYApiUtil;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.common.adapter.SimilarMusicAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.mvmodule.MvDetailActivity;
import wzp.com.texturemusic.util.StringUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:相似歌曲推荐
 * on 2018/2/18.
 */

public class SimilarMusicActivity extends BaseActivityWrapper {
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;
    @BindView(R.id.m_recyclerView)
    RecyclerView mRecyclerView;
    private SimilarMusicAdapter adapter;
    private String musicId;
    private int offset = 0;
    private int limit = 20;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBottomView(true);
        setContentView(R.layout.activity_similar_music);
        ButterKnife.bind(this);
        toolbarTitleTv.setText("相似推荐");
        musicId = getIntent().getStringExtra("musicId");
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        adapter = new SimilarMusicAdapter(mContext);
        adapter.getItemClickSubject()
                .compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        int position = itemBean.getPosition();
                        if (view.getId() == R.id.item_operation_img) {
                            showMusicInfoPop(adapter.getDataList().get(position), null);
                        } else if (view.getId() == R.id.item_show_mv_img) {
                            if (adapter.getDataList().get(position).getHasMV()) {
                                //跳转到Mv中去
                                Long mvId = adapter.getDataList().get(position).getMvBean().getMvId();
                                if (mvId != null && mvId != 0) {
                                    Intent intent = new Intent(mContext, MvDetailActivity.class);
                                    intent.putExtra(AppConstant.UI_INTENT_KEY_MV, mvId);
                                    startActivity(intent);
                                } else {
                                    ToastUtil.showNormalMsg("MV暂时无法观看");
                                }
                            } else {
                                playMusic(adapter.getDataList().get(position));
                            }

                        } else {
                            playMusic(adapter.getDataList().get(position));
                        }
                    }
                });
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(layoutManager);
        if (!StringUtil.isEmpty(musicId)) {
            loadData(musicId, offset, limit, false);
        } else {
            ToastUtil.showNormalMsg("违法数据,暂无数据");
        }
    }

    @OnClick(R.id.toolar_return_img)
    public void onViewClicked() {
        finish();
    }

    @Override
    protected void onNetworkChange(boolean isConnected, boolean isVpn) {
        super.onNetworkChange(isConnected, isVpn);
        if (isConnected&&!hasLoadData){
            loadData(musicId,offset,limit,false);
        }
    }

    @SuppressLint("CheckResult")
    private void loadData(String musicId, int offset, int limit, final boolean loadMore) {
        WYApiUtil.getInstance().buildSongService()
                .getSimiMusic(musicId, offset, limit)
                .map(s -> jsonToEntiy(s)).compose(this.<List<MusicBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    hasLoadData = true;
                    if (!loadMore) {
                        adapter.clearDataList();
                    }
                    adapter.addDataList(list);
                    adapter.notifyDataSetChanged();
                });

    }

    private List<MusicBean> jsonToEntiy(String json) {
        List<MusicBean> list = new ArrayList<>();
        if (!StringUtil.isEmpty(json)) {
            JSONObject jsonObject = JSONObject.parseObject(json);
            if (jsonObject.getIntValue("code") == 200) {
                JSONArray songs = jsonObject.getJSONArray("songs");
                if (songs != null && songs.size() > 0) {
                    int size = songs.size();
                    JSONObject itemObject;
                    for (int i = 0; i < size; i++) {
                        itemObject = songs.getJSONObject(i);
                        MusicBean bean = new MusicBean();
                        bean.setLocalMusic(false);
                        bean.setMusicName(itemObject.getString("name"));
                        bean.setMusicId(String.valueOf(itemObject.getLongValue("id")));
                        bean.setCommentId(itemObject.getString("commentThreadId"));
                        long mvId = itemObject.getLongValue("mvid");
                        if (mvId == 0L) {
                            bean.setHasMV(false);
                        } else {
                            MvBean mvBean = new MvBean();
                            mvBean.setMvId(mvId);
                            bean.setMvBean(mvBean);
                            bean.setHasMV(true);
                        }
                        JSONObject albumObject = itemObject.getJSONObject("album");
                        if (albumObject != null) {
                            bean.setAlbumName(albumObject.getString("name"));
                            bean.setAlbumImgUrl(albumObject.getString("picUrl"));
                            bean.setAlbumId(String.valueOf(albumObject.getLongValue("id")));
                            bean.setLocalAlbum(false);
                            bean.setCoverImgUrl(albumObject.getString("picUrl"));
                            JSONArray artistObject = albumObject.getJSONArray("artists");
                            if (artistObject != null && artistObject.size() > 0) {
                                JSONObject artistItem = artistObject.getJSONObject(0);
                                bean.setArtistImgUrl(artistItem.getString("picUrl"));
                                bean.setArtistId(String.valueOf(artistItem.getLongValue("id")));
                                bean.setArtistName(artistItem.getString("name"));
                            }
                        }
                        bean.setAllTime(itemObject.getLong("duration"));
                        JSONObject privilegeObject = itemObject.getJSONObject("privilege");
                        if (privilegeObject != null) {
                            if (privilegeObject.getLongValue("maxbr") > 320000L) {
                                bean.setSQMusic(true);
                            }
                        }
                        list.add(bean);
                    }
                }
            }
        }
        return list;
    }
}
