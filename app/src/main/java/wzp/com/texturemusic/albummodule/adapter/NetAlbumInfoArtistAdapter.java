package wzp.com.texturemusic.albummodule.adapter;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.interf.OnRecycleItemClickListener;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:用于网络专辑的详细信息界面顶部布局
 * on 2018/1/26.
 */

public class NetAlbumInfoArtistAdapter extends BaseAdapter<AlbumBean,NetAlbumInfoArtistAdapter.NetAlbumInfoArtistViewholder> {
    private AlbumBean albumBean;
    private OnRecycleItemClickListener itemClickListener;

    public void setItemClickListener(OnRecycleItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public NetAlbumInfoArtistAdapter(Context c) {
        super(c);

    }

    public AlbumBean getAlbumBean() {
        return albumBean;
    }

    public void setAlbumBean(AlbumBean albumBean) {
        this.albumBean = albumBean;
    }

    @Override
    public NetAlbumInfoArtistViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NetAlbumInfoArtistViewholder(
                LayoutInflater.from(mContext).inflate(R.layout.item_net_album_info_artist, parent, false)
        );
    }


    @Override
    public void onBindViewHolder(NetAlbumInfoArtistViewholder holder, int position) {
        if (albumBean != null) {
            ImageUtil.loadImage(mContext,
                    albumBean.getArtistBean().getArtistImgUrl() + AppConstant.WY_IMG_200_200,
                    holder.itemArtistImg);
            holder.itemArtistNameTv.setText(albumBean.getArtistBean().getArtistName());
            holder.itemAlbumDescTv.setText(albumBean.getDescription());
            holder.itemCommentCountTv.setText(albumBean.getCommentCount() + "");
            holder.itemShareCountTv.setText(albumBean.getShareCount() + "");
            holder.itemLikedCountTv.setText(albumBean.getLikedCount() + "");
            String company = albumBean.getPublishCompany();
            if (TextUtils.isEmpty(company) || company.equals("null")) {
                company = "暂无";
            }
            holder.itemPublishCompanyTv.setText("发行公司：" + company);
            holder.itemPublishTimeTv.setText("发行时间：" + FormatData.unixTimeTostring(albumBean.getPublishTime()));
        }

    }

    @Override
    public int getItemCount() {
        return albumBean == null ? 0 : 1;
    }

    class NetAlbumInfoArtistViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_artist_img)
        CircleImageView itemArtistImg;
        @BindView(R.id.item_artist_name_tv)
        TextView itemArtistNameTv;
        @BindView(R.id.item_publish_company_tv)
        TextView itemPublishCompanyTv;
        @BindView(R.id.item_publish_time_tv)
        TextView itemPublishTimeTv;
        @BindView(R.id.item_comment_count_tv)
        TextView itemCommentCountTv;
        @BindView(R.id.item_comment_count_linear)
        LinearLayout itemCommentCountLinear;
        @BindView(R.id.item_share_count_tv)
        TextView itemShareCountTv;
        @BindView(R.id.item_share_count_linear)
        LinearLayout itemShareCountLinear;
        @BindView(R.id.item_liked_count_tv)
        TextView itemLikedCountTv;
        @BindView(R.id.item_liked_count_linear)
        LinearLayout itemLikedCountLinear;
        @BindView(R.id.item_expand_img)
        ImageView itemExpandImg;
        @BindView(R.id.item_album_desc_tv)
        TextView itemAlbumDescTv;
        @BindView(R.id.item_artist_cos)
        ConstraintLayout itemArtist;

        public NetAlbumInfoArtistViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemLikedCountLinear.setOnClickListener(this);
            itemCommentCountLinear.setOnClickListener(this);
            itemShareCountLinear.setOnClickListener(this);
            itemArtist.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, 0);
            }

        }
    }

}
