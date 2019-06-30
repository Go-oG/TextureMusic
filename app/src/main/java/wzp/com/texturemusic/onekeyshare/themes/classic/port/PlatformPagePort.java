

package wzp.com.texturemusic.onekeyshare.themes.classic.port;

import java.util.ArrayList;

import wzp.com.texturemusic.onekeyshare.OnekeyShareThemeImpl;
import wzp.com.texturemusic.onekeyshare.themes.classic.PlatformPage;
import wzp.com.texturemusic.onekeyshare.themes.classic.PlatformPageAdapter;


/** 竖屏的九宫格页面 */
public class PlatformPagePort extends PlatformPage {

	public PlatformPagePort(OnekeyShareThemeImpl impl) {
		super(impl);
	}

	public void onCreate() {
		requestPortraitOrientation();
		super.onCreate();
	}

	protected PlatformPageAdapter newAdapter(ArrayList<Object> cells) {
		return new PlatformPageAdapterPort(this, cells);
	}

}
