package wzp.com.texturemusic.artistmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

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
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.ArtistApiManager;
import wzp.com.texturemusic.artistmodule.adapter.ArtistDetailMusicAdapter;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.mvmodule.MvDetailActivity;
import wzp.com.texturemusic.util.SPSetingUtil;

/**
 * Created by Go_oG
 * Description:展示获取艺术家的部分歌曲
 * on 2017/11/13.
 */
public class ArtistDetailMusicFragment extends BaseFragment {
    @BindView(R.id.m_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private ArtistDetailMusicAdapter adapter;
    private boolean dataHasLoad = false;
    private ArtistBean artistBean;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArtistDetailMusicAdapter(mContext);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        if (position == adapter.getDataList().size()) {
                            //查看更多
                            Intent intent = new Intent(mContext, ArtistAllSongActivity.class);
                            intent.putExtra(ArtistAllSongActivity.INTENT_KEY_ARTIST_ALL_SONG, artistBean);
                            startActivity(intent);
                        } else {
                            MusicBean bean = adapter.getDataList().get(position);
                            int id = itemBean.getView().getId();
                            if (id == R.id.item_operation_img) {
                                ((ArtistDetailActivity) getActivity())
                                        .showMusicInfoPop(bean, null);

                            } else if (id == R.id.item_item) {
                                ((ArtistDetailActivity) getActivity())
                                        .playMusic(bean);

                            } else if (id == R.id.music_name_tv) {//跳转到Mv中去
                                long mvId = bean.getMvBean().getMvId();
                                Intent intent = new Intent(mContext, MvDetailActivity.class);
                                intent.putExtra(AppConstant.UI_INTENT_KEY_MV, mvId);
                                startActivity(intent);
                            }
                        }
                    }
                });
        artistBean = getArguments().getParcelable(ArtistDetailActivity.BUNDLE_KEY);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_artist_detail_music, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecycleview.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycleview.setLayoutManager(layoutManager);
        SlideInLeftAnimator animator = new SlideInLeftAnimator(new OvershootInterpolator(0f));
        mRecycleview.setItemAnimator(animator);
        mRecycleview.getItemAnimator().setAddDuration(600);
        mRecycleview.getItemAnimator().setChangeDuration(600);
        mRecycleview.getItemAnimator().setAddDuration(600);
    }

    @Override
    public void lazyLoading() {
        if (!dataHasLoad) {
            if (artistBean != null) {
                if (!artistBean.getLocalArtist()) {
                    loadData(artistBean.getArtistId());
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private void loadData(String artistID) {
        ArtistApiManager
                .getArtistsHotSong(artistID)
                .map(s -> jsonToMusicEntiy(s))
                .compose(this.<List<MusicBean>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    dataHasLoad = true;
                    adapter.addDataList(list);
                    adapter.notifyItemRangeInserted(0, list.size());
                }, throwable -> {

                });

    }

    private List<MusicBean> jsonToMusicEntiy(String json) {
        int bit = SPSetingUtil.getIntValue(AppConstant.SP_KEY_PLAY_QUALITY, AppConstant.MUSIC_BITRATE_NORMAL);
        List<MusicBean> list = new ArrayList<>();
        JSONObject rootObject = JSON.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            JSONArray songsObject = rootObject.getJSONArray("hotSongs");
            int size = songsObject.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    JSONObject itemObject = songsObject.getJSONObject(i);
                    MusicBean bean = new MusicBean();
                    bean.setMusicName(itemObject.getString("name"));
                    bean.setAllTime(itemObject.getLongValue("duration"));
                    bean.setMusicId(String.valueOf(itemObject.getLongValue("id")));
                    bean.setArtistName(artistBean.getArtistName());
                    bean.setArtistId(artistBean.getArtistId());
                    bean.setAlbumName(itemObject.getJSONObject("album").getString("name"));
                    bean.setAlbumId(String.valueOf(itemObject.getJSONObject("album").getLongValue("id")));
                    String coverImg = itemObject.getJSONObject("album").getString("picUrl");
                    bean.setCoverImgUrl(coverImg);
                    bean.setAlbumImgUrl(coverImg);
                    bean.setArtistImgUrl(itemObject.getJSONArray("artists").getJSONObject(0).getString("img1v1Url"));
                    bean.setLocalMusic(false);
                    bean.setMusicBitrate(bit);
                    long mvId = itemObject.getLongValue("mvid");
                    if (mvId == 0) {
                        bean.setHasMV(false);
                    } else {
                        bean.setHasMV(true);
                        MvBean mvBean = new MvBean();
                        mvBean.setMvId(mvId);
                        bean.setMvBean(mvBean);
                    }
                    list.add(bean);
                }
            }
        }
        return list;
    }

}
