package wzp.com.texturemusic.artistmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/11/13.
 */

public class ArtistDetailAlbumAdapter extends BaseAdapter<AlbumBean, ArtistDetailAlbumAdapter.ArtistDetailAlbumViewholder> {

    public ArtistDetailAlbumAdapter(Context c) {
        super(c);
    }

    @Override
    public ArtistDetailAlbumViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_artist_detail_album, parent, false);
        return new ArtistDetailAlbumViewholder(view);
    }

    @Override
    public void onBindViewHolder(ArtistDetailAlbumViewholder holder, int position) {
        holder.itemView.setTag(position);
        ImageUtil.loadImage(mContext,
                dataList.get(position).getAlbumImgUrl() + AppConstant.WY_IMG_200_200,
                holder.albumImg,
                R.drawable.ic_large_album);
        holder.albumNameTv.setText(dataList.get(position).getAlbumName());
        holder.albumInfoTv.setText(FormatData.unixTimeTostring(dataList.get(position).getPublishTime()) +
                " 歌曲" + dataList.get(position).getMusicCount());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ArtistDetailAlbumViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView albumNameTv, albumInfoTv;
        ImageView albumImg;
        RelativeLayout item;

        public ArtistDetailAlbumViewholder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item_item);
            albumNameTv = itemView.findViewById(R.id.artist_name_tv);
            albumInfoTv = itemView.findViewById(R.id.album_info_tv);
            albumImg = itemView.findViewById(R.id.album_img);
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (Integer) itemView.getTag()));
            }
        }
    }

}
