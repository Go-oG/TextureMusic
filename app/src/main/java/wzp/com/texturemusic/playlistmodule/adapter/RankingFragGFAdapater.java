package wzp.com.texturemusic.playlistmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapterForVlayout;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.playlistmodule.bean.PlaylistRankBean;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Wang on 2017/6/19.
 * 歌单排行榜 官方榜
 */

public class RankingFragGFAdapater extends BaseAdapterForVlayout<PlaylistRankBean,RankingFragGFAdapater.RankingGFViewHolder> {

    public RankingFragGFAdapater(Context c) {
        super(c);
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        LinearLayoutHelper linearLayoutHelper = new LinearLayoutHelper(2);
        return linearLayoutHelper;
    }

    @Override
    public RankingGFViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_fr_ranking_guanfang, parent, false);
        return new RankingGFViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RankingGFViewHolder holder,  int position) {
        final int index = position;
        holder.itemView.setTag(position);
        holder.imgView.setTransitionName(mContext.getString(R.string.shareplaylistdetail));
        PlaylistRankBean bean = dataList.get(position);
        if (position == 0) {
            ImageUtil.loadImage(mContext,
                    bean.getCoverImgUrl() + AppConstant.WY_IMG_300_300,
                    holder.imgView,R.mipmap.clound_1);
        }
        if (position == 1) {
            ImageUtil.loadImage(mContext,
                    bean.getCoverImgUrl() + AppConstant.WY_IMG_300_300,
                    holder.imgView,R.mipmap.clound_2);

        }
        if (position == 2) {
            ImageUtil.loadImage(mContext,
                    bean.getCoverImgUrl() + AppConstant.WY_IMG_300_300,
                    holder.imgView,R.mipmap.clound_3);
        }
        if (position == 3) {
            ImageUtil.loadImage(mContext,
                    bean.getCoverImgUrl() + AppConstant.WY_IMG_300_300,
                    holder.imgView,R.mipmap.clound_4);
        }
        holder.oneText.setText("1." + bean.getMusicList().get(0).getMusicName() + "-" + bean.getMusicList().get(0).getArtistName());
        holder.twoText.setText("2." + bean.getMusicList().get(1).getMusicName() + "-" + bean.getMusicList().get(1).getArtistName());
        holder.threeText.setText("3." + bean.getMusicList().get(2).getMusicName() + "-" + bean.getMusicList().get(2).getArtistName());
        holder.updateText.setText(bean.getTips());
        holder.itemRelative.setOnClickListener(new View.OnClickListener() {
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

    static class RankingGFViewHolder extends RecyclerView.ViewHolder {
        TextView oneText, twoText, threeText, updateText;
        ImageView imgView;
        RelativeLayout itemRelative;

        public RankingGFViewHolder(View itemView) {
            super(itemView);
            oneText = itemView.findViewById(R.id.fr_ranking_gf_rec_textview_one);
            twoText = itemView.findViewById(R.id.fr_ranking_gf_rec_textview_two);
            threeText = itemView.findViewById(R.id.fr_ranking_gf_rec_textview_three);
            updateText = itemView.findViewById(R.id.fr_ranking_update_text);
            imgView = itemView.findViewById(R.id.fr_ranking_gf_rec_img);
            itemRelative = itemView.findViewById(R.id.fr_ranking_gf_rec_item);
        }

    }
}
