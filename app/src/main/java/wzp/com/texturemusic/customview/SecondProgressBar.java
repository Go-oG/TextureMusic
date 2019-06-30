package wzp.com.texturemusic.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.util.BaseUtil;


/**
 * Created by Go_oG
 * Description:自定义进度条用于显示两种信息
 * on 2017/10/31.
 */

public class SecondProgressBar extends View {
    private OnProgressDragListener dragListener;
    private int defaultWidth;//默认宽度
    private int defaultHeight;//默认高度
    private int viewBackColor;//底层颜色也是背景颜色
    private int centerProgreeColor;//处于中间图层的颜色
    private int topProgressColor;//最顶层的进度条颜色
    private int pointColor;//画点的颜色
    private int viewHight;//view 的高度
    private int viewWidth;//view的宽度
    private int centerValue;//中间图层进度值
    private int centerMaxValue;//中间图层的最大值
    private int topValue;//最顶层进度值
    private int topMaxValue;//顶层图层的最大值
    private int progressHeight;//进度条的高度
    private Paint backgrountPaint;
    private Paint centerPaint;
    private Paint topPaint;
    private Paint pointPaint;//画点
    private boolean drawPoint = false;//标识是否要画点

    private ValueAnimator animator;

    public SecondProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public SecondProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SecondProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setDragListener(OnProgressDragListener dragListener) {
        this.dragListener = dragListener;
    }

    public int getViewBackColor() {
        return viewBackColor;
    }

    public void setViewBackColor(int backgroundColor) {
        this.viewBackColor = backgroundColor;
    }

    public int getCenterProgreeColor() {
        return centerProgreeColor;
    }

    public void setCenterProgreeColor(int centerProgreeColor) {
        this.centerProgreeColor = centerProgreeColor;
    }

    public int getTopProgressColor() {
        return topProgressColor;
    }

    public void setTopProgressColor(int topProgressColor) {
        this.topProgressColor = topProgressColor;
    }

    public int getPointColor() {
        return pointColor;
    }

    public void setPointColor(int pointColor) {
        this.pointColor = pointColor;
    }

    public int getViewHight() {
        return viewHight;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public int getCenterValue() {
        return centerValue;
    }

    public void setCenterValue(int centerValue) {
        this.centerValue = centerValue;
    }

    public int getCenterMaxValue() {
        return centerMaxValue;
    }

    public void setCenterMaxValue(int centerMaxValue) {
        this.centerMaxValue = centerMaxValue;
    }

    public int getTopValue() {
        return topValue;
    }

    public void setTopValue(int topValue) {
        this.topValue = topValue;
    }

    public int getTopMaxValue() {
        return topMaxValue;
    }

    public void setTopMaxValue(int topMaxValue) {
        this.topMaxValue = topMaxValue;
    }

    public int getProgressHeight() {
        return progressHeight;
    }

    public void setProgressHeight(int progressHeight) {
        this.progressHeight = progressHeight;
    }

    private void init(Context context, AttributeSet attrs) {
        backgrountPaint = new Paint();
        backgrountPaint.setStyle(Paint.Style.STROKE);
        backgrountPaint.setAntiAlias(true);
        backgrountPaint.setColor(viewBackColor);
        backgrountPaint.setStrokeWidth(progressHeight);

        centerPaint = new Paint();
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setAntiAlias(true);
        centerPaint.setColor(centerProgreeColor);
        centerPaint.setStrokeWidth(progressHeight);

        topPaint = new Paint();
        topPaint.setStyle(Paint.Style.FILL);
        topPaint.setAntiAlias(true);
        topPaint.setColor(topProgressColor);
        topPaint.setStrokeWidth(progressHeight);

        pointPaint = new Paint();
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(pointColor);
        pointPaint.setStrokeWidth(progressHeight + 8);

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SecondProgressBar);
            defaultWidth = array.getDimensionPixelSize(R.styleable.SecondProgressBar_defaultWidth, BaseUtil.dp2px(200));
            defaultHeight = array.getDimensionPixelSize(R.styleable.SecondProgressBar_defaultHeight, BaseUtil.dp2px(4));
            viewBackColor = array.getColor(R.styleable.SecondProgressBar_viewBackColor, Color.parseColor("#ffffffff"));
            centerProgreeColor = array.getColor(R.styleable.SecondProgressBar_centerProgressColor, Color.parseColor("#aa000000"));
            topProgressColor = array.getColor(R.styleable.SecondProgressBar_topProgressColor, Color.parseColor("#fff66762"));
            pointColor = array.getColor(R.styleable.SecondProgressBar_pointColor, Color.parseColor("#ff3F51B5"));
            centerValue = array.getInt(R.styleable.SecondProgressBar_centerValue, 0);
            centerMaxValue = array.getInt(R.styleable.SecondProgressBar_centerMaxValue, 100);
            topValue = array.getInt(R.styleable.SecondProgressBar_topValue, 0);
            topMaxValue = array.getInt(R.styleable.SecondProgressBar_topMaxValue, 100);
            progressHeight = array.getDimensionPixelSize(R.styleable.SecondProgressBar_progressHeight, BaseUtil.dp2px(4));
            array.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        cavasBackground(canvas);
        cavasCenterProgress(canvas);
        cavasTopProgress(canvas);
        if (dragListener != null) {
            dragListener.onProgressChange(topValue);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED://无限大小
                viewWidth = defaultWidth;
                break;
            case MeasureSpec.AT_MOST://包含自己
                viewWidth = defaultWidth;
                break;
            case MeasureSpec.EXACTLY://固定大小
                viewWidth = widthSize;
                break;
            default:
                break;
        }
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED://无限大小
                viewHight = defaultHeight;
                break;
            case MeasureSpec.AT_MOST://包含自己
                viewHight = defaultHeight;
                break;
            case MeasureSpec.EXACTLY://固定大小
                viewHight = heightSize;
                break;
            default:
                break;
        }
        if (viewWidth < viewHight) {
            viewWidth = viewHight;
        }
        setMeasuredDimension(viewWidth, viewHight);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x;
        boolean result = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getX();
                result = true;
                drawPoint = true;
                break;
            case MotionEvent.ACTION_MOVE:
                x = (int) event.getX();
                topValue = (int) (((float) x / (float) viewWidth) * topMaxValue);
                if (dragListener != null) {
                    int percent = (int) ((float) topValue / (float) topMaxValue) * 100;
                    dragListener.onDrag(percent);
                }
                drawPoint = true;
                break;
            case MotionEvent.ACTION_UP:
                x = (int) event.getX();
                topValue = (int) (((float) x / (float) viewWidth) * topMaxValue);
                drawPoint = false;
                break;
            default:
                result = super.onTouchEvent(event);
                drawPoint = false;
                break;
        }
        invalidate();
        return result;
    }

    private void cavasBackground(Canvas canvas) {
        canvas.drawRect(0, 0, viewWidth, viewHight, backgrountPaint);
    }

    private void cavasCenterProgress(Canvas canvas) {
        int w = (int) (((float) centerValue / (float) centerMaxValue) * viewWidth);
        canvas.drawRect(0, 0, w, viewHight, centerPaint);
    }

    private void cavasTopProgress(Canvas canvas) {
        int w = (int) (((float) topValue / (float) topMaxValue) * viewWidth);
        canvas.drawRect(0, 0, w, viewHight, topPaint);
        if (drawPoint) {
            canvas.drawCircle(w, viewHight / 2, (viewHight + BaseUtil.dp2px(4) / 2), pointPaint);
        }
    }

    interface OnProgressDragListener {
        void onDrag(int percent);

        void onProgressChange(int value);
    }


    public void startAnimation(long durationTime, int maxValue) {
        animator = new ValueAnimator();
        animator.setDuration(durationTime);
        animator.setIntValues(0, maxValue);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

            }
        });
    }

    public void stopAnimation() {
        if (animator != null) {
            animator.end();
            animator = null;
        }
    }

}
