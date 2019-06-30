package wzp.com.texturemusic.playlistmodule.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commit451.nativestackblur.NativeStackBlur;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.PlayListBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.interf.OnImageLoadListener;
import wzp.com.texturemusic.playlistmodule.bean.PlayListHeadBean;
import wzp.com.texturemusic.util.FormatData;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * author:Go_oG
 * date: on 2018/5/10
 * packageName: wzp.com.texturemusic.playlistmodule.adapter
 */
public class IndexPlaylistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<PlayListBean> dataList;
    protected Context mContext;
    protected PublishSubject<ItemBean> itemClickSubject = PublishSubject.create();
    private PlayListHeadBean headPlaylist;
    private ItemClickListener clickListener;
    private String buttonText = "全部";

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public IndexPlaylistAdapter(Context c) {
        mContext = c;
        dataList = new ArrayList<>();
    }

    public PlayListHeadBean getHeadPlaylist() {
        return headPlaylist;
    }

    public void setHeadPlaylist(PlayListHeadBean headPlaylist) {
        this.headPlaylist = headPlaylist;
    }

    public List<PlayListBean> getDataList() {
        return dataList;
    }

    public void addDataList(List<PlayListBean> dataList) {
        this.dataList.addAll(dataList);
    }

    public void clearDataList() {
        this.dataList.clear();
    }

    @SuppressLint("CheckResult")
    public Observable<ItemBean> getItemClickSubject() {
        return itemClickSubject.throttleFirst(300, TimeUnit.MILLISECONDS)
                .filter(new Predicate<ItemBean>() {
                    @Override
                    public boolean test(ItemBean itemBean) throws Exception {
                        return itemBean != null && dataList != null && !dataList.isEmpty();
                    }
                });
    }

    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_fr_playlist_head_view, parent, false);
            return new PlayListHeadViewHolder(view);
        } else if (viewType == 1) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_fr_playlist_type_view, parent, false);
            return new PlayListTypeViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_fr_playlist_main_item, parent, false);
            return new PlayListContentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PlayListHeadViewHolder) {
            if (headPlaylist == null) {
                return;
            }
            final PlayListHeadViewHolder headViewHolder = (PlayListHeadViewHolder) holder;
            ImageUtil.loadImage(mContext, headPlaylist.getCoverImgUrl() + "?param=300y300", R.drawable.ic_large_album,
                    new OnImageLoadListener() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            headViewHolder.headImg.setImageBitmap(bitmap);
                            headViewHolder.itemRelative.setBackground(new BitmapDrawable(mContext.getResources
                                    (), NativeStackBlur.process(bitmap, 100)));
                        }
                    });
            headViewHolder.playlistName.setText(headPlaylist.getPlaylistName());
            headViewHolder.playlistDesc.setText(headPlaylist.getPlaylistDesc());
        } else if (holder instanceof PlayListTypeViewHolder) {
            ((PlayListTypeViewHolder) holder).typeBtn.setText(buttonText);
        } else if (holder instanceof PlayListContentViewHolder) {
            PlayListContentViewHolder contentViewHolder = (PlayListContentViewHolder) holder;
            int index = position - 2;
            contentViewHolder.itemView.setTag(index);
            PlayListBean bean = dataList.get(index);
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) contentViewHolder.itemLinear.getLayoutParams();
            if (index % 2 == 0) {
                params.setMarginEnd(2);
                params.setMarginStart(0);
            } else {
                params.setMarginStart(2);
                params.setMarginEnd(0);
            }
            contentViewHolder.itemLinear.setLayoutParams(params);
            ImageUtil.loadImage(contentViewHolder.img,
                    bean.getCoverImgUr() + AppConstant.WY_IMG_400_400, R.drawable.ic_large_album);
            contentViewHolder.playlistName.setText(bean.getPlaylistName());
            contentViewHolder.createrName.setText(bean.getCreaterName());
            contentViewHolder.playCount.setText(FormatData.longValueToString(bean.getPlayCount()));
        }

    }

    @Override
    public int getItemCount() {
        int size = dataList.size() + 1;
        if (headPlaylist != null) {
            size++;
        }
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == 1) {
            return 1;
        } else {
            return 2;
        }
    }

    class PlayListHeadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView headImg;
        private RelativeLayout itemRelative;
        private TextView playlistName, playlistDesc;

        public PlayListHeadViewHolder(View itemView) {
            super(itemView);
            headImg = itemView.findViewById(R.id.fr_playlist_head_img);
            itemRelative = itemView.findViewById(R.id.fr_playlist_head_item_relative);
            playlistDesc = itemView.findViewById(R.id.fr_playlist_head_desc);
            playlistName = itemView.findViewById(R.id.fr_playlist_head_listname);
            itemRelative.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.click(true, 0, null);
            }

        }
    }

    class PlayListTypeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView huayu, gufeng, qingMusic;
        private Button typeBtn;

        public PlayListTypeViewHolder(View itemView) {
            super(itemView);
            huayu = itemView.findViewById(R.id.fr_playlist_type_chinese);
            gufeng = itemView.findViewById(R.id.fr_playlist_type_antiquity);
            qingMusic = itemView.findViewById(R.id.fr_playlist_type_light_music);
            typeBtn = itemView.findViewById(R.id.fr_playlist_type_btn);
            huayu.setOnClickListener(this);
            gufeng.setOnClickListener(this);
            qingMusic.setOnClickListener(this);
            typeBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                if (v instanceof Button) {
                    clickListener.click(false, 0, ((TextView) v).getText().toString());
                    return;
                }
                if (v instanceof TextView) {
                    clickListener.click(false, 1, ((TextView) v).getText().toString());
                }
            }
        }

    }

    class PlayListContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView playCount, createrName, playlistName;
        private ImageView img;
        private LinearLayout itemLinear;

        public PlayListContentViewHolder(View itemView) {
            super(itemView);
            playCount = itemView.findViewById(R.id.fr_playlist_main_playcount);
            createrName = itemView.findViewById(R.id.fr_playlist_main_creatername);
            playlistName = itemView.findViewById(R.id.fr_playlist_main_listname);
            img = itemView.findViewById(R.id.fr_playlist_main_img);
            itemLinear = itemView.findViewById(R.id.fr_playlist_main_item);
            itemLinear.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }

        }
    }

    public interface ItemClickListener {
        void click(boolean isHead, int position, String tvContent);
    }

}
