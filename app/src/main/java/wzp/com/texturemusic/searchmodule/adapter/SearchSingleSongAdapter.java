package wzp.com.texturemusic.searchmodule.adapter;

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
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;

/**
 * Created by Go_oG
 * Description:搜索fragment的单曲适配器
 * on 2017/9/16.
 */

public class SearchSingleSongAdapter extends BaseAdapter<MusicBean, SearchSingleSongAdapter.SearchSingleSongViewHolder> {

    public SearchSingleSongAdapter(Context context) {
        super(context);
    }

    @Override
    public SearchSingleSongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_search_single_song, parent, false);
        return new SearchSingleSongViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SearchSingleSongViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.musicName.setText(dataList.get(position).getMusicName());
        holder.musicInfo.setText(dataList.get(position).getAlbumName() + "-" + dataList.get(position).getArtistName());
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    class SearchSingleSongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout item;
        ImageView operationImg;
        TextView musicName, musicInfo;

        public SearchSingleSongViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item_song_item);
            operationImg = itemView.findViewById(R.id.item_song_operation);
            musicName = itemView.findViewById(R.id.item_song_name);
            musicInfo = itemView.findViewById(R.id.item_song_info);
            item.setOnClickListener(this);
            operationImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickSubject.onNext(new ItemBean(v,(Integer) itemView.getTag()));
        }
    }

}
