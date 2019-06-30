package wzp.com.texturemusic.common.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.util.BaseUtil;


/**
 * Created by Wang on 2017/6/19.
 * 该类用作填充布局
 */

public class PaddingViewAdapater extends DelegateAdapter.Adapter<PaddingViewAdapater.PersonUtilViewHolder> {
    private Context context;

    public PaddingViewAdapater(Context context) {
        this.context = context;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        SingleLayoutHelper singleLayoutHelper = new SingleLayoutHelper();
        singleLayoutHelper.setItemCount(1);
        singleLayoutHelper.setMarginTop(BaseUtil.dp2px(2));
        return singleLayoutHelper;
    }

    @Override
    public PersonUtilViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.util_view, parent, false);
        return new PersonUtilViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PersonUtilViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class PersonUtilViewHolder extends RecyclerView.ViewHolder {
        public PersonUtilViewHolder(final View itemView) {
            super(itemView);
        }
    }
}
