package wzp.com.texturemusic.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import wzp.com.texturemusic.util.BaseUtil;

/**
 * Created by Wang on 2018/3/14.
 */

public class CustomPath extends View {
    private static final String TAG = "CustomPath";
    private Paint mPointPaint;
    private Paint mPathPaint;
    private float viewHight = BaseUtil.dp2px(400);
    private float viewWidth = BaseUtil.dp2px(350);
    //圆的半径
    private float circleRadius = BaseUtil.dp2px(64);
    //圆心位置
    private float circleX = 0;
    private float circleY = 0;
    private float pointWidth = BaseUtil.dp2px(4);
    private byte[] mData;
    private int viewCount = 48;
    private int pointColor = 0xffF44336;

    public void setPointColor(int pointColor) {
        this.pointColor = pointColor;
    }

    public void setData(byte[] mData) {
        this.mData = mData;
        invalidate();
    }

    public CustomPath(Context context) {
        super(context);
        init();
    }

    public CustomPath(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomPath(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPointPaint = new Paint();
        mPointPaint.setStrokeWidth(12);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPathPaint = new Paint();
        mPathPaint.setStrokeWidth(pointWidth);
        mPathPaint.setStyle(Paint.Style.STROKE);

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (mData == null) {
            canvas.drawColor(0x00ffffff);
            return;
        }
        mPointPaint.setColor(pointColor);
        mPathPaint.setColor(pointColor);
        circleRadius = (viewHight -  BaseUtil.dp2px(16)) / 2f;
        circleX = viewWidth * 0.5f;
        if (circleX > viewWidth * 0.5f) {
            circleX = viewWidth * 0.5f;
        }
        circleY = viewHight * 0.5F;
        float radio2 = circleRadius / 2f;
        float x, y;
        byte val;
        //半径偏移量
        float rOffset;
        if (viewCount > mData.length) {
            viewCount = mData.length;
        }
        //间隔度数
        float hd = 270f / (float) viewCount * 0.5f;
        canvas.drawCircle(circleX, circleY, radio2, mPathPaint);
        canvas.translate(circleX, circleY);
        for (int i = 0; i < viewCount; i++) {
            val = mData[i];
            rOffset = ((float) val / 125f) * radio2;
            int sVal = BaseUtil.dp2px(12);
            if (rOffset >= 0) {
                if (rOffset > sVal) {
                    rOffset = sVal;
                }
            } else {
                if (rOffset < -sVal) {
                    rOffset = -sVal;
                }
            }
            x = (float) Math.cos(Math.toRadians(135 + (hd * 2 * i)));
            y = (float) Math.sin(Math.toRadians(135 + (hd * 2 * i)));
            canvas.drawPoint(x * (circleRadius + rOffset), y * (rOffset + circleRadius), mPointPaint);
        }
        mData = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (widthMode) {
            //固定大小
            case MeasureSpec.EXACTLY:
                viewWidth = widthSize;
                break;
            default:
                viewWidth = widthSize;
                break;
        }
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                viewHight = heightSize;
                break;
            default:
                viewHight = heightSize;
                break;
        }
        int min = (int) (viewHight > viewWidth ? viewWidth : viewHight);
        viewWidth = min;
        viewHight = min;
        setMeasuredDimension(min, min);
    }
}
