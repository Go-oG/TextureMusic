package wzp.com.texturemusic.albummodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.FragmentEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.albummodule.NetAlbumDetailActivity;
import wzp.com.texturemusic.albummodule.adapter.NetAlbumInfoArtistAdapter;
import wzp.com.texturemusic.api.AlbumApiManager;
import wzp.com.texturemusic.artistmodule.ui.ArtistDetailActivity;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ShareBean;
import wzp.com.texturemusic.bean.SubCommentBean;
import wzp.com.texturemusic.common.ui.CommentActivity;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.interf.OnRecycleItemClickListener;

/**
 * Created by Go_oG
 * Description:网络专辑的详细信息界面
 * on 2018/1/26.
 */

public class NetAlbumInfoFragment extends BaseFragment {
    @BindView(R.id.m_recyclerview)
    RecyclerView mRecyclerview;
    Unbinder unbinder;
    private AlbumBean albumBean;
    private NetAlbumInfoArtistAdapter artistAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumBean = getArguments().getParcelable("album");
        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        artistAdapter = new NetAlbumInfoArtistAdapter(mContext);
        artistAdapter.setItemClickListener(new OnRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ArtistBean bean = artistAdapter.getAlbumBean().getArtistBean();
                AlbumBean albBean = artistAdapter.getAlbumBean();
                switch (view.getId()) {
                    case R.id.item_artist_cos:
                        //歌手
                        if (bean != null) {
                            Intent intent = new Intent(mContext, ArtistDetailActivity.class);
                            intent.putExtra(AppConstant.UI_INTENT_KEY_ARTIST, bean);
                            startActivity(intent);
                        }
                        break;
                    case R.id.item_comment_count_linear:
                        //评论
                        SubCommentBean commentBean = new SubCommentBean();
                        if (albBean != null) {
                            if (TextUtils.isEmpty(albBean.getCommentId())) {
                                if (albBean.getAlbumId() != null) {
                                    albBean.setCommentId("R_AL_3_" + albBean.getAlbumId());
                                }
                            }
                            Intent intent = new Intent(mContext, CommentActivity.class);
                            commentBean.setCommentId(albBean.getCommentId());
                            commentBean.setTitle(albBean.getAlbumName());
                            commentBean.setSubTitle(albBean.getArtistBean().getArtistName());
                            commentBean.setCoverImgUrl(albBean.getAlbumImgUrl());
                            commentBean.setAlbumBean(albBean);
                            commentBean.setCommentType(AppConstant.COMMENT_TYPE_ALBUM);
                            intent.putExtra(AppConstant.UI_INTENT_KEY_COMMENT, commentBean);
                            startActivity(intent);
                        }
                        break;
                    case R.id.item_share_count_linear:
                        //分享
                        ShareBean shareBean = new ShareBean();
                        shareBean.setText(albBean.getDescription());
                        shareBean.setTitle(albBean.getAlbumName() + "-" + bean.getArtistName());
                        shareBean.setImgUrl(albBean.getAlbumImgUrl());
                        shareBean.setWebUrl("http://music.163.com/#/album?id=" + albBean.getAlbumId());
                        ((NetAlbumDetailActivity) getActivity()).shareMusic(shareBean);
                        break;
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
            mContentView = inflater.inflate(R.layout.fragment_net_album_info, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecyclerview.setAdapter(artistAdapter);
        mRecyclerview.setLayoutManager(layoutManager);
    }


    @Override
    public void lazyLoading() {
        if (!hasLoadData && albumBean != null) {
            getAlbumDetail(Long.parseLong(albumBean.getAlbumId()));
        }
    }

    @SuppressLint("CheckResult")
    private void getAlbumDetail(final long albumID) {
        AlbumApiManager.getAlbumDetail(albumID, albumID, 0, 100)
                .map(new Function<String, AlbumBean>() {
                    @Override
                    public AlbumBean apply(String s) throws Exception {
                        return jsonToAlbumDetail(s);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<AlbumBean>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<AlbumBean>() {
                    @Override
                    public void accept(AlbumBean bean) throws Exception {
                        hasLoadData = true;
                        albumBean = bean;
                        artistAdapter.setAlbumBean(bean);
                        artistAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        showErrorView();
                    }
                });
    }

    private AlbumBean jsonToAlbumDetail(String json) {
        AlbumBean bean = new AlbumBean();
        JSONObject resultObject = JSONObject.parseObject(json);
        if (resultObject.getIntValue("code") == 200) {
            bean = new AlbumBean();
            JSONObject albumObject = resultObject.getJSONObject("album");
            JSONObject infoObject = albumObject.getJSONObject("info");
            JSONObject artistObject = albumObject.getJSONObject("artist");

            ArtistBean artistBean = new ArtistBean();
            if (artistObject != null) {
                artistBean.setArtistImgUrl(artistObject.getString("picUrl"));
                artistBean.setArtistName(artistObject.getString("name"));
                artistBean.setArtistId(String.valueOf(artistObject.getLongValue("id")));
                artistBean.setDecriptions(artistObject.getJSONArray("alias").toString());
                artistBean.setAlbumCount(artistObject.getInteger("albumSize"));
                artistBean.setMusicCount(artistObject.getIntValue("musicSize"));
                artistBean.setLocalArtist(false);
            }
            bean.setArtistBean(artistBean);
            bean.setCommentId(albumObject.getString("commentThreadId"));
            bean.setAlbumId(String.valueOf(albumObject.getLongValue("id")));
            String albumName = albumObject.getString("name");
            bean.setAlbumName(albumName);
            bean.setLikedCount(infoObject.getIntValue("likedCount"));
            bean.setShareCount(infoObject.getIntValue("shareCount"));
            bean.setCommentCount(infoObject.getIntValue("commentCount"));
            bean.setDescription(albumObject.getString("description"));
            bean.setAlbumImgUrl(albumObject.getString("picUrl"));
            bean.setAlbumId(String.valueOf(albumObject.getLongValue("id")));
            bean.setPublishTime(albumObject.getLongValue("publishTime"));
            bean.setPublishCompany(albumObject.getString("company"));
            bean.setLocalAlbum(false);
        }
        return bean;
    }

}
