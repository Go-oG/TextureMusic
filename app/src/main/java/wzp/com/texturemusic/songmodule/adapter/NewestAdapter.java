package wzp.com.texturemusic.songmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:最新音乐的数据
 * on 2017/10/30.
 */

public class NewestAdapter extends BaseAdapter<MusicBean, RecyclerView.ViewHolder> {

    private int imgResouceId;

    public NewestAdapter(Context c) {
        super(c);
    }

    public void setImgResouceId(int imgResouceId) {
        this.imgResouceId = imgResouceId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_newest_song_top, parent, false);
            return new NewestHeadHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_local_songs, parent, false);
            return new NewestViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NewestViewHolder) {
            int index = position - 1;
            NewestViewHolder viewHolder = (NewestViewHolder) holder;
            viewHolder.itemView.setTag(index);
            viewHolder.musicnameTv.setText(dataList.get(index).getMusicName());
            viewHolder.musicDescTv.setText(dataList.get(index).getAlbumName() + "-" + dataList.get(index).getArtistName());
            ImageUtil.loadImage(mContext,
                    dataList.get(index).getCoverImgUrl() + AppConstant.WY_IMG_100_100,
                    viewHolder.coverImg,
                    R.drawable.ic_large_album);
        } else if (holder instanceof NewestHeadHolder) {
            ((NewestHeadHolder) holder).imageView.setImageResource(imgResouceId);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    class NewestHeadHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public NewestHeadHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_newest_img);
        }
    }

    class NewestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout item;
        ImageView coverImg, operationImg;
        TextView musicnameTv, musicDescTv;

        public NewestViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.local_songs_item);
            coverImg = itemView.findViewById(R.id.item_cover_img);
            operationImg = itemView.findViewById(R.id.item_operation_img);
            musicnameTv = itemView.findViewById(R.id.item_musicname_tv);
            musicDescTv = itemView.findViewById(R.id.item_musicdesc_tv);
            item.setOnClickListener(this);
            operationImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) item.getTag()));
            }
        }
    }


}
