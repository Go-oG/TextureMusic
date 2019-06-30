package wzp.com.texturemusic.downloadmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhouzhuo.zzhorizontalprogressbar.ZzHorizontalProgressBar;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.DownloadBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.util.ImageUtil;
import wzp.com.texturemusic.util.StringUtil;

/**
 * Created by Go_oG
 * Description:用于正在下载界面的适配器
 * on 2018/1/31.
 */
public class DownloadingAdapter extends BaseAdapter<DownloadBean, DownloadingAdapter.DownloadingViewholder> {

    public DownloadingAdapter(Context c) {
        super(c);
    }

    @Override
    public DownloadingViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DownloadingViewholder(LayoutInflater.from(mContext).inflate(R.layout.item_downloading, parent, false));
    }

    @Override
    public void onBindViewHolder(DownloadingViewholder holder, int position) {
        holder.itemView.setTag(position);
        DownloadBean bean = dataList.get(position);
        if (bean.getDownloading() != null && bean.getDownloading()) {
            holder.itemLinear.setVisibility(View.VISIBLE);
            holder.itemSubTipsTv.setVisibility(View.GONE);
            float progress = ((float) bean.getCurrentByte() / (float) bean.getTotalByte()) * 100;
            holder.mProgressBar.setProgress((int) progress);
            holder.itemDownloadInfoTv.setText(bean.getSpeed() + "Kb/s");
        } else {
            holder.itemLinear.setVisibility(View.GONE);
            holder.itemSubTipsTv.setVisibility(View.VISIBLE);
        }
        Boolean isMv = bean.getMvData();
        if (isMv != null && isMv) {
            ImageUtil.loadImage(mContext,
                    R.drawable.ic_mv, holder.itemDownloadType, R.drawable.ic_mv);
            MvBean mvBean = bean.getMvBean();
            if (mvBean != null) {
                holder.itemDownloadNameTv.setText(mvBean.getMvName());
                holder.itemSubTipsTv.setText("");
            } else {
                holder.itemDownloadNameTv.setText("暂无");
                holder.itemSubTipsTv.setText("暂无");
            }
        }

        if (isMv != null && !isMv) {
            ImageUtil.loadImage(mContext,
                    R.drawable.ic_download_music, holder.itemDownloadType, R.drawable.ic_download_music);
            MusicBean musicBean = bean.getMusicBean();
            if (musicBean != null) {
                String name = musicBean.getMusicName();
                int length = name.length();
                SpannableStringBuilder builder = new SpannableStringBuilder();
                if (!StringUtil.isEmpty(musicBean.getAlias())) {
                    name = name + "( " + musicBean.getAlias() + ")";
                    builder = StringUtil.buildStringColor(name, 0x89000000, length, name.length());
                    builder = StringUtil.builderStringSize(builder, 13, length, builder.length());
                } else {
                    builder.append(name);
                }
                holder.itemDownloadNameTv.setText(builder);
                holder.itemSubTipsTv.setText(musicBean.getArtistName() + "-" + musicBean.getAlbumName());
            } else {
                holder.itemDownloadNameTv.setText("暂无");
                holder.itemSubTipsTv.setText("暂无");
            }
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class DownloadingViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_download_type)
        ImageView itemDownloadType;
        @BindView(R.id.item_download_delete)
        ImageView itemDownloadDelete;
        @BindView(R.id.item_download_name_tv)
        TextView itemDownloadNameTv;
        @BindView(R.id.item_sub_tips_tv)
        TextView itemSubTipsTv;
        @BindView(R.id.item_download_info_tv)
        TextView itemDownloadInfoTv;
        @BindView(R.id.m_progress_bar)
        ZzHorizontalProgressBar mProgressBar;
        @BindView(R.id.item_progress_linear)
        RelativeLayout itemLinear;

        public DownloadingViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemDownloadDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (Integer) itemView.getTag()));
            }
        }
    }

}
