package wzp.com.texturemusic.playlistmodule.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.PlayListBean;
import wzp.com.texturemusic.common.viewholder.PlayAllDataViewholder;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.dbmodule.util.DbCollectUtil;
import wzp.com.texturemusic.interf.OnImageLoadListener;
import wzp.com.texturemusic.interf.OnOperationListener;
import wzp.com.texturemusic.playlistmodule.OnAnimatorFinishListener;
import wzp.com.texturemusic.playlistmodule.bean.PlaylistAdapterBean;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;
import wzp.com.texturemusic.util.StringUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * author:Go_oG
 * date: on 2018/5/14
 * packageName: wzp.com.texturemusic.playlistmodule.adapter
 */
public class PlaylistDetailAdapter extends BaseAdapter<PlaylistAdapterBean, RecyclerView.ViewHolder> {
    private OnOperationListener operationListener;
    private OnInfoCallback infoCallback;
    private ImageSpan imageSpan;
    private OnAnimatorFinishListener animatorListener;
    private int color2 = 0;
    private boolean hasLoadCoverImg = false;

    public void setAnimatorListener(OnAnimatorFinishListener animatorListener) {
        this.animatorListener = animatorListener;
    }

    public void setInfoCallback(OnInfoCallback infoCallback) {
        this.infoCallback = infoCallback;
    }

    public void setOperationListener(OnOperationListener operationListener) {
        this.operationListener = operationListener;
    }

    public PlaylistDetailAdapter(Context c) {
        super(c);
        imageSpan = new ImageSpan(c, R.drawable.ic_text_sq, ImageSpan.ALIGN_BASELINE);
    }

    @Override
    public Observable<ItemBean> getItemClickSubject() {
        return itemClickSubject.throttleFirst(clickTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_playlist_detail_info, parent, false);
            return new ViewholderInfoV2(view);
        } else if (viewType == 1) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.common_item_top_select, parent, false);
            return new PlayAllDataViewholder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycle_music, parent, false);
            return new ViewholderItemV2(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PlayListBean listBean = dataList.get(position).getPlayList();
        if (holder instanceof ViewholderInfoV2) {
            final ViewholderInfoV2 infoHolder = (ViewholderInfoV2) holder;
            if (!hasLoadCoverImg) {
                String url = listBean.getCoverImgUr();
                if (StringUtil.isEmpty(url)) {
                    return;
                }
                ImageUtil.loadImage(mContext, url + AppConstant.WY_IMG_300_300,
                        R.drawable.drawable_error_background, new OnImageLoadListener() {
                            @Override
                            public void onSuccess(final Bitmap bitmap) {
                                hasLoadCoverImg = true;
                                infoHolder.mHeadimg.setImageBitmap(bitmap);
                                if (color2 == 0) {
                                    Palette palette = Palette.from(bitmap).generate();
                                    Palette.Swatch swatch = palette.getDarkMutedSwatch();
                                    color2 = (swatch == null) ? color : swatch.getRgb();
                                }
                                infoHolder.mConstrainLayout.setBackgroundColor(color2);
                                animatorListener.onFinishi(color2);
                            }
                        });
            }
        }
        if (holder instanceof PlayAllDataViewholder) {
            PlayAllDataViewholder opertionHolder = (PlayAllDataViewholder) holder;
            opertionHolder.infoTv.setText("播放全部(共" + dataList.get(position).getMusicSize() + "首)");
            opertionHolder.muiltiChoiceLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (operationListener != null) {
                        operationListener.onMultipleChoice();
                    }
                }
            });
            opertionHolder.itemSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (operationListener != null) {
                        operationListener.onPlayAll();
                    }
                }
            });
        }
        if (holder instanceof ViewholderItemV2) {
            ViewholderItemV2 itemHolder = (ViewholderItemV2) holder;
            itemHolder.itemView.setTag(position);
            MusicBean bean = dataList.get(position).getMusicBean();
            SpannableStringBuilder builder;
            if (bean.getSQMusic() != null && bean.getSQMusic()) {
                builder = new SpannableStringBuilder(" " + bean.getArtistName() + "-" + bean.getAlbumName());
                builder.setSpan(imageSpan, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            } else {
                builder = new SpannableStringBuilder(bean.getArtistName() + "-" + bean.getAlbumName());
            }
            itemHolder.itemAlbumName.setText(builder);
            itemHolder.itemDurrationTime.setText(FormatData.timeValueToString(bean.getAllTime()));
            itemHolder.itemMusicIndex.setText((position - 1) + "");
            builder.clearSpans();
            builder.clear();
            String name = bean.getMusicName();
            int length = name.length();
            if (!StringUtil.isEmpty(bean.getAlias())) {
                name = name + "( " + bean.getAlias() + ")";
                builder = StringUtil.buildStringColor(name, 0x89000000, length, name.length());
                builder = StringUtil.builderStringSize(builder, 13, length, builder.length());
            } else {
                builder.append(name);
            }
            itemHolder.itemMusicName.setText(builder);
            if (bean.getHasMV() != null && bean.getHasMV()) {
                itemHolder.itemShowMvImg.setVisibility(View.VISIBLE);
            } else {
                itemHolder.itemShowMvImg.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).getType();
    }

    public class ViewholderInfoV2 extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.m_headimg)
        public ImageView mHeadimg;
        @BindView(R.id.m_like_img)
        public ImageView mLikedimg;
        @BindView(R.id.playcount_tv)
        public TextView playcountTv;
        @BindView(R.id.m_playlist_name_tv)
        public TextView mPlaylistNameTv;
        @BindView(R.id.m_creater_head_img)
        public CircleImageView mCreaterHeadImg;
        @BindView(R.id.m_creater_name_tv)
        public TextView mCreaterNameTv;
        @BindView(R.id.m_creater_linear)
        public LinearLayout mCreaterLinear;
        @BindView(R.id.m_collection_tv)
        public TextView mCollectionTv;
        @BindView(R.id.m_collection_linear)
        public LinearLayout mCollectionLinear;
        @BindView(R.id.m_comment_count_tv)
        public TextView mCommentCountTv;
        @BindView(R.id.m_comment_linear)
        public LinearLayout mCommentLinear;
        @BindView(R.id.m_sharecount_tv)
        public TextView mSharecountTv;
        @BindView(R.id.m_share_linear)
        public LinearLayout mShareLinear;
        @BindView(R.id.m_download_linear)
        public LinearLayout mDownloadLinear;
        @BindView(R.id.m_constrain_layout)
        public ConstraintLayout mConstrainLayout;

        public ViewholderInfoV2(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mHeadimg.setOnClickListener(this);
            mCreaterLinear.setOnClickListener(this);
            mCollectionLinear.setOnClickListener(this);
            mCommentLinear.setOnClickListener(this);
            mShareLinear.setOnClickListener(this);
            mDownloadLinear.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.m_collection_linear) {
                if (dataList == null || dataList.isEmpty() || dataList.get(0) == null) {
                    return;
                }
                PlayListBean bean = dataList.get(0).getPlayList();
                Boolean islike = bean.getLiked();
                if (islike != null && islike) {
                    DbCollectUtil.deleteLikedPlaylist(bean);
                    bean.setLiked(false);
                    ToastUtil.showNormalMsg("取消成功");
                    mLikedimg.setImageResource(R.drawable.ic_unlike);
                } else {
                    DbCollectUtil.addLikedPlaylist(bean);
                    bean.setLiked(true);
                    ToastUtil.showNormalMsg("收藏成功");
                    mLikedimg.setImageResource(R.drawable.ic_collection);
                }
            } else {
                if (infoCallback != null) {
                    infoCallback.onViewClick(v);
                }
            }
        }

    }

    public class ViewholderItemV2 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemAlbumName, itemMusicName, itemDurrationTime, itemMusicIndex;
        ImageView itemOperationImg, itemShowMvImg;
        ConstraintLayout itemLinear;

        public ViewholderItemV2(View itemView) {
            super(itemView);
            itemAlbumName = itemView.findViewById(R.id.item_music_album);
            itemMusicName = itemView.findViewById(R.id.item_music_tv);
            itemDurrationTime = itemView.findViewById(R.id.item_music_time);
            itemOperationImg = itemView.findViewById(R.id.item_operation_img);
            itemLinear = itemView.findViewById(R.id.item_music_info);
            itemMusicIndex = itemView.findViewById(R.id.item_music_index_tv);
            itemShowMvImg = itemView.findViewById(R.id.item_show_mv_img);
            itemLinear.setOnClickListener(this);
            itemOperationImg.setOnClickListener(this);
            itemShowMvImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                int position = (int) itemView.getTag();
                itemClickSubject.onNext(new ItemBean(v, position));
            }
        }

    }

    public interface OnInfoCallback {
        void onViewClick(View view);
    }


}
