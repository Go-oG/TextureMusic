
package wzp.com.texturemusic.onekeyshare;

import com.mob.tools.FakeActivity;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;

/** 快捷分享的基类 */
public class OnekeySharePage extends FakeActivity {
	private OnekeyShareThemeImpl impl;

	public OnekeySharePage(OnekeyShareThemeImpl impl) {
		this.impl = impl;
	}

	/** 分享界面是否弹窗模式 */

	protected final boolean isDialogMode() {
		return impl.dialogMode;
	}

	protected final HashMap<String, Object> getShareParamsMap() {
		return impl.shareParamsMap;
	}

	/** 静默分享开关（没有界面，直接分享 ）*/
	protected final boolean isSilent() {
		return impl.silent;
	}

	protected final ArrayList<CustomerLogo> getCustomerLogos() {
		return impl.customerLogos;
	}

	protected final HashMap<String, String> getHiddenPlatforms() {
		return impl.hiddenPlatforms;
	}

	protected final PlatformActionListener getCallback() {
		return impl.callback;
	}

	protected final ShareContentCustomizeCallback getCustomizeCallback() {
		return impl.customizeCallback;
	}

	protected final boolean isDisableSSO() {
		return impl.disableSSO;
	}

	protected final void shareSilently(Platform platform) {
		impl.shareSilently(platform);
	}

	protected final ShareParams formateShareData(Platform platform) {
		if (impl.formateShareData(platform)) {
			return impl.shareDataToShareParams(platform);
		}
		return null;
	}

	protected final boolean isUseClientToShare(Platform platform) {
		return impl.isUseClientToShare(platform);
	}

}
