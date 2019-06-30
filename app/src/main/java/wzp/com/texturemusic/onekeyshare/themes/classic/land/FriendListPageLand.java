
package wzp.com.texturemusic.onekeyshare.themes.classic.land;


import com.mob.tools.utils.ResHelper;

import wzp.com.texturemusic.onekeyshare.OnekeyShareThemeImpl;
import wzp.com.texturemusic.onekeyshare.themes.classic.FriendListPage;

/** 横屏的好友列表 */
public class FriendListPageLand extends FriendListPage {
	private static final int DESIGN_SCREEN_WIDTH = 1280;
	private static final int DESIGN_TITLE_HEIGHT = 70;

	public FriendListPageLand(OnekeyShareThemeImpl impl) {
		super(impl);
	}

	protected float getRatio() {
		float screenWidth = ResHelper.getScreenWidth(activity);
		return screenWidth / DESIGN_SCREEN_WIDTH;
	}

	protected int getDesignTitleHeight() {
		return DESIGN_TITLE_HEIGHT;
	}

}
