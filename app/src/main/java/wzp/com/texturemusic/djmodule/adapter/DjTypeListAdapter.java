package wzp.com.texturemusic.djmodule.adapter;

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
 * Description:
 * on 2017/11/29.
 */

public class DjTypeListAdapter extends BaseAdapter<AlbumBean, DjTypeListAdapter.DjTypeListViewholder> {


    public DjTypeListAdapter(Context c) {
        super(c);
    }

    @Override
    public DjTypeListViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dj_type_list, parent, false);
        return new DjTypeListViewholder(view);
    }

    @Override
    public void onBindViewHolder(DjTypeListViewholder holder, int position) {
        final int index = position;
        ImageUtil.loadImage(mContext,
                dataList.get(position).getAlbumImgUrl() + AppConstant.WY_IMG_300_300,
                holder.itemImg,
                R.drawable.ic_large_album);
        holder.djNameTv.setText(dataList.get(position).getAlbumName());
        holder.djDescTv.setText(dataList.get(position).getDescription());
        holder.djInfoTv.setText("节目:" + dataList.get(position).getMusicCount() + ",订阅:" + dataList.get(position).getLikedCount());
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

    static class DjTypeListViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_img)
        ImageView itemImg;
        @BindView(R.id.dj_name_tv)
        TextView djNameTv;
        @BindView(R.id.dj_desc_tv)
        TextView djDescTv;
        @BindView(R.id.dj_info_tv)
        TextView djInfoTv;
        @BindView(R.id.item_item)
        RelativeLayout itemItem;

        public DjTypeListViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
