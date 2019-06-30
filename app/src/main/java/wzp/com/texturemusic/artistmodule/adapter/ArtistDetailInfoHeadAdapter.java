package wzp.com.texturemusic.artistmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.core.adapter.BaseAdapterForVlayout;

public class ArtistDetailInfoHeadAdapter extends BaseAdapterForVlayout<ArtistBean, ArtistDetailInfoHeadAdapter.ArtistDetailInfoHeadViewholder> {

    private ArtistBean artistBean;

    public ArtistBean getArtistBean() {
        return artistBean;
    }

    public void setArtistBean(ArtistBean artistBean) {
        this.artistBean = artistBean;
    }

    public ArtistDetailInfoHeadAdapter(Context c) {
        super(c);
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public ArtistDetailInfoHeadViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_artistdetail_info_head, parent, false);
        return new ArtistDetailInfoHeadViewholder(view);
    }

    @Override
    public void onBindViewHolder(ArtistDetailInfoHeadViewholder holder, int position) {
        holder.mHeadText.setText(artistBean.getArtistName() + "简介");
        holder.mContentTv.setText(artistBean.getDecriptions());
    }

    @Override
    public int getItemCount() {
        if (artistBean != null) {
            return 1;
        }
        return 0;
    }

    @Override
    public Observable<ItemBean> getItemClickSubject() {
        return itemClickSubject.throttleFirst(clickTime, TimeUnit.MILLISECONDS)
                .filter(new Predicate<ItemBean>() {
                    @Override
                    public boolean test(ItemBean itemBean) throws Exception {
                        return artistBean != null;
                    }
                });
    }

    class ArtistDetailInfoHeadViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.m_head_text)
        TextView mHeadText;
        @BindView(R.id.m_content_tv)
        TextView mContentTv;
        @BindView(R.id.m_more_content_tv)
        TextView mMoreContentTv;

        public ArtistDetailInfoHeadViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mMoreContentTv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, 0));
            }
        }
    }

}
