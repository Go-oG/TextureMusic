package wzp.com.texturemusic.localmodule.adapter;

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
import wzp.com.texturemusic.dbmodule.bean.DbCollectPlaylistEntiy;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Wang on 2018/3/10.
 * 本地歌单的适配器
 */

public class CollectPlaylistAdapter extends BaseAdapter<DbCollectPlaylistEntiy, CollectPlaylistAdapter.CollectPlaylistViewholder> {
    private StringBuilder builder;

    public CollectPlaylistAdapter(Context c) {
        super(c);
    }

    @Override
    public CollectPlaylistViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CollectPlaylistViewholder(LayoutInflater.from(mContext).inflate(R.layout.item_collect_playlist, parent, false));
    }

    @Override
    public void onBindViewHolder(CollectPlaylistViewholder holder, int position) {
        holder.itemView.setTag(position);
        DbCollectPlaylistEntiy entiy = dataList.get(position);
        ImageUtil.loadImage(
                mContext, entiy.getCoverImgUr() + AppConstant.WY_IMG_200_200,
                holder.itemPlaylistCover,
                R.drawable.ic_large_album,
                R.drawable.ic_large_album);
        holder.itemPlaylistName.setText(entiy.getPlaylistName());
        builder = new StringBuilder();
        if (entiy.getMusicCount() != null) {
            builder.append(entiy.getMusicCount()).append(" 首");
        }
        if (entiy.getCreaterName() != null) {
            builder.append(" by ").append(entiy.getCreaterName());
        }
        holder.itemFinePlaylistInfo.setText(builder.toString());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class CollectPlaylistViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_playlist)
        ConstraintLayout itemCL;
        @BindView(R.id.item_playlist_cover)
        ImageView itemPlaylistCover;
        @BindView(R.id.item_playlist_operation)
        ImageView itemPlaylistOperation;
        @BindView(R.id.item_playlist_name)
        TextView itemPlaylistName;
        @BindView(R.id.item_fine_playlist_info)
        TextView itemFinePlaylistInfo;

        public CollectPlaylistViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemCL.setOnClickListener(this);
            itemPlaylistOperation.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (Integer) itemView.getTag()));
            }
        }
    }

}
