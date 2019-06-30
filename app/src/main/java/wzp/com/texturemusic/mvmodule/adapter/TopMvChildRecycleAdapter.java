package wzp.com.texturemusic.mvmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.mvmodule.bean.MvContentBean;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Wang on 2017/6/10.
 */

public class TopMvChildRecycleAdapter extends BaseAdapter<MvContentBean, TopMvChildRecycleAdapter.TopMvChildViewHolder> {

    public TopMvChildRecycleAdapter(Context c) {
        super(c);
    }

    @Override
    public TopMvChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_fr_mv_top, parent, false);
        return new TopMvChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TopMvChildViewHolder holder, int position) {
        holder.itemView.setTag(position);
        MvContentBean bean = dataList.get(position);
        ImageUtil.loadImage(mContext,
                bean.getCoverImgUrl() + AppConstant.WY_IMG_800_400,
                holder.imgView,
                R.drawable.ic_large_album);
        holder.playcountTv.setText(FormatData.longValueToString(bean.getPlayCount()));
        holder.descTv.setText(bean.getMvName() + "-" + bean.getArtistName());
        holder.indexTv.setText((position + 1) + "");
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    class TopMvChildViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private FrameLayout itemFramr;
        private TextView playcountTv, indexTv, descTv;
        private ImageView imgView;

        public TopMvChildViewHolder(View itemView) {
            super(itemView);
            itemFramr = itemView.findViewById(R.id.fr_mv_top_item);
            playcountTv = itemView.findViewById(R.id.fr_mv_top_item_playcount);
            indexTv = itemView.findViewById(R.id.fr_mv_top_item_index_tv);
            descTv = itemView.findViewById(R.id.fr_mv_top_item_desc_tv);
            imgView = itemView.findViewById(R.id.fr_mv_top_item_img);
            itemFramr.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }
        }

    }

}
