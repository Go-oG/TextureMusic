package wzp.com.texturemusic.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Go_oG
 * Description:竖直柱状图
 * on 2017/11/2.
 */
public class MusicVerticalFFTView extends View {
    //用于调节柱状图的高度与view高度的比值 值越小view越高
    private static final float PROPORTION = 0.4f;
    private float rectSpace = 8;//柱状图间隔
    private float rectWidth = 24;//柱状图宽度
    private boolean isTwoRect = true;
    private float viewWidth;
    private float viewHeight;
    private float defaultWidth = 300;
    private float defaultHeight = 120;
    private byte[] fftData;
    private Paint rectPaint;
    private Paint bkPaint;
    private LinearGradient hLinearGradilent;
    //用来存储颜色的渐变值
    private int[] colorData = new int[]{
            Color.parseColor("#F44336"),
            Color.parseColor("#e91e63"),
            Color.parseColor("#673ab7"),
    };
    //柱状图的数量
    private int viewCount = 0;

    public MusicVerticalFFTView(Context context) {
        super(context);
        init(context, null);
    }

    public MusicVerticalFFTView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MusicVerticalFFTView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet set) {
        rectPaint = new Paint();
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setAntiAlias(true);
        bkPaint = new Paint();
        bkPaint.setAntiAlias(true);
        bkPaint.setAntiAlias(true);
        PorterDuffXfermode  fftDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        bkPaint.setXfermode(fftDuffXfermode);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (fftData == null || fftData.length < 10) {
            canvas.drawColor(0x00ffffff);
            return;
        }
        viewCount = (int) (viewWidth / (rectSpace + rectWidth));
        int saved = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        hLinearGradilent = new LinearGradient(0, 0, viewWidth, viewHeight, colorData, null, Shader.TileMode.CLAMP);
        if (fftData.length < viewCount) {
            viewCount = fftData.length;
            rectSpace = (viewWidth / viewCount) - rectWidth;
        }
        float top;
        float bottom;
        float left;
        float right;
        byte val;
        int rectHeight;
        float centerY = viewHeight / 2f;
        float realWidth = rectSpace + rectWidth;
        for (int i = 0; i < viewCount; i++) {
            val = (byte) Math.abs(fftData[i]);
            rectHeight = (int) ((val / (256 * PROPORTION)) * viewHeight / 2f);
            if (rectHeight > centerY) {
                rectHeight = (int) centerY;
            }
            left = i * realWidth;
            right = i * realWidth + rectWidth;
            if (isTwoRect) {
                //双层
                top = centerY - rectHeight;
                bottom = centerY + rectHeight;
            } else {
                //单层
                top = viewHeight - rectHeight;
                bottom = viewHeight;
            }
            canvas.drawRect(left, top, right, bottom, rectPaint);
        }
        bkPaint.setShader(hLinearGradilent);
        canvas.drawRect(0, 0, viewWidth, viewHeight, bkPaint);
        canvas.restoreToCount(saved);
        fftData = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (widthMode) {
            //包含自己
            case MeasureSpec.AT_MOST:
                viewWidth = defaultWidth;
                break;
            //固定大小
            case MeasureSpec.EXACTLY:
                viewWidth = widthSize;
                break;
            default:
                viewWidth = widthSize;
                break;
        }
        switch (heightMode) {
            //包含自己
            case MeasureSpec.AT_MOST:
                viewHeight = defaultHeight;
                break;
            //固定大小
            case MeasureSpec.EXACTLY:
                viewHeight = heightSize;
                break;
            default:
                viewHeight = heightSize;
                break;
        }
        setMeasuredDimension((int) viewWidth, (int) viewHeight);
    }

    public void setFftData(byte[] fftData) {
        this.fftData = fftData;
        invalidate();
    }

    public void setColorData(int[] colorData) {
        this.colorData = colorData;
        invalidate();
    }

    public void setRectSpace(float rectSpace) {
        this.rectSpace = rectSpace;
    }

    public void setRectWidth(float rectWidth) {
        this.rectWidth = rectWidth;
    }

}
