package wzp.com.texturemusic.mvmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.CommentBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapterForVlayout;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/11/30.
 */

public class MvDetailCommentAdapter extends BaseAdapterForVlayout<CommentBean,MvDetailCommentAdapter.MvDetailCommentViewholder> {


    public MvDetailCommentAdapter(Context c) {
        super(c);
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper(BaseUtil.dp2px(4));
    }

    @Override
    public MvDetailCommentViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mv_detail_comment, parent, false);
        return new MvDetailCommentViewholder(view);

    }

    @Override
    public void onBindViewHolder(MvDetailCommentViewholder holder, int position) {
        final int index = position;
        CommentBean bean = dataList.get(position);
        ImageUtil.loadImage(mContext,
                bean.getUserBean().getUserCoverImgUrl() + AppConstant.WY_IMG_100_100,
                holder.userHeadImg, R.drawable.ic_user_head);
        holder.userNameTv.setText(bean.getUserBean().getNickName());
        holder.createTimeTv.setText(FormatData.unixTimeTostring(Long.valueOf(bean.getCreateTime())));
        holder.itemMarkTv.setText("" + bean.getLikeCount());
        holder.commentContentTv.setText("" + bean.getDescription());
        holder.commentReplayTv.setText("");
        holder.userHeadImg.setOnClickListener(new View.OnClickListener() {
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

    class MvDetailCommentViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_head_img)
        CircleImageView userHeadImg;
        @BindView(R.id.item_mark_tv)
        TextView itemMarkTv;
        @BindView(R.id.user_name_tv)
        TextView userNameTv;
        @BindView(R.id.create_time_tv)
        TextView createTimeTv;
        @BindView(R.id.comment_content_tv)
        TextView commentContentTv;
        @BindView(R.id.comment_replay_tv)
        TextView commentReplayTv;

        public MvDetailCommentViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
