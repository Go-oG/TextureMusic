package wzp.com.texturemusic.mvmodule.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Wang on 2017/6/8.
 * MvCnannelActivity中viewpager的适配器
 */

public class MvChannelViewpagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> list;

    public List<Fragment> getList() {
        return list;
    }

    public void setList(List<Fragment> list) {
        this.list = list;
    }

    public MvChannelViewpagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        if (list != null)
            return list.size();
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "精选MV";
        } else if (position == 1) {
            return "排行榜";
        } else if (position == 2) {
            return "全部MV";
        } else {
            return "";
        }
    }
}
