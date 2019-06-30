

package wzp.com.texturemusic.onekeyshare.themes.classic.land;

import java.util.ArrayList;

import wzp.com.texturemusic.onekeyshare.OnekeyShareThemeImpl;
import wzp.com.texturemusic.onekeyshare.themes.classic.PlatformPage;
import wzp.com.texturemusic.onekeyshare.themes.classic.PlatformPageAdapter;


/** 横屏的九宫格页面 */
public class PlatformPageLand extends PlatformPage {

	public PlatformPageLand(OnekeyShareThemeImpl impl) {
		super(impl);
	}

	public void onCreate() {
		requestLandscapeOrientation();
		super.onCreate();
	}

	protected PlatformPageAdapter newAdapter(ArrayList<Object> cells) {
		return new PlatformPageAdapterLand(this, cells);
	}

}

