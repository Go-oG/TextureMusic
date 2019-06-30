package wzp.com.texturemusic.common.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.interf.OnRecycleItemClickListener;

/**
 * Created by Go_oG
 * Description:mv播放界面的额菜单
 * on 2017/11/15.
 */

public class MvQualityPopwindow extends PopupWindow {
    private MvItemAdapter adapter;
    private OnRecycleItemClickListener itemClickListener;

    public void setItemClickListener(OnRecycleItemClickListener listener) {
        itemClickListener = listener;
    }


    public MvQualityPopwindow(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_mv, null);
        RecyclerView mRecycleview = view.findViewById(R.id.m_recycleview);
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable(0xff5b5d63));
        adapter = new MvItemAdapter(context);
        mRecycleview.setAdapter(adapter);
        mRecycleview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }

    public void setData(List<String> list) {
        adapter.clearDataList();
        adapter.addDataList(list);
        adapter.notifyDataSetChanged();
        this.update();
    }

    class MvItemAdapter extends BaseAdapter<String, MvItemAdapter.MvItemViewholder> {


        public MvItemAdapter(Context c) {
            super(c);
        }

        @Override
        public MvItemViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_pop_mv, parent, false);
            return new MvItemViewholder(view);
        }

        @Override
        public void onBindViewHolder(MvItemViewholder holder, final int position) {
            holder.itemTv.setText(dataList.get(position));
            holder.itemTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(v, position);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class MvItemViewholder extends RecyclerView.ViewHolder {
            TextView itemTv;

            public MvItemViewholder(View itemView) {
                super(itemView);
                itemTv = itemView.findViewById(R.id.item_tv);
            }
        }
    }

}
