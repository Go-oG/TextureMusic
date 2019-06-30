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
 * Description:个性推荐界面中推荐歌单的适配器
 * on 2017/9/17.
 */

public class IndexPersonPlaylistAdapter extends BaseAdapterForVlayout<IndexPersonEntiy, IndexPersonViewHolder> {

    private  Drawable drawable;
    public IndexPersonPlaylistAdapter(Context c) {
        super(c);
         drawable = mContext.getDrawable(R.drawable.ic_textdrawable_erji);
         if (drawable!=null){
             drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
         }
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        GridLayoutHelper helper = new GridLayoutHelper(3);
        helper.setHGap(BaseUtil.dp2px(2));
        helper.setMarginTop(BaseUtil.dp2px(8));
        return helper;
    }

    @Override
    public IndexPersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.common_index_item, parent, false);
        return new IndexPersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IndexPersonViewHolder holder, int position) {
        final int index = position;
        holder.itemView.setTag(position);
        holder.nameTv.setVisibility(View.GONE);
        holder.playImg.setVisibility(View.GONE);
        holder.playCountTv.setVisibility(View.VISIBLE);
        holder.descTv.setText(dataList.get(position).getName());
        holder.descTv.setTextSize(15);
        holder.bkImg.setTransitionName(mContext.getString(R.string.shareplaylistdetail));
        holder.playCountTv.setText(FormatData.longValueToString(dataList.get(position).getPlayCount()));

        if (drawable != null) {
            holder.playCountTv.setCompoundDrawablesRelative(drawable, null, null, null);
            holder.playCountTv.setCompoundDrawablePadding(8);
        }
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
        if (dataList.size() > 6) {
            return 6;
        } else {
            return dataList.size();
        }
    }


}
