package wzp.com.texturemusic.common.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.customview.CarouselTextView;
import wzp.com.texturemusic.interf.OnPopItemClick;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.StringUtil;

/**
 * Created by Go_oG
 * Description:本地音乐信息弹窗
 * 用于修改IDV3 Tag标签信息
 * on 2018/1/24.
 */

public class MusicInfoPopwindow extends PopupWindow  {
    private OnPopItemClick popItemClick;

    public void setPopItemClick(OnPopItemClick popItemClick) {
        this.popItemClick = popItemClick;
    }

    public MusicInfoPopwindow(Context context, MusicBean bean) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_music_info, null);
        TextView  musicNameTv = rootView.findViewById(R.id.dialog_music_name_tv);
        TextView editInfoTv = rootView.findViewById(R.id.edit_music_info_tv);
        TextView  fileNameTv = rootView.findViewById(R.id.dialog_file_name_tv);
        TextView  fileTimeTv = rootView.findViewById(R.id.dialog_file_duration_time_tv);
        TextView fileSizeTv = rootView.findViewById(R.id.dialog_file_size_tv);
        TextView filePathTv = rootView.findViewById(R.id.dialog_file_path_tv);
        musicNameTv.setText("歌曲：" + bean.getMusicName());
        fileTimeTv.setText(FormatData.timeValueToString(bean.getAllTime()));
        if (StringUtil.isEmpty(bean.getCoverImgUrl())) {
            bean.setCoverImgUrl("");
        }
        editInfoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popItemClick != null) {
                    popItemClick.popItemClick(0);
                    dismiss();
                }
            }
        });

        File file = new File(bean.getPlayPath());
        if (file.exists() && file.isFile()) {
            fileNameTv.setText(file.getName());
            fileSizeTv.setText(FormatData.fileSizeToString(file.length()));
            filePathTv.setText(file.getParent());
        }
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x80000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.MyPopAnnimation);
    }

}
