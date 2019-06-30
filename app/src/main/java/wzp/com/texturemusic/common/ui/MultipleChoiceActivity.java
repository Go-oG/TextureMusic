package wzp.com.texturemusic.common.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.common.adapter.MultipleChoiceAdapter;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.util.DownloadUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:音乐多选界面
 * on 2018/1/24.
 */
public class MultipleChoiceActivity extends BaseActivityWrapper {
    public static final String INTENT_DATA_LIST = "intent_data_list";
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;
    @BindView(R.id.m_recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.toolbar_right_tv)
    TextView toolbarRightTv;
    private MultipleChoiceAdapter adapter;
    private boolean allChoice = true;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBottomView(false);
        initService(true);
        setContentView(R.layout.activity_multiple_choice);
        ButterKnife.bind(this);
        toolbarRightTv.setVisibility(View.VISIBLE);
        toolbarRightTv.setText("全选");
        ArrayList<MusicBean> dataList = getIntent().getParcelableArrayListExtra(INTENT_DATA_LIST);
        adapter = new MultipleChoiceAdapter(mContext);
        adapter.getItemClickSubject().subscribe(itemBean -> {
            int size = getChoiceData().size();
            toolbarTitleTv.setText("已选择" + size + "项");
        });
        adapter.addDataList(dataList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.setLayoutManager(layoutManager);
        toolbarTitleTv.setText("已选择" + getChoiceData().size() + "项");
    }

    @OnClick({R.id.toolar_return_img, R.id.next_play_linear, R.id.now_play_linear, R.id.download_linear,
            R.id.toolbar_right_tv})
    public void onViewClicked(View view) {
        ArrayList<MusicBean> dataList;
        switch (view.getId()) {
            case R.id.toolar_return_img:
                finish();
                break;
            case R.id.next_play_linear:
                dataList = getChoiceData();
                if (dataList.size() == 0) {
                    ToastUtil.showNormalMsg("请选择歌曲");
                } else {
                    addNextMusic(dataList);
                    ToastUtil.showNormalMsg("已添加到下一曲播放");
                }
                break;
            case R.id.now_play_linear:
                //立即播放
                dataList = getChoiceData();
                if (dataList.size() == 0) {
                    ToastUtil.showNormalMsg("请选择歌曲");
                } else {
                    addPlayQueueData(dataList);
                    playMusic(dataList.get(0));
                    ToastUtil.showNormalMsg("马上播放歌曲");
                }
                break;
            case R.id.download_linear:
                //下载歌曲
                dataList = getChoiceData();
                if (dataList.size() == 0) {
                    ToastUtil.showNormalMsg("请选择歌曲");
                } else {
                    DownloadUtil.downloadPlaylist(mContext, dataList);
                }
                break;
            case R.id.toolbar_right_tv:
                //全选or全不选
                int size = adapter.getDataList().size();
                if (allChoice) {
                    allChoice = false;
                    toolbarRightTv.setText("取消全选");
                    for (int i = 0; i < size; i++) {
                        adapter.getDataList().get(i).setHasCheck(true);
                    }
                    adapter.notifyDataSetChanged();
                    toolbarTitleTv.setText("已选择" + size + "项");
                } else {
                    allChoice = true;
                    toolbarRightTv.setText("全选");
                    for (int i = 0; i < size; i++) {
                        adapter.getDataList().get(i).setHasCheck(false);
                    }
                    adapter.notifyDataSetChanged();
                    toolbarTitleTv.setText("已选择" + 0 + "项");
                }
                break;
        }
    }

    private ArrayList<MusicBean> getChoiceData() {
        ArrayList<MusicBean> dataList = new ArrayList<>();
        for (MusicBean bean : adapter.getDataList()) {
            Boolean b = bean.getHasCheck();
            if (b != null && b) {
                dataList.add(bean);
            }
        }
        return dataList;
    }
}
