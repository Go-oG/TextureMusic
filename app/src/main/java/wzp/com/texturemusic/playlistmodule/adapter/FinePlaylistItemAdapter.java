package wzp.com.texturemusic.playlistmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.PlayListBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/10/3.
 */

public class FinePlaylistItemAdapter extends BaseAdapter<PlayListBean,FinePlaylistItemAdapter.FineViewholder> {


    public FinePlaylistItemAdapter(Context c) {
        super(c);
    }

    @Override
    public FineViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_fine_recycleview, parent, false);
        return new FineViewholder(view);
    }

    @Override
    public void onBindViewHolder(FineViewholder holder, int position) {
        holder.itemView.setTag(position);
        PlayListBean bean = dataList.get(position);
        ImageUtil.loadImage(mContext,
                bean.getCoverImgUr() + AppConstant.WY_IMG_300_300,
                holder.itemImg,
                R.drawable.ic_large_album);
        ImageUtil.loadImage(mContext,bean.getCreater().getUserCoverImgUrl() + AppConstant.WY_IMG_50_50,
                holder.itemUserImg);
        holder.itemImg.setTransitionName(mContext.getString(R.string.shareplaylistdetail));
        holder.itemPlaylistName.setText(bean.getPlaylistName());
        holder.itemUsername.setText("by " + bean.getCreater().getNickName());
        holder.itemDesc.setText(bean.getSubDescription());
        holder.itemPlaycount.setText(FormatData.longValueToString(bean.getPlayCount()));

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class FineViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout itemRelative;
        ImageView itemImg;
        TextView itemPlaylistName, itemUsername, itemDesc;
        LinearLayout itemUserLinear;
        CircleImageView itemUserImg;
        TextView itemPlaycount;

        public FineViewholder(View itemView) {
            super(itemView);
            itemImg = itemView.findViewById(R.id.item_fine2_img);
            itemRelative = itemView.findViewById(R.id.item_fine2_relative);
            itemPlaylistName = itemView.findViewById(R.id.item_fine_playlist_name_tv);
            itemUsername = itemView.findViewById(R.id.item_fine_user_info);
            itemDesc = itemView.findViewById(R.id.item_fine_playlist_info);
            itemUserLinear = itemView.findViewById(R.id.item_fine_user_linear);
            itemUserImg = itemView.findViewById(R.id.item_fine_user_img);
            itemPlaycount = itemView.findViewById(R.id.item_fine_playcount);
            itemRelative.setOnClickListener(this);
            itemUserLinear.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }

        }

    }


}
