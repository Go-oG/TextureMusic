package wzp.com.texturemusic.playlistmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapterForVlayout;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.playlistmodule.bean.PlaylistRankBean;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Wang on 2017/6/19.
 * 歌单排行榜 全球榜
 */

public class RankingFragQQAdapter extends BaseAdapterForVlayout<PlaylistRankBean,RankingFragQQAdapter.RankingQQViewHolder> {

    public RankingFragQQAdapter(Context c) {
        super(c);
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(3);
        gridLayoutHelper.setAutoExpand(false);
        gridLayoutHelper.setHGap(BaseUtil.dp2px(2));
        gridLayoutHelper.setMarginTop(BaseUtil.dp2px(2));
        return gridLayoutHelper;
    }

    @Override
    public RankingQQViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_fr_ranking_quanqiu, parent, false);
        return new RankingQQViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RankingQQViewHolder holder,  int position) {
        final int index = position;
        holder.itemView.setTag(position);
        holder.imageView.setTransitionName(mContext.getString(R.string.shareplaylistdetail));
        PlaylistRankBean bean = dataList.get(position);
        ImageUtil.loadImage(mContext,
                bean.getCoverImgUrl() + AppConstant.WY_IMG_300_300,
                holder.imageView);
        holder.updateText.setText(bean.getTips());
        holder.playlistNameText.setText(bean.getPlaylistName());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
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

    static class RankingQQViewHolder extends RecyclerView.ViewHolder {
        private TextView updateText, playlistNameText;
        private ImageView imageView;

        public RankingQQViewHolder(View itemView) {
            super(itemView);
            updateText = itemView.findViewById(R.id.fr_ranking_qq_rec_item_update_tv);
            playlistNameText = itemView.findViewById(R.id.fr_ranking_qq_rec_item_introduce_tv);
            imageView = itemView.findViewById(R.id.fr_ranking_qq_rec_item_img);
        }

    }

}
