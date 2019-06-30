package wzp.com.texturemusic.searchmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:搜索电台的数据
 * on 2017/9/16.
 */

public class SearchDJAdapter extends BaseAdapter<AlbumBean, SearchDJAdapter.SearchDjViewholder> {

    public SearchDJAdapter(Context c) {
        super(c);
    }

    @Override
    public SearchDjViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_dj, parent, false);
        return new SearchDjViewholder(view);
    }

    @Override
    public void onBindViewHolder(SearchDjViewholder holder, int position) {
        final int index = position;
        AlbumBean bean = dataList.get(position);
        ImageUtil.loadImage(mContext,
                bean.getArtistBean().getArtistImgUrl() + AppConstant.WY_IMG_200_200,
                holder.itemmImg,
                R.drawable.ic_large_album);
        holder.itemName.setText(bean.getAlbumName());
        holder.itemInfo.setText(bean.getArtistBean().getArtistName());
        holder.itemItem.setOnClickListener(new View.OnClickListener() {
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

    class SearchDjViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemm_img)
        ImageView itemmImg;
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.item_info)
        TextView itemInfo;
        @BindView(R.id.item_item)
        RelativeLayout itemItem;

        public SearchDjViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
