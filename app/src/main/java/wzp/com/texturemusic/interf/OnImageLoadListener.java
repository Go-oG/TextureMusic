package wzp.com.texturemusic.interf;

import android.graphics.Bitmap;

/**
 * Created by Wang on 2018/2/22.
 * 用于处理图片加载的回调
 */

public interface OnImageLoadListener {
    void onSuccess(Bitmap bitmap);
}
