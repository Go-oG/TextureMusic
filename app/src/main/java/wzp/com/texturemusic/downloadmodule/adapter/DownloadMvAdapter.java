package wzp.com.texturemusic.downloadmodule.adapter;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.dbmodule.bean.DbDownloadEntiy;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;

public class DownloadMvAdapter extends BaseAdapter<DbDownloadEntiy, DownloadMvAdapter.DownloadEndViewholder> {

    public DownloadMvAdapter(Context c) {
        super(c);
    }

    @Override
    public DownloadEndViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_download_mv, parent, false);
        return new DownloadEndViewholder(view);
    }

    @Override
    public void onBindViewHolder(DownloadEndViewholder holder, int position) {
        holder.itemView.setTag(position);
        ImageUtil.loadImage(mContext, dataList.get(position).getCoverImgUrl() + AppConstant.WY_IMG_300_200, holder.itemImg);
        holder.itemMvName.setText(dataList.get(position).getName());
        String content;
        content= FormatData.fileSizeToString(dataList.get(position).getFileSize())+" by"+dataList.get(position).getArtistName();
        holder.itemMvInfo.setText(content);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class DownloadEndViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_img)
        ImageView itemImg;
        @BindView(R.id.item_mv_name)
        TextView itemMvName;
        @BindView(R.id.item_mv_info)
        TextView itemMvInfo;
        @BindView(R.id.item_operation_img)
        ImageView itemOperationImg;
        @BindView(R.id.m_item_content)
        ConstraintLayout mItem;

        public DownloadEndViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemOperationImg.setOnClickListener(this);
            mItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject!=null){
                itemClickSubject.onNext(new ItemBean(v,(Integer) itemView.getTag()));
            }
        }
    }
}
