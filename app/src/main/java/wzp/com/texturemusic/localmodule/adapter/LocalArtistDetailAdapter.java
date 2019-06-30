package wzp.com.texturemusic.localmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import io.reactivex.functions.Consumer;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description: LocalArtistDetailActivity的适配器
 * on 2017/12/25.
 */

public class LocalArtistDetailAdapter extends BaseAdapter<MusicBean, RecyclerView.ViewHolder> {
    private int ITEM_TYPE_ONE = 0;
    private int ITEM_TYPE_OTHER = 1;
    private LocalArtistDetailAdapterAdapter adapterAdapter;
    private RecyclerView.RecycledViewPool viewPool;


    public LocalArtistDetailAdapter(Context c) {
        super(c);
        adapterAdapter = new LocalArtistDetailAdapterAdapter(c);
        adapterAdapter.getItemClickSubject()
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        itemClickSubject.onNext(itemBean);
                    }
                });

        viewPool = new RecyclerView.RecycledViewPool();
    }

    public List<MusicBean> getDataList() {
        return dataList;
    }

    public void addDataList(List<MusicBean> dataList) {
        this.dataList.addAll(dataList);
        adapterAdapter.addDataList(dataList);
    }

    public void clearDataList() {
        dataList.clear();
        adapterAdapter.clearDataList();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_TYPE_ONE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_recycleview, parent, false);
            return new LocalArtistDetailOneViewholder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_local_songs, parent, false);
            return new LocalArtistDetailOtherViewholder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int index = position;
        if (holder instanceof LocalArtistDetailOneViewholder) {
            LocalArtistDetailOneViewholder oneViewholder = (LocalArtistDetailOneViewholder) holder;
            oneViewholder.itemRecycle.setAdapter(adapterAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, GridLayoutManager.HORIZONTAL, false);
            oneViewholder.itemRecycle.setLayoutManager(layoutManager);
            oneViewholder.itemRecycle.setRecycledViewPool(viewPool);
        } else {
            final LocalArtistDetailOtherViewholder otherViewholder = (LocalArtistDetailOtherViewholder) holder;
            otherViewholder.musicnameTv.setText(dataList.get(position - 1).getMusicName());
            otherViewholder.musicDescTv.setText(dataList.get(position - 1).getAlbumName() + "-" + dataList.get(position - 1).getArtistName());
            ImageUtil.loadImage(mContext,
                    dataList.get(position - 1).getCoverImgUrl(),
                    otherViewholder.coverImg,
                    R.drawable.ic_large_album);
            otherViewholder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickSubject != null) {
                        itemClickSubject.onNext(new ItemBean(v, index - 1));
                    }
                }
            });
            otherViewholder.operationImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickSubject != null) {
                        itemClickSubject.onNext(new ItemBean(v, index - 1));
                    }
                }
            });


        }

    }

    @Override
    public int getItemCount() {
        if (dataList.isEmpty()) {
            return 0;
        } else {
            return dataList.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position == 0 ? ITEM_TYPE_ONE : ITEM_TYPE_OTHER;
    }


    static class LocalArtistDetailOtherViewholder extends RecyclerView.ViewHolder {
        RelativeLayout item;
        ImageView coverImg, operationImg;
        TextView musicnameTv, musicDescTv;

        public LocalArtistDetailOtherViewholder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.local_songs_item);
            coverImg = itemView.findViewById(R.id.item_cover_img);
            operationImg = itemView.findViewById(R.id.item_operation_img);
            musicnameTv = itemView.findViewById(R.id.item_musicname_tv);
            musicDescTv = itemView.findViewById(R.id.item_musicdesc_tv);
        }

    }

    static class LocalArtistDetailOneViewholder extends RecyclerView.ViewHolder {
        RecyclerView itemRecycle;

        public LocalArtistDetailOneViewholder(View itemView) {
            super(itemView);
            itemRecycle = itemView.findViewById(R.id.item_recyclerview);
        }
    }


}
