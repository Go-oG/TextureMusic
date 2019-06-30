package wzp.com.texturemusic.localmodule.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Go_oG
 * Description:
 * on 2017/9/26.
 */

public class LocalViewpagerAdapter extends FragmentPagerAdapter {
    private static final String[] TITLE = new String[]{"歌曲", "专辑", "艺术家"};
    private List<Fragment> fragmentList;

    public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
    }

    public LocalViewpagerAdapter(FragmentManager fm) {
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
        return TITLE[position % TITLE.length];
    }
}
