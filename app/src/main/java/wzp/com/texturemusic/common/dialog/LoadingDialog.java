package wzp.com.texturemusic.common.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.airbnb.lottie.LottieAnimationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import wzp.com.texturemusic.R;

/**
 * Created by Go_oG
 * Description:加载等待视图
 * on 2017/11/8.
 */

public class LoadingDialog extends DialogFragment {
    @BindView(R.id.loading_lottie)
    LottieAnimationView loadingLottie;
    Unbinder unbinder;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.dialog_loading, container, false);
        }
        unbinder = ButterKnife.bind(this, view);
        showLoadingAnim();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.dimAmount = 0.0f;
                window.setAttributes(layoutParams);
            }
        }
    }

    public void showLoadingAnim() {
        loadingLottie.setAnimation("loading.json");
        loadingLottie.loop(true);
        loadingLottie.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });
        loadingLottie.addColorFilter(new PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP));
        loadingLottie.addColorFilterToLayer("layername", new PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP));
        loadingLottie.playAnimation();

    }

    public void cancleLoadingAnim() {
        if (loadingLottie != null && loadingLottie.isActivated()) {
            loadingLottie.cancelAnimation();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancleLoadingAnim();
        unbinder.unbind();
    }
}
