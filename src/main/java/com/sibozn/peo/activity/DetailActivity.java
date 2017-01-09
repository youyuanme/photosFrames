package com.sibozn.peo.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.sibozn.peo.R;
import com.sibozn.peo.adapter.DetailRecyclerViewAdapter;
import com.sibozn.peo.bean.DetailBean;
import com.sibozn.peo.nohttp.CallServer;
import com.sibozn.peo.nohttp.HttpListener;
import com.sibozn.peo.utils.Appconfig;
import com.sibozn.peo.utils.Constants;
import com.sibozn.peo.utils.DetailMessageEvent;
import com.sibozn.peo.utils.MessageEvent;
import com.sibozn.peo.utils.MyUtils;
import com.sibozn.peo.utils.PreferenceHelper;
import com.sibozn.peo.view.FullyGridLayoutManager;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadRequest;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


/**
 * Created by Administrator on 2016/11/23.
 */
@RuntimePermissions
public class DetailActivity extends BaseActivity implements HttpListener {
    private static final int GET_DETAIL_CODE = 100;
    private static final int PERMISSION_DOWNLOAD_CODE = 101;
    private static final int PERMISSION_IS_DOWNLOADED_CODE = 102;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_use)
    TextView tvUse;
    @BindView(R.id.tv_title_name)
    TextView tv_title_name;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.iv_close)
    ImageView iv_close;
    @BindView(R.id.detail_progressBar)
    ProgressBar progressBar;
    @BindView(R.id.iv_topic)
    ImageView ivTopic;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.adView)
    NativeExpressAdView adView;
    private String topic, name, Description, id, online, downlink, feature;
    private int position;
    private List<DetailBean> detailBeen;
    private DetailRecyclerViewAdapter detailRecyclerViewAdapter;
    private DownloadListener downloadListener;
    private String downloadFilePath, downloadFileName;
    private String beanString;

    @Override
    public int getContentViewId() {
        return R.layout.activity_detail;
    }

    @Override
    protected void onMyCreate(Bundle savedInstanceState) {
        AdRequest adrequest = new AdRequest.Builder()
                //.addTestDevice("YOUR_DEVICE_ID")
                .build();
        adView.loadAd(adrequest);
        Intent intent = getIntent();
        beanString = intent.getStringExtra("beans");
        String[] str = beanString.split("#");
        id = str[0];
        online = str[1];
        name = str[2];
        feature = str[3];
        topic = str[4];
        Description = str[5];
        downlink = str[6];
        if (!TextUtils.equals(feature, "0")) {
            position = intent.getIntExtra("position", -1);
        }
        final FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 4);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        manager.setSmoothScrollbarEnabled(true);
        recycleView.setLayoutManager(manager);
        //recycleView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recycleView.setItemAnimator(new DefaultItemAnimator());
        detailBeen = new ArrayList<DetailBean>();
        Request<String> request = NoHttp.createStringRequest(Constants.DETAIL_URL
                + "online=" + online + "&parent_id=" + id, RequestMethod.GET);
        CallServer.getRequestInstance().add(this, GET_DETAIL_CODE, request, this, false, true);

        downloadFilePath = MyUtils.getDownloadFileFolder(mContext);
        if (TextUtils.isEmpty(downlink)) {// downlink为空没有必要下载，肯定是在线
            return;
        }
        downloadFileName = MyUtils.getUrlFileName(downlink);
        downloadListener = new DownloadListener() {
            @Override
            public void onDownloadError(int what, Exception exception) {
                Log.e(TAG, "onDownloadError: " + exception.toString());
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers
                    responseHeaders, long allCount) {
                //Log.e(TAG, "onStart: -rangeSize->>>" + rangeSize + "-allCount->>" + allCount);
            }

            @Override
            public void onProgress(final int what, final int progress, long fileCount) {
                if (!TextUtils.equals(feature, "0")) {
                    EventBus.getDefault().postSticky(new DetailMessageEvent(progress, feature,
                            position, false));// 发送黏性事件
                }
                if (TextUtils.equals(id, what + "")) {
                    //Log.e(TAG, "-----------------onProgress: " + "----id----" + id + "----what--" + what);
                    //ProgressBar progressBar = (ProgressBar) findViewById(R.id.detail_progressBar);
                    progressBar.setProgress(progress);
                    //Log.e(TAG, "onProgress:------progressBar.toString()-- " + progressBar.toString());
                    //Log.e(TAG, "onProgress:---------- " + progress);
                }

            }

            @Override
            public void onFinish(int what, String filePath) {
                if (!TextUtils.equals(feature, "0")) {
                    // 发送黏性事件
                    EventBus.getDefault().postSticky(new DetailMessageEvent(true, feature));
                }
                // 0是正在下载，1是下载完成，-1没有下载
                PreferenceHelper.write(mContext, Appconfig.APP_CONFIG, what + "", "1");
                displayDownloadedState();
                boolean isSucceed = MyUtils.unzip(filePath,
                        MyUtils.getUpZipFileFolder(mContext)
                                + File.separator + downloadFileName);
                if (isSucceed) {
                    MyUtils.delFile(filePath);
                }
            }

            @Override
            public void onCancel(int what) {
                Log.e(TAG, "onCancel:-----what--- " + what);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_title_name.setText(name);
        tvName.setText(name);
        tvDescription.setText(Description);
        Picasso.with(this).load(topic).into(ivTopic);
        if (TextUtils.equals("1", online)) {// 1是在线，0是下载
            displayDownloadedState();
        } else if (TextUtils.equals("0", online)) {// 1是在线，0是下载
            // 0是正在下载，1是下载完成，-1没有下载
            String state = PreferenceHelper.readString(this, Appconfig.APP_CONFIG
                    , id, "-1");//默认为-1状态，没有下载完成
            switch (state) {
                case "-1"://-1是没有下载
                    displayNoDownloadState();
                    break;
                case "0"://0是正在下载
                    displayDownloadingState();
                    break;
                case "1"://1是下载完成
                    // 下载完成并且查看本地是否删除下载文件
                    DetailActivityPermissionsDispatcher
                            .WriteSDPermissionWithCheck(this, PERMISSION_IS_DOWNLOADED_CODE);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DetailActivityPermissionsDispatcher
                .onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)//权限申请成功
    public void WriteSDPermission(int type) {
        switch (type) {
            case PERMISSION_DOWNLOAD_CODE:
                PreferenceHelper.write(mContext, Appconfig.APP_CONFIG, id, "0");
                tvUse.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                iv_close.setVisibility(View.VISIBLE);
                DownloadRequest downloadRequest = NoHttp.createDownloadRequest(downlink, downloadFilePath,
                        downloadFileName, true, true);
                CallServer.getRequestInstance().getDownloadInstance().add(Integer.parseInt(id),
                        downloadRequest, downloadListener);
                CallServer.getRequestInstance().mDownloadRequests.put(id, downloadRequest);
                break;
            case PERMISSION_IS_DOWNLOADED_CODE:
                String downloadFileName = MyUtils.getUrlFileName(downlink);
                if (MyUtils.isDownloadImageIntegrity(this, downloadFileName)) {// 显示下载完成状态
                    displayDownloadedState();
                } else {// 本地没有下载文件，显示下载状态同-1状态
                    displayNoDownloadState();
                }
                break;
        }

    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) //申请前告知用户为什么需要该权限
    public void showRationaleForWriteSD(final PermissionRequest request) {
        builder.setMessage(R.string.sd_permission_string)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                })
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();
                    }
                })
                .create().show();
    }

    //被拒绝
    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void onWriteSDDenied() {
        MyUtils.showToast(mContext, getString(R.string.refuse_sd_permission_string));
    }

    //被拒绝并且勾选了不再提醒
    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void onWriteSDNeverAskAgain() {
        builder.setMessage(R.string.sd_permission_setting)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + mContext.getPackageName())); // 根据包名打开对应的设置界面
                        startActivity(intent);
                    }
                })
                .create().show();
    }

    // 当一个Message Event提交的时候这个方法会被调用
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(MessageEvent messageEvent) {
        if (!TextUtils.equals("0", feature)) {
            int progress = messageEvent.getProgress();
            boolean isFinish = messageEvent.isFinish();
            long what = messageEvent.getWhat();
            if (isFinish) {
                String downloadFileName = MyUtils.getUrlFileName(downlink);
                if (MyUtils.isFileExists(this, downloadFileName)) {
                    displayDownloadedState();
                } else {
                    displayNoDownloadState();
                }
            } else if (TextUtils.equals(id, what + "")) {// what传过来的就是id，判断id相等的progressBar
                progressBar.setProgress(progress);
            }
        }
    }

    @OnClick({R.id.iv_back, R.id.iv_share, R.id.tv_use, R.id.iv_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_use:
                // 事件统计
                Map<String, String> HashMap = new HashMap<>();
                HashMap.put("effectID", id);
                MobclickAgent.onEvent(mContext, "count_effect", HashMap);
                if (TextUtils.equals(getString(R.string.use_string), tvUse.getText().toString())) {
                    Intent intent = new Intent(mContext, FrameActivity.class);
                    intent.putExtra("beans", beanString);
                    startActivity(intent);
                } else if (TextUtils.equals(getString(R.string.download), tvUse.getText().toString())) {
                    // 0是正在下载，1是下载完成，-1没有下载
                    DetailActivityPermissionsDispatcher.WriteSDPermissionWithCheck(this, PERMISSION_DOWNLOAD_CODE);
                }
                break;
            case R.id.iv_close:
                builder.setMessage(R.string.stop_images)
                        .setCancelable(false)
                        .setNegativeButton(R.string.jixu_string, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.stop_download, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PreferenceHelper.write(DetailActivity.this, Appconfig.APP_CONFIG, id, "-1");
                                // 0是正在下载，1是下载完成，-1没有下载
                                if (CallServer.getRequestInstance() != null) {
                                    CallServer.getRequestInstance().cancelDownloadByKey(id);
                                }
                                displayNoDownloadState();
                                EventBus.getDefault().postSticky(new MessageEvent(true, feature));
                                dialogInterface.dismiss();
                            }
                        })
                        .create().show();
                break;
            case R.id.iv_share:
                break;
        }
    }

    @Override
    public void onSucceed(int what, Response response) {
        String online = null;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response.get().toString());
            online = jsonObject.getString("online");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.equals("0", online)) {
            try {
                JSONObject JSON = jsonObject.getJSONObject("effect_list");
                Iterator<String> iterator = JSON.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    JSONObject json = JSON.getJSONObject(key);
                    detailBeen.add(new DetailBean(json.getString("id"),
                            json.getString("low_pic"), json.getString("pic")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (TextUtils.equals("1", online)) {
            try {
                JSONObject JSON = jsonObject.getJSONObject("effect_list");
                Iterator<String> iterator = JSON.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    JSONObject json = JSON.getJSONObject(key);
                    detailBeen.add(new DetailBean(json.getString("id"),
                            json.getString("low_pic"), "",
                            json.getString("name"), json.getString("api_id")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        detailRecyclerViewAdapter = new DetailRecyclerViewAdapter(this, detailBeen);
        recycleView.setAdapter(detailRecyclerViewAdapter);
    }

    @Override
    public void onFailed(int what, Response response) {
        MyUtils.showToast(this, getString(R.string.data_download_defeated) + response.get());
        Log.e(TAG, "onFailed: " + response.get());
    }

    /**
     * 显示下载完成状态
     */
    private void displayDownloadedState() {
        tvUse.setVisibility(View.VISIBLE);
        tvUse.setText(getString(R.string.use_string));
        progressBar.setVisibility(View.INVISIBLE);
        iv_close.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示正在下载状态
     */
    private void displayDownloadingState() {
        tvUse.setText(getString(R.string.download));
        tvUse.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        iv_close.setVisibility(View.VISIBLE);
    }

    /**
     * 显示没有下载状态
     */
    private void displayNoDownloadState() {
        tvUse.setVisibility(View.VISIBLE);
        tvUse.setText(getString(R.string.download));
        progressBar.setVisibility(View.INVISIBLE);
        iv_close.setVisibility(View.INVISIBLE);
    }

}
