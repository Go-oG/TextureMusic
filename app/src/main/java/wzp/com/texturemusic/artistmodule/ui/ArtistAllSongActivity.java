package wzp.com.texturemusic.artistmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import wzp.com.texturemusic.api.ArtistApiManager;
import wzp.com.texturemusic.artistmodule.adapter.ArtistAllSongAdapter;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.common.ui.MultipleChoiceActivity;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.interf.OnOperationListener;
import wzp.com.texturemusic.core.customui.OnLoadMoreForLinearLayoutManager;

public class ArtistAllSongActivity extends BaseActivityWrapper {
    public static final String INTENT_KEY_ARTIST_ALL_SONG = "artist_all_song";
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;
    @BindView(R.id.m_recyclerView)
    RecyclerView mRecyclerView;
    private ArtistBean artistBean;
    private ArtistAllSongAdapter songAdapter;
    private int offset = 0;
    private int limit = 50;
    private boolean hasMoreData = true;


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBottomView(true);
        setContentView(R.layout.activity_artist_allsong);
        ButterKnife.bind(this);
        artistBean = getIntent().getParcelableExtra(INTENT_KEY_ARTIST_ALL_SONG);
        toolbarTitleTv.setText(artistBean.getArtistName() + "的全部歌曲");
        songAdapter = new ArtistAllSongAdapter(mContext);
        songAdapter.setOperationListener(new OnOperationListener() {
            @Override
            public void onPlayAll() {
                playMusicList(songAdapter.getDataList(), true);
            }

            @Override
            public void onMultipleChoice() {
                ArrayList<MusicBean> list = (ArrayList<MusicBean>) songAdapter.getDataList();
                Intent intent = new Intent(mContext, MultipleChoiceActivity.class);
                intent.putParcelableArrayListExtra(MultipleChoiceActivity.INTENT_DATA_LIST, list);
                startActivity(intent);
            }
        });
        songAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        int position = itemBean.getPosition();
                        playMusic(songAdapter.getDataList().get(position));
                    }
                });


        mRecyclerView.setAdapter(songAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addOnScrollListener(new OnLoadMoreForLinearLayoutManager() {
            @Override
            public void onLoadMore() {
                if (hasMoreData) {
                    offset++;
                    loadData(artistBean, offset * limit, limit, true);
                }
            }
        });
        loadData(artistBean, offset * limit, limit, false);
    }

    @OnClick(R.id.toolar_return_img)
    public void onViewClicked() {
        finish();
    }


    @SuppressLint("CheckResult")
    private void loadData(ArtistBean bean, int offset, int limit, final boolean isLoadMore) {
        ArtistApiManager
                .getArtistAllSong(bean.getArtistId(), offset, limit)
                .map(s -> jsonToEntiy(s)).subscribeOn(Schedulers.io())
                .compose(this.<List<MusicBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    if (!isLoadMore) {
                        songAdapter.clearDataList();
                    }
                    songAdapter.addDataList(list);
                    songAdapter.notifyDataSetChanged();
                }, throwable -> {

                });
    }


    private List<MusicBean> jsonToEntiy(String json) {
        List<MusicBean> list = new ArrayList<>();
        JSONObject jsonObject = JSONObject.parseObject(json);
        if (jsonObject.getIntValue("code") == 200) {
            hasMoreData = jsonObject.getBooleanValue("more");
            JSONArray songs = jsonObject.getJSONArray("songs");
            if (songs != null && songs.size() > 0) {
                int size = songs.size();
                JSONObject itemObject;
                for (int i = 0; i < size; i++) {
                    itemObject = songs.getJSONObject(i);
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
