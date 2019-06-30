package wzp.com.texturemusic.localmodule.adapter;

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
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.dbmodule.bean.DbHistoryEntiy;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.StringUtil;

/**
 * Created by Go_oG
 * Description:PlayHistoryActivity界面的适配器
 * on 2017/11/20.
 */

public class PlayHistoryAdapter extends BaseAdapter<DbHistoryEntiy, PlayHistoryAdapter.PlayHistoryViewholder> {

    public PlayHistoryAdapter(Context c) {
        super(c);
    }

    @Override
    public PlayHistoryViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_playhistory, parent, false);
        return new PlayHistoryViewholder(view);
    }

    @Override
    public void onBindViewHolder(PlayHistoryViewholder holder, int position) {
        final int index = position;
        DbHistoryEntiy bean = dataList.get(position);
        holder.itemMusicIndex.setText((position + 1) + "");
        holder.itemMusicTime.setText(FormatData.timeValueToString(dataList.get(position).getDuration()));
        if (bean.getHasMv() != null && bean.getHasMv()) {
            holder.itemShowMvImg.setVisibility(View.VISIBLE);
        } else {
            holder.itemShowMvImg.setVisibility(View.GONE);
        }
        String name = bean.getMusicName();
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (!StringUtil.isEmpty(bean.getAlias())) {
            String aName = name + "( " + bean.getAlias() + ")";
            builder = StringUtil.buildStringColor(aName, color, name.length(), aName.length());
            builder = StringUtil.builderStringSize(builder, 13, name.length(), builder.length());
        } else {
            builder.append(name);
        }
        holder.itemMusicName.setText(builder);
        builder.clearSpans();
        builder.clear();
        if (bean.getIsSQMusic() != null && bean.getIsSQMusic()) {
            builder = new SpannableStringBuilder(" " + bean.getArtistName() + "-" + bean.getAlbumName());
            builder = StringUtil.buildStringImage(builder, R.drawable.ic_text_sq, 0, 1, false);
        } else {
            builder = new SpannableStringBuilder(bean.getArtistName() + "-" + bean.getAlbumName());
        }
        holder.itemMusicAlbum.setText(builder);
        holder.itemMusicInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickSubject != null) {
                    itemClickSubject.onNext(new ItemBean(v, index));
                }
            }
        });
        holder.itemOperationImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickSubject != null) {
                    itemClickSubject.onNext(new ItemBean(v, index));
                }
            }
        });
        holder.itemShowMvImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickSubject != null) {
                    itemClickSubject.onNext(new ItemBean(v, index));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class PlayHistoryViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_music_tv)
        TextView itemMusicName;
        @BindView(R.id.item_operation_img)
        ImageView itemOperationImg;
        @BindView(R.id.item_music_time)
        TextView itemMusicTime;
        @BindView(R.id.item_music_album)
        TextView itemMusicAlbum;
        @BindView(R.id.item_music_info)
        ConstraintLayout itemMusicInfo;
        @BindView(R.id.item_music_index_tv)
        TextView itemMusicIndex;
        @BindView(R.id.item_show_mv_img)
        ImageView itemShowMvImg;

        public PlayHistoryViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
