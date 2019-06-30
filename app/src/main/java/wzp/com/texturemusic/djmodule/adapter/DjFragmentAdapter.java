package wzp.com.texturemusic.djmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapterForVlayout;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/10/15.
 */

public class DjFragmentAdapter extends BaseAdapterForVlayout<AlbumBean, DjFragmentAdapter.DjFragmentViewholder> {

    public DjFragmentAdapter(Context c) {
        super(c);
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(3);
        gridLayoutHelper.setHGap(BaseUtil.dp2px(2));
        return gridLayoutHelper;
    }

    @Override
    public DjFragmentViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vv = LayoutInflater.from(mContext).inflate(R.layout.common_index_item, parent, false);
        return new DjFragmentViewholder(vv);
    }

    @Override
    public void onBindViewHolder(DjFragmentViewholder holder, int position) {
        final int index = position;
        holder.bkImg.setTransitionName(mContext.getString(R.string.sharedjdetail));
        holder.itemLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickSubject != null) {
                    itemClickSubject.onNext(new ItemBean(v, index));
                }
            }
        });
        ImageUtil.loadImage(mContext,
                dataList.get(position).getAlbumImgUrl() + AppConstant.WY_IMG_300_300,
                holder.bkImg,
                R.drawable.ic_large_album);
        holder.descTv.setText(dataList.get(position).getAlbumName());


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class DjFragmentViewholder extends RecyclerView.ViewHolder {
        TextView playCountTv, nameTv, descTv;
        ImageView bkImg, playImg;
        LinearLayout itemLinear;

        DjFragmentViewholder(View itemView) {
            super(itemView);
            playCountTv = itemView.findViewById(R.id.comment_index_playcount);
            nameTv = itemView.findViewById(R.id.comment_index_artist_name);
            descTv = itemView.findViewById(R.id.comment_index_info);
            bkImg = itemView.findViewById(R.id.comment_index_img);
            playImg = itemView.findViewById(R.id.comment_index_play_img);
            itemLinear = itemView.findViewById(R.id.comment_index_item);
            playImg.setVisibility(View.GONE);
            nameTv.setVisibility(View.GONE);
            playCountTv.setVisibility(View.GONE);
        }

    }

}
