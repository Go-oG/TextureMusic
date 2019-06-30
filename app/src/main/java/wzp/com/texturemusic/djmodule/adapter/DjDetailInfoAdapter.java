package wzp.com.texturemusic.djmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.CommentBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:电台评论的适配器
 * on 2017/11/17.
 */

public class DjDetailInfoAdapter extends BaseAdapter<CommentBean, DjDetailInfoAdapter.DjDetailInfoViewholder> {

    public DjDetailInfoAdapter(Context c) {
        super(c);
    }

    @Override
    public DjDetailInfoViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dj_detail_info, parent, false);
        return new DjDetailInfoViewholder(view);
    }

    @Override
    public void onBindViewHolder(DjDetailInfoViewholder holder, int position) {
        holder.itemView.setTag(position);
        ImageUtil.loadImage(mContext,
                dataList.get(position).getCommentCreaterImgUrl() + AppConstant.WY_IMG_100_100,
                holder.itemUserImg,
                R.drawable.ic_user_head,
                R.drawable.ic_user_head);
        holder.itemUserNameTv.setText(dataList.get(position).getCommnetCreaterName());
        holder.itemContentTv.setText(dataList.get(position).getDescription());
        holder.itemTipsTv.setText("——" + dataList.get(position).getVal());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class DjDetailInfoViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_user_img)
        CircleImageView itemUserImg;
        @BindView(R.id.item_user_name_tv)
        TextView itemUserNameTv;
        @BindView(R.id.item_content_tv)
        TextView itemContentTv;
        @BindView(R.id.item_tips_tv)
        TextView itemTipsTv;

        public DjDetailInfoViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemUserImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }

        }
    }

}
