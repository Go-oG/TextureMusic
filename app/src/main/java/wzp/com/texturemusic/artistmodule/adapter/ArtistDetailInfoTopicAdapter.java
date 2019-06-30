package wzp.com.texturemusic.artistmodule.adapter;

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
import wzp.com.texturemusic.bean.TopicBean;
import wzp.com.texturemusic.core.adapter.BaseAdapterForVlayout;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;

public class ArtistDetailInfoTopicAdapter extends BaseAdapterForVlayout<TopicBean, ArtistDetailInfoTopicAdapter.ArtistDetailInfoTopicViewholder> {


    public ArtistDetailInfoTopicAdapter(Context c) {
        super(c);
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public ArtistDetailInfoTopicViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_artistdetail_info_topic, parent, false);
        return new ArtistDetailInfoTopicViewholder(view);
    }

    @Override
    public void onBindViewHolder(ArtistDetailInfoTopicViewholder holder, int position) {
        holder.itemView.setTag(position);
        ImageUtil.loadImage(mContext,
                dataList.get(position).getCoverUrl() + AppConstant.WY_IMG_300_300,
                holder.itemTopicImg);
        String conten = "by " + dataList.get(position).getCreator().getNickName() +
                "   阅读" + FormatData.longValueToString(dataList.get(position).getReadCount());
        holder.itemTopicContent.setText(conten);
        holder.itemTopicTitle.setText(dataList.get(position).getMainTitle());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ArtistDetailInfoTopicViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_topic_img)
        ImageView itemTopicImg;
        @BindView(R.id.item_topic_title)
        TextView itemTopicTitle;
        @BindView(R.id.item_topic_content)
        TextView itemTopicContent;
        @BindView(R.id.item_topic)
        LinearLayout itemTopic;

        public ArtistDetailInfoTopicViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemTopic.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (Integer) itemView.getTag()));
            }

        }
    }

}
