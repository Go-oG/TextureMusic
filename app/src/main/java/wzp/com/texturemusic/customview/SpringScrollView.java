package wzp.com.texturemusic.customview;

import android.content.Context;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.core.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Wang on 2018/2/21.
 * 带回弹的滚动控件
 */

public class SpringScrollView extends NestedScrollView {
    private float startDragY;
    private SpringAnimation springAnimation;

    public SpringScrollView(Context context) {
        this(context, null);
    }

    public SpringScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpringScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        springAnimation = new SpringAnimation(this, SpringAnimation.TRANSLATION_Y, 0);
        //刚度 默认1200 值越大回弹的速度越快
        springAnimation.getSpring().setStiffness(800.0f);
        //阻尼 默认0.5 值越小，回弹之后来回的次数越多
        springAnimation.getSpring().setDampingRatio(0.50f);

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (getScrollY() <= 0) {
                    //顶部下拉
                    if (startDragY == 0) {
                        startDragY = e.getRawY();
                    }
                    if (e.getRawY() - startDragY >= 0) {
                        setTranslationY((e.getRawY() - startDragY) / 3);
                        return true;
                    } else {
                        startDragY = 0;
                        springAnimation.cancel();
                        setTranslationY(0);
                    }
                } else if ((getScrollY() + getHeight()) >= getChildAt(0).getMeasuredHeight()) {
                    //底部上拉
                    if (startDragY == 0) {
                        startDragY = e.getRawY();
                    }
                    if (e.getRawY() - startDragY <= 0) {
                        setTranslationY((e.getRawY() - startDragY) / 3);
                        return true;
                    } else {
                        startDragY = 0;
                        springAnimation.cancel();
                        setTranslationY(0);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (getTranslationY() != 0) {
                    springAnimation.start();
                }
                startDragY = 0;
                break;
        }
        return super.onTouchEvent(e);
    }

}
