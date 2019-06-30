package wzp.com.texturemusic.mvmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.mvmodule.bean.MvContentBean;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;


public class AllMvRecycleAdapter extends BaseAdapter<MvContentBean, RecyclerView.ViewHolder> {

    private int dp2 = BaseUtil.dp2px(2);

    public AllMvRecycleAdapter(Context c) {
        super(c);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_person_mv, parent, false);
        AllMvContentViewHolder viewHolder = new AllMvContentViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        AllMvContentViewHolder viewHolder = (AllMvContentViewHolder) holder;
        MvContentBean bean = dataList.get(position);
        ImageUtil.loadImage(mContext,
                bean.getCoverImgUrl() + AppConstant.WY_IMG_500_300,
                viewHolder.imgView,
                R.drawable.ic_large_album);
        viewHolder.playCountTv.setText(FormatData.longValueToString(bean.getPlayCount()));
        viewHolder.descTv.setText(bean.getDescription());
        viewHolder.mvNameTv.setText(bean.getMvName());
        viewHolder.mvArtistNameTv.setText(bean.getArtistName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private class AllMvContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView playCountTv, descTv, mvNameTv, mvArtistNameTv;
        private ImageView imgView;
        private LinearLayout itemLinear;

        public AllMvContentViewHolder(View itemView) {
            super(itemView);
            playCountTv = itemView.findViewById(R.id.person_mv_playcount);
            descTv = itemView.findViewById(R.id.person_mv_desc);
            mvNameTv = itemView.findViewById(R.id.person_mv_name);
            mvArtistNameTv = itemView.findViewById(R.id.person_mv_artistname);
            imgView = itemView.findViewById(R.id.person_mv_img);
            itemLinear = itemView.findViewById(R.id.person_mv_linear);
            itemLinear.setOnClickListener(this);
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) itemLinear.getLayoutParams();
            params.setMarginStart(dp2);
            params.setMarginEnd(dp2);
            itemLinear.setLayoutParams(params);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }
        }
    }


}
