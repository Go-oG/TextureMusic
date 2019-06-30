package wzp.com.texturemusic.playlistmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.KeyValueBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;

/**
 * Created by Go_oG
 * Description:
 * on 2017/11/22.
 */

public class PlaylistTypeAdapter extends BaseAdapter<KeyValueBean, PlaylistTypeAdapter.PlaylistTypeViewholder> {

    public PlaylistTypeAdapter(Context c) {
        super(c);
    }

    @Override
    public PlaylistTypeViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_playlist_type, parent, false);
        return new PlaylistTypeViewholder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistTypeViewholder holder, int position) {
        final int index = position;
        holder.textView.setText(dataList.get(position).getValue());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickSubject != null) {
                    itemClickSubject.onNext(new ItemBean(v, index));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class PlaylistTypeViewholder extends RecyclerView.ViewHolder {
        TextView textView;

        public PlaylistTypeViewholder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_txt);
        }
    }
}
