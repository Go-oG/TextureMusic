package wzp.com.texturemusic.artistmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;

public class ArtistDetailFullAdapter extends BaseAdapter<ArtistBean.ArtistExperience, ArtistDetailFullAdapter.ArtistDetailFullViewholder> {

    public ArtistDetailFullAdapter(Context c) {
        super(c);
    }

    @Override
    public ArtistDetailFullViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_artist_full_description, parent, false);
        return new ArtistDetailFullViewholder(view);
    }

    @Override
    public void onBindViewHolder(ArtistDetailFullViewholder holder, int position) {
        holder.navText.setText(dataList.get(position).getTitle());
        holder.mContentTv.setText(dataList.get(position).getInfo());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ArtistDetailFullViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.nav_text)
        TextView navText;
        @BindView(R.id.m_content_tv)
        TextView mContentTv;

        public ArtistDetailFullViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
