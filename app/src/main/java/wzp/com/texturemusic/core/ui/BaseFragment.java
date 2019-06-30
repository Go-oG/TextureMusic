package wzp.com.texturemusic.core.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.trello.rxlifecycle2.components.support.RxFragment;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.util.ToastUtil;


/**
 * Created by wang on 2017/5/4.
 * 主要实现懒加载
 */

public abstract class BaseFragment extends RxFragment {
    protected String TAG = getClass().getSimpleName();
    protected boolean isInitView = false;
    private boolean isVisible = false;
    protected Context mContext;
    private View mRootVirtualView;
    private LinearLayout mRootFrame;
    private LottieAnimationView mStatusView;
    protected View mContentView;

    //用于标识 是否已经加载过了数据
    protected volatile boolean hasLoadData = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootVirtualView == null) {
            mRootVirtualView = inflater.inflate(R.layout.fragment_base, container, false);
        }
        mStatusView = mRootVirtualView.findViewById(R.id.m_error_view);
        mRootFrame = mRootVirtualView.findViewById(R.id.m_root_frame);//承载view的容器
        onCustomView(inflater, mRootFrame, savedInstanceState);//主体的显示的内容
        isInitView = true;
        if (isVisible) {
            lazyLoading();
        }
        return mRootVirtualView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInitView = false;
        isVisible = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mStatusView.getVisibility() == View.VISIBLE) {
            if (mStatusView.isAnimating()) {
                mStatusView.cancelAnimation();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (isVisible && isInitView) {
            lazyLoading();
        }
    }

    protected abstract void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    //懒加载
    //该方法保证其在每次滚动到Fragment时都会调用
    //已经避免了View还没有创建时就回调的NPE问题
    @SuppressLint("CheckResult")
    public void lazyLoading() {
    }

    protected void toast(String str) {
        if (!TextUtils.isEmpty(str)) {
            ToastUtil.showNormalMsg(str);
        }
    }

    public void showLoadingView(boolean hintContentView) {
        if (hintContentView) {
            mRootFrame.setVisibility(View.GONE);
        }
        if (mStatusView != null) {
            if (mStatusView.isAnimating()) {
                mStatusView.cancelAnimation();
            }
            mStatusView.setVisibility(View.VISIBLE);
            mStatusView.setAnimation("loading.json");
            mStatusView.loop(true);
            mStatusView.setScale(0.3F);
            mStatusView.playAnimation();
        }
    }

    public void cancleLoadingView(boolean showContentView) {
        if (mStatusView != null) {
            if (mStatusView.isAnimating()) {
                mStatusView.cancelAnimation();
            }
            mStatusView.setVisibility(View.GONE);
        }
        if (showContentView) {
            if (mRootFrame.getVisibility()!=View.VISIBLE){
                mRootFrame.setVisibility(View.VISIBLE);
            }
        }
    }

    protected int getMainColor() {
        int mainColor = Color.parseColor("#6e75a4");
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            TypedArray array = getActivity().obtainStyledAttributes(new int[]{
                    R.attr.main_color
            });
            mainColor = array.getColor(0, Color.parseColor("#6e75a4"));
            array.recycle();
        }
        return mainColor;
    }

    protected void showErrorView() {
        if (mStatusView != null) {
            if (mStatusView.isAnimating()) {
                mStatusView.cancelAnimation();
            }
            mRootFrame.setVisibility(View.GONE);
            mStatusView.setVisibility(View.VISIBLE);
            mStatusView.setAnimation("network_error.json");
            mStatusView.loop(true);
            mStatusView.setScale(1.0F);
            mStatusView.playAnimation();
        }
    }

    protected void hindErrorView() {
        if (mStatusView != null) {
            mStatusView.cancelAnimation();
            mStatusView.setVisibility(View.GONE);
            mRootFrame.setVisibility(View.VISIBLE);
        }
    }

    protected void showContentView() {
        if (mStatusView != null) {
            if (mStatusView.isAnimating()) {
                mStatusView.cancelAnimation();
            }
            mStatusView.setVisibility(View.GONE);
        }
        hindErrorView();
        mRootFrame.setVisibility(View.VISIBLE);
    }

    /**
     * 当网络发生改变时会回调该方法
     *
     * @param isConnected 网络是否为连接的
     * @param isVpn       是不是VPN连接
     */
    public void onNetworkChange(boolean isConnected, boolean isVpn) {
        if (isConnected && !hasLoadData && isVisible && isInitView) {
            showContentView();
            lazyLoading();
        }
    }

    protected void playMusic(MusicBean bean) {
        Activity activity = getActivity();
        if (activity != null && activity instanceof BaseActivity) {
            ((BaseActivity) activity).playMusic(bean);
        }

    }

}
