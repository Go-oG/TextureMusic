
package wzp.com.texturemusic.onekeyshare.themes.classic;

import android.content.Context;
import android.content.res.Configuration;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import wzp.com.texturemusic.onekeyshare.OnekeyShareThemeImpl;
import wzp.com.texturemusic.onekeyshare.themes.classic.land.EditPageLand;
import wzp.com.texturemusic.onekeyshare.themes.classic.land.PlatformPageLand;
import wzp.com.texturemusic.onekeyshare.themes.classic.port.EditPagePort;
import wzp.com.texturemusic.onekeyshare.themes.classic.port.PlatformPagePort;


/** 九宫格经典主题样式的实现类*/
public class ClassicTheme extends OnekeyShareThemeImpl {

	/** 展示平台列表*/

	// 两次弹出九宫格间隔不能少于1000毫秒
	private static final int MIN_CLICK_DELAY_TIME = 1000;
	private static long lastTime;

	protected void showPlatformPage(Context context) {
		PlatformPage page;
		int orientation = context.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			page = new PlatformPagePort(this);
		} else {
			page = new PlatformPageLand(this);
		}
		long currentTime = System.currentTimeMillis();
		if ((currentTime - lastTime) >= MIN_CLICK_DELAY_TIME) {
			page.show(context, null);
		}
		lastTime = currentTime;

	}

	/** 展示编辑界面*/
	protected void showEditPage(Context context, Platform platform, ShareParams sp) {
		EditPage page;
		int orientation = context.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			page = new EditPagePort(this);
		} else {
			page = new EditPageLand(this);
		}
		page.setPlatform(platform);
		page.setShareParams(sp);
		page.show(context, null);
	}

}
