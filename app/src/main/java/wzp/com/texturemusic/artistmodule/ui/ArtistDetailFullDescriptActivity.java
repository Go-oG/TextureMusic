package wzp.com.texturemusic.artistmodule.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.artistmodule.adapter.ArtistDetailFullAdapter;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * 歌手完整介绍界面
 */
public class ArtistDetailFullDescriptActivity extends BaseActivityWrapper {
    public static final String INTENT_KEY_FULL_DESC = "artist_full_desc";
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;
    @BindView(R.id.m_recyclerView)
    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initService(false);
        super.onCreate(savedInstanceState);
        showBottomView(false);
        setContentView(R.layout.activity_artist_detail_full_descript);
        ButterKnife.bind(this);
        toolbarTitleTv.setText("歌手介绍");
        ArtistDetailFullAdapter fullAdapter = new ArtistDetailFullAdapter(mContext);
        List<ArtistBean.ArtistExperience> list = getIntent().getParcelableArrayListExtra(INTENT_KEY_FULL_DESC);
        if (list != null) {
            fullAdapter.addDataList(list);
            mRecyclerView.setAdapter(fullAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        } else {
            ToastUtil.showNormalMsg("暂无更多介绍");
        }
    }


    @OnClick(R.id.toolar_return_img)
    public void onViewClicked() {
        finish();
    }
}
