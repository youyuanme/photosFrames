package com.sibozn.peo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sibozn.peo.R;
import com.sibozn.peo.bean.DetailBean;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2016/11/26.
 */

public class DetailRecyclerViewAdapter extends RecyclerView.Adapter<MyDetailViewHolder> {

    private List<DetailBean> detailBeen;
    private Context context;

    public DetailRecyclerViewAdapter(Context context, List<DetailBean> detailBeen) {
        this.context = context;
        this.detailBeen = detailBeen;
    }

    @Override
    public MyDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail_layout, parent, false);
        return new MyDetailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyDetailViewHolder holder, int position) {
        DetailBean detailBean = detailBeen.get(position);
        holder.setImagePic(context, detailBean.getLow_pic());
    }

    @Override
    public int getItemCount() {
        return detailBeen.size();
    }

}

class MyDetailViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.iv_pic)
    ImageView iv_pic;

    public MyDetailViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setImagePic(Context context, String url) {
        Picasso.with(context).load(url).into(iv_pic);
    }
}