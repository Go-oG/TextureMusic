package wzp.com.texturemusic.mvmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.core.adapter.BaseAdapterForVlayout;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:MV详情界面的相似MV适配器
 * on 2017/11/30.
 */

public class MvDetailSimilarAdapter extends BaseAdapterForVlayout<MvBean,MvDetailSimilarAdapter.MvDetailSimilarViewholder> {

    public MvDetailSimilarAdapter(Context c) {
        super(c);
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper(BaseUtil.dp2px(4));
    }

    @Override
    public MvDetailSimilarViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mv_detail_similar, parent, false);
        return new MvDetailSimilarViewholder(view);
    }

    @Override
    public void onBindViewHolder(MvDetailSimilarViewholder holder, int position) {
        final int index = position;
        MvBean bean = dataList.get(position);
        ImageUtil.loadImage(mContext,bean.getCoverImgUrl() + AppConstant.WY_IMG_300_200,holder.mvCoverImg);
        holder.mvPlaycountTv.setText(FormatData.longValueToString(bean.getPlayCount()));
        holder.mvNameTv.setText(bean.getMvName());
        holder.mvInfoTv.setText(FormatData.timeValueToString(bean.getDurationTime()) + "  by " + bean.getArtistName());

        holder.itemItem.setOnClickListener(new View.OnClickListener() {
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

    class MvDetailSimilarViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.mv_cover_img)
        ImageView mvCoverImg;
        @BindView(R.id.mv_playcount_tv)
        TextView mvPlaycountTv;
        @BindView(R.id.mv_name_tv)
        TextView mvNameTv;
        @BindView(R.id.mv_info_tv)
        TextView mvInfoTv;
        @BindView(R.id.item_item)
        LinearLayout itemItem;

        public MvDetailSimilarViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
