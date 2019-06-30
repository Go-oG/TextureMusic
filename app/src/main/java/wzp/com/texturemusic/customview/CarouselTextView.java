package wzp.com.texturemusic.customview;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by wang on 2017/4/4.
 * 自动显示没有显示完的文本
 * 跑马灯效果
 */

public class CarouselTextView extends androidx.appcompat.widget.AppCompatTextView {
    public CarouselTextView(Context context) {
        super(context);
    }

    public CarouselTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CarouselTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
