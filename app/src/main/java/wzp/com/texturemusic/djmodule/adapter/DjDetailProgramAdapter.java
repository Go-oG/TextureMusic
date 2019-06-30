package wzp.com.texturemusic.djmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.DjSongBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.util.FormatData;

/**
 * Created by Go_oG
 * Description:电台详情界面的节目数据
 * on 2017/11/16.
 */

public class DjDetailProgramAdapter extends BaseAdapter<DjSongBean, DjDetailProgramAdapter.DjDetailProgramViewholder> {

    public DjDetailProgramAdapter(Context c) {
        super(c);
    }

    @Override
    public DjDetailProgramViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dj_detail, parent, false);
        return new DjDetailProgramViewholder(view);
    }

    @Override
    public void onBindViewHolder(DjDetailProgramViewholder holder, int position) {
        holder.itemView.setTag(position);
        DjSongBean bean = dataList.get(position);
        holder.itemSongNameTv.setText(bean.getMusicBean().getMusicName());
        String infoTxt = FormatData.unixTimeTostring(bean.getCreatTime()) +
                "  播放:" + FormatData.longValueToString(bean.getPlayCount()) +
                "  时长:" + FormatData.timeValueToString(bean.getMusicBean().getAllTime());
        holder.itemSongInfoTv.setText(infoTxt);
        holder.itemIndexTv.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class DjDetailProgramViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_index_tv)
        TextView itemIndexTv;
        @BindView(R.id.item_song_name_tv)
        TextView itemSongNameTv;
        @BindView(R.id.item_song_info_tv)
        TextView itemSongInfoTv;
        @BindView(R.id.item_operation_img)
        ImageView itemOperationImg;
        @BindView(R.id.item_item)
        RelativeLayout itemItem;

        public DjDetailProgramViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemItem.setOnClickListener(this);
            itemOperationImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }

        }
    }


}
