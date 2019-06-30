package wzp.com.texturemusic.common.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:用于在用户动态的界面的图片适配器
 * on 2017/11/27.
 */

public class ImgAdapter extends BaseAdapter<String, ImgAdapter.ImgViewHolder> {

    public ImgAdapter(Context c) {
        super(c);
    }

    @Override
    public ImgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.commont_item_img, parent, false);
        return new ImgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImgViewHolder holder, int position) {
        final int index = position;
        ImageUtil.loadImage(mContext,
                dataList.get(position) + AppConstant.WY_IMG_300_300,
                holder.imageView);
        if (itemClickSubject != null) {
            holder.rootFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickSubject.onNext(new ItemBean(v, index));
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ImgViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        FrameLayout rootFrame;

        public ImgViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_child_img);
            rootFrame = itemView.findViewById(R.id.item_parent_frame);
        }
    }
}
