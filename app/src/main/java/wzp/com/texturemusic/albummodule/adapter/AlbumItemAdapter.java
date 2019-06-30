package wzp.com.texturemusic.albummodule.adapter;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.StringUtil;

/**
 * Created by Go_oG
 * Description: 专辑详情界面中 每个专辑对应的歌曲列表适配器
 * on 2017/9/16.
 */

public class AlbumItemAdapter extends BaseAdapter<MusicBean, AlbumItemAdapter.AlbumItemViewHolder> {
    private SpannableStringBuilder stringBuilder;

    public AlbumItemAdapter(Context c) {
        super(c);
        stringBuilder = new SpannableStringBuilder();
    }

    @Override
    public AlbumItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycle_music, parent, false);
        return new AlbumItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumItemViewHolder holder, int position) {
        holder.itemView.setTag(position);
        MusicBean bean = dataList.get(position);
        holder.musicName.setText(bean.getMusicName());
        holder.playTime.setText(FormatData.timeValueToString(bean.getAllTime()));
        holder.itemMusicIndex.setText((position + 1) + "");
        if (bean.getHasMV() != null && bean.getHasMV()) {
            holder.itemShowMvImg.setVisibility(View.VISIBLE);
        } else {
            holder.itemShowMvImg.setVisibility(View.GONE);
        }
        String name = bean.getMusicName();
        stringBuilder.clearSpans();
        stringBuilder.clear();
        if (!StringUtil.isEmpty(bean.getAlias())) {
            String aName = name + "( " + bean.getAlias() + ")";
            stringBuilder = StringUtil.buildStringColor(aName, color, name.length(), aName.length());
            stringBuilder = StringUtil.builderStringSize(stringBuilder, 13, name.length(), stringBuilder.length());
        } else {
            stringBuilder.append(name);
        }
        holder.musicName.setText(stringBuilder);
        stringBuilder.clearSpans();
        stringBuilder.clear();
        if (bean.getSQMusic() != null && bean.getSQMusic()) {
            stringBuilder = new SpannableStringBuilder(" " + bean.getArtistName() + "-" + bean.getAlbumName());
            stringBuilder = StringUtil.buildStringImage(stringBuilder, R.drawable.ic_text_sq, 0, 1, false);
        } else {
            stringBuilder = new SpannableStringBuilder(bean.getArtistName() + "-" + bean.getAlbumName());
        }
        holder.albumName.setText(stringBuilder);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class AlbumItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView musicName, itemMusicIndex;
        TextView albumName;
        TextView playTime;
        ConstraintLayout item;
        ImageView operation, itemShowMvImg;

        public AlbumItemViewHolder(View itemView) {
            super(itemView);
            musicName = itemView.findViewById(R.id.item_music_tv);
            albumName = itemView.findViewById(R.id.item_music_album);
            playTime = itemView.findViewById(R.id.item_music_time);
            item = itemView.findViewById(R.id.item_music_info);
            itemMusicIndex = itemView.findViewById(R.id.item_music_index_tv);
            operation = itemView.findViewById(R.id.item_operation_img);
            itemShowMvImg = itemView.findViewById(R.id.item_show_mv_img);
            operation.setOnClickListener(this);
            item.setOnClickListener(this);
            itemShowMvImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                int position = (int) itemView.getTag();
                itemClickSubject.onNext(new ItemBean(v, position));
            }

        }
    }

}
