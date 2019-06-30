package wzp.com.texturemusic.common.adapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import wzp.com.texturemusic.R;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2018/1/7.
 */

public class LocalShowImgAdapter extends PagerAdapter {
    private List<View> mViewList;

    public LocalShowImgAdapter(Context context, List<String> list) {
        mViewList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.viewpager_img, null);
            ImageView imageView = view.findViewById(R.id.m_img);
            ImageUtil.loadImage(imageView,list.get(i),imageView);
            mViewList.add(view);
        }
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }
}
