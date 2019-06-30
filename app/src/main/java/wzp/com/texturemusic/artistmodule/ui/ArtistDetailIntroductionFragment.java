package wzp.com.texturemusic.artistmodule.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.ArtistApiManager;
import wzp.com.texturemusic.artistmodule.adapter.ArtistDetailInfoHeadAdapter;
import wzp.com.texturemusic.artistmodule.adapter.ArtistDetailInfoTopicAdapter;
import wzp.com.texturemusic.bean.ArtistBean;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.TopicBean;
import wzp.com.texturemusic.bean.UserBean;
import wzp.com.texturemusic.core.ui.BaseFragment;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:艺术家详细信息
 * on 2017/11/13.
 */
public class ArtistDetailIntroductionFragment extends BaseFragment {
    @BindView(R.id.m_recyclerView)
    RecyclerView mRecyclerView;
    Unbinder unbinder;
    private ArtistBean bean;
    private ArtistDetailInfoHeadAdapter headAdapter;
    private ArtistDetailInfoTopicAdapter topicAdapter;
    private DelegateAdapter adapter;
    private VirtualLayoutManager layoutManager;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bean = getArguments().getParcelable(ArtistDetailActivity.BUNDLE_KEY);
        headAdapter = new ArtistDetailInfoHeadAdapter(mContext);
        headAdapter.getItemClickSubject()
                .subscribe(itemBean -> {
                    Intent intent = new Intent(mContext, ArtistDetailFullDescriptActivity.class);
                    ArrayList<ArtistBean.ArtistExperience> list = (ArrayList<ArtistBean.ArtistExperience>) bean.getArtistExperienceList();
                    if (list != null && !list.isEmpty()) {
                        intent.putParcelableArrayListExtra(ArtistDetailFullDescriptActivity.INTENT_KEY_FULL_DESC, list);
                        startActivity(intent);
                    } else {
                        ToastUtil.showNormalMsg("暂无更多介绍");
                    }

                });
        topicAdapter = new ArtistDetailInfoTopicAdapter(mContext);
        topicAdapter.getItemClickSubject()
                .subscribe(itemBean -> {
                    int position = itemBean.getPosition();
                    String webUrl = topicAdapter.getDataList().get(position).getTopicUrl();
                    Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
                    it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                    getContext().startActivity(it);
                });

        layoutManager = new VirtualLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        adapter = new DelegateAdapter(layoutManager, true);
        List<DelegateAdapter.Adapter> adapterList = new ArrayList<>();
        adapterList.add(headAdapter);
        adapterList.add(topicAdapter);
        adapter.setAdapters(adapterList);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected void onCustomView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_artist_detail_info, container, true);
        }
        unbinder = ButterKnife.bind(this, mContentView);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(layoutManager);

    }

    @Override
    public void lazyLoading() {
        if (!hasLoadData) {
            if (!bean.getLocalArtist()) {
                loadData(bean.getArtistId());
            }
        }
    }

    @SuppressLint("CheckResult")
    private void loadData(String artistID) {
        ArtistApiManager
                .getArtistDescription(artistID)
                .map(new Function<String, ArtistBean>() {
                    @Override
                    public ArtistBean apply(String s) throws Exception {
                        return jsonToArtistEntiy(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<ArtistBean>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Consumer<ArtistBean>() {
                               @Override
                               public void accept(ArtistBean artistBean) throws Exception {
                                   hasLoadData = true;
                                   artistBean.setArtistName(bean.getArtistName());
                                   artistBean.setArtistId(bean.getArtistId());
                                   artistBean.setArtistImgUrl(bean.getArtistImgUrl());
                                   bean = artistBean;
                                   headAdapter.setArtistBean(artistBean);
                                   topicAdapter.addDataList(artistBean.getTopicList());
                                   headAdapter.notifyDataSetChanged();
                                   topicAdapter.notifyDataSetChanged();
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   showErrorView();
                               }
                           });

    }

    private ArtistBean jsonToArtistEntiy(String json) {
        ArtistBean artistBean = new ArtistBean();
        JSONObject rootObject = JSON.parseObject(json);
        if (rootObject.getIntValue("code") == 200) {
            JSONArray introduceArray = rootObject.getJSONArray("introduction");
            int size = rootObject.getIntValue("count");
            String shortDesc = rootObject.getString("briefDesc");
            JSONArray topicDataArray = rootObject.getJSONArray("topicData");
            artistBean.setDecriptions(shortDesc);
            int eventSize = introduceArray.size();
            if (eventSize > 0) {
                List<ArtistBean.ArtistExperience> list = new ArrayList<>();
                for (int i = 0; i < eventSize; i++) {
                    //详细信息
                    JSONObject item = introduceArray.getJSONObject(i);
                    ArtistBean.ArtistExperience experience = new ArtistBean.ArtistExperience();
                    experience.setTitle(item.getString("ti"));
                    experience.setInfo(item.getString("txt"));
                    list.add(experience);
                }
                artistBean.setArtistExperienceList(list);
            }
            //专栏文章
            List<TopicBean> topicBeanList = new ArrayList<>();
            if (topicDataArray != null) {
                int tSize = topicDataArray.size();
                if (tSize > 0) {
                    JSONObject itemObject;
                    for (int i = 0; i < tSize; i++) {
                        TopicBean bean = new TopicBean();
                        itemObject = topicDataArray.getJSONObject(i);
                        bean.setTopicId(itemObject.getJSONObject("topic").getLongValue("id"));
                        bean.setAddTime(itemObject.getJSONObject("topic").getLongValue("addTime"));
                        bean.setMainTitle(itemObject.getJSONObject("topic").getString("mainTitle"));
                        bean.setTitle(itemObject.getJSONObject("topic").getString("title"));
                        JSONArray contentArraay = itemObject.getJSONObject("topic").getJSONArray("content");
                        if (contentArraay != null && contentArraay.size() > 0) {
                            List<String> contentList = new ArrayList<>();
                            int siz = contentArraay.size();
                            for (int j = 0; j < siz; j++) {
                                String cs = contentArraay.getJSONObject(j).getString("content");
                                if (!TextUtils.isEmpty(cs)) {
                                    contentList.add(cs);
                                }
                            }
                            bean.setContentList(contentList);
                        }
                        bean.setReadCount(itemObject.getJSONObject("topic").getIntValue("readCount"));
                        bean.setStartText(itemObject.getJSONObject("topic").getString("startText"));
                        bean.setSummary(itemObject.getJSONObject("topic").getString("summary"));
                        bean.setNumber(itemObject.getJSONObject("topic").getIntValue("number"));
                        bean.setShareCount(itemObject.getIntValue("shareCount"));
                        bean.setCommentCount(itemObject.getIntValue("commentCount"));
                        bean.setLikeCount(itemObject.getInteger("likedCount"));
                        bean.setCoverUrl(itemObject.getString("coverUrl"));
                        bean.setCommentId(itemObject.getString("commentThreadId"));
                        bean.setTopicUrl("http://music.163.com" + itemObject.getString("url"));
                        UserBean userBean = new UserBean();
                        userBean.setSignnature(itemObject.getJSONObject("creator").getString("signature"));
                        userBean.setUserCoverImgUrl(itemObject.getJSONObject("creator").getString("avatarUrl"));
                        userBean.setNickName(itemObject.getJSONObject("creator").getString("nickname"));
                        userBean.setUserId(itemObject.getJSONObject("creator").getLongValue("userId"));
                        userBean.setGender(itemObject.getJSONObject("creator").getIntValue("gender"));
                        bean.setCreator(userBean);
                        topicBeanList.add(bean);
                    }
                }
            }
            artistBean.setTopicList(topicBeanList);
        }
        return artistBean;
    }

}
