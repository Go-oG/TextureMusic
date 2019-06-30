package wzp.com.texturemusic.util;

import android.content.Context;
import android.content.res.TypedArray;

/**
 * Created by Go_oG
 * Description:主题工具类
 * on 2018/2/6.
 */

public class ThemeUtil {
    public static int getThemeColor(Context context,int attrId, int defaultColor) {
        int mainColor;
        TypedArray array = context.obtainStyledAttributes(new int[]{attrId});
        mainColor = array.getColor(0, defaultColor);
        array.recycle();
        return mainColor;
    }

}
