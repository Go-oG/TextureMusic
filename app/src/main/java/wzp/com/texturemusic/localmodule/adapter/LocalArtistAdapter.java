package wzp.com.texturemusic.localmodule.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:LocalArtistFragment 中的适配器
 * on 2017/9/27.
 */

public class LocalArtistAdapter extends BaseAdapter<ArtistBean, LocalArtistAdapter.LocalartistViewholder> {

    public LocalArtistAdapter(Context c) {
        super(c);
    }

    @Override
    public LocalartistViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_local_artist, parent, false);
        return new LocalartistViewholder(view);
    }

    @Override
    public void onBindViewHolder(LocalartistViewholder holder, int position) {
        holder.itemView.setTag(position);
        holder.headImg.setTransitionName(mContext.getString(R.string.shareartistdetail));
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) holder.item.getLayoutParams();
        if (!(position == 0 || position == 1)) {
            params.topMargin = 16;
        } else {
            params.topMargin = 4;
        }
        if (position % 2 == 0) {
            params.setMarginEnd(4);
            params.setMarginStart(0);
        } else {
            params.setMarginStart(4);
            params.setMarginEnd(0);
        }
        holder.item.setLayoutParams(params);
        String imgUrl = dataList.get(position).getArtistImgUrl();
        if (dataList.get(position).getLocalArtist()) {
            ImageUtil.loadImage(mContext,
                    imgUrl,
                    holder.headImg, R.drawable.ic_large_album);
        } else {
            ImageUtil.loadImage(mContext,
                    imgUrl + AppConstant.WY_IMG_500_300,
                    holder.headImg, R.drawable.ic_large_album);
        }
        holder.artistName.setText(dataList.get(position).getArtistName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class LocalartistViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView item;
        TextView artistName;
        ImageView headImg;

        public LocalartistViewholder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.local_artist_item);
            artistName = itemView.findViewById(R.id.local_artist_name_tv);
            headImg = itemView.findViewById(R.id.local_artist_img);
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
