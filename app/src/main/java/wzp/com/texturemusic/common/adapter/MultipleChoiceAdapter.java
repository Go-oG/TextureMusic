package wzp.com.texturemusic.common.adapter;

import android.content.Context;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;

/**
 * Created by Go_oG
 * Description:
 * on 2018/1/25.
 */

public class MultipleChoiceAdapter extends BaseAdapter<MusicBean, MultipleChoiceAdapter.MultipleChoiceViewholder> {


    public MultipleChoiceAdapter(Context c) {
        super(c);
    }

    @Override
    public MultipleChoiceViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MultipleChoiceViewholder(LayoutInflater.from(mContext).inflate(R.layout.item_multipe_choice, parent, false));
    }

    @Override
    public void onBindViewHolder(final MultipleChoiceViewholder holder, int position) {
        final int index = position;
        MusicBean bean = dataList.get(position);
        holder.itemAlbumNameTv.setText(bean.getArtistName() + "-" + bean.getAlbumName());
        String name = bean.getMusicName();
        if (!TextUtils.isEmpty(bean.getAlias())) {
            name = name + "(" + bean.getAlias() + ")";
        }
        holder.itemMusicNameTv.setText(name);
        if (bean.getHasCheck() == null) {
            bean.setHasCheck(false);
        }
        holder.itemCheckBox.setChecked(bean.getHasCheck());
        holder.itemLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = holder.itemCheckBox.isChecked();
                holder.itemCheckBox.setChecked(!check);
                dataList.get(index).setHasCheck(!check);
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

    static class MultipleChoiceViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_check_box)
        AppCompatCheckBox itemCheckBox;
        @BindView(R.id.item_music_name_tv)
        TextView itemMusicNameTv;
        @BindView(R.id.item_album_name_tv)
        TextView itemAlbumNameTv;
        @BindView(R.id.item_linear)
        LinearLayout itemLinear;

        public MultipleChoiceViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
