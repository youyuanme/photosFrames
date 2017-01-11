package com.sibozn.peo.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.sibozn.peo.R;
import com.sibozn.peo.activity.DetailActivity;
import com.sibozn.peo.activity.FrameActivity;
import com.sibozn.peo.adapter.FeatureListViewAdapter;
import com.sibozn.peo.bean.Beans;
import com.sibozn.peo.nohttp.CallServer;
import com.sibozn.peo.nohttp.HttpListener;
import com.sibozn.peo.utils.Appconfig;
import com.sibozn.peo.utils.Constants;
import com.sibozn.peo.utils.DetailMessageEvent;
import com.sibozn.peo.utils.MessageEvent;
import com.sibozn.peo.utils.MyUtils;
import com.sibozn.peo.utils.PreferenceHelper;
import com.sibozn.peo.view.MyListView;
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
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static com.sibozn.peo.R.id.progressBar;

//http://www.jianshu.com/p/d4a9855e92d3
//https://github.com/hotchemi/PermissionsDispatcher
@RuntimePermissions
public class OneFragment extends BaseFragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx
        .OnPageChangeListener, FeatureListViewAdapter.OnDownLoadClickListener, HttpListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int APP_UPDATA_URL_CODE = 0x1000;
    private final int HOT = 100;
    private final int NEW = 101;
    private String mParam1;
    private String mParam2;
    @BindView(R.id.slider)
    SliderLayout slider;
    @BindView(R.id.hot_lv)
    MyListView hot_lv;
    @BindView(R.id.new_lv)
    MyListView new_lv;
    @BindView(R.id.adView)
    NativeExpressAdView adView;

    private Context mContext;
    private List<Beans> featureList;
    private FeatureListViewAdapter featureHotListViewAdapter, featureNEWListViewAdapter;
    private List<Beans> featureHot, featureNew, featureTopic;


    public void setFeatureList(List<Beans> featureList) {
        this.featureList = featureList;
    }

    public OneFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static OneFragment newInstance(String param1, String param2) {
        OneFragment fragment = new OneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateThisFragment(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.fragment_one, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initViews(View viewFragment) {
        mContext = getActivity();
        hot_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 发送黏性事件
                //EventBus.getDefault().postSticky(new MessageEvent(false, featureHot.get(position).id));
                gotoDetailActivity(featureHot.get(position).toString(), position);
            }
        });
        new_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //EventBus.getDefault().postSticky(new MessageEvent(false, featureHot.get(position).id));
                gotoDetailActivity(featureNew.get(position).toString(), position);
            }
        });
    }

    @Override
    protected void initData() {
        AdRequest adrequest = new AdRequest.Builder()
                //.addTestDevice("YOUR_DEVICE_ID")
                .build();
        adView.loadAd(adrequest);
        if (featureList != null) {
            featureTopic = new ArrayList<Beans>();
            featureHot = new ArrayList<Beans>();
            featureNew = new ArrayList<Beans>();
            for (int i = 0; i < featureList.size(); i++) {
                Beans beans = featureList.get(i);
                if (TextUtils.equals(beans.feature, "1")) {//1是top
                    featureTopic.add(beans);
                    continue;
                } else if (TextUtils.equals("2", beans.feature)) {//2是热门
                    featureHot.add(beans);
                    continue;
                } else if (TextUtils.equals("3", beans.feature)) {//3是最新
                    featureNew.add(beans);
                    continue;
                }
            }
            featureHotListViewAdapter = new FeatureListViewAdapter(mContext, featureHot, this, HOT);
            hot_lv.setAdapter(featureHotListViewAdapter);
            featureNEWListViewAdapter = new FeatureListViewAdapter(mContext, featureNew, this, NEW);
            new_lv.setAdapter(featureNEWListViewAdapter);
            for (int i = 0; i < featureTopic.size(); i++) {
                Beans beans = featureTopic.get(i);
                TextSliderView textSliderView = new TextSliderView(mContext);
                textSliderView.getView().findViewById(R.id.description_layout).setBackground(null);
                // initialize a SliderLayout
                textSliderView
                        .description(beans.name)
                        .image(beans.topic)
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);
                //add your extra information
                Bundle bundle = new Bundle();
                bundle.putString("beans", beans.toString() + "=" + i);
                //bundle.putInt("pisition", i);
                textSliderView.bundle(bundle);
                slider.addSlider(textSliderView);
            }
            slider.setPresetTransformer(SliderLayout.Transformer.Default);
            slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            slider.setCustomAnimation(new DescriptionAnimation());
            slider.setDuration(4000);
            slider.addOnPageChangeListener(this);
            Request<String> request = NoHttp.createStringRequest(Constants.APP_UPDATA_URL, RequestMethod.GET);
            CallServer.getRequestInstance().add(this.getActivity(), APP_UPDATA_URL_CODE, request, this, false, false);
        } else {
            Log.e(TAG, "initData--DemoAcitvity传过来的数据-featureList为空->>: ");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (featureHotListViewAdapter != null) {
            featureHotListViewAdapter.notifyDataSetChanged();
        }
        if (featureNEWListViewAdapter != null) {
            featureNEWListViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        slider.stopAutoCycle();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    // 当一个Message Event提交的时候这个方法会被调用
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(DetailMessageEvent detailMessageEvent) {
        int progress = detailMessageEvent.getProgress();
        int position = detailMessageEvent.getPosition();
        String feature = detailMessageEvent.getFeature();//1是top,2是热门，3是最新
        boolean isFinish = detailMessageEvent.isFinish();
        ProgressBar progressBar1 = null;
        TextView textView = null;
        switch (feature) {
            case "1"://1是top,2是热门，3是最新
                EventBus.getDefault().postSticky(new MessageEvent(progress, featureTopic.get(position).id));
                break;
            case "2"://1是top,2是热门，3是最新
                featureHot.get(position).id.toString();
                EventBus.getDefault().postSticky(new MessageEvent(progress, featureHot.get(position).id));// 发送黏性事件
                progressBar1 = (ProgressBar) hot_lv.getChildAt(position).findViewById(progressBar);
                textView = (TextView) hot_lv.getChildAt(position).findViewById(R.id.tv_download);
                progressBar1.setProgress(progress);
                break;
            case "3"://1是top,2是热门，3是最新
                EventBus.getDefault().postSticky(new MessageEvent(progress, featureNew.get(position).id));// 发送黏性事件
                progressBar1 = (ProgressBar) new_lv.getChildAt(position).findViewById(progressBar);
                textView = (TextView) new_lv.getChildAt(position).findViewById(R.id.tv_download);
                progressBar1.setProgress(progress);
                break;
        }
        if (isFinish) {
            EventBus.getDefault().postSticky(new MessageEvent(isFinish));// 发送黏性事件
            if (!TextUtils.equals("1", feature)) {
                textView.setVisibility(View.VISIBLE);
                progressBar1.setVisibility(View.GONE);
                textView.setBackgroundResource(R.drawable.selector_itme_download);
                textView.setText(R.string.use_string);
                textView.setTextColor(mContext.getResources().getColor(android.R.color.white));
                textView.setEnabled(true);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Bundle bundle = slider.getBundle();
        String beanstring = bundle.getString("beans");
        String position = beanstring.substring(beanstring.indexOf("=") + 1);
        gotoDetailActivity(bundle.getString("beans"), Integer.parseInt(position));//-1是topic传过去的
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Log.e(TAG, "onPageScrolled: " + position);
    }

    @Override
    public void onPageSelected(int position) {
        // Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //  Log.e(TAG, "onPageScrollStateChanged: " + state);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将回调交给代理类处理
        OneFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onDownLoadClickListener(View view, ProgressBar progressBar) {
        Beans beans = (Beans) view.getTag(R.id.tag_beans);
        final int position = (int) view.getTag(R.id.tag_position);
        final int type = (int) view.getTag(R.id.tag_type);
        String str = ((TextView) view).getText().toString();
        // 事件统计
        Map<String, String> HashMap = new HashMap<>();
        HashMap.put("effectID", beans.id + "");
        MobclickAgent.onEvent(mContext, "count_effect", HashMap);
        if (TextUtils.equals(str, getString(R.string.use_string))) {
            Intent intent = new Intent(mContext, FrameActivity.class);
            intent.putExtra("beans", beans.toString());
            startActivity(intent);
            return;
        } else if (TextUtils.equals(str, getString(R.string.on_line))) {
            Intent intent = new Intent(mContext, FrameActivity.class);
            intent.putExtra("beans", beans.toString());
            startActivity(intent);
            return;
        }
        if (TextUtils.equals("0", beans.online)) {// 0是下载，1是在线
            progressBar.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
            OneFragmentPermissionsDispatcher.downloadWithCheck(this, beans, type, position);
        } else if (TextUtils.equals("1", beans.online)) {// 在线

        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)//权限申请成功
    public void download(Beans beans, final int type, final int position) {
        // 0是正在下载，1是下载完成，-1没有下载
        PreferenceHelper.write(mContext, Appconfig.APP_CONFIG, beans.id.toString(), "0");
        String downlink = beans.downlink;
        final String downloadFilePath = MyUtils.getDownloadFileFolder(mContext);
        final String downloadFileName = MyUtils.getUrlFileName(downlink);
        DownloadRequest downloadRequest = NoHttp.createDownloadRequest(downlink, downloadFilePath,
                downloadFileName, true, true);
        CallServer.getRequestInstance().getDownloadInstance()
                .add(Integer.parseInt(beans.id.toString()), downloadRequest, new DownloadListener() {
                    @Override
                    public void onDownloadError(int what, Exception exception) {
                        // 下载发生错误
                        Log.e(TAG, "onDownloadError: " + exception.toString());
                    }

                    @Override
                    public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long
                            allCount) {
                        // 下载开始
                        Log.e(TAG, "onStart: -rangeSize->>>" + rangeSize + "-allCount->>" + allCount);
                    }

                    @Override
                    public void onProgress(int what, int progress, long fileCount) {
                        // 更新下载进度
                        EventBus.getDefault().postSticky(new MessageEvent(progress, what));// 发送黏性事件
                        ProgressBar progressBar1 = null;
                        if (type == HOT) {//热门
                            progressBar1 = (ProgressBar) hot_lv.getChildAt(position).findViewById(R.id.progressBar);
                        } else if (type == NEW) {// 最新
                            progressBar1 = (ProgressBar) new_lv.getChildAt(position).findViewById(R.id.progressBar);
                        }
                        progressBar1.setProgress(progress);
//                            Log.e(TAG, "onProgress: --progress->>" + progress + "-fileCount-->>>" + fileCount +
//                                    "---position-->>" + position + "----what--" + what);
                    }

                    @Override
                    public void onFinish(int what, String filePath) {
                        // 下载完成
                        // 0是正在下载，1是下载完成，-1没有下载
                        PreferenceHelper.write(mContext, Appconfig.APP_CONFIG, what + "", "1");
                        EventBus.getDefault().postSticky(new MessageEvent(true));// 发送黏性事件
                        TextView textView = null;
                        ProgressBar progressBar1 = null;
                        if (type == HOT) {//热门
                            progressBar1 = (ProgressBar) hot_lv.getChildAt(position).findViewById(
                                    R.id.progressBar);
                            textView = (TextView) hot_lv.getChildAt(position).findViewById(R.id.tv_download);
                        } else if (type == NEW) {// 最新
                            progressBar1 = (ProgressBar) new_lv.getChildAt(position).findViewById(
                                    R.id.progressBar);
                            textView = (TextView) new_lv.getChildAt(position).findViewById(R.id.tv_download);
                        }
                        textView.setVisibility(View.VISIBLE);
                        progressBar1.setVisibility(View.GONE);
                        textView.setBackgroundResource(R.drawable.selector_itme_download);
                        textView.setText(R.string.use_string);
                        textView.setTextColor(mContext.getResources().getColor(android.R.color.white));
                        textView.setEnabled(true);
                        boolean isSucceed = MyUtils.unzip(filePath, MyUtils.getUpZipFileFolder(mContext)
                                + File.separator + downloadFileName);
                        if (isSucceed) {
                            MyUtils.delFile(filePath);
                        }
                    }

                    @Override
                    public void onCancel(int what) {
                        // 下载被取消或者暂停
                        Log.e(TAG, "onCancel:--what->>> " + what);
                    }
                });
        CallServer.getRequestInstance().mDownloadRequests.put(beans.id.toString(), downloadRequest);
        //Log.e(TAG, "onDownLoadClickListener: " + CallServer.getRequestInstance().mDownloadRequests
        //.get(beans.id.toString().toString()));
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)//申请前告知用户为什么需要该权限
    public void showRationaleForDownlod(final PermissionRequest request) {
        builder.setMessage(R.string.sd_permission_string);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                request.cancel();
            }
        });
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                request.proceed();
            }
        });
        builder.create().show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void onDownloadDenied() {//被拒绝
        MyUtils.showToast(mContext, getString(R.string.refuse_sd_permission_string));
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)//被拒绝并且勾选了不再提醒
    public void onDownloadNeverAskAgain() {
        builder.setMessage(R.string.sd_permission_setting);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + mContext.getPackageName())); // 根据包名打开对应的设置界面
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    /**
     * 跳转到DeatailActivity页面
     *
     * @param beans    beans对象转化成字符串格式
     * @param position listView中的位置
     */
    private void gotoDetailActivity(String beans, int position) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra("beans", beans.toString());
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    public void onSucceed(int what, Response response) {
        switch (what) {
            case APP_UPDATA_URL_CODE:
                try {
                    JSONObject jsonObject = new JSONObject(response.get().toString());
                    int ver_code = jsonObject.getInt("ver_code");
                    int my_ver_code = MyUtils.getAppVersion(mContext);
                    if (ver_code > my_ver_code) {
                        builder.setMessage(R.string.update_message)
                                .setNegativeButton(R.string.no_string, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        MyUtils.Rate(mContext);
                                        dialogInterface.dismiss();
                                    }
                                })
                                .create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onFailed(int what, Response response) {
        Log.e(TAG, "onFailed: ---->>获取版本号失败");
    }
}
