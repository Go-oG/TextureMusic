package wzp.com.texturemusic.downloadmodule.adapter;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.dbmodule.bean.DbDownloadEntiy;

public class DownloadMusicAdapter extends BaseAdapter<DbDownloadEntiy, DownloadMusicAdapter.DownloadMusicViewholder> {

    public DownloadMusicAdapter(Context c) {
        super(c);
    }

    @Override
    public DownloadMusicViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycle_music, parent, false);
        return new DownloadMusicViewholder(view);
    }

    @Override
    public void onBindViewHolder(DownloadMusicViewholder holder, int position) {
        holder.itemView.setTag(position);
        holder.itemMusicIndex.setText(position + 1 + "");
        holder.itemAlbumName.setText(dataList.get(position).getAlbumName() == null ? "" : dataList.get(position).getAlbumName());
        holder.itemMusicName.setText(dataList.get(position).getName() == null ? "" : dataList.get(position).getName());
        holder.itemShowMvImg.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class DownloadMusicViewholder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView itemAlbumName, itemMusicName, itemDurrationTime, itemMusicIndex;
        ImageView itemOperationImg, itemShowMvImg;
        ConstraintLayout itemLinear;

        public DownloadMusicViewholder(View itemView) {
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
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject!=null){
                itemClickSubject.onNext(new ItemBean(v,(int)itemView.getTag()));
            }

        }

    }


}
