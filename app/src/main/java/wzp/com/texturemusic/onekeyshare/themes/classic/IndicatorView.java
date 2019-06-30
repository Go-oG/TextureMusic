
package wzp.com.texturemusic.onekeyshare.themes.classic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/** 九宫格滑动时，下面显示的圆圈 */
public class IndicatorView extends View {
	private static final int DESIGN_INDICATOR_RADIUS = 6;
	private static final int DESIGN_INDICATOR_DISTANCE = 14;
	private static final int DESIGN_BOTTOM_HEIGHT = 52;
	private Paint paint=new Paint();
	/** 九格宫有多少页数 */
	private int count;
	/** 当前显示的是九格宫中的第几页 */
	private int current;

	public IndicatorView(Context context) {
		super(context);
	}

	public void setScreenCount(int count) {
		this.count = count;
	}

	public void onScreenChange(int currentScreen, int lastScreen) {
		if (currentScreen != current) {
			current = currentScreen;
			postInvalidate();
		}
	}

	protected void onDraw(Canvas canvas) {
		if (count <= 1) {
			this.setVisibility(View.GONE);
			return;
		}
		float height = getHeight();
		float radius = height * DESIGN_INDICATOR_RADIUS / DESIGN_BOTTOM_HEIGHT;
		float distance = height * DESIGN_INDICATOR_DISTANCE / DESIGN_BOTTOM_HEIGHT;
		float windowWidth = radius * 2 * count + distance * (count - 1);
		float left = (getWidth() - windowWidth) / 2;
		float cy = height / 2;
		canvas.drawColor(0xffffffff);
		paint.setAntiAlias(true);
		for (int i = 0; i < count; i++) {
			if (i == current) {
				paint.setColor(0xff5d71a0);
			} else {
				paint.setColor(0xffafb1b7);
			}
			float cx = left + (radius * 2 + distance) * i;
			canvas.drawCircle(cx, cy, radius, paint);
		}
	}

}
