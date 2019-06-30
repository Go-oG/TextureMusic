package wzp.com.texturemusic.common.adapter;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
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
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.StringUtil;

/**
 * Created by Go_oG
 * Description:相似歌曲推荐界面
 * on 2018/2/18.
 */

public class SimilarMusicAdapter extends BaseAdapter<MusicBean, SimilarMusicAdapter.SimilarMusicViewholder> {

    public SimilarMusicAdapter(Context c) {
        super(c);
    }

    @Override
    public SimilarMusicViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycle_music, parent, false);
        return new SimilarMusicViewholder(view);
    }

    @Override
    public void onBindViewHolder(SimilarMusicViewholder holder, int position) {
        holder.itemView.setTag(position);
        MusicBean bean = dataList.get(position);
        SpannableStringBuilder builder;
        if (bean.getSQMusic() != null && bean.getSQMusic()) {
            builder = new SpannableStringBuilder(" " + bean.getArtistName() + "-" + bean.getAlbumName());
            builder = StringUtil.buildStringImage(builder, R.drawable.ic_text_sq, 0, 1, false);
        } else {
            builder = new SpannableStringBuilder(bean.getArtistName() + "-" + bean.getAlbumName());
        }
        holder.itemMusicAlbum.setText(builder);
        holder.itemMusicTime.setText(FormatData.timeValueToString(bean.getAllTime()));
        holder.itemMusicIndexTv.setText((position + 1) + "");
        builder.clearSpans();
        builder.clear();
        String name = bean.getMusicName();
        if (!StringUtil.isEmpty(bean.getAlias())) {
            String aName = name + "( " + bean.getAlias() + ")";
            builder = StringUtil.buildStringColor(aName, color, name.length(), aName.length());
            builder = StringUtil.builderStringSize(builder, 13, name.length(), builder.length());
        } else {
            builder.append(name);
        }
        holder.itemMusicTv.setText(builder);
        if (bean.getHasMV() != null && bean.getHasMV()) {
            holder.itemShowMvImg.setVisibility(View.VISIBLE);
        } else {
            holder.itemShowMvImg.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class SimilarMusicViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_operation_img)
        ImageView itemOperationImg;
        @BindView(R.id.item_music_time)
        TextView itemMusicTime;
        @BindView(R.id.item_music_index_tv)
        TextView itemMusicIndexTv;
        @BindView(R.id.item_music_tv)
        TextView itemMusicTv;
        @BindView(R.id.item_show_mv_img)
        ImageView itemShowMvImg;
        @BindView(R.id.item_music_album)
        TextView itemMusicAlbum;
        @BindView(R.id.item_music_info)
        ConstraintLayout itemMusicInfo;

        public SimilarMusicViewholder(View itemView) {
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
