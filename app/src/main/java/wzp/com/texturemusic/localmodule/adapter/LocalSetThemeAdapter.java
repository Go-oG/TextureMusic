package wzp.com.texturemusic.localmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.ThemeBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:本地设置主题的适配器
 * on 2017/12/26.
 */

public class LocalSetThemeAdapter extends BaseAdapter<ThemeBean, LocalSetThemeAdapter.LocalSetThemeViewholder> {


    public LocalSetThemeAdapter(Context c) {
        super(c);
    }

    @Override
    public LocalSetThemeViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LocalSetThemeViewholder(LayoutInflater.from(mContext).inflate(R.layout.item_set_theme, parent, false));
    }

    @Override
    public void onBindViewHolder(LocalSetThemeViewholder holder, int position) {
        final int index = position;
        ThemeBean bean = dataList.get(position);
        if (bean.getCheck()) {
            holder.itemCheckImg.setVisibility(View.VISIBLE);
        } else {
            holder.itemCheckImg.setVisibility(View.GONE);
        }
        ImageUtil.loadImage(mContext,bean.getThemeImgResId(),holder.itemImg,bean.getThemeImgResId());

        holder.itemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickSubject != null) {
                    itemClickSubject.onNext(new ItemBean(v, index));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class LocalSetThemeViewholder extends RecyclerView.ViewHolder {
        ImageView itemImg, itemCheckImg;

        public LocalSetThemeViewholder(View itemView) {
            super(itemView);
            itemImg = itemView.findViewById(R.id.item_theme_img);
            itemCheckImg = itemView.findViewById(R.id.item_theme_check_img);
        }
    }
}
