package wzp.com.texturemusic.indexmodule.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Go_oG
 * Description:
 * on 2017/9/17.
 */

public class IndexViewpageAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private static final String[] title = new String[]{"精彩推荐", "歌单", "主播电台", "排行榜"};

     public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
    }

    public IndexViewpageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
