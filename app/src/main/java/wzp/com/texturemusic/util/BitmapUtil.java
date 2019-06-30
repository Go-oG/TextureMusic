package wzp.com.texturemusic.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Wang on 2018/2/20.
 * 图片处理类
 */

public class BitmapUtil {
    /**
     * bitmap转byte[]
     *
     * @param bm
     * @return
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap butes2Bitmap(byte[] imgBytes) {
        if (imgBytes != null) {
            return BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        } else {
            return null;
        }
    }

    public static void saveBitmap(Bitmap bitmap, File file) throws IOException {
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);
        os.close();
        bitmap.recycle();
    }

    public static Drawable bitmapToDrawable(Context context,Bitmap bitmap) {
        return  new BitmapDrawable(context.getResources(),bitmap);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

}
