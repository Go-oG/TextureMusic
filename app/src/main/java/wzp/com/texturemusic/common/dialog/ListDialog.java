package wzp.com.texturemusic.common.dialog;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.ItemBean;
import wzp.com.texturemusic.bean.KeyValueBean;
import wzp.com.texturemusic.core.adapter.BaseAdapter;
import wzp.com.texturemusic.interf.OnRecycleItemClickListener;

/**
 * Created by Go_oG
 * Description:
 * on 2017/11/22.
 */

public class ListDialog extends DialogFragment {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.m_recycleview)
    RecyclerView mRecycleview;
    Unbinder unbinder;
    private View view;
    private Context mContext;
    private ListAdapter listAdapter;
    private  String titles="";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listAdapter = new ListAdapter(mContext);
    }

    @SuppressLint("CheckResult")
    public void setItemClickListener(final OnRecycleItemClickListener listener) {
        if (listAdapter != null) {
            listAdapter.getItemClickSubject()
                    .subscribe(new Consumer<ItemBean>() {
                        @Override
                        public void accept(ItemBean itemBean) throws Exception {
                            listener.onItemClick(itemBean.getView(),itemBean.getPosition());
                        }
                    });
        }
    }

    public void setAdapterData(List<KeyValueBean> list) {
        if (listAdapter != null) {
            listAdapter.clearDataList();
            listAdapter.addDataList(list);
            listAdapter.notifyDataSetChanged();
        }
    }

    public void setTitle(String s) {
       titles=s;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.dialog_list_view, container, false);
        }
        unbinder = ButterKnife.bind(this, view);
        mRecycleview.setAdapter(listAdapter);
        mRecycleview.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        title.setText(titles);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    class ListAdapter extends BaseAdapter<KeyValueBean, ListAdapter.ListViewholder> {


        public ListAdapter(Context c) {
            super(c);
        }

        @Override
        public ListViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_dialog_list, parent, false);
            return new ListViewholder(view);
        }

        @Override
        public void onBindViewHolder(ListViewholder holder, int position) {
            final int index = position;
            holder.textView.setText(dataList.get(position).getValue());
            holder.textView.setOnClickListener(v -> {
                if (itemClickSubject != null) {
                    itemClickSubject.onNext(new ItemBean(v, index));
                }
            });


        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class ListViewholder extends RecyclerView.ViewHolder {
            TextView textView;

            public ListViewholder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.item_title);
            }
        }
    }


}
