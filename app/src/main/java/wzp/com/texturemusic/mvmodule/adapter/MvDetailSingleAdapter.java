package wzp.com.texturemusic.mvmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import wzp.com.texturemusic.R;

/**
 * Created by Go_oG
 * Description:
 * on 2017/11/30.
 */

public class MvDetailSingleAdapter extends DelegateAdapter.Adapter<MvDetailSingleAdapter.MvDetailSingleViewholder> {

    public String title;
    private boolean hide = false;
    private Context mContext;

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public MvDetailSingleAdapter(Context c, String title) {
        mContext = c;
        this.title = title;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new SingleLayoutHelper();
    }

    @Override
    public MvDetailSingleViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mv_detail_single, parent, false);
        return new MvDetailSingleViewholder(view);
    }

    @Override
    public void onBindViewHolder(MvDetailSingleViewholder holder, int position) {
        holder.singleTitle.setText(title);
    }

    @Override
    public int getItemCount() {
        return hide ? 0 : 1;
    }

    class MvDetailSingleViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.single_title)
        TextView singleTitle;

        public MvDetailSingleViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
