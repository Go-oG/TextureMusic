package wzp.com.texturemusic.core.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.util.ThemeUtil;

/**
 * Created by Wang on 2018/3/1.
 * 基础适配器类
 * 专门用于VLayout
 */

public abstract class BaseAdapterForVlayout<D, VH extends RecyclerView.ViewHolder> extends DelegateAdapter.Adapter<VH> {
    protected String TAG = this.getClass().getSimpleName();
    protected List<D> dataList;
    protected Context mContext;
    protected PublishSubject<ItemBean> itemClickSubject = PublishSubject.create();
    //主题的item_text_sub颜色
    protected int color;
    //点击事件的间隔 毫秒
    protected int clickTime = 300;

    public void setClickTime(int clickTime) {
        this.clickTime = clickTime;
    }

    public Observable<ItemBean> getItemClickSubject() {
        return itemClickSubject.throttleFirst(clickTime, TimeUnit.MILLISECONDS)
                .filter(itemBean -> itemBean != null && dataList != null && !dataList.isEmpty());
    }

    public BaseAdapterForVlayout(Context c) {
        mContext = c;
        color = ThemeUtil.getThemeColor(mContext, R.attr.item_text_sub_color, 0xff000000);
        dataList = new ArrayList<>();
    }

    public List<D> getDataList() {
        return dataList;
    }

    public void addDataList(List<D> dataList) {
        this.dataList.addAll(dataList);
    }

    public void clearDataList() {
        this.dataList.clear();
    }

}
