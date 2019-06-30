package wzp.com.texturemusic.common.popwindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import io.reactivex.functions.Consumer;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.common.adapter.PopPlayQueueAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.SPSetingUtil;

/**
 * Created by Go_oG
 * Description:音乐播放队列展示
 * on 2017/11/7.
 */

public class PlayQueuePopwindow extends PopupWindow implements View.OnClickListener {
    private PopPlayQueueAdapter adapter;
    private OnPopClickListener popClickListener;

    @SuppressLint("CheckResult")
    public PlayQueuePopwindow(Context context, List<MusicBean> list) {
        final View view = LayoutInflater.from(context).inflate(R.layout.pop_play_queue, null);
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        this.setHeight((displayMetrics.heightPixels * 2) / 3);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x80000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.MyPopAnnimation);
        TextView playModeTv = view.findViewById(R.id.pop_playmode_tv);
        TextView downloadAllTv = view.findViewById(R.id.pop_download_all);
        FrameLayout deleteFrame = view.findViewById(R.id.pop_delete_all);
        RecyclerView recyclerView = view.findViewById(R.id.pop_recycle);
        deleteFrame.setOnClickListener(this);
        playModeTv.setOnClickListener(this);
        downloadAllTv.setOnClickListener(this);
        adapter = new PopPlayQueueAdapter(context);
        adapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        if (popClickListener != null) {
                            popClickListener.clickRecycleItem(itemBean.getView(), itemBean.getPosition());
                        }
                        if (itemBean.getView().getId() != R.id.item_pop_close_img) {
                            dismiss();
                        }
                    }
                });

        adapter.addDataList(list);
        int playMode = SPSetingUtil.getIntValue(AppConstant.SP_KEY_PLAY_MODE, AppConstant.PLAY_MODE_LOOP);
        if (playMode == AppConstant.PLAY_MODE_SINGLE) {
            playModeTv.setText("单曲循环(" + list.size() + ")");
        } else if (playMode == AppConstant.PLAY_MODE_LOOP) {
            playModeTv.setText("列表循环(" + list.size() + ")");
        } else if (playMode == AppConstant.PLAY_MODE_RANDOM) {
            playModeTv.setText("随机播放(" + list.size() + ")");
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pop_delete_all:
                if (popClickListener != null) {
                    popClickListener.clickDeleteAll();
                }
                break;
            case R.id.pop_playmode_tv:
                if (popClickListener != null) {
                    popClickListener.clickPlayMode();
                }
                break;
            case R.id.pop_download_all:
                if (popClickListener != null) {
                    popClickListener.clickDownlaodAll(adapter.getDataList());
                }
                break;
        }
        dismiss();
    }

    public void setPopClickListener(OnPopClickListener popClickListener) {
        this.popClickListener = popClickListener;
    }

    public interface OnPopClickListener {
        void clickPlayMode();

        void clickDownlaodAll(List<MusicBean> list);

        void clickDeleteAll();

        void clickRecycleItem(View view, int position);


    }

    public void removeData(int position) {
        if (adapter != null && adapter.getDataList().size() > 0) {
            adapter.getDataList().remove(position);
            adapter.notifyDataSetChanged();
        }
    }

    public void updateUI(List<MusicBean> list) {
        adapter.clearDataList();
        adapter.addDataList(list);
        adapter.notifyDataSetChanged();
    }

    public List<MusicBean> getDataList() {
        return adapter.getDataList();
    }
}
