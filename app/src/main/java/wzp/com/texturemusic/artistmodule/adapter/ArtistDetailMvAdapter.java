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
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/11/13.
 */

public class ArtistDetailMvAdapter extends BaseAdapter<MvBean, ArtistDetailMvAdapter.ArtistDetailMvViewholder> {


    public ArtistDetailMvAdapter(Context c) {
        super(c);
    }

    @Override
    public ArtistDetailMvViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_artist_detail_mv, parent, false);
        return new ArtistDetailMvViewholder(view);
    }

    @Override
    public void onBindViewHolder(ArtistDetailMvViewholder holder, int position) {
        holder.itemView.setTag(position);
        ImageUtil.loadImage(mContext,
                dataList.get(position).getCoverImgUrl() + AppConstant.WY_IMG_200_200,
                holder.mvImg,R.drawable.ic_large_album);
        holder.mvNameTv.setText(dataList.get(position).getMvName());
        holder.mvInfoTv.setText(dataList.get(position).getPublishTime());

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ArtistDetailMvViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout item;
        ImageView mvImg;
        TextView playCountTv, mvNameTv, mvInfoTv;

        public ArtistDetailMvViewholder(View itemView) {
            super(itemView);
            mvImg = itemView.findViewById(R.id.mv_img);
            playCountTv = itemView.findViewById(R.id.mv_playcount_tv);
            mvNameTv = itemView.findViewById(R.id.mv_name_tv);
            mvInfoTv = itemView.findViewById(R.id.mv_info_tv);
            item = itemView.findViewById(R.id.item_item);
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
