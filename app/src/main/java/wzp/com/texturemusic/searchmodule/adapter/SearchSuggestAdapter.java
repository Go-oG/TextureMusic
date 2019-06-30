package wzp.com.texturemusic.searchmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;

/**
 * Created by Wang on 2018/2/22.
 * 搜索建议弹窗的适配器
 */

public class SearchSuggestAdapter extends BaseAdapter<String, SearchSuggestAdapter.SearchSuggestViewholder> {

    public SearchSuggestAdapter(Context c) {
        super(c);
    }

    @Override
    public SearchSuggestViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchSuggestViewholder(LayoutInflater.from(mContext).inflate(R.layout.item_pop_search_suggest, parent, false));
    }

    @Override
    public void onBindViewHolder(SearchSuggestViewholder holder, int position) {
        holder.itemView.setTag(position);
        holder.itemSuggestTv.setText(dataList.get(position));

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class SearchSuggestViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_suggest_tv)
        TextView itemSuggestTv;
        @BindView(R.id.item_search_suggest)
        RelativeLayout itemSearchSuggest;

        public SearchSuggestViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemSearchSuggest.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (Integer) itemView.getTag()));
            }

        }
    }

}
