package wzp.com.texturemusic.searchmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.KeyValueBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;

/**
 * Created by Wang on 2018/2/21.
 * 搜索历史的适配器
 */

public class SearchHistoryAdapter extends BaseAdapter<KeyValueBean, SearchHistoryAdapter.SearchHistoryViewholder> {


    public SearchHistoryAdapter(Context c) {
        super(c);
    }

    @Override
    public SearchHistoryViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchHistoryViewholder(LayoutInflater.from(mContext).inflate(R.layout.item_search_history, parent, false));
    }

    @Override
    public void onBindViewHolder(SearchHistoryViewholder holder, int position) {
        holder.itemView.setTag(position);
        holder.itemSearchTv.setText(dataList.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class SearchHistoryViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_history_img)
        ImageView itemHistoryImg;
        @BindView(R.id.item_close_img)
        ImageView itemCloseImg;
        @BindView(R.id.item_relative)
        RelativeLayout itemRelative;
        @BindView(R.id.item_search_tv)
        TextView itemSearchTv;

        public SearchHistoryViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemCloseImg.setOnClickListener(this);
            itemRelative.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                ItemBean bean = new ItemBean(v, (Integer) itemView.getTag());
                itemClickSubject.onNext(bean);
            }
        }
    }

}
