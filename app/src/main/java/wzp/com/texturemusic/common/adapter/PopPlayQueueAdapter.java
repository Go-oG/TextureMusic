package wzp.com.texturemusic.common.adapter;

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

/**
 * Created by Go_oG
 * Description: 用于显示当前的播放列表的window
 * on 2017/11/7.
 */

public class PopPlayQueueAdapter extends BaseAdapter<MusicBean, PopPlayQueueAdapter.PopPlayQueueViewholder> {

    public PopPlayQueueAdapter(Context c) {
        super(c);
    }

    @Override
    public PopPlayQueueViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pop_music_info, parent, false);
        return new PopPlayQueueViewholder(view);
    }

    @Override
    public void onBindViewHolder(PopPlayQueueViewholder holder, int position) {
        holder.itemView.setTag(position);
        Boolean hasCheck=dataList.get(position).getHasCheck();
        if (hasCheck!=null&&hasCheck) {
            holder.playingImg.setVisibility(View.VISIBLE);
        } else {
            holder.playingImg.setVisibility(View.GONE);
        }
        holder.musicInfoTv.setText(dataList.get(position).getMusicName() + "-" + dataList.get(position).getArtistName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class PopPlayQueueViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout item;
        ImageView playingImg;
        ImageView closeImg;
        TextView musicInfoTv;

        public PopPlayQueueViewholder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item_pop_item);
            playingImg = itemView.findViewById(R.id.item_pop_playing_img);
            closeImg = itemView.findViewById(R.id.item_pop_close_img);
            musicInfoTv = itemView.findViewById(R.id.item_pop_music_info_tv);
            closeImg.setOnClickListener(this);
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v,(int) itemView.getTag()));
            }
        }
    }

}
