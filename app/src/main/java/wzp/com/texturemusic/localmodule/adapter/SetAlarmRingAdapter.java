package wzp.com.texturemusic.localmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;

/**
 * Created by Go_oG
 * Description:
 * on 2017/12/25.
 */

public class SetAlarmRingAdapter extends BaseAdapter<MusicBean, SetAlarmRingAdapter.SetAlarmRingViewholder> {


    public SetAlarmRingAdapter(Context c) {
        super(c);
    }

    @Override
    public SetAlarmRingViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_set_alarm_ring, parent, false);
        return new SetAlarmRingViewholder(view);
    }

    @Override
    public void onBindViewHolder(SetAlarmRingViewholder holder, int position) {
        holder.itemView.setTag(position);
        holder.itemCheck.setChecked(dataList.get(position).getHasCheck());
        holder.itemTv.setText(dataList.get(position).getMusicName() + "-" + dataList.get(position).getArtistName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class SetAlarmRingViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_check)
        CheckBox itemCheck;
        @BindView(R.id.item_tv)
        TextView itemTv;

        public SetAlarmRingViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemCheck.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }
        }
    }

}
