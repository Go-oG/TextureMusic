package wzp.com.texturemusic.common.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.dbmodule.bean.DbCollectPlaylistEntiy;
import wzp.com.texturemusic.interf.OnPopItemClick;

/**
 * Created by Wang on 2018/3/11.
 */

public class CollectPopwindow extends PopupWindow implements View.OnClickListener {
    private View rootView;
    private TextView musicNameTv;
    private LinearLayout itemDelete, itemShare;
    private OnPopItemClick popItemClick;

    public void setPopItemClick(OnPopItemClick popItemClick) {
        this.popItemClick = popItemClick;
    }

    public CollectPopwindow(Context context, MusicBean bean) {
        rootView = LayoutInflater.from(context).inflate(R.layout.pop_collect, null);
        musicNameTv = rootView.findViewById(R.id.popt_item_music_name_tv);
        itemShare = rootView.findViewById(R.id.item_share_linear);
        itemDelete = rootView.findViewById(R.id.item_delete_linear);
        itemShare.setOnClickListener(this);
        itemDelete.setOnClickListener(this);
        musicNameTv.setText("歌曲：" + bean.getMusicName());
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x80000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.MyPopAnnimation);
    }

    public CollectPopwindow(Context context, DbCollectPlaylistEntiy playlist) {
        rootView = LayoutInflater.from(context).inflate(R.layout.pop_collect, null);
        musicNameTv = rootView.findViewById(R.id.popt_item_music_name_tv);
        itemShare = rootView.findViewById(R.id.item_share_linear);
        itemDelete = rootView.findViewById(R.id.item_delete_linear);
        itemShare.setOnClickListener(this);
        itemDelete.setOnClickListener(this);
        musicNameTv.setText("歌单：" + playlist.getPlaylistName());
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x80000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.MyPopAnnimation);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (popItemClick == null) {
            return;
        }
        if (v.getId() == R.id.item_share_linear) {
            popItemClick.popItemClick(0);
        } else {
            popItemClick.popItemClick(1);
        }

    }

}
