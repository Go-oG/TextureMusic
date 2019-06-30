package wzp.com.texturemusic.albummodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.AlbumBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.ImageUtil;


/**
 * Created by Go_oG
 * Description:新碟上架的适配器
 * on 2017/11/10.
 */

public class NewAlbumShelvesAdapter extends BaseAdapter<AlbumBean, NewAlbumShelvesAdapter.NewAlbumShelvesViewholder> {


    public NewAlbumShelvesAdapter(Context c) {
        super(c);
    }

    @Override
    public NewAlbumShelvesViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_new_album_shelves, parent, false);
        return new NewAlbumShelvesViewholder(view);
    }

    @Override
    public void onBindViewHolder(NewAlbumShelvesViewholder holder, int position) {
        holder.itemView.setTag(position);
        holder.imageView.setTransitionName(mContext.getString(R.string.sharealbumdetail));
        ImageUtil.loadImage(mContext,
                dataList.get(position).getAlbumImgUrl() + AppConstant.WY_IMG_300_300,
                holder.imageView, R.drawable.ic_large_album, R.drawable.ic_large_album);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class NewAlbumShelvesViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;

        public NewAlbumShelvesViewholder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_album_shelves_img);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }

        }

    }

}
