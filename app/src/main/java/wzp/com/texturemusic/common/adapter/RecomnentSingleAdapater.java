package wzp.com.texturemusic.common.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.customview.CommonNavView;
import wzp.com.texturemusic.interf.OnRecycleItemClickListener;
import wzp.com.texturemusic.util.BaseUtil;

/**
 * Created by wang on 2017/5/12.
 * 通栏布局
 */

public class RecomnentSingleAdapater extends DelegateAdapter.Adapter<RecomnentSingleAdapater.SingleViewHolder> {
    private Context context;
    private String titleText = "";
    private boolean showImg = true;

    public void showRightImg(boolean isShow) {
        this.showImg = isShow;
    }

    public void setContentText(String str) {
        this.titleText = str;
    }

    private OnRecycleItemClickListener clickListener;

    public RecomnentSingleAdapater(Context context, String titleText) {
        this.context = context;
        this.titleText = titleText;
    }

    public void setClickListener(OnRecycleItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        SingleLayoutHelper singleLayoutHelper = new SingleLayoutHelper();
        singleLayoutHelper.setItemCount(1);
        singleLayoutHelper.setMarginTop(BaseUtil.dp2px(4));
        singleLayoutHelper.setMarginBottom(BaseUtil.dp2px(4));
        return singleLayoutHelper;
    }

    @Override
    public SingleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fr_person_singleview, parent, false);
        return new SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SingleViewHolder holder, int position) {
        holder.navigaTionTextView.setContentText(titleText);
        holder.itemView.setTag(position);
        if (showImg) {
            holder.navigaTionTextView.showRightImg();
        } else {
            holder.navigaTionTextView.hindRightImg();
        }
        holder.navigaTionTextView.setContentTextSize(16);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class SingleViewHolder extends RecyclerView.ViewHolder {
        private CommonNavView navigaTionTextView;

        public SingleViewHolder(final View itemView) {
            super(itemView);
            navigaTionTextView = itemView.findViewById(R.id.single_nav_text);
            navigaTionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onItemClick(v, (int) itemView.getTag());
                    }
                }
            });
        }

    }

}
