package wzp.com.texturemusic.localmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.dbmodule.bean.DbMusicEntiy;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:本地搜索界面的适配器
 * on 2017/12/25.
 */


public class LocalSearchAdapter extends BaseAdapter<DbMusicEntiy, LocalSearchAdapter.LocalSearchViewholder> {

    public LocalSearchAdapter(Context c) {
        super(c);
    }

    @Override
    public LocalSearchViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_local_songs, parent, false);
        return new LocalSearchViewholder(view);
    }

    @Override
    public void onBindViewHolder(LocalSearchViewholder holder, int position) {
        holder.itemView.setTag(position);
        holder.musicnameTv.setText(dataList.get(position).getMusicName());
        holder.musicDescTv.setText(dataList.get(position).getAlbumName() + "-" + dataList.get(position).getArtistName());
        ImageUtil.loadImage(mContext,
                dataList.get(position).getCoverImgUrl(),
                holder.coverImg,
                R.drawable.ic_large_album);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class LocalSearchViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout item;
        ImageView coverImg, operationImg;
        TextView musicnameTv, musicDescTv;

        public LocalSearchViewholder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.local_songs_item);
            coverImg = itemView.findViewById(R.id.item_cover_img);
            operationImg = itemView.findViewById(R.id.item_operation_img);
            musicnameTv = itemView.findViewById(R.id.item_musicname_tv);
            musicDescTv = itemView.findViewById(R.id.item_musicdesc_tv);
            item.setOnClickListener(this);
            operationImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }

        }
    }

}

