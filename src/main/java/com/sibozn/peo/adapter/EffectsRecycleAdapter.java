package com.sibozn.peo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sibozn.peo.R;
import com.sibozn.peo.bean.Beans;
import com.sibozn.peo.view.FilterImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/22.
 */

public class EffectsRecycleAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private List<Beans> effectsList;
    private Context context;
    private View.OnClickListener onClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public EffectsRecycleAdapter(Context context, List<Beans> effectsList) {
        this.context = context;
        this.effectsList = effectsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_name_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Beans beans = effectsList.get(position);
        holder.setImagePic(context, beans, onClickListener);
    }

    @Override
    public int getItemCount() {
        return effectsList.size();
    }

    /**
     * 决定元素的布局使用哪种类型
     *
     * @param position 数据源的下标
     * @return 一个int型标志，传递给onCreateViewHolder的第二个参数
     */
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}


class MyViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_pic)
    FilterImageView iv_pic;
    @BindView(R.id.tv_name)
    TextView tv_name;

    public MyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setImagePic(Context context, Beans beans, View.OnClickListener onClickListener) {
        Picasso.with(context).load(beans.icon).into(iv_pic);
        tv_name.setText(beans.name);
        iv_pic.setOnClickListener(onClickListener);
        iv_pic.setTag(beans);
    }
}
