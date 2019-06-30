package wzp.com.texturemusic.common.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.common.adapter.LocalShowImgAdapter;
import wzp.com.texturemusic.core.ui.BaseActivity;

/**
 * Created by Go_oG
 * Description:专门用于显示大图片
 * on 2018/1/7.
 */

public class ShowImgActivity extends BaseActivity {
    public static final String INTENT_KEY_DATA="list";
    public static final String INTENT_KEY_INDEX="img_index";
    @BindView(R.id.m_viewpager)
    ViewPager mViewpager;
    @BindView(R.id.m_text)
    TextView mText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initService(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_img);
        setStatusBarHeight(0);
        ButterKnife.bind(this);

        List<String> urls = getIntent().getStringArrayListExtra(INTENT_KEY_DATA);
        int index = getIntent().getIntExtra(INTENT_KEY_INDEX, 0);
        LocalShowImgAdapter imgAdapter = new LocalShowImgAdapter(mContext, urls);
        mViewpager.setAdapter(imgAdapter);

        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String page = (position + 1) + "/" + mViewpager.getAdapter().getCount();
                mText.setText(page);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewpager.setCurrentItem(index, true);

    }

    @OnClick(R.id.save_cover_tv)
    public void onViewClicked() {
        //保存封面
    }
}
