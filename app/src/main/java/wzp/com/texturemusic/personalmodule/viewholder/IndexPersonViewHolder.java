package wzp.com.texturemusic.personalmodule.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import wzp.com.texturemusic.R;

/**
 * Created by Go_oG
 * Description:首页界面的适配器
 * on 2017/9/17.
 */

public class IndexPersonViewHolder extends RecyclerView.ViewHolder {
    public TextView playCountTv, nameTv, descTv;
    public ImageView bkImg, playImg;
    public LinearLayout itemLinear;

    public IndexPersonViewHolder(View itemView) {
        super(itemView);
        playCountTv = itemView.findViewById(R.id.comment_index_playcount);
        nameTv =  itemView.findViewById(R.id.comment_index_artist_name);
        descTv =  itemView.findViewById(R.id.comment_index_info);
        bkImg =  itemView.findViewById(R.id.comment_index_img);
        playImg =  itemView.findViewById(R.id.comment_index_play_img);
        itemLinear =  itemView.findViewById(R.id.comment_index_item);
    }

}
