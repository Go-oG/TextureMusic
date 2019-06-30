package wzp.com.texturemusic.artistmodule.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;

/**
 * Created by Go_oG
 * Description:
 * on 2017/11/13.
 */

public class ArtistDetailMusicAdapter extends BaseAdapter<MusicBean, RecyclerView.ViewHolder> {

    private Drawable drawable;

    public ArtistDetailMusicAdapter(Context c) {
        super(c);
        drawable = mContext.getDrawable(R.drawable.ic_textdrawable_mv);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_artist_detail_show_all_song, parent, false);
            return new ArtistDetailMusicEndViewholder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_artist_detail_song, parent, false);
            return new ArtistDetailMusicViewholder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        viewHolder.itemView.setTag(position);
        if (viewHolder instanceof ArtistDetailMusicViewholder) {
            ArtistDetailMusicViewholder holder = (ArtistDetailMusicViewholder) viewHolder;
            holder.positionTv.setText((position + 1) + "");
            holder.musicNameTv.setText(dataList.get(position).getMusicName());
            holder.musicInfoTv.setText(dataList.get(position).getArtistName() + "-" + dataList.get(position).getAlbumName());
            if (dataList.get(position).getHasMV()) {
                if (drawable != null) {
                    holder.musicNameTv.setCompoundDrawablesRelative(null, null, drawable, null);
                    holder.musicNameTv.setCompoundDrawablePadding(8);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (dataList.size() > 0) {
            return dataList.size() + 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == dataList.size()) {
            return 0;
        } else {
            return 1;
        }
    }

    class ArtistDetailMusicViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RelativeLayout item;
        private TextView musicNameTv, musicInfoTv;
        private ImageView itemImg;
        private TextView positionTv;

        public ArtistDetailMusicViewholder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item_item);
            musicNameTv = itemView.findViewById(R.id.music_name_tv);
            musicInfoTv = itemView.findViewById(R.id.music_info_tv);
            itemImg = itemView.findViewById(R.id.item_operation_img);
            positionTv = itemView.findViewById(R.id.position_tv);
            item.setOnClickListener(this);
            itemImg.setOnClickListener(this);
            musicNameTv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }

        }
    }

    class ArtistDetailMusicEndViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout itemItem;

        public ArtistDetailMusicEndViewholder(View itemView) {
            super(itemView);
            itemItem = itemView.findViewById(R.id.m_show_item);
            itemItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }
        }
    }

}
