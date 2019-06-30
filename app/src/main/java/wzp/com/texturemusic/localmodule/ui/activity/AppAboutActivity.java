package wzp.com.texturemusic.localmodule.ui.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.util.StringUtil;

/**
 * Created by Go_oG
 * Description:对APP的简介界面
 * on 2017/9/17.
 */

public class AppAboutActivity extends BaseActivityWrapper {
    @BindView(R.id.app_content_tv)
    TextView appContentTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initService(false);
        super.onCreate(savedInstanceState);
        showBottomView(false);
        setContentView(R.layout.activity_app_about);
        ButterKnife.bind(this);
        appContentTv.setMovementMethod(LinkMovementMethod.getInstance());
        StringUtil.removeUnderlines((Spannable)appContentTv.getText());

    }

    @OnClick(R.id.m_return_img)
    public void onViewClicked() {
        finish();
    }
}
