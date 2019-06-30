package wzp.com.texturemusic.personalmodule.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapterForVlayout;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.personalmodule.bean.IndexPersonEntiy;
import wzp.com.texturemusic.personalmodule.viewholder.IndexPersonViewHolder;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/9/17.
 */

public class IndexPersonDjAdapter extends BaseAdapterForVlayout<IndexPersonEntiy, IndexPersonViewHolder> {


    public IndexPersonDjAdapter(Context c) {
        super(c);
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(3);
        gridLayoutHelper.setItemCount(6);
        gridLayoutHelper.setHGap(BaseUtil.dp2px(2));
        gridLayoutHelper.setMarginTop(BaseUtil.dp2px(8));
        return gridLayoutHelper;
    }

    @Override
    public IndexPersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.common_index_item, parent, false);
        return new IndexPersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IndexPersonViewHolder holder, int position) {
        final int index = position;
        holder.playImg.setVisibility(View.VISIBLE);
        holder.playCountTv.setVisibility(View.GONE);
        holder.nameTv.setVisibility(View.GONE);
        holder.playImg.setImageResource(R.drawable.ic_index_play);
        holder.descTv.setText(dataList.get(position).getName());
        holder.bkImg.setTransitionName(mContext.getString(R.string.sharedjdetail));
        ImageUtil.loadImage(mContext,
                dataList.get(position).getCoverImgUrl() + AppConstant.WY_IMG_300_300,
                holder.bkImg,
                R.drawable.ic_large_album);
        holder.itemLinear.setOnClickListener(new View.OnClickListener() {
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
        return dataList == null ? 0 : dataList.size();
    }
}
