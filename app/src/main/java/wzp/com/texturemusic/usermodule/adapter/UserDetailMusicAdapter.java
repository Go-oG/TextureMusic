package wzp.com.texturemusic.usermodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapterForVlayout;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.usermodule.bean.UserInfoBean;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/11/27.
 */

public class UserDetailMusicAdapter extends BaseAdapterForVlayout<UserInfoBean, UserDetailMusicAdapter.UserDetailMusicViewholder> {
    private int tag = 0;//标识是电台节目还是 歌单 0为电台 1为歌单

    public int getTag() {
        return tag;
    }

    public UserDetailMusicAdapter(Context c, int tag) {
        super(c);
        this.tag = tag;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper(BaseUtil.dp2px(0.5f));
    }

    @Override
    public UserDetailMusicViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user_detail_music, parent, false);
        return new UserDetailMusicViewholder(view);
    }

    @Override
    public void onBindViewHolder(UserDetailMusicViewholder holder, int position) {
        holder.itemView.setTag(position);
        if (tag == 0) {
            ImageUtil.loadImage(mContext,
                    dataList.get(position).getMusicBean().getCoverImgUrl() + AppConstant.WY_IMG_200_200,
                    holder.itemImg,
                    R.drawable.ic_large_album);
            holder.itemTitle.setText(dataList.get(position).getMusicBean().getMusicName());
            String info = "专辑 " + dataList.get(position).getMusicBean().getAlbumName() +
                    "  艺术家 " + dataList.get(position).getMusicBean().getArtistName() +
                    " 播放 " + dataList.get(position).getVal();
            holder.itemInfo.setText(info);
        } else {
            ImageUtil.loadImage(mContext,
                    dataList.get(position).getImgUrl() + AppConstant.WY_IMG_300_300,
                    holder.itemImg,
                    R.drawable.ic_large_album);
            holder.itemTitle.setText(dataList.get(position).getName());
            String info = "歌曲 " + dataList.get(position).getVal3() + "首" +
                    "  播放 " + FormatData.longValueToString(Long.parseLong(dataList.get(position).getVal1())) + "次";
            holder.itemInfo.setText(info);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class UserDetailMusicViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_img)
        ImageView itemImg;
        @BindView(R.id.item_operation_img)
        ImageView itemOperationImg;
        @BindView(R.id.item_title)
        TextView itemTitle;
        @BindView(R.id.item_info)
        TextView itemInfo;
        @BindView(R.id.item_item)
        RelativeLayout itemItem;

        public UserDetailMusicViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemItem.setOnClickListener(this);
            itemOperationImg.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }
        }
    }

}
