package wzp.com.texturemusic.common.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;

import wzp.com.texturemusic.R;

/**
 * Created by Go_oG
 * Description:
 * on 2018/1/6.
 */

public class CommentTitleAdapter extends DelegateAdapter.Adapter<CommentTitleAdapter.CommentTitleViewholder> {
    private String title="精彩评论";
    private Context mContext;

    public CommentTitleAdapter (Context c){
        this.mContext=c;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new SingleLayoutHelper();
    }

    @Override
    public CommentTitleViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentTitleViewholder(LayoutInflater.from(mContext).inflate(R.layout.item_comment_title,parent,false));
    }

    @Override
    public void onBindViewHolder(CommentTitleViewholder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class CommentTitleViewholder extends RecyclerView.ViewHolder{

        public CommentTitleViewholder(View itemView) {
            super(itemView);
        }
    }
}
