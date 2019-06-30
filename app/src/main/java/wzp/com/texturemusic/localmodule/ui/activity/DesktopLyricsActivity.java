package wzp.com.texturemusic.localmodule.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;
import wzp.com.texturemusic.MyApplication;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:桌面歌词的实现
 * on 2017/11/29.
 */

public class DesktopLyricsActivity extends AppCompatActivity implements View.OnTouchListener {
    private View desktopView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions.request(Manifest.permission.SYSTEM_ALERT_WINDOW)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            initWindow();
//                            if (aBoolean) {
//                                if (ROMUtil.isMiui()) {
//                                    ToastUtil.showNormalMsg("你的手机为小米手机,请手动打开悬浮窗的额权限");
//                                }
//
//                            } else {
//                                ToastUtil.showNormalMsg("权限请求失败");
//                                Intent intent = new Intent(DesktopLyricsActivity.this, IndexActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ToastUtil.showNormalMsg("未知错误");
                            finish();
                        }
                    });
        } else {
            initWindow();
        }


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ToastUtil.showNormalMsg("点击了");
                break;
//            case MotionEvent.ACTION_UP:
//                //getRawX/Y 是获取相对于Device的坐标位置 注意区别getX/Y[相对于View]
//                layoutParams.y = (int) event.getRawY() - (desktopView.getHeight() / 2);
//                //更新"桌面歌词"的位置
//                windowManager.updateViewLayout(desktopView, layoutParams);
//                break;
            case MotionEvent.ACTION_MOVE:
                layoutParams.y = (int) event.getRawY() - (desktopView.getHeight() / 2);
                windowManager.updateViewLayout(desktopView, layoutParams);
                break;
        }
        return false;
    }

    private void initWindow() {
        windowManager = (WindowManager) MyApplication.getInstace().getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        desktopView = LayoutInflater.from(this).inflate(R.layout.common_desktop_lyrics_view, null);
        desktopView.setOnTouchListener(this);
        windowManager.addView(desktopView, layoutParams);
        finish();
    }

    private void removeView() {
        if (windowManager != null && desktopView != null) {
            windowManager.removeView(desktopView);
        }
    }

}
