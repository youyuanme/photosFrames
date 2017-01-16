package com.sibozn.peo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sibozn.peo.R;
import com.sibozn.peo.bean.Beans;
import com.sibozn.peo.utils.Appconfig;
import com.sibozn.peo.utils.MyUtils;
import com.sibozn.peo.utils.PreferenceHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sibozn.peo.utils.PreferenceHelper.readString;

/**
 * Created by Administrator on 2016/11/22.
 */

public class FeatureListViewAdapter extends MyBaseAdapter<Beans> {

    private List<Beans> featureList;
    private Context context;
    private OnDownLoadClickListener DownLoadClickListener;
    private int type;

    public FeatureListViewAdapter(Context context, List<Beans> data, OnDownLoadClickListener DownLoadClickListener,
                                  int type) {
        super(context, data);
        this.context = context;
        this.featureList = data;
        this.DownLoadClickListener = DownLoadClickListener;
        this.type = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolderData viewHolderData;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_layout, null);
            viewHolderData = new ViewHolderData(convertView);
            convertView.setTag(viewHolderData);
        } else {
            viewHolderData = (ViewHolderData) convertView.getTag();
        }
        Beans beans = featureList.get(position);
        Picasso.with(context)
                .load(beans.icon)
                .into(viewHolderData.iconIv);
        viewHolderData.tvName.setText(beans.name);
        viewHolderData.tvAbstract.setText(beans.Abstract);
        if (TextUtils.equals("1", beans.online)) {//在线处理
            viewHolderData.progressBar.setVisibility(View.GONE);
            viewHolderData.tvDownload.setVisibility(View.VISIBLE);
            viewHolderData.tvDownload.setBackgroundResource(R.drawable.selector_itme_download);
            viewHolderData.tvDownload.setText(R.string.on_line);
            viewHolderData.tvDownload.setTextColor(context.getResources().getColor(android.R.color.white));
            viewHolderData.tvDownload.setEnabled(true);
        } else if (TextUtils.equals("0", beans.online)) {// 下载到本地处理
            // 0是正在下载，1是下载完成，-1没有下载
            String state = PreferenceHelper.readString(context, Appconfig.APP_CONFIG
                    , beans.id.toString(), "-1");//默认为-1状态，没有下载完成
            switch (state) {
                case "-1":// 没有下载完成
                    displayNoDownloadState(viewHolderData);
                    break;
                case "0":// 正在下载
                    displayDownloadingState(viewHolderData);
                    break;
                case "1":// 下载完成
                    // 下载完成并且查看本地是否删除下载文件
                    String downloadFileName = MyUtils.getUrlFileName(beans.downlink);
                    if (MyUtils.isDownloadImageIntegrity(context, downloadFileName)) {// 本地有下载文件
                        displayDownloadedState(viewHolderData);
                    } else {// 本地没有下载文件，显示下载状态同-1状态
                        displayNoDownloadState(viewHolderData);
                    }
                    break;

            }
        }
        viewHolderData.tvDownload.setTag(R.id.tag_type, type);
        viewHolderData.tvDownload.setTag(R.id.tag_position, position);
        viewHolderData.tvDownload.setTag(R.id.tag_beans, beans);
        viewHolderData.tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownLoadClickListener.onDownLoadClickListener(view, viewHolderData.progressBar);
            }
        });
        return convertView;
    }

    /**
     * 显示下载完成状态
     *
     * @param viewHolderData
     */
    private void displayDownloadedState(ViewHolderData viewHolderData) {
        viewHolderData.tvDownload.setVisibility(View.VISIBLE);
        viewHolderData.progressBar.setVisibility(View.GONE);
        //viewHolderData.tvDownload.setBackgroundResource(R.drawable.shap_gray_border_radius);
        //viewHolderData.tvDownload.setTextColor(context.getResources().getColor(android.R.color
        //.darker_gray));
        //viewHolderData.tvDownload.setText(R.string.download_over);
        viewHolderData.tvDownload.setText(R.string.use_string);
        //viewHolderData.tvDownload.setEnabled(false);
    }

    /**
     * 显示正在下载状态
     *
     * @param viewHolderData
     */
    private void displayDownloadingState(ViewHolderData viewHolderData) {
        viewHolderData.tvDownload.setVisibility(View.GONE);
        viewHolderData.progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 显示没有下载状态
     *
     * @param viewHolderData
     */
    private void displayNoDownloadState(ViewHolderData viewHolderData) {
        viewHolderData.tvDownload.setVisibility(View.VISIBLE);
        viewHolderData.progressBar.setVisibility(View.GONE);
        viewHolderData.tvDownload.setBackgroundResource(R.drawable.selector_itme_download);
        viewHolderData.tvDownload.setText(R.string.download);
        viewHolderData.tvDownload.setTextColor(context.getResources().getColor(android.R.color.white));
        viewHolderData.tvDownload.setEnabled(true);
    }

    public interface OnDownLoadClickListener {
        void onDownLoadClickListener(View view, ProgressBar progressBar);
    }

    static class ViewHolderData {
        @BindView(R.id.icon_iv)
        ImageView iconIv;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_abstract)
        TextView tvAbstract;
        @BindView(R.id.tv_download)
        TextView tvDownload;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        public ViewHolderData(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
