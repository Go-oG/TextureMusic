package wzp.com.texturemusic.mvmodule.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Wang on 2017/6/10.
 * mv排行榜中viewpager适配器
 */

public class TopMvViewpagerAdapater extends FragmentPagerAdapter {
    private String[] title = new String[]{"内地", "港台", "欧美", "韩国", "日本"};
    private List<Fragment> list;

    public List<Fragment> getList() {
        return list;
    }

    public void setList(List<Fragment> list) {
        this.list = list;
    }

    public TopMvViewpagerAdapater(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        else
            return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }


}
