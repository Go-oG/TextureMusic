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
 * Description:个性推荐页面的album适配器
 * on 2017/10/5.
 */

public class IndexPersonAlbumAdapter extends BaseAdapterForVlayout<IndexPersonEntiy, IndexPersonViewHolder> {


    public IndexPersonAlbumAdapter(Context c) {
        super(c);
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        GridLayoutHelper layoutHelper = new GridLayoutHelper(3);
        layoutHelper.setHGap(BaseUtil.dp2px(2));
        layoutHelper.setMarginTop(BaseUtil.dp2px(8));
        return layoutHelper;
    }

    @Override
    public IndexPersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.common_index_item, parent, false);
        return new IndexPersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IndexPersonViewHolder holder, int position) {
        final int index = position;
        holder.itemLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickSubject != null) {
                    itemClickSubject.onNext(new ItemBean(v, index));
                }
            }
        });
        holder.nameTv.setVisibility(View.VISIBLE);
        holder.nameTv.setText(dataList.get(position).getName());
        holder.playImg.setVisibility(View.GONE);
        holder.playCountTv.setVisibility(View.GONE);
        holder.descTv.setVisibility(View.VISIBLE);
        holder.descTv.setText(dataList.get(position).getSubName());
        ImageUtil.loadImage(mContext,
                dataList.get(position).getCoverImgUrl() + AppConstant.WY_IMG_300_300,
                holder.bkImg,
                R.drawable.ic_large_album);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


}
