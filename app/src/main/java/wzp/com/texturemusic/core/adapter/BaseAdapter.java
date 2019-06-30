package wzp.com.texturemusic.core.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

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
 * Created by Go_oG
 * Description:基类适配器
 * on 2017/10/30.
 */

public abstract class BaseAdapter<E, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected String TAG = this.getClass().getSimpleName();
    protected List<E> dataList;
    protected Context mContext;
    protected PublishSubject<ItemBean> itemClickSubject = PublishSubject.create();
    //主题的item_text_sub颜色
    protected int color;
    //点击事件的间隔 毫秒
    protected int clickTime = 300;

    public void setClickTime(int clickTime) {
        this.clickTime = clickTime;
    }

    @SuppressLint("CheckResult")
    public Observable<ItemBean> getItemClickSubject() {
        return itemClickSubject.throttleFirst(clickTime, TimeUnit.MILLISECONDS)
                .filter(itemBean -> itemBean != null && dataList != null && !dataList.isEmpty());
    }

    public BaseAdapter(Context c) {
        mContext = c;
        color = ThemeUtil.getThemeColor(c, R.attr.main_color, 0xff6e75a4);
        dataList = new ArrayList<>();
    }

    public List<E> getDataList() {
        return dataList;
    }

    public void addDataList(List<E> dataList) {
        this.dataList.addAll(dataList);
    }

    public void clearDataList() {
        this.dataList.clear();
    }

}
