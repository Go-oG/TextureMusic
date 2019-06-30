package wzp.com.texturemusic.searchmodule.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Go_oG
 * Description:
 * on 2017/9/16.
 */

public class SearchPageAdapter extends FragmentPagerAdapter {

    public static final String[] SEARCH_TILES = new String[]{"单曲", "歌手", "专辑", "歌单", "MV", "主播电台", "用户"};

    private List<Fragment> fragmentList;

    public List<Fragment> getFragmentList() {
        return fragmentList;
    }

    public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
    }

    public SearchPageAdapter(FragmentManager fm) {
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
        if (position < SEARCH_TILES.length) {
            return SEARCH_TILES[position];
        } else {
            return null;
        }
    }
}
