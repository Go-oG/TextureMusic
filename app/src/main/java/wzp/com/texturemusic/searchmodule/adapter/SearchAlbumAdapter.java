package wzp.com.texturemusic.searchmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/9/16.
 */

public class SearchAlbumAdapter extends BaseAdapter<AlbumBean, SearchAlbumAdapter.AlbumViewHolder> {


    public SearchAlbumAdapter(Context c) {
        super(c);
    }


    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AlbumViewHolder holder, int position) {
        holder.itemView.setTag(position);
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) holder.item.getLayoutParams();
        if (position % 2 == 0) {
            params.setMarginEnd(2);
            params.setMarginStart(0);
        } else {
            params.setMarginStart(2);
            params.setMarginEnd(0);
        }
        holder.item.setLayoutParams(params);
        AlbumBean bean = dataList.get(position);
        holder.albumName.setText(bean.getAlbumName());
        holder.artistName.setText(bean.getArtistBean().getArtistName());
        ImageUtil.loadImage(mContext,
                bean.getAlbumImgUrl() + AppConstant.WY_IMG_300_300,
                holder.albumImg,
                R.drawable.ic_large_album);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView albumName, artistName;
        ImageView albumImg;
        LinearLayout item;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item_album_item);
            albumImg = itemView.findViewById(R.id.item_album_img);
            albumName = itemView.findViewById(R.id.item_album_name);
            artistName = itemView.findViewById(R.id.item_album_artist_name);
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }
        }
    }
}
