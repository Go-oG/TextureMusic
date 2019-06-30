package wzp.com.texturemusic.djmodule.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.RadioBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:电台排行榜 新晋电台和最热电台的适配器
 * on 2017/10/28.
 */

public class DjRankRadioAdapter extends BaseAdapter<RadioBean, DjRankRadioAdapter.DjRankProgrameViewholder> {
    private Drawable upDrawable;
    private Drawable downDrawable;

    public DjRankRadioAdapter(Context c) {
        super(c);
        upDrawable = mContext.getDrawable(R.drawable.ic_text_up);
        upDrawable.setBounds(0, 0, BaseUtil.dp2px(8), BaseUtil.dp2px(8));
        downDrawable = mContext.getDrawable(R.drawable.ic_text_down);
        downDrawable.setBounds(0, 0, BaseUtil.dp2px(8), BaseUtil.dp2px(8));
    }

    @Override
    public DjRankProgrameViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_radio_top, parent,
                false);
        return new DjRankProgrameViewholder(view);
    }

    @Override
    public void onBindViewHolder(DjRankProgrameViewholder holder, int position) {
        holder.itemView.setTag(position);
        if (position >= 0 && position <= 8) {
            holder.indexTv.setTextSize(BaseUtil.sp2px(16));
        } else if (position > 8) {
            holder.indexTv.setTextSize(BaseUtil.sp2px(12));
        }
        RadioBean bean = dataList.get(position);
        ImageUtil.loadImage(mContext,
                bean.getCoverImgUrl() + AppConstant.WY_IMG_200_200,
                holder.coverImg, R.drawable.ic_large_album);
        holder.nameTv.setText(bean.getRadioName().trim());
        holder.descTv.setText(bean.getDjErName().trim());
        holder.indexTv.setText((position + 1) + "");
        holder.hotScroeTv.setText(bean.getHotScore() + "");
        if (position < 9) {
            if (position <= 2) {
                holder.indexTv.setTextColor(0xffd81e06);
                holder.indexTv.setTextSize(27);
            } else {
                holder.indexTv.setTextColor(0xff5c6bc0);
                holder.indexTv.setTextSize(23);
            }
        } else if (position <= 98) {
            holder.indexTv.setTextColor(0xff515151);
            holder.indexTv.setTextSize(17);
        } else {
            holder.indexTv.setTextColor(0xff515151);
            holder.indexTv.setTextSize(15);
        }
        int lastVal = bean.getRank() - bean.getLastRank();
        if (lastVal == 0) {
            //排名持平
            holder.sortInfoTv.setCompoundDrawables(null, null, null, null);
            holder.sortInfoTv.setText("-0");
            holder.sortInfoTv.setTextColor(0xff515151);
        } else if (lastVal > 0) {
            //排名下降
            if (downDrawable != null) {
                holder.sortInfoTv.setText(Math.abs(lastVal) + "");
                holder.sortInfoTv.setCompoundDrawables(downDrawable, null, null, null);
            } else {
                holder.sortInfoTv.setText("↓" + Math.abs(lastVal));
            }
            holder.sortInfoTv.setTextColor(Color.parseColor("#2196f3"));
        } else {
            //排名上升
            if (upDrawable != null) {
                holder.sortInfoTv.setText(Math.abs(lastVal) + "");
                holder.sortInfoTv.setCompoundDrawables(upDrawable, null, null, null);
            } else {
                holder.sortInfoTv.setText("↑" + Math.abs(lastVal));
            }
            holder.sortInfoTv.setTextColor(Color.parseColor("#ce3d3a"));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class DjRankProgrameViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView indexTv, nameTv, descTv, hotScroeTv, sortInfoTv;
        private ImageView coverImg;
        private RelativeLayout itemRelative;

        public DjRankProgrameViewholder(View itemView) {
            super(itemView);
            indexTv = itemView.findViewById(R.id.fr_radio_top_item_index);
            nameTv = itemView.findViewById(R.id.fr_radio_top_item_name);
            descTv = itemView.findViewById(R.id.fr_radio_top_item_desc);
            hotScroeTv = itemView.findViewById(R.id.fr_radio_top_item_hotscroe);
            coverImg = itemView.findViewById(R.id.fr_radio_top_item_coverimg);
            itemRelative = itemView.findViewById(R.id.fr_radio_top_item);
            sortInfoTv = itemView.findViewById(R.id.sort_info_tv);
            itemRelative.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }
        }
    }

}
