package wzp.com.texturemusic.util;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import wzp.com.texturemusic.MyApplication;
import wzp.com.texturemusic.interf.OnDialogListener;


/**
 * Created by Go_oG
 * Description:弹窗工具类
 * on 2017/10/4.
 */

public class ToastUtil {
    private static Toast toast;
    static {
        //toasy配置
        if (toast==null){
            toast=Toast.makeText(MyApplication.getInstace(),"",Toast.LENGTH_SHORT);
        }
    }

    public static void showErrorMsg(String msg) {
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }

    public static void showNormalMsg(String msg) {
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }

    public static void showLongNormalMsg(String msg) {
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText(msg);
        toast.show();
    }


    public static void showCustomDialog(Context context, String title, String content, final OnDialogListener listener) {
        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .autoDismiss(true)
                .cancelable(true)
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        listener.onResult(true);
                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        listener.onResult(false);
                    }
                })
                .build().show();
    }

    public static void showCustomDialog(Context context, String content, @Nullable final OnDialogListener listener) {
        new MaterialDialog.Builder(context)
                .content(content)
                .autoDismiss(true)
                .cancelable(true)
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (listener != null) {
                            listener.onResult(true);
                        }
                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (listener != null) {
                            listener.onResult(false);
                        }
                    }
                })
                .build()
                .show();
    }

}
