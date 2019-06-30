package wzp.com.texturemusic.common.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.KeyValueBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.common.adapter.MusicOperationAdapter;
import wzp.com.texturemusic.interf.OnMusicPopItemListener;

/**
 * Created by Go_oG
 * Description: 音乐详细操作的popwindow
 * on 2017/12/28.
 */

public class MusicOperationPopwindow extends PopupWindow {
    public static final int ITEM_TAG_NEXT_PLAY = 0;
    public static final int ITEM_TAG_COMMENT = 1;
    public static final int ITEM_TAG_SHARE = 2;
    public static final int ITEM_TAG_ARTIST = 3;
    public static final int ITEM_TAG_ALBUM = 4;
    public static final int ITEM_TAG_SEE_INFO = 5;
    public static final int ITEM_TAG_SET_ALARM = 6;
    public static final int ITEM_TAG_DELETE = 7;
    public static final int ITEM_TAG_MV = 8;
    public static final int ITEM_TAG_DOWNLOAD = 9;
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private Context mContext;
    private MusicBean musicBean;
    private LinearLayoutManager layoutManager;
    private MusicOperationAdapter adapter;
    private View rootView;

    public MusicOperationPopwindow(Context context, MusicBean bean, OnMusicPopItemListener listener) {
        mContext = context;
        this.musicBean = bean;
        adapter = new MusicOperationAdapter(context, bean, listener);
        List<KeyValueBean> list = new ArrayList<>();
        KeyValueBean nextBean = new KeyValueBean();
        nextBean.setTag(ITEM_TAG_NEXT_PLAY);
        nextBean.setValue("下一首播放");
        nextBean.setIntVal(R.drawable.ic_pop_item_next_play);
        list.add(nextBean);
        Boolean localMusic = musicBean.getLocalMusic();
        if (localMusic != null && !localMusic) {
            KeyValueBean downlaodBean = new KeyValueBean();
            downlaodBean.setTag(ITEM_TAG_DOWNLOAD);
            downlaodBean.setValue("下载");
            downlaodBean.setIntVal(R.drawable.ic_pop_item_download);
            list.add(downlaodBean);
            if (musicBean.getCommentCount() != null) {
                KeyValueBean commentBean = new KeyValueBean();
                commentBean.setTag(ITEM_TAG_COMMENT);
                commentBean.setValue("评论(" + musicBean.getCommentCount() + ")");
                commentBean.setIntVal(R.drawable.ic_pop_item_commont);
                list.add(commentBean);
            } else {
                if (!TextUtils.isEmpty(musicBean.getMusicId())) {
                    musicBean.setCommentId("R_SO_4_" + musicBean.getMusicId());
                    KeyValueBean commentBean = new KeyValueBean();
                    commentBean.setTag(ITEM_TAG_COMMENT);
                    commentBean.setValue("查看评论");
                    commentBean.setIntVal(R.drawable.ic_pop_item_commont);
                    list.add(commentBean);
                }
            }
        }

        KeyValueBean shareBean = new KeyValueBean();
        shareBean.setTag(ITEM_TAG_SHARE);
        shareBean.setValue("分享");
        shareBean.setIntVal(R.drawable.ic_pop_item_share);
        list.add(shareBean);

        KeyValueBean artistBean = new KeyValueBean();
        artistBean.setTag(ITEM_TAG_ARTIST);
        artistBean.setValue("歌手:" + musicBean.getArtistName());
        artistBean.setIntVal(R.drawable.ic_pop_item_artist);
        list.add(artistBean);

        KeyValueBean albumBean = new KeyValueBean();
        albumBean.setTag(ITEM_TAG_ALBUM);
        albumBean.setValue("专辑:" + musicBean.getAlbumName());
        albumBean.setIntVal(R.drawable.ic_pop_item_album);
        list.add(albumBean);
        if (localMusic != null && localMusic) {
            KeyValueBean seeBean = new KeyValueBean();
            seeBean.setTag(ITEM_TAG_SEE_INFO);
            seeBean.setValue("查看歌曲信息");
            seeBean.setIntVal(R.drawable.ic_pop_item_info);
            list.add(seeBean);

            KeyValueBean setBean = new KeyValueBean();
            setBean.setTag(ITEM_TAG_SET_ALARM);
            setBean.setValue("设为铃声");
            setBean.setIntVal(R.drawable.ic_pop_item_set_alarm);
            list.add(setBean);

            KeyValueBean delBean = new KeyValueBean();
            delBean.setTag(ITEM_TAG_DELETE);
            delBean.setValue("删除");
            delBean.setIntVal(R.drawable.ic_pop_item_delete);
            list.add(delBean);
        } else {
            Boolean hasMv = musicBean.getHasMV();
            if (hasMv != null && hasMv) {
                KeyValueBean mvBean = new KeyValueBean();
                mvBean.setTag(ITEM_TAG_MV);
                mvBean.setValue("查看MV");
                mvBean.setIntVal(R.drawable.ic_pop_item_mv);
                list.add(mvBean);
            }
        }
        adapter.addDataList(list);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rootView = LayoutInflater.from(mContext).inflate(R.layout.pop_music_operation, null);
        mRecyclerView = rootView.findViewById(R.id.m_recyclerview);
        mTextView = rootView.findViewById(R.id.music_name_tv);
        mTextView.setText("歌曲: " + bean.getMusicName());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(layoutManager);
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x80000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.MyPopAnnimation);
    }

    public MusicOperationPopwindow(Context context, List<KeyValueBean> datalist, MusicBean bean, OnMusicPopItemListener listener) {
        mContext = context;
        this.musicBean = bean;
        adapter = new MusicOperationAdapter(context, bean, listener);
        adapter.addDataList(datalist);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rootView = LayoutInflater.from(mContext).inflate(R.layout.pop_music_operation, null);
        mRecyclerView = rootView.findViewById(R.id.m_recyclerview);
        mTextView = rootView.findViewById(R.id.music_name_tv);
        mTextView.setText("歌曲: " + bean.getMusicName());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(layoutManager);
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
