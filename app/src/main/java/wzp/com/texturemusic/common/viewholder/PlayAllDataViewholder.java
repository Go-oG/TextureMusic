package wzp.com.texturemusic.common.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wzp.com.texturemusic.R;

/**
 * 用于添加播放全部的item
 */
public class PlayAllDataViewholder extends RecyclerView.ViewHolder {
    public ImageView playAllImg;
    public TextView infoTv;
    public LinearLayout muiltiChoiceLinear;
    public RelativeLayout itemSelect;

    public PlayAllDataViewholder(View itemView) {
        super(itemView);
        playAllImg = itemView.findViewById(R.id.item_select_play_all_img);
        infoTv = itemView.findViewById(R.id.item_info_tv);
        muiltiChoiceLinear = itemView.findViewById(R.id.item_select_operation_linear);
        itemSelect = itemView.findViewById(R.id.item_select_view);
    }
}
