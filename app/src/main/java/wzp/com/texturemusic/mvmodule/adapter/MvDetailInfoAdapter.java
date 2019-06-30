package wzp.com.texturemusic.mvmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.interf.OnRecycleItemClickListener;
import wzp.com.texturemusic.util.FormatData;

/**
 * Created by Go_oG
 * Description:MV详情界面recycleview的适配器
 * on 2017/11/30.
 */

public class MvDetailInfoAdapter extends DelegateAdapter.Adapter<MvDetailInfoAdapter.MvDetailInfoViewholder> {
    private Context mContext;
    private MvBean mvBean;
    private OnRecycleItemClickListener listener;

    public void setListener(OnRecycleItemClickListener listener) {
        this.listener = listener;
    }

    public MvBean getMvBean() {
        return mvBean;
    }

    public void setMvBean(MvBean mvBean) {
        this.mvBean = mvBean;
    }

    public MvDetailInfoAdapter(Context context) {
        mContext = context;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public MvDetailInfoViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mv_detail_info, parent, false);
        return new MvDetailInfoViewholder(view);
    }

    @Override
    public void onBindViewHolder(final MvDetailInfoViewholder holder, int position) {
        holder.artistNameTv.setText("歌手：" + mvBean.getArtistName());
        holder.publishInfoTv.setText("发布：" + mvBean.getPublishTime() + "    播放：" + FormatData.longValueToString(mvBean.getPlayCount()));
        holder.mvNameTv.setText(mvBean.getMvName());
        String info = mvBean.getDescription();
        if (TextUtils.isEmpty(info)) {
            holder.expandImg.setVisibility(View.GONE);
        } else {
            holder.expandImg.setVisibility(View.VISIBLE);
            holder.mvDescTv.setText(info);
            holder.expandImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.mvDescTv.getVisibility() == View.VISIBLE) {
                        holder.mvDescTv.setVisibility(View.GONE);
                        ((ImageView) v).setImageResource(R.drawable.ic_textdrawable_down);
                    } else {
                        holder.mvDescTv.setVisibility(View.VISIBLE);
                        ((ImageView) v).setImageResource(R.drawable.ic_textdrawable_up);
                    }
                    notifyDataSetChanged();
                }
            });
        }
        holder.markCountTv.setText(FormatData.longValueToString(mvBean.getLikeCount()));
        holder.commentCountTv.setText(FormatData.longValueToString(mvBean.getCommentCount()));
        holder.shareCountTv.setText(FormatData.longValueToString(mvBean.getShareCount()));
        holder.collectionCountTv.setText(FormatData.longValueToString(mvBean.getCommentCount()));
        holder.item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(v, 0);
                }
            }
        });
        holder.item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(v, 1);
                }
            }
        });
        holder.item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(v, 2);
                }
            }
        });
        holder.item4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(v, 3);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mvBean == null) {
            return 0;
        } else {
            return 1;
        }
    }

    class MvDetailInfoViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.mv_name_tv)
        TextView mvNameTv;
        @BindView(R.id.expand_img)
        ImageView expandImg;
        @BindView(R.id.artist_name_tv)
        TextView artistNameTv;
        @BindView(R.id.publish_info_tv)
        TextView publishInfoTv;
        @BindView(R.id.mv_desc_tv)
        TextView mvDescTv;
        @BindView(R.id.mark_count_tv)
        TextView markCountTv;
        @BindView(R.id.collection_count_tv)
        TextView collectionCountTv;
        @BindView(R.id.comment_count_tv)
        TextView commentCountTv;
        @BindView(R.id.share_count_tv)
        TextView shareCountTv;
        @BindView(R.id.item_1)
        LinearLayout item1;
        @BindView(R.id.item_2)
        LinearLayout item2;
        @BindView(R.id.item_3)
        LinearLayout item3;
        @BindView(R.id.item_4)
        LinearLayout item4;

        public MvDetailInfoViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
