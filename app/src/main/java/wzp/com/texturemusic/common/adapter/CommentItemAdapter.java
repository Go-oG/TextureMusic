package wzp.com.texturemusic.common.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.CommentBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapterForVlayout;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:评论的适配器
 * on 2018/1/6.
 */

public class CommentItemAdapter extends BaseAdapterForVlayout<CommentBean, CommentItemAdapter.CommentItemViewholder> {

    public CommentItemAdapter(Context c) {
        super(c);
    }

    public List<CommentBean> getDataList() {
        return dataList;
    }

    public void addDataList(List<CommentBean> dataList) {
        this.dataList.addAll(dataList);
    }

    public void clearDataList() {
        this.dataList.clear();
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public CommentItemViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentItemViewholder(LayoutInflater.from(mContext).inflate(R.layout.item_comment_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(CommentItemViewholder holder, int position) {
        final int index = position;
        CommentBean bean = dataList.get(position);
        holder.itemCommentContentTv.setText(bean.getDescription() + "");
        holder.itemCommentMarkTv.setText(bean.getLikeCount() + "");
        holder.itemCommentUserNameTv.setText(bean.getUserBean().getNickName());
        ImageUtil.loadImage(mContext,
                bean.getUserBean().getUserCoverImgUrl() + AppConstant.WY_IMG_100_100,
                holder.itemCommentUserImg,
                R.drawable.ic_user_head,
                R.drawable.ic_user_head);
        holder.itemCommentTimeTv.setText(bean.getCreateTime());

        holder.itemCommentUserImg.setOnClickListener(new View.OnClickListener() {
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

    class CommentItemViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_comment_user_img)
        CircleImageView itemCommentUserImg;
        @BindView(R.id.item_comment_user_name_tv)
        TextView itemCommentUserNameTv;
        @BindView(R.id.item_comment_time_tv)
        TextView itemCommentTimeTv;
        @BindView(R.id.item_comment_mark_tv)
        TextView itemCommentMarkTv;
        @BindView(R.id.item_comment_content_tv)
        TextView itemCommentContentTv;

        public CommentItemViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
