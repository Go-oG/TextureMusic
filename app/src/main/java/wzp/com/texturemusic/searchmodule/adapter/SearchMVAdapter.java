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
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:搜索界面MV的适配器
 * on 2017/9/16.
 */

public class SearchMVAdapter extends BaseAdapter<MvBean, SearchMVAdapter.MvViewHolder> {

    public SearchMVAdapter(Context c) {
        super(c);
    }

    @Override
    public MvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_mv, parent, false);
        return new MvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MvViewHolder holder, int position) {
        holder.itemView.setTag(position);
        MvBean bean = dataList.get(position);
        ImageUtil.loadImage(mContext,
                bean.getCoverImgUrl() + AppConstant.WY_IMG_200_200,
                holder.imageView,
                R.drawable.ic_large_album);
        holder.mvNameTv.setText(bean.getMusicName());
        holder.playCountTv.setText(FormatData.longValueToString(bean.getPlayCount()));
        holder.mvArtistName.setText(bean.getArtistName());

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MvViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        RelativeLayout item;
        TextView playCountTv, mvArtistName, mvNameTv;

        public MvViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_mv_img);
            item = itemView.findViewById(R.id.item_mv_item);
            item.setOnClickListener(this);
            playCountTv = itemView.findViewById(R.id.item_mv_count);
            mvArtistName = itemView.findViewById(R.id.item_mv_artist_name);
            mvNameTv = itemView.findViewById(R.id.item_mv_mvname);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }
        }
    }
}
