package wzp.com.texturemusic.searchmodule;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.flexbox.AlignContent;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewEditorActionEvent;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.api.WYApiUtil;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.KeyValueBean;
import wzp.com.texturemusic.core.ui.BaseActivityWrapper;
import wzp.com.texturemusic.dbmodule.util.DbSearchHistoryUtil;
import wzp.com.texturemusic.searchmodule.adapter.SearchHistoryAdapter;
import wzp.com.texturemusic.searchmodule.adapter.SearchPageAdapter;
import wzp.com.texturemusic.searchmodule.adapter.SearchSuggestAdapter;
import wzp.com.texturemusic.searchmodule.listener.OnSearchListener;
import wzp.com.texturemusic.searchmodule.ui.SearchAlbumFragment;
import wzp.com.texturemusic.searchmodule.ui.SearchArtistFragment;
import wzp.com.texturemusic.searchmodule.ui.SearchDjFragment;
import wzp.com.texturemusic.searchmodule.ui.SearchMvFragment;
import wzp.com.texturemusic.searchmodule.ui.SearchPlayListFragment;
import wzp.com.texturemusic.searchmodule.ui.SearchSongFragment;
import wzp.com.texturemusic.searchmodule.ui.SearchUserFragment;
import wzp.com.texturemusic.util.KeyBoardUtil;
import wzp.com.texturemusic.util.StringUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Go_oG
 * Description:搜索
 * on 2017/9/15.
 */
public class SearchActivity extends BaseActivityWrapper {
    private static final String MAP_KEY_HOT = "map_key_hot";
    private static final String MAP_KEY_HIISTORY = "map_key_history";
    @BindView(R.id.ac_search_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.ac_search_edit)
    EditText mSearchEdit;
    @BindView(R.id.ac_search_viewpager)
    ViewPager mViewpager;
    @BindView(R.id.ac_search_break_img)
    ImageView breakImg;
    @BindView(R.id.m_main_content_linear)
    LinearLayout mMainContentLinear;
    @BindView(R.id.m_flexbox_layout)
    FlexboxLayout mFlexboxLayout;
    @BindView(R.id.m_hot_search_recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.show_history_linear)
    LinearLayout mHistoryLinear;
    @BindView(R.id.ac_search_linear)
    LinearLayout mSearchLinear;
    @BindView(R.id.m_start_search_tv)
    TextView mStartSearchTv;
    @BindView(R.id.m_suggest_recyclever)
    RecyclerView mSuggestRecyclever;
    @BindView(R.id.m_suggest_linear)
    CardView mSuggestLinear;
    @BindView(R.id.m_root_linear)
    LinearLayout mRootLinear;
    private List<Fragment> fragmentList;
    private SearchHistoryAdapter historyAdapter;
    private SearchSuggestAdapter suggestAdapter;

    /////////////
    //是否显示搜索建议
    private boolean showSearchSuggest = true;
    //是否允许搜索
    private boolean allowSearch = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBottomView(true);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initListener();
        init();
        loadData();
    }

    @SuppressLint("CheckResult")
    private void initListener() {
        RxTextView.editorActionEvents(mSearchEdit)
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<TextViewEditorActionEvent>() {
                    @Override
                    public boolean test(TextViewEditorActionEvent actionEvent) throws Exception {
                        int actionId = actionEvent.actionId();
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }).map(new Function<TextViewEditorActionEvent, String>() {
            @Override
            public String apply(TextViewEditorActionEvent actionEvent) throws Exception {
                return actionEvent.view().getText().toString();
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                boolean result = TextUtils.isEmpty(s);
                return !result;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        allowSearch = true;
                        showSearchSuggest = false;
                        startSearch(s);
                    }
                });

        RxTextView.textChanges(mSearchEdit)
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<CharSequence>() {
                    @Override
                    public boolean test(CharSequence sequence) throws Exception {
                        String str = sequence.toString();
                        boolean isEmpty = TextUtils.isEmpty(str);
                        return (!isEmpty);
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<CharSequence, ObservableSource<List<String>>>() {
                    @Override
                    public ObservableSource<List<String>> apply(CharSequence str) throws Exception {
                        return loadSuggest(str.toString());
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> list) throws Exception {
                        if (allowSearch && !showSearchSuggest) {
                            startSearch(mSearchEdit.getText().toString());
                        } else if ((!allowSearch) && showSearchSuggest) {
                            showSearchSuggest(mSearchEdit.getText().toString(), list);
                        }
                    }
                });
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String text = mSearchEdit.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    allowSearch = true;
                    showSearchSuggest = false;
                    startSearch(text);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        breakImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressLint("CheckResult")
    private void init() {
        fragmentList = new ArrayList<>();
        SearchPageAdapter pageAdapter = new SearchPageAdapter(getSupportFragmentManager());
        SearchSongFragment songFragment = new SearchSongFragment();
        SearchArtistFragment artistFragment = new SearchArtistFragment();
        SearchAlbumFragment albumFragment = new SearchAlbumFragment();
        SearchPlayListFragment playListFragment = new SearchPlayListFragment();
        SearchMvFragment mvFragment = new SearchMvFragment();
        SearchDjFragment djFragment = new SearchDjFragment();
        SearchUserFragment userFragment = new SearchUserFragment();
        fragmentList.add(songFragment);
        fragmentList.add(artistFragment);
        fragmentList.add(albumFragment);
        fragmentList.add(playListFragment);
        fragmentList.add(mvFragment);
        fragmentList.add(djFragment);
        fragmentList.add(userFragment);
        pageAdapter.setFragmentList(fragmentList);
        mViewpager.setAdapter(pageAdapter);
        mTablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTablayout.setupWithViewPager(mViewpager);
        mMainContentLinear.setVisibility(View.GONE);
        mHistoryLinear.setVisibility(View.VISIBLE);
        historyAdapter = new SearchHistoryAdapter(mContext);
        historyAdapter.getItemClickSubject()
                .compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        View view = itemBean.getView();
                        int position = itemBean.getPosition();
                        String s = historyAdapter.getDataList().get(position).getValue();
                        if (view.getId() == R.id.item_close_img) {
                            DbSearchHistoryUtil.deleteHistory(s);
                            historyAdapter.getDataList().remove(position);
                            historyAdapter.notifyDataSetChanged();
                        } else {
                            if (mSearchEdit != null) {
                                allowSearch = false;
                                showSearchSuggest = true;
                                mSearchEdit.setText(s);
                            }
                        }
                    }
                });
        mRecyclerview.setAdapter(historyAdapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mFlexboxLayout.setFlexWrap(FlexWrap.WRAP);
        mFlexboxLayout.setAlignContent(AlignContent.FLEX_START);
        mSearchEdit.setFocusable(true);
        suggestAdapter = new SearchSuggestAdapter(mContext);
        suggestAdapter.setClickTime(20);
        suggestAdapter.getItemClickSubject().compose(this.<ItemBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        if (mSearchEdit != null) {
                            String startStr = mSearchEdit.getText().toString();
                            String endStr = suggestAdapter.getDataList().get(itemBean.getPosition());
                            if (!startStr.equals(endStr)) {
                                allowSearch = true;
                                showSearchSuggest = false;
                                mSearchEdit.setText(endStr);
                            }
                        }
                    }
                });
        mSuggestRecyclever.setAdapter(suggestAdapter);
        mSuggestRecyclever.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @OnClick({R.id.m_start_search_tv, R.id.m_main_content_linear, R.id.show_history_linear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_start_search_tv:
                startSearch(mSearchEdit.getText().toString());
                break;
            case R.id.m_main_content_linear:
            case R.id.show_history_linear:
                if (mSuggestLinear != null && mSuggestLinear.getVisibility() == View.VISIBLE) {
                    hindSuggestWindow();
                }
                break;
        }
    }

    /**
     * 复写该方法解决当有软键盘弹出时无法
     * 在onBackPressed中获得返回事件的bug
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //有输入法
            if (mSuggestLinear != null && mSuggestLinear.getVisibility() == View.VISIBLE) {
                KeyBoardUtil.hideSoftInput(mSearchEdit);
                hindSuggestWindow();
                return true;
            } else {
                return super.dispatchKeyEvent(event);
            }
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @SuppressLint("CheckResult")
    private void loadData() {
        Observable.zip(loadHotSearchData(), loadSearchHistorData(),
                new BiFunction<List<KeyValueBean>, List<KeyValueBean>, Map<String, List<KeyValueBean>>>() {
                    @Override
                    public Map<String, List<KeyValueBean>> apply(List<KeyValueBean> list1, List<KeyValueBean> list2) throws Exception {
                        Map<String, List<KeyValueBean>> map = new HashMap<>();
                        map.put(MAP_KEY_HOT, list1);
                        map.put(MAP_KEY_HIISTORY, list2);
                        return map;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Map<String, List<KeyValueBean>>>() {
                    @Override
                    public void accept(Map<String, List<KeyValueBean>> map) throws Exception {
                        historyAdapter.addDataList(map.get(MAP_KEY_HIISTORY));
                        historyAdapter.notifyDataSetChanged();
                        refreshHotView(map.get(MAP_KEY_HOT));

                    }
                });
    }

    private void startSearch(String s) {
        if (!TextUtils.isEmpty(s)) {
            mSuggestLinear.setVisibility(View.GONE);
            mHistoryLinear.setVisibility(View.GONE);
            mMainContentLinear.setVisibility(View.VISIBLE);
            for (Fragment searchFragment : fragmentList) {
                ((OnSearchListener) searchFragment).onSearch(s);
            }
            DbSearchHistoryUtil.addHistoy(s);
        } else {
            ToastUtil.showNormalMsg("搜索不能为空");
        }
    }

    public String getSearchStr() {
        if (mSearchEdit != null) {
            return mSearchEdit.getText().toString();
        } else {
            return "";
        }
    }

    private void refreshHotView(List<KeyValueBean> list) {
        int i = 0;
        for (final KeyValueBean bean : list) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_hot, null);
            TextView textView = view.findViewById(R.id.m_hot_search_tv);
            textView.setText(bean.getValue());
            textView.setTag(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSearchEdit != null) {
                        allowSearch = false;
                        showSearchSuggest = true;
                        mSearchEdit.setText(bean.getValue());
                    }
                }
            });
            FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
            mFlexboxLayout.addView(view);
            i++;
        }
    }

    private Observable<List<KeyValueBean>> loadHotSearchData() {
        return WYApiUtil.getInstance()
                .buildSearchService()
                .getHotSearch()
                .map(new Function<String, List<KeyValueBean>>() {
                    @Override
                    public List<KeyValueBean> apply(String s) throws Exception {
                        List<KeyValueBean> list = new ArrayList<>();
                        if (!StringUtil.isEmpty(s)) {
                            JSONObject object = JSONObject.parseObject(s);
                            if (object.getIntValue("code") == 200) {
                                JSONArray array = object.getJSONObject("result").getJSONArray("hots");
                                if (array != null && array.size() > 0) {
                                    int size = array.size();
                                    for (int i = 0; i < size; i++) {
                                        KeyValueBean bean = new KeyValueBean();
                                        bean.setValue(array.getJSONObject(i).getString("first"));
                                        list.add(bean);
                                    }
                                }
                            }
                        }
                        return list;
                    }
                });
    }

    private Observable<List<KeyValueBean>> loadSearchHistorData() {
        return Observable.create(new ObservableOnSubscribe<List<KeyValueBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<KeyValueBean>> e) throws Exception {
                List<String> list = DbSearchHistoryUtil.getAllHistory();
                List<KeyValueBean> beanList = new ArrayList<>();
                for (String string : list) {
                    KeyValueBean bean = new KeyValueBean();
                    bean.setValue(string);
                    beanList.add(bean);
                }
                e.onNext(beanList);
            }
        });
    }

    /**
     * 搜索并解析
     *
     * @param str
     * @return
     */
    private Observable<List<String>> loadSuggest(String str) {
        return WYApiUtil.getInstance().buildSearchService()
                .getSearchSuggest(str)
                .map(new Function<String, List<String>>() {
                    @Override
                    public List<String> apply(String s) throws Exception {
                        Set<String> list = new HashSet<>();
                        if (!StringUtil.isEmpty(s)) {
                            JSONObject object = JSONObject.parseObject(s);
                            if (object.getIntValue("code") == 200) {
                                object = object.getJSONObject("result");
                                if (object != null) {
                                    JSONArray songArray = object.getJSONArray("songs");
                                    JSONArray albumsArray = object.getJSONArray("albums");
                                    JSONArray playlistAray = object.getJSONArray("playlists");
                                    if (songArray != null && songArray.size() > 0) {
                                        int size = songArray.size();
                                        for (int i = 0; i < size; i++) {
                                            list.add(songArray.getJSONObject(i).getString("name"));
                                        }
                                    }
                                    if (albumsArray != null && albumsArray.size() > 0) {
                                        int size = albumsArray.size();
                                        for (int i = 0; i < size; i++) {
                                            list.add(albumsArray.getJSONObject(i).getString("name"));
                                        }
                                    }
                                    if (playlistAray != null && playlistAray.size() > 0) {
                                        int size = playlistAray.size();
                                        for (int i = 0; i < size; i++) {
                                            list.add(playlistAray.getJSONObject(i).getString("name"));
                                        }
                                    }
                                }
                            }
                        }
                        return new ArrayList<>(list);
                    }
                });
    }

    /**
     * 显示搜索建议
     *
     * @param searchStr
     * @param list
     */
    private void showSearchSuggest(final String searchStr, List<String> list) {
        if (!TextUtils.isEmpty(searchStr) && mSuggestLinear != null) {
            String builde = "搜索\"" + searchStr + "\"";
            SpannableStringBuilder string = StringUtil.buildStringColor(builde, getMainColor(), 0, builde.length());
            mStartSearchTv.setText(string);
            suggestAdapter.clearDataList();
            suggestAdapter.addDataList(list);
            suggestAdapter.notifyDataSetChanged();
            if (mSuggestLinear.getVisibility() != View.VISIBLE) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(mSuggestLinear, "alpha", 0f, 1f)
                        .setDuration(500);
                animator.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        mSuggestLinear.setAlpha(0f);
                        mSuggestLinear.setVisibility(View.VISIBLE);
                    }
                });
                animator.setInterpolator(new LinearInterpolator());
                animator.start();
            }
        }
    }

    private void hindSuggestWindow() {
        if (mSuggestLinear != null && mSuggestLinear.getVisibility() == View.VISIBLE) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(mSuggestLinear, "alpha", 1f, 0f)
                    .setDuration(500);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSuggestLinear.setVisibility(View.GONE);
                }
            });
            animator.start();
        }
    }


}
