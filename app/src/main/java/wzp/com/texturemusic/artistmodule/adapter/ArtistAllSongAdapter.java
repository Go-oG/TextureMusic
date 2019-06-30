package wzp.com.texturemusic.artistmodule.adapter;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.common.viewholder.PlayAllDataViewholder;
import wzp.com.texturemusic.interf.OnOperationListener;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.StringUtil;

@SuppressWarnings("ALL")
public class ArtistAllSongAdapter extends BaseAdapter<MusicBean, RecyclerView.ViewHolder> {
    private OnOperationListener operationListener;
    private ImageSpan imageSpan;

    public void setOperationListener(OnOperationListener operationListener) {
        this.operationListener = operationListener;
    }

    public ArtistAllSongAdapter(Context c) {
        super(c);
        imageSpan = new ImageSpan(c, R.drawable.ic_text_sq, ImageSpan.ALIGN_BASELINE);
    }

    @SuppressWarnings("Annotator")
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
           View view = LayoutInflater.from(mContext).inflate(R.layout.common_item_top_select, parent, false);
            return new PlayAllDataViewholder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycle_music, parent, false);
            return new ArtistAllSongViewholder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PlayAllDataViewholder) {
            PlayAllDataViewholder opertionHolder = (PlayAllDataViewholder) holder;
            opertionHolder.infoTv.setText("播放全部(共" + dataList.size() + "首)");
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
        if (holder instanceof ArtistAllSongViewholder) {
            int realIndex = position - 1;
            ArtistAllSongViewholder itemHolder = (ArtistAllSongViewholder) holder;
            itemHolder.itemView.setTag(realIndex);
            MusicBean bean = dataList.get(realIndex);
            SpannableStringBuilder builder;
            if (bean.getSQMusic() != null && bean.getSQMusic()) {
                builder = new SpannableStringBuilder(" " + bean.getArtistName() + "-" + bean.getAlbumName());
                builder.setSpan(imageSpan, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            } else {
                builder = new SpannableStringBuilder(bean.getArtistName() + "-" + bean.getAlbumName());
            }
            itemHolder.itemMusicAlbum.setText(builder);
            itemHolder.itemMusicTime.setText(FormatData.timeValueToString(bean.getAllTime()));
            itemHolder.itemMusicIndexTv.setText(position + "");
            builder.clearSpans();
            builder.clear();
            String name = bean.getMusicName();
            if (!StringUtil.isEmpty(bean.getAlias())) {
                name = name + "( " + bean.getAlias() + ")";
                builder = StringUtil.buildStringColor(name, color, name.length(), name.length());
                builder = StringUtil.builderStringSize(builder, 13, name.length(), builder.length());
            } else {
                builder.append(name);
            }
            itemHolder.itemMusicTv.setText(builder);
            if (bean.getHasMV() != null && bean.getHasMV()) {
                itemHolder.itemShowMvImg.setVisibility(View.VISIBLE);
            } else {
                itemHolder.itemShowMvImg.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (dataList.isEmpty()) {
            return 0;
        } else {
            return dataList.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    class ArtistAllSongViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_operation_img)
        ImageView itemOperationImg;
        @BindView(R.id.item_music_time)
        TextView itemMusicTime;
        @BindView(R.id.item_music_index_tv)
        TextView itemMusicIndexTv;
        @BindView(R.id.item_music_tv)
        TextView itemMusicTv;
        @BindView(R.id.item_music_album)
        TextView itemMusicAlbum;
        @BindView(R.id.item_show_mv_img)
        ImageView itemShowMvImg;
        @BindView(R.id.item_music_info)
        ConstraintLayout itemMusicInfo;

        public ArtistAllSongViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemMusicInfo.setOnClickListener(this);
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


}
