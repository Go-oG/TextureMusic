package wzp.com.texturemusic.playlistmodule;

import androidx.recyclerview.widget.RecyclerView;
import android.view.animation.Interpolator;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import wzp.com.texturemusic.playlistmodule.adapter.PlaylistDetailAdapter;

/**
 * Created by Wang on 2018/2/21.
 * 歌单详情的界面的Adapter动画
 */

public class PlaylistDetailAnimator extends SlideInLeftAnimator {

    public PlaylistDetailAnimator(Interpolator interpolator) {
        super(interpolator);
    }

    @Override
    public void onAnimationFinished(RecyclerView.ViewHolder viewHolder) {
        super.onAnimationFinished(viewHolder);
    }

    @Override
    protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        if (holder instanceof PlaylistDetailAdapter.ViewholderItemV2) {
            super.animateRemoveImpl(holder);
        }
    }

    @Override
    protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        if (holder instanceof PlaylistDetailAdapter.ViewholderItemV2) {
            super.preAnimateAddImpl(holder);
        }
    }

    @Override
    protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
        if (holder instanceof PlaylistDetailAdapter.ViewholderItemV2) {
            super.animateAddImpl(holder);
        }
    }


}
