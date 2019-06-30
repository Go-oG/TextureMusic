
package wzp.com.texturemusic.onekeyshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.mob.MobApplication;
import com.mob.MobSDK;
import com.mob.tools.utils.BitmapHelper;
import com.mob.tools.utils.ResHelper;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

/**
* 快捷分享的入口
* <p>
* 通过不同的setter设置参数，然后调用{@link #show(Context)}方法启动快捷分享
*/
public class OnekeyShare {
	private HashMap<String, Object> params;

	public OnekeyShare() {
		params = new HashMap<>();
		params.put("customers", new ArrayList<CustomerLogo>());
		params.put("hiddenPlatforms", new HashMap<String, String>());
	}

	/**
	 * title标题，在印象笔记、邮箱、信息、微信（包括好友、朋友圈和收藏）、
	 * 易信（包括好友、朋友圈）、人人网和QQ空间使用，否则可以不提供
	 */
	public void setTitle(String title) {
		params.put("title", title);
	}

	/** titleUrl是标题的网络链接，仅在人人网和QQ空间使用，否则可以不提供 */
	public void setTitleUrl(String titleUrl) {
		params.put("titleUrl", titleUrl);
	}

	/** text是分享文本，所有平台都需要这个字段 */
	public void setText(String text) {
		params.put("text", text);
	}

	/** 获取text字段的值 */
	public String getText() {
		return params.containsKey("text") ? String.valueOf(params.get("text")) : null;
	}

	/** imagePath是本地的图片路径，除Linked-In外的所有平台都支持这个字段 */
	public void setImagePath(String imagePath) {
		if(!TextUtils.isEmpty(imagePath)) {
			params.put("imagePath", imagePath);
		}
	}

	/** imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段 */
	public void setImageUrl(String imageUrl) {
		if (!TextUtils.isEmpty(imageUrl)) {
			params.put("imageUrl", imageUrl);
		}
	}

	/** imageData是bitmap图片，微信、易信支持此字段 */
	public void setImageData(String iamgeData) {
		if(!TextUtils.isEmpty(iamgeData)) {
			params.put("imageData", iamgeData);
		}
	}

	/** url在微信（包括好友、朋友圈收藏）和易信（包括好友和朋友圈）中使用，否则可以不提供 */
	public void setUrl(String url) {
		params.put("url", url);
	}

	/** filePath是待分享应用程序的本地路劲，仅在微信（易信）好友和Dropbox中使用，否则可以不提供 */
	public void setFilePath(String filePath) {
		params.put("filePath", filePath);
	}

	/** comment是我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供 */
	public void setComment(String comment) {
		params.put("comment", comment);
	}

	/** site是分享此内容的网站名称，仅在QQ空间使用，否则可以不提供 */
	public void setSite(String site) {
		params.put("site", site);
	}

	/** siteUrl是分享此内容的网站地址，仅在QQ空间使用，否则可以不提供 */
	public void setSiteUrl(String siteUrl) {
		params.put("siteUrl", siteUrl);
	}

	/** 设置编辑页的初始化选中平台 */
	public void setPlatform(String platform) {
		params.put("platform", platform);
	}

	/** 设置微信分享的音乐的地址 */
	public void setMusicUrl(String musicUrl) {
		params.put("musicUrl", musicUrl);
	}


	/** 设置一个总开关，用于在分享前若需要授权，则禁用sso功能 */
	public void disableSSOWhenAuthorize() {
		params.put("disableSSO", true);
	}

	/** 设置视频网络地址 */
	public void setVideoUrl(String url) {
		params.put("url", url);
		params.put("shareType", Platform.SHARE_VIDEO);
	}

	/** 设置一个将被截图分享的View , surfaceView是截不了图片的*/
	public void setViewToShare(View viewToShare) {
		try {
			Bitmap bm = BitmapHelper.captureView(viewToShare, viewToShare.getWidth(), viewToShare.getHeight());
			params.put("viewToShare", bm);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/** 设置分享界面的样式，目前只有一种，不需要设置 */
	public void setTheme(OnekeyShareTheme theme) {
		params.put("theme", theme.getValue());
	}

	@SuppressWarnings("unchecked")
	public void show(Context context) {
		HashMap<String, Object> shareParamsMap = new HashMap<String, Object>();
		shareParamsMap.putAll(params);

		if (!(context instanceof MobApplication)) {
			MobSDK.init(context.getApplicationContext());
		}

		// 打开分享菜单的统计
		ShareSDK.logDemoEvent(1, null);

		int iTheme = 0;
		try {
			iTheme = ResHelper.parseInt(String.valueOf(shareParamsMap.remove("theme")));
		} catch (Throwable t) {}
		OnekeyShareTheme theme = OnekeyShareTheme.fromValue(iTheme);
		OnekeyShareThemeImpl themeImpl = theme.getImpl();

		themeImpl.setShareParamsMap(shareParamsMap);
		themeImpl.setDialogMode(shareParamsMap.containsKey("dialogMode") ? ((Boolean) shareParamsMap.remove("dialogMode")) : false);
		themeImpl.setSilent(shareParamsMap.containsKey("silent") ? ((Boolean) shareParamsMap.remove("silent")) : false);
		themeImpl.setCustomerLogos((ArrayList<CustomerLogo>) shareParamsMap.remove("customers"));
		themeImpl.setHiddenPlatforms((HashMap<String, String>) shareParamsMap.remove("hiddenPlatforms"));
		themeImpl.setPlatformActionListener((PlatformActionListener) shareParamsMap.remove("callback"));
		themeImpl.setShareContentCustomizeCallback((ShareContentCustomizeCallback) shareParamsMap.remove("customizeCallback"));
		if (shareParamsMap.containsKey("disableSSO") ? ((Boolean) shareParamsMap.remove("disableSSO")) : false) {
			themeImpl.disableSSO();
		}

		themeImpl.show(context.getApplicationContext());
	}

}
