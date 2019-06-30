package wzp.com.texturemusic.searchmodule.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
 * on 2017/9/16.
 */

public class SearchPlayListAdapter extends BaseAdapter<PlayListBean, SearchPlayListAdapter.PlayListViewHolder> {

    private Drawable drawable;

    public SearchPlayListAdapter(Context c) {
        super(c);
        drawable = mContext.getDrawable(R.drawable.ic_textdrawable_camera);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
    }

    @Override
    public PlayListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_seaarch_playlist, parent, false);
        return new PlayListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PlayListViewHolder holder, int position) {
        holder.itemView.setTag(position);
        PlayListBean bean = dataList.get(position);
        holder.playlistName.setText(bean.getPlaylistName());
        GridLayoutManager.LayoutParams params= (GridLayoutManager.LayoutParams) holder.item.getLayoutParams();
        if (position%2==0){
            params.setMarginEnd(2);
        }else {
            params.setMarginStart(2);
        }
        holder.item.setLayoutParams(params);
        if (drawable != null) {
            holder.playCount.setCompoundDrawablesRelative(drawable, null, null, null);
            holder.playCount.setCompoundDrawablePadding(8);
        }
        holder.playCount.setText(FormatData.longValueToString(bean.getPlayCount()));
        holder.createrName.setText(bean.getCreaterName());
        ImageUtil.loadImage(mContext,
                bean.getCoverImgUr() + AppConstant.WY_IMG_400_400,
                holder.coverImg,
                R.drawable.ic_large_album);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    class PlayListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView playlistName;
        TextView playCount;
        TextView createrName;
        ImageView coverImg;
        LinearLayout item;

        public PlayListViewHolder(View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.item_playlist_name);
            playCount = itemView.findViewById(R.id.item_playlist_count);
            createrName = itemView.findViewById(R.id.item_playlist_artist_name);
            coverImg = itemView.findViewById(R.id.item_playlist_img);
            item = itemView.findViewById(R.id.item_playlist_item);
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }
        }
    }

}
