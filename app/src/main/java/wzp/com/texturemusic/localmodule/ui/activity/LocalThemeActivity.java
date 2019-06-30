package wzp.com.texturemusic.localmodule.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;

public class LocalThemeActivity extends BaseActivityWrapper {
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_theme);
        ButterKnife.bind(this);
        toolbarTitleTv.setText("个性换肤");
    }


    @OnClick({R.id.toolar_return_img, R.id.theme_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolar_return_img:
                finish();
                break;
            case R.id.theme_img:
                Intent intent = new Intent(mContext, SetThemeActivity.class);
                startActivity(intent);
                break;
        }
    }

}
