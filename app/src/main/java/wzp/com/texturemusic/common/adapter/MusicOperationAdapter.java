package wzp.com.texturemusic.common.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.KeyValueBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.common.popwindow.MusicOperationPopwindow;
import wzp.com.texturemusic.interf.OnMusicPopItemListener;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/12/28.
 */

public class MusicOperationAdapter extends RecyclerView.Adapter<MusicOperationAdapter.MusicOperationViewholder> {
    private List<KeyValueBean> dataList;
    private MusicBean musicData;
    private OnMusicPopItemListener itemListener;
    private Context mContext;

    public MusicOperationAdapter(Context context, MusicBean bean, OnMusicPopItemListener popItemListener) {
        this.itemListener = popItemListener;
        mContext = context;
        this.musicData = bean;
        dataList = new ArrayList<>();
    }

    public List<KeyValueBean> getDataList() {
        return dataList;
    }

    public void addDataList(List<KeyValueBean> dataList) {
        this.dataList.addAll(dataList);
    }

    @Override
    public MusicOperationViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicOperationViewholder(LayoutInflater.from(mContext).inflate(R.layout.item_pop_music, parent, false));
    }

    @Override
    public void onBindViewHolder(MusicOperationViewholder holder, int position) {
        final KeyValueBean bean = dataList.get(position);
        holder.popItemText.setText(bean.getValue());
        int tag = bean.getTag();
        if (tag == MusicOperationPopwindow.ITEM_TAG_NEXT_PLAY) {
            ImageUtil.loadImage(mContext,R.drawable.ic_pop_item_next_play,holder.popItemImg,R.drawable.ic_pop_item_next_play);
            holder.popItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onNextPlay(musicData);
                }
            });
        } else if (tag == MusicOperationPopwindow.ITEM_TAG_DOWNLOAD) {
            ImageUtil.loadImage(mContext,R.drawable.ic_pop_item_download,holder.popItemImg,R.drawable.ic_pop_item_download);
            holder.popItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onDownload(musicData);
                }
            });
        } else if (tag == MusicOperationPopwindow.ITEM_TAG_COMMENT) {
            ImageUtil.loadImage(mContext,R.drawable.ic_pop_item_commont,holder.popItemImg,R.drawable.ic_pop_item_commont);
            holder.popItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onCommont(musicData);
                }
            });
        } else if (tag == MusicOperationPopwindow.ITEM_TAG_SHARE) {
            ImageUtil.loadImage(mContext,R.drawable.ic_pop_item_share,holder.popItemImg,R.drawable.ic_pop_item_share);

            holder.popItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onShare(musicData);
                }
            });
        } else if (tag == MusicOperationPopwindow.ITEM_TAG_ARTIST) {
            ImageUtil.loadImage(mContext,R.drawable.ic_pop_item_artist,holder.popItemImg,R.drawable.ic_pop_item_artist);
            holder.popItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onArtist(musicData);
                }
            });
        } else if (tag == MusicOperationPopwindow.ITEM_TAG_ALBUM) {
            ImageUtil.loadImage(mContext,R.drawable.ic_pop_item_album,holder.popItemImg,R.drawable.ic_pop_item_album);
            holder.popItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onAlbum(musicData);
                }
            });
        } else if (tag == MusicOperationPopwindow.ITEM_TAG_MV) {
            ImageUtil.loadImage(mContext,R.drawable.ic_pop_item_mv,holder.popItemImg,R.drawable.ic_pop_item_mv);
            holder.popItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onMv(musicData);
                }
            });
        } else if (tag == MusicOperationPopwindow.ITEM_TAG_SEE_INFO) {
            ImageUtil.loadImage(mContext,R.drawable.ic_pop_item_info,holder.popItemImg,R.drawable.ic_pop_item_info);

            holder.popItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onMusicInfo(musicData);
                }
            });
        } else if (tag == MusicOperationPopwindow.ITEM_TAG_SET_ALARM) {
            ImageUtil.loadImage(mContext,R.drawable.ic_pop_item_set_alarm,holder.popItemImg,R.drawable.ic_pop_item_set_alarm);
            holder.popItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onSetAlarm(musicData);
                }
            });
        } else if (tag == MusicOperationPopwindow.ITEM_TAG_DELETE) {
            ImageUtil.loadImage(mContext,R.drawable.ic_pop_item_delete,holder.popItemImg,R.drawable.ic_pop_item_delete);
            holder.popItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onDelete(musicData);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MusicOperationViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.pop_item_img)
        ImageView popItemImg;
        @BindView(R.id.pop_item_text)
        TextView popItemText;
        @BindView(R.id.pop_item_linear)
        RelativeLayout popItem;

        public MusicOperationViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}