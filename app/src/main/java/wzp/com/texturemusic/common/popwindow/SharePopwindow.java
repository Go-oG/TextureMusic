package wzp.com.texturemusic.common.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.interf.OnRecycleItemClickListener;

/**
 * Created by Go_oG
 * Description:分享popwindow
 */

public class SharePopwindow extends PopupWindow implements View.OnClickListener {

    private OnRecycleItemClickListener itemClickListener;

    public void setItemClickListener(OnRecycleItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public SharePopwindow(Context c) {
        View view = LayoutInflater.from(c).inflate(R.layout.pop_share_view, null);
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x80000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.MyPopAnnimation);
        LinearLayout mItemWxFrends = view.findViewById(R.id.share_wx_frends_circle);
        LinearLayout mItemWx = view.findViewById(R.id.share_wx_friends);
        LinearLayout mItemQQZone = view.findViewById(R.id.share_qq_zone);
        LinearLayout mItemQQ = view.findViewById(R.id.share_qq);
        LinearLayout  mItemWB = view.findViewById(R.id.share_wb);
        LinearLayout mItemDB = view.findViewById(R.id.share_db);
        LinearLayout  mItemMore = view.findViewById(R.id.share_more);
        mItemWxFrends.setOnClickListener(this);
        mItemWx.setOnClickListener(this);
        mItemQQZone.setOnClickListener(this);
        mItemQQ.setOnClickListener(this);
        mItemWB.setOnClickListener(this);
        mItemDB.setOnClickListener(this);
        mItemMore.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_wx_frends_circle:
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, 0);
                }
                break;
            case R.id.share_wx_friends:
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, 1);
                }
                break;
            case R.id.share_qq_zone:
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, 2);
                }
                break;
            case R.id.share_qq:
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, 3);
                }
                break;
            case R.id.share_wb:
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, 4);
                }
                break;
            case R.id.share_db:
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, 5);
                }
                break;
            case R.id.share_more:
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, 6);
                }
                break;
        }
        dismiss();
    }

}
