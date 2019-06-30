package wzp.com.texturemusic.personalmodule.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:个性推荐页面的mv适配器
 * <p>
 * on 2017/9/17.
 */

public class IndexPersonMvAdapter extends BaseAdapterForVlayout<IndexPersonEntiy, IndexPersonViewHolder> {
    private Drawable drawable;

    public IndexPersonMvAdapter(Context c) {
        super(c);
        drawable = mContext.getDrawable(R.drawable.ic_textdrawable_camera);
        if (drawable!=null){
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(2);
        gridLayoutHelper.setItemCount(4);
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
        holder.nameTv.setVisibility(View.GONE);
        holder.playImg.setVisibility(View.GONE);

        if (drawable != null) {
            holder.playCountTv.setCompoundDrawablesRelative(drawable, null, null, null);
            holder.playCountTv.setCompoundDrawablePadding(8);
        }
        holder.playCountTv.setText(FormatData.longValueToString((int) dataList.get(position).getPlayCount()));
        holder.playCountTv.setTextColor(0xffffffff);
        holder.descTv.setText(dataList.get(position).getName());
        ImageUtil.loadImage(mContext,
                dataList.get(position).getCoverImgUrl() + AppConstant.WY_IMG_500_300,
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
