package wzp.com.texturemusic.artistmodule.adapter;

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
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/11/15.
 */

public class ArtistAdapter extends BaseAdapter<ArtistBean, ArtistAdapter.ArtistViewholder> {


    public ArtistAdapter(Context c) {
        super(c);
    }

    @Override
    public ArtistViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_artist, parent, false);
        return new ArtistViewholder(view);
    }

    @Override
    public void onBindViewHolder(ArtistViewholder holder, int position) {
        holder.itemView.setTag(position);
        ImageUtil.loadImage(mContext,
                dataList.get(position).getArtistImgUrl() + AppConstant.WY_IMG_400_400,
                holder.itemImg,R.drawable.png_artist_cover,R.drawable.png_artist_cover);
        holder.itemArtistNameTv.setText(dataList.get(position).getArtistName());
        holder.itemArtistInfoTv.setText(dataList.get(position).getDecriptions());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ArtistViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_img)
        ImageView itemImg;
        @BindView(R.id.item_artist_name_tv)
        TextView itemArtistNameTv;
        @BindView(R.id.item_artist_info_tv)
        TextView itemArtistInfoTv;
        @BindView(R.id.item_item)
        CardView itemItem;

        public ArtistViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
