package wzp.com.texturemusic.localmodule.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
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
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/12/25.
 */

public class LocalArtistDetailAdapterAdapter extends BaseAdapter<MusicBean, LocalArtistDetailAdapterAdapter.LocalArtistDetailAdapterViewholder> {

    public LocalArtistDetailAdapterAdapter(Context c) {
        super(c);
    }

    @Override
    public LocalArtistDetailAdapterViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LocalArtistDetailAdapterViewholder(LayoutInflater
                .from(mContext).inflate(R.layout.item_local_artist_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(LocalArtistDetailAdapterViewholder holder, int position) {
        final int index = position;
        ImageUtil.loadImage(mContext,dataList.get(position).getCoverImgUrl(),holder.itemImg);
        holder.itemAlbumNameTv.setText(dataList.get(position).getAlbumName());
        holder.itemLinear.setOnClickListener(new View.OnClickListener() {
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

    static class LocalArtistDetailAdapterViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_img)
        ImageView itemImg;
        @BindView(R.id.item_album_name_tv)
        TextView itemAlbumNameTv;
        @BindView(R.id.item_linear)
        CardView itemLinear;

        public LocalArtistDetailAdapterViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
