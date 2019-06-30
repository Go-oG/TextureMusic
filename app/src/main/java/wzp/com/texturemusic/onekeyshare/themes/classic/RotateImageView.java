package wzp.com.texturemusic.onekeyshare.themes.classic;

import android.content.Context;
import android.graphics.Canvas;
import androidx.appcompat.widget.AppCompatImageView;

/** 在At好友页面中，下拉刷新列表头部的旋转箭头 */
public class RotateImageView extends AppCompatImageView {
	private float rotation;

	public RotateImageView(Context context) {
		super(context);
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
		invalidate();
	}

	protected void onDraw(Canvas canvas) {
		canvas.rotate(rotation, getWidth() / 2, getHeight() / 2);
		super.onDraw(canvas);
	}

}
