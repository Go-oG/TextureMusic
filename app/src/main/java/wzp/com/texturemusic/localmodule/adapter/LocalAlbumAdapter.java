package wzp.com.texturemusic.localmodule.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:在Adapater前加一个A表示用的是阿里巴巴的适配器
 * on 2017/9/27.
 */

public class LocalAlbumAdapter extends BaseAdapter<AlbumBean, LocalAlbumAdapter.LocalAlbumAViewholder> {


    public LocalAlbumAdapter(Context c) {
        super(c);
    }

    @Override
    public LocalAlbumAViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_local_album, parent, false);
        return new LocalAlbumAdapter.LocalAlbumAViewholder(view);

    }

    @Override
    public void onBindViewHolder(LocalAlbumAViewholder holder, int position) {
        holder.itemView.setTag(position);

        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) holder.item.getLayoutParams();
        if (position == 0 || position == 1) {
            params.topMargin = 0;
        } else {
            params.topMargin = 16;
        }
        if (position % 2 == 0) {
            params.setMarginEnd(4);
            params.setMarginStart(4);
        } else {
            params.setMarginStart(4);
            params.setMarginEnd(4);
        }
        holder.item.setLayoutParams(params);
        AlbumBean albumBean = dataList.get(position);
        ArtistBean artistBean = albumBean.getArtistBean();
        String info;
        if (artistBean != null && !TextUtils.isEmpty(artistBean.getArtistName())) {
            info = albumBean.getAlbumName() + "\n" + artistBean.getArtistName();
        } else {
            info = albumBean.getAlbumName();
        }
        holder.albumName.setText(info);
        if (albumBean.getLocalAlbum()) {
            ImageUtil.loadImage(mContext,
                    dataList.get(position).getAlbumImgUrl(),
                    holder.coverImg, R.drawable.ic_large_album);
        } else {
            ImageUtil.loadImage(mContext,
                    dataList.get(position).getAlbumImgUrl() + AppConstant.WY_IMG_500_300,
                    holder.coverImg, R.drawable.ic_large_album);
        }
        holder.coverImg.setTransitionName(mContext.getString(R.string.sharealbumdetail));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class LocalAlbumAViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView item;
        ImageView coverImg;
        TextView albumName;

        public LocalAlbumAViewholder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.local_album_item);
            coverImg = itemView.findViewById(R.id.local_album_img);
            albumName = itemView.findViewById(R.id.local_album_name_tv);
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
