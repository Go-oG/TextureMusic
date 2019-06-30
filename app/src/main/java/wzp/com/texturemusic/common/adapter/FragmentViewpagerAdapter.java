package wzp.com.texturemusic.common.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Go_oG
 * Description:用于viewpager的适配器
 * on 2017/10/28.
 */

public class FragmentViewpagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private String[] title;

    public void setTitle(String[] title) {
        this.title = title;
    }

    public FragmentViewpagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentList = new ArrayList<>();
    }

    public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList.clear();
        this.fragmentList.addAll(fragmentList);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (title != null) {
            return title[position];
        }
        return "";
    }
}
