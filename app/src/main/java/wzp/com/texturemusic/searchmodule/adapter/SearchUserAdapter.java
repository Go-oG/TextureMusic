package wzp.com.texturemusic.searchmodule.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.UserBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.core.config.AppConstant;
import wzp.com.texturemusic.util.ImageUtil;

/**
 * Created by Go_oG
 * Description:
 * on 2017/9/16.
 */

public class SearchUserAdapter extends BaseAdapter<UserBean,SearchUserAdapter.UserViewHolder> {

    private static final int USER_TYPE_MAN = 1;
    private static final int USER_TYPE_WOMAN = 0;

    public SearchUserAdapter(Context c) {
        super(c);
    }

    @Override
    public SearchUserAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchUserAdapter.UserViewHolder holder, int position) {
        holder.itemView.setTag(position);
        UserBean bean = dataList.get(position);
        ImageUtil.loadImage(mContext,
                bean.getUserCoverImgUrl()+ AppConstant.WY_IMG_100_100,
                holder.userheadImg,
                R.drawable.ic_large_user);
        holder.userNameTv.setText(bean.getNickName());
        holder.userInfoTv.setText(bean.getSignnature());
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }


    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView userNameTv, userInfoTv;
        RelativeLayout item;
        CircleImageView userheadImg;

        public UserViewHolder(View itemView) {
            super(itemView);
            userNameTv = itemView.findViewById(R.id.item_user_name);
            userInfoTv = itemView.findViewById(R.id.item_user_info);
            userheadImg = itemView.findViewById(R.id.item_user_head_img);
            item = itemView.findViewById(R.id.item_user_item);
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickSubject != null) {
                itemClickSubject.onNext(new ItemBean(v, (int) itemView.getTag()));
            }
        }
    }


}
