package wzp.com.texturemusic.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wzp.com.texturemusic.R;


/**
 * Created by Wang on 2017/6/18.
 * 自定义导航textview
 */

public class CommonNavView extends RelativeLayout {
    private RelativeLayout item;
    private TextView contentText;
    private View leftView;
    private ImageView rightImg;
    private String textStr;
    private float textSize;
    private int imgSrc;
    private int leftViewColor;
    private int textColor;
    private int rightImgTint;

    public CommonNavView(Context context) {
        this(context, null, 0, 0);
    }

    public CommonNavView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public CommonNavView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CommonNavView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initTypedArray(context, attrs);
        initView(context);
    }

    private void initTypedArray(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonNavView);
        leftViewColor = mTypedArray.getColor(R.styleable.CommonNavView_leftViewColor, Color.BLACK);
        textColor = mTypedArray.getColor(R.styleable.CommonNavView_textColor, Color.BLACK);
        textSize = mTypedArray.getDimensionPixelSize(R.styleable.CommonNavView_textSize, 18);
        textStr = mTypedArray.getString(R.styleable.CommonNavView_contentText);
        imgSrc = mTypedArray.getResourceId(R.styleable.CommonNavView_rightImgSrc, R.drawable.ic_textview_goto);
        rightImgTint = mTypedArray.getColor(R.styleable.CommonNavView_rightImgTint, Color.WHITE);
        mTypedArray.recycle();
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.combina_navigation_text, this);
        contentText = findViewById(R.id.combina_navigationtext_text);
        leftView = findViewById(R.id.combina_navigationtext_view);
        rightImg = findViewById(R.id.combina_navigationtext_img);
        item = findViewById(R.id.combina_navigationtext_item);
        setLeftViewColor(leftViewColor);
        setContentText(textStr);
        setContentTextColor(textColor);
        setContentTextSize(textSize);
        setRightImgSrc(imgSrc);
        setRightImgTint(rightImgTint);
    }

    /**
     * 设置内容文本
     */
    public void setContentText(CharSequence text) {
        contentText.setText(text);
    }

    public void setContentTextColor(int color) {
        contentText.setTextColor(color);
    }

    public void setContentTextSize(float size) {
        contentText.setTextSize(size);
    }

    public void setLeftViewColor(int color) {
        leftView.setBackgroundColor(color);
    }

    public void setRightImgSrc(int resouceId) {
        rightImg.setImageResource(resouceId);
    }

    public void setRightImgSrc(Bitmap bitmap) {
        rightImg.setImageBitmap(bitmap);
    }

    public void setRightImgSrc(Drawable drawable) {
        rightImg.setImageDrawable(drawable);

    }

    public void hindRightImg() {
        rightImg.setVisibility(View.GONE);
    }

    public void showRightImg() {
        rightImg.setVisibility(View.VISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void setRightImgSrc(Icon icon) {
        rightImg.setImageIcon(icon);
    }

    public void setRightImgTint(int color) {
        ColorStateList colorStateList = ColorStateList.valueOf(color);
        rightImg.setImageTintList(colorStateList);
    }

    public void setOnClickListener(OnClickListener listener) {
        item.setOnClickListener(listener);
    }

}
