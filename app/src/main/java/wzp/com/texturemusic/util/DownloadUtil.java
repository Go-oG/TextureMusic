package wzp.com.texturemusic.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import wzp.com.texturemusic.bean.DownloadBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.bean.MvBean;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.core.service.DownloadService;
import wzp.com.texturemusic.interf.OnDialogListener;


/**
 * Created by Go_oG
 * Description:
 * on 2017/12/2.
 */

public class DownloadUtil {
    public static void downloadPlaylist(final Context mContext, final List<MusicBean> list) {
        if (list==null||list.isEmpty()){
            return;
        }
        int size = SPSetingUtil.getIntValue(AppConstant.SP_KEY_DOWNLOAD_QUALITY, AppConstant.MUSIC_BITRATE_NORMAL);
        if (size == AppConstant.MUSIC_BITRATE_LOW) {
            size = list.size() * 4;
        } else if (size == AppConstant.MUSIC_BITRATE_NORMAL) {
            size = list.size() * 6;
        } else if (size == AppConstant.MUSIC_BITRATE_HIGHT) {
            size = list.size() * 10;
        } else {
            size = list.size()*30;
        }
        ToastUtil.showCustomDialog(mContext, "当前能下载" + list.size() + "首歌,预计需要" + size + "MB存储空间\n请问是否下载",
                new OnDialogListener() {
                    @Override
                    public void onResult(boolean success) {
                        if (success) {
                            if (!list.isEmpty()) {
                                Intent intent = new Intent(mContext, DownloadService.class);
                                Bundle bundle = new Bundle();
                                ArrayList<DownloadBean> downloadQueuq = new ArrayList<>();
                                for (MusicBean bean : list) {
                                    downloadQueuq.add(musicToDownloadEntiy(bean));
                                }
                                bundle.putParcelableArrayList(AppConstant.DOWNLOAD_QUEUE, downloadQueuq);
                                intent.putExtras(bundle);
                                mContext.startService(intent);
                                ToastUtil.showNormalMsg("开始下载歌曲");

                            } else {
                                ToastUtil.showNormalMsg("下载内容不能为空");
                            }
                        } else {
                            ToastUtil.showNormalMsg("已取消下载");
                        }
                    }
                });
    }

    public static void downloadMusic(Context context, MusicBean bean) {
        if (bean==null){
            return;
        }
        Intent intent = new Intent(context, DownloadService.class);
        Bundle bundle = new Bundle();
        ArrayList<DownloadBean> list = new ArrayList<>();
        list.add(musicToDownloadEntiy(bean));
        bundle.putParcelableArrayList(AppConstant.DOWNLOAD_QUEUE, list);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    public static void downloadMv(Context context, MvBean bean) {
        if (bean==null){
            return;
        }
        Intent intent = new Intent(context, DownloadService.class);
        Bundle bundle = new Bundle();
        ArrayList<DownloadBean> list = new ArrayList<>();
        list.add(mvToDownloadEntiy(bean));
        bundle.putParcelableArrayList(AppConstant.DOWNLOAD_QUEUE, list);
        intent.putExtras(bundle);
        context.startService(intent);
    }


    private static DownloadBean musicToDownloadEntiy(MusicBean bean) {

        DownloadBean downloadBean = new DownloadBean();
        downloadBean.setMvData(false);
        downloadBean.setMusicBean(bean);
        return downloadBean;
    }

    private static DownloadBean mvToDownloadEntiy(MvBean bean) {

        DownloadBean downloadBean = new DownloadBean();
        downloadBean.setMvData(true);
        downloadBean.setMvBean(bean);
        return downloadBean;
    }

}
