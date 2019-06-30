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
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/9/16.
 */

public class SearchArtistAdapter extends BaseAdapter<ArtistBean, SearchArtistAdapter.ArtistViewHolder> {


    public SearchArtistAdapter(Context c) {
        super(c);
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_artist, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ArtistViewHolder holder, int position) {
        holder.itemView.setTag(position);
        ArtistBean bean = dataList.get(position);
        ImageUtil.loadImage(mContext,
                bean.getArtistImgUrl() + AppConstant.WY_IMG_100_100,
                holder.artistImg,
                R.drawable.ic_large_album);

        holder.artistName.setText(bean.getArtistName());

    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout item;
        TextView artistName;
        TextView artistInfo;
        ImageView operationImg;
        ImageView artistImg;

        public ArtistViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item_artist_parent);
            artistName = itemView.findViewById(R.id.item_artist_name);
            artistInfo = itemView.findViewById(R.id.item_artist_follow);
            operationImg = itemView.findViewById(R.id.item_artist_operation);
            artistImg = itemView.findViewById(R.id.item_artist_img);
            item.setOnClickListener(this);
            operationImg.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }

        }
    }
}
