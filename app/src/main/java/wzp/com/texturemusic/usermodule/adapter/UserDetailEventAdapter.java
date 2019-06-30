package wzp.com.texturemusic.usermodule.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.functions.Consumer;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.CommentBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.common.adapter.ImgAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;


/**
 * Created by Go_oG
 * Description:
 * on 2017/11/27.
 */

public class UserDetailEventAdapter extends BaseAdapter<CommentBean, UserDetailEventAdapter.UserDetailEventViewholder> {
    private RecyclerView.RecycledViewPool viewPool;

    public UserDetailEventAdapter(Context c) {
        super(c);
        viewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public UserDetailEventViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user_detail_event, parent, false);
        return new UserDetailEventViewholder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(UserDetailEventViewholder holder, int position) {
        final int index = position;
        ImgAdapter imgAdapter = new ImgAdapter(mContext);
        imgAdapter.getItemClickSubject().subscribe(new Consumer<ItemBean>() {
            @Override
            public void accept(ItemBean itemBean) throws Exception {
                if (itemClickSubject != null) {
                    itemBean.getView().setTag(itemBean.getPosition());
                    itemClickSubject.onNext(new ItemBean(itemBean.getView(), index));
                }
            }
        });
        imgAdapter.clearDataList();
        imgAdapter.addDataList(dataList.get(position).getImgUrls());

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
        layoutManager.setRecycleChildrenOnDetach(true);
        holder.mRecycleview.setRecycledViewPool(viewPool);
        holder.mRecycleview.setAdapter(imgAdapter);
        holder.mRecycleview.setLayoutManager(layoutManager);
        ImageUtil.loadImage(mContext,
                dataList.get(position).getUserBean().getUserCoverImgUrl() + AppConstant.WY_IMG_100_100,
                holder.userHeadImg,
                R.drawable.ic_user_head, R.drawable.ic_user_head);
        holder.itemUserName.setText(dataList.get(position).getUserBean().getNickName());
        holder.itemMarkTv.setText(FormatData.longValueToString(dataList.get(position).getLikeCount()));
        holder.itemCommentCountTv.setText(FormatData.longValueToString(dataList.get(position).getCommentCount()));
        holder.itemShareCountTv.setText(dataList.get(position).getShareCount() + "");
        MusicBean musicBean = dataList.get(position).getMusicBean();
        if (musicBean != null) {
            holder.itemMusicItem.setVisibility(View.VISIBLE);
            ImageUtil.loadImage(mContext,
                    musicBean.getCoverImgUrl() + AppConstant.WY_IMG_200_200,
                    holder.itemMusicImg,
                    R.drawable.ic_large_album);
            holder.itemMusicInfoTv.setText(musicBean.getMusicName() + "-" + musicBean.getArtistName());
            holder.contentTv.setText(dataList.get(position).getDescription());
        } else {
            holder.itemMusicItem.setVisibility(View.GONE);
            holder.contentTv.setText(Html.fromHtml(dataList.get(position).getDescription()));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class UserDetailEventViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_head_img)
        CircleImageView userHeadImg;
        @BindView(R.id.item_user_name)
        TextView itemUserName;
        @BindView(R.id.content_tv)
        TextView contentTv;
        @BindView(R.id.m_recycleview)
        RecyclerView mRecycleview;
        @BindView(R.id.item_music_tv)
        ImageView itemMusicImg;
        @BindView(R.id.item_music_info_tv)
        TextView itemMusicInfoTv;
        @BindView(R.id.item_music_item)
        RelativeLayout itemMusicItem;
        @BindView(R.id.item_mark_tv)
        TextView itemMarkTv;
        @BindView(R.id.item_comment_count_tv)
        TextView itemCommentCountTv;
        @BindView(R.id.item_share_count_tv)
        TextView itemShareCountTv;

        public UserDetailEventViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
