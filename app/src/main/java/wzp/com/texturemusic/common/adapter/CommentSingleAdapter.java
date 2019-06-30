package wzp.com.texturemusic.common.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.SubCommentBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.interf.OnOperationListener;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2018/1/6.
 */

public class CommentSingleAdapter extends DelegateAdapter.Adapter<CommentSingleAdapter.CommentSingleViewholder> {
    private Context mContext;
    private OnOperationListener listener;
    private SubCommentBean bean;

    public CommentSingleAdapter(Context c, SubCommentBean bean, OnOperationListener listener) {
        mContext = c;
        this.bean = bean;
        this.listener = listener;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        SingleLayoutHelper layoutHelper = new SingleLayoutHelper();
        layoutHelper.setItemCount(1);
        return layoutHelper;
    }

    @Override
    public CommentSingleViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentSingleViewholder(LayoutInflater.from(mContext).inflate(R.layout.item_comment_single, parent, false));
    }

    @Override
    public void onBindViewHolder(CommentSingleViewholder holder, int position) {
        holder.itemTitle.setText(bean.getTitle());
        holder.itemSubtitle.setText(bean.getSubTitle());
        ImageUtil.loadImage(mContext,
                bean.getCoverImgUrl() + AppConstant.WY_IMG_300_300,
                holder.itemImg);
        holder.itemSingleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPlayAll();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class CommentSingleViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_img)
        ImageView itemImg;
        @BindView(R.id.item_title)
        TextView itemTitle;
        @BindView(R.id.item_subtitle)
        TextView itemSubtitle;
        @BindView(R.id.item_single_item)
        RelativeLayout itemSingleItem;

        public CommentSingleViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
