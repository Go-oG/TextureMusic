package wzp.com.texturemusic.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.mob.MobSDK;

import cn.sharesdk.douban.Douban;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import wzp.com.texturemusic.BuildConfig;
import wzp.com.texturemusic.MyApplication;
import wzp.com.texturemusic.bean.ShareBean;
import wzp.com.texturemusic.onekeyshare.OnekeyShare;

/**
 * Created by Wang on 2017/6/28.
 * 分享操作工具类
 */

public final class ShareUtil {
    private static boolean isInitSDK = false;

    static {
        if (!isInitSDK) {
            MobSDK.init(MyApplication.getInstace(), BuildConfig.shareSdks1, BuildConfig.shareSdkS2);
            isInitSDK = true;
        }
    }

    public static void shareForWxFriend(ShareBean bean) {
        OnekeyShare oks = new OnekeyShare();
        oks.setPlatform(Wechat.NAME);
        oks.disableSSOWhenAuthorize();
        if (bean.getTitle() != null) {
            oks.setTitle(bean.getTitle());
        }
        if (bean.getText() != null) {
            oks.setText(bean.getText());
        }
        if (!TextUtils.isEmpty(bean.getImgpath())) {
            oks.setImagePath(bean.getImgpath());
        }
        if (!TextUtils.isEmpty(bean.getFileUrl())) {
            oks.setFilePath(bean.getFileUrl());
        }
        if (!TextUtils.isEmpty(bean.getImgUrl())) {
            oks.setImageUrl(bean.getImgUrl());
        }
        if (!TextUtils.isEmpty(bean.getWebUrl())) {
            oks.setUrl(bean.getWebUrl());
            oks.setTitleUrl(bean.getWebUrl());
        }
        oks.show(MyApplication.getInstace());
    }

    public static void shareForWxFriendCircle(ShareBean bean) {
        OnekeyShare oks = new OnekeyShare();
        oks.setPlatform(WechatMoments.NAME);
        oks.disableSSOWhenAuthorize();
        if (bean.getTitle() != null) {
            oks.setTitle(bean.getTitle());
        }
        if (bean.getText() != null) {
            oks.setText(bean.getText());
        }
        if (!TextUtils.isEmpty(bean.getImgpath())) {
            oks.setImagePath(bean.getImgpath());
        }
        if (!TextUtils.isEmpty(bean.getFileUrl())) {
            oks.setFilePath(bean.getFileUrl());
        }
        if (!TextUtils.isEmpty(bean.getImgUrl())) {
            oks.setImageUrl(bean.getImgUrl());
        }
        if (!TextUtils.isEmpty(bean.getWebUrl())) {
            oks.setUrl(bean.getWebUrl());
            oks.setTitleUrl(bean.getWebUrl());
        }
        oks.show(MyApplication.getInstace());
    }

    public static void shareForQQZone(ShareBean bean) {
        OnekeyShare oks = new OnekeyShare();
        oks.setPlatform(QZone.NAME);
        oks.disableSSOWhenAuthorize();
        if (bean.getTitle() != null) {
            oks.setTitle(bean.getTitle());
        }
        if (bean.getText() != null) {
            oks.setText(bean.getText());
        }
        if (!TextUtils.isEmpty(bean.getImgpath())) {
            oks.setImagePath(bean.getImgpath());
        }
        if (!TextUtils.isEmpty(bean.getFileUrl())) {
            oks.setFilePath(bean.getFileUrl());
        }
        if (!TextUtils.isEmpty(bean.getImgUrl())) {
            oks.setImageUrl(bean.getImgUrl());
        }
        if (!TextUtils.isEmpty(bean.getWebUrl())) {
            oks.setUrl(bean.getWebUrl());
            oks.setTitleUrl(bean.getWebUrl());
        }
        oks.show(MyApplication.getInstace());
    }

    public static void shareForQQFriend(ShareBean bean) {
        OnekeyShare oks = new OnekeyShare();
        oks.setPlatform(QQ.NAME);
        oks.disableSSOWhenAuthorize();
        if (bean.getTitle() != null) {
            oks.setTitle(bean.getTitle());
        }
        if (bean.getText() != null) {
            oks.setText(bean.getText());
        }
        if (!TextUtils.isEmpty(bean.getImgpath())) {
            oks.setImagePath(bean.getImgpath());
        }
        if (!TextUtils.isEmpty(bean.getFileUrl())) {
            oks.setFilePath(bean.getFileUrl());
        }
        if (!TextUtils.isEmpty(bean.getImgUrl())) {
            oks.setImageUrl(bean.getImgUrl());
        }
        if (!TextUtils.isEmpty(bean.getWebUrl())) {
            oks.setUrl(bean.getWebUrl());
            oks.setTitleUrl(bean.getWebUrl());
        }
        oks.show(MyApplication.getInstace());

    }

    //新浪微博
    public static void shareForWeibo(ShareBean bean) {
        OnekeyShare oks = new OnekeyShare();
        oks.setPlatform(SinaWeibo.NAME);
        oks.disableSSOWhenAuthorize();
        if (bean.getTitle() != null) {
            oks.setTitle(bean.getTitle());
        }
        if (bean.getText() != null) {
            oks.setText(bean.getText());
        }
        if (!TextUtils.isEmpty(bean.getImgpath())) {
            oks.setImagePath(bean.getImgpath());
        }
        if (!TextUtils.isEmpty(bean.getFileUrl())) {
            oks.setFilePath(bean.getFileUrl());
        }
        if (!TextUtils.isEmpty(bean.getImgUrl())) {
            oks.setImageUrl(bean.getImgUrl());
        }
        if (!TextUtils.isEmpty(bean.getWebUrl())) {
            oks.setUrl(bean.getWebUrl());
            oks.setTitleUrl(bean.getWebUrl());
        }
        oks.show(MyApplication.getInstace());

    }

    //豆瓣
    public static void shareForDb(ShareBean bean) {
        OnekeyShare oks = new OnekeyShare();
        oks.setPlatform(Douban.NAME);
        oks.disableSSOWhenAuthorize();
        if (bean.getTitle() != null) {
            oks.setTitle(bean.getTitle());
        }
        if (bean.getText() != null) {
            oks.setText(bean.getText());
        }
        if (!TextUtils.isEmpty(bean.getImgpath())) {
            oks.setImagePath(bean.getImgpath());
        }
        if (!TextUtils.isEmpty(bean.getFileUrl())) {
            oks.setFilePath(bean.getFileUrl());
        }
        if (!TextUtils.isEmpty(bean.getImgUrl())) {
            oks.setImageUrl(bean.getImgUrl());
        }
        if (!TextUtils.isEmpty(bean.getWebUrl())) {
            oks.setUrl(bean.getWebUrl());
            oks.setTitleUrl(bean.getWebUrl());
        }
        oks.show(MyApplication.getInstace());
    }

    public static void shareForIntent(Context context, ShareBean bean) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (bean.getTitle() != null) {
            intent.putExtra(Intent.EXTRA_TITLE, bean.getTitle());
        }
        if (bean.getText() != null) {
            intent.putExtra(Intent.EXTRA_TEXT, bean.getText());
        }
        context.startActivity(Intent.createChooser(intent, "请选择分享的平台"));
    }


}
