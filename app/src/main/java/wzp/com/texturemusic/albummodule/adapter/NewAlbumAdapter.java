package wzp.com.texturemusic.albummodule.adapter;

import android.content.Context;

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
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:NewAlbumFragment界面的适配器
 * on 2018/1/25.
 */

public class NewAlbumAdapter extends BaseAdapter<AlbumBean, NewAlbumAdapter.NewAlbumViewholder> {

    public NewAlbumAdapter(Context c) {
        super(c);
    }

    @Override
    public NewAlbumViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewAlbumViewholder(LayoutInflater.from(mContext).inflate(R.layout.item_new_album, parent, false));
    }

    @Override
    public void onBindViewHolder(final NewAlbumViewholder holder, int position) {
        AlbumBean bean = dataList.get(position);
        holder.itemView.setTag(position);
        ImageUtil.loadImage(mContext,bean.getAlbumImgUrl() + AppConstant.WY_IMG_300_300,holder.itemImg,R.drawable.ic_large_album);
        holder.itemPublishTimeTv.setTextColor(0xffffffff);
        holder.itemPublishCompanyTv.setTextColor(0xffffffff);
        holder.itemArtistNameTv.setTextColor(0xffffffff);
        ImageUtil.loadImage(mContext, bean.getArtistBean().getArtistImgUrl() + AppConstant.WY_IMG_100_100, holder.itemArtistImg);
        holder.itemAlbumNameTv.setText(bean.getAlbumName());
        holder.itemArtistNameTv.setText("歌手：" + bean.getArtistBean().getArtistName());
        String company = bean.getPublishCompany();
        if (TextUtils.isEmpty(company) || company.equals("null")) {
            company = "暂无";
        }
        holder.itemPublishCompanyTv.setText("发行公司：" + company);
        holder.itemPublishTimeTv.setText("发行时间:" + FormatData.unixTimeTostring(bean.getPublishTime()));

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class NewAlbumViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_img)
        ImageView itemImg;
        @BindView(R.id.item_publish_time_tv)
        TextView itemPublishTimeTv;
        @BindView(R.id.item_publish_company_tv)
        TextView itemPublishCompanyTv;
        @BindView(R.id.item_artist_name_tv)
        TextView itemArtistNameTv;
        @BindView(R.id.item_artist_img)
        CircleImageView itemArtistImg;
        @BindView(R.id.item_artist_linear)
        LinearLayout itemArtistLinear;
        @BindView(R.id.item_album_name_tv)
        TextView itemAlbumNameTv;
        @BindView(R.id.item_bk_linear)
        LinearLayout itemBkLinear;

        public NewAlbumViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemArtistLinear.setOnClickListener(this);
            itemImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (Integer) itemView.getTag()));
            }
        }
    }

}
