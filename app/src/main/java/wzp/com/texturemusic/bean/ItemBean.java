package wzp.com.texturemusic.bean;

import android.view.View;

/**
 * Created by Go_oG
 * Description:用于存储列表的点击事件的对象与位置
 * on 2018/2/4.
 */

public class ItemBean {
    private View view;
    private Integer position;

    public ItemBean() {
    }

    public ItemBean(View view, Integer position) {
        this.view = view;
        this.position = position;
    }

    public ItemBean(View view) {
        this.view = view;
    }

    public ItemBean(Integer position) {
        this.position = position;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }


}
