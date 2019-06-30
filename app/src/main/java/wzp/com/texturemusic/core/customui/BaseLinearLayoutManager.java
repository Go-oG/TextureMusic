package wzp.com.texturemusic.core.customui;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * author:Go_oG
 * date: on 2018/5/11
 * packageName: wzp.com.texturemusic.core.customui
 * 让RecyclerView能够提前加载部分数据
 */
public class BaseLinearLayoutManager extends LinearLayoutManager {

    //让RecyclerView能够预先加载部分数据
    private int extraLayoutSpace = 300;

    public void setExtraLayoutSpace(int extraLayoutSpace) {
        this.extraLayoutSpace = extraLayoutSpace;
    }

    public BaseLinearLayoutManager(Context context) {
        super(context);
    }

    public BaseLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public BaseLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        return extraLayoutSpace;
    }

}
