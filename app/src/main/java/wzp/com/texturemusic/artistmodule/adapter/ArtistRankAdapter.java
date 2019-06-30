package wzp.com.texturemusic.artistmodule.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.artistmodule.bean.RankArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.ImageUtil;
import wzp.com.texturemusic.util.StringUtil;

/**
 * author:Go_oG
 * date: on 2018/5/10
 * packageName: wzp.com.texturemusic.artistmodule.adapter
 */
public class ArtistRankAdapter extends BaseAdapter<RankArtistBean, ArtistRankAdapter.ArtistRankViewholder> {
    private Drawable downDrawable;
    private Drawable upDrawable;

    public ArtistRankAdapter(Context c) {
        super(c);
        downDrawable = mContext.getDrawable(R.drawable.ic_text_down);
        downDrawable.setBounds(0, 0, BaseUtil.dp2px(8), BaseUtil.dp2px(8));
        upDrawable = mContext.getDrawable(R.drawable.ic_text_up);
        upDrawable.setBounds(0, 0, BaseUtil.dp2px(8), BaseUtil.dp2px(8));
    }

    @Override
    public ArtistRankViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ArtistRankViewholder(LayoutInflater.from(mContext).inflate(R.layout.item_artist_rank, parent, false));
    }

    @Override
    public void onBindViewHolder(ArtistRankViewholder holder, int position) {
        holder.itemView.setTag(position);
        RankArtistBean bean = dataList.get(position);
        holder.itemIndexTv.setText((position + 1) + "");
        ImageUtil.loadImage(mContext,
                bean.getArtist().getArtistImgUrl() + AppConstant.WY_IMG_200_200,
                holder.itemArtistCoverimg, R.drawable.png_artist_cover);
        SpannableStringBuilder builder = new SpannableStringBuilder(bean.getArtist().getArtistName());
        if (!TextUtils.isEmpty(bean.getTransName())) {
            String content = "(" + bean.getTransName().trim() + ")";
            builder.append(StringUtil.buildStringColor(content, 0x89000000, 0, content.length()));
        }
        holder.itemArtistNameTv.setText(builder);
        builder.clear();
        builder.clearSpans();
        String topicInfo = "#" + bean.getArtist().getArtistName() + "#" + bean.getTopicPerson() + "人正在讨论";
        builder.append(StringUtil.buildStringColor(topicInfo, 0xff477aac, 0, bean.getArtist().getArtistName().length() + 2));
        holder.itemArtistTopicTv.setText(builder);
        holder.itemArtistScoreTv.setText(bean.getScore() + "");
        if (position < 9) {
            if (position <= 2) {
                holder.itemIndexTv.setTextColor(0xffd81e06);
                holder.itemIndexTv.setTextSize(27);
            } else {
                holder.itemIndexTv.setTextColor(0xff5c6bc0);
                holder.itemIndexTv.setTextSize(23);
            }
        } else if (position <= 98) {
            holder.itemIndexTv.setTextColor(0xff515151);
            holder.itemIndexTv.setTextSize(17);
        } else {
            holder.itemIndexTv.setTextColor(0xff515151);
            holder.itemIndexTv.setTextSize(15);
        }
        int lastVal = bean.getLastRank() == null ? 0 : bean.getLastRank();
        lastVal = position - lastVal;
        holder.itemSortInfoTv.setTextSize(13);
        if (lastVal == 0) {
            //排名持平
            holder.itemSortInfoTv.setCompoundDrawables(null, null, null, null);
            holder.itemSortInfoTv.setText("-0");
            holder.itemSortInfoTv.setTextColor(0xff515151);
        } else if (lastVal > 0) {
            //排名下降
            if (downDrawable != null) {
                holder.itemSortInfoTv.setText(Math.abs(lastVal) + "");
                holder.itemSortInfoTv.setCompoundDrawables(downDrawable, null, null, null);
            } else {
                holder.itemSortInfoTv.setText("↓" + Math.abs(lastVal));
            }
            holder.itemSortInfoTv.setTextColor(Color.parseColor("#2196f3"));
        } else {
            //排名上升
            if (upDrawable != null) {
                holder.itemSortInfoTv.setText(Math.abs(lastVal) + "");
                holder.itemSortInfoTv.setCompoundDrawables(upDrawable, null, null, null);
            } else {
                holder.itemSortInfoTv.setText("↑" + Math.abs(lastVal));
            }
            holder.itemSortInfoTv.setTextColor(Color.parseColor("#ce3d3a"));
        }


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ArtistRankViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_index_tv)
        TextView itemIndexTv;
        @BindView(R.id.item_sort_info_tv)
        TextView itemSortInfoTv;
        @BindView(R.id.item_artist_coverimg)
        ImageView itemArtistCoverimg;
        @BindView(R.id.item_artist_name_tv)
        TextView itemArtistNameTv;
        @BindView(R.id.item_artist_score_tv)
        TextView itemArtistScoreTv;
        @BindView(R.id.item_artist_topic_tv)
        TextView itemArtistTopicTv;
        @BindView(R.id.item_artist_rank_rl)
        RelativeLayout itemArtistRankItem;

        public ArtistRankViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemArtistRankItem.setOnClickListener(this);
            itemArtistTopicTv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (Integer) itemView.getTag()));
            }

        }
    }

}
