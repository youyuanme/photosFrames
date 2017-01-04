package com.sibozn.peo.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import com.sibozn.peo.R;
import com.sibozn.peo.adapter.FragmentViewPagerAdapter;
import com.sibozn.peo.application.MyApplication;
import com.sibozn.peo.bean.Beans;
import com.sibozn.peo.fragment.OneFragment;
import com.sibozn.peo.fragment.TwoFragment;
import com.sibozn.peo.gen.BeansDao;
import com.sibozn.peo.nohttp.CallServer;
import com.sibozn.peo.nohttp.HttpListener;
import com.sibozn.peo.utils.Constants;
import com.sibozn.peo.utils.MyUtils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends BaseActivity implements HttpListener {

    private final int MAIN_URL_CODE = 0x100;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.activity_demo)
    LinearLayout activityDemo;
    private List<Beans> featureList, effectsList;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private List<String> titles;
    private List<Fragment> fragments;
    private FragmentViewPagerAdapter fragmentViewPagerAdapter;
    private BeansDao beansDao;

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onMyCreate(Bundle savedInstanceState) {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        int memClass = activityManager.getMemoryClass();//64，以m为单位
        Log.e(TAG, "onMyCreate:-----memClass----->>>> " + memClass);
        int LargeMemory = activityManager.getLargeMemoryClass();
        Log.e(TAG, "onMyCreate:--------LargeMemory=-->> " + LargeMemory);
        featureList = new ArrayList<Beans>();
        effectsList = new ArrayList<Beans>();
        fragments = new ArrayList<Fragment>();
        titles = new ArrayList<String>();
        titles.add(getString(R.string.editors_choice));
        titles.add(getString(R.string.more_effects));
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        tabs.addTab(tabs.newTab().setText(titles.get(0)), true);
        tabs.addTab(tabs.newTab().setText(titles.get(1)), false);
        beansDao = MyApplication.instance.getDaoSession().getBeansDao();
        showDialog();
        Request<String> request = NoHttp.createStringRequest(Constants.MAIN_URL, RequestMethod.GET);
        CallServer.getRequestInstance().add(this, MAIN_URL_CODE, request, this, false, false);
        MainActivityPermissionsDispatcher.needsPermissionWithCheck(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)//权限申请成功
    public void needsPermission() {
        Log.e(TAG, "needsPermission: ----权限申请成功----");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)//申请前告知用户为什么需要该权限
    public void showRationaleForApp(final PermissionRequest request) {
        builder.setMessage(R.string.sd_permission);
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
        //showRationaleDialog("使用此功能需要打开照相机的权限", request);
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void onSdDenied() {//被拒绝
        builder.setMessage(R.string.refuse_sd_permission);
        builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.this.finish();
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

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)//被拒绝并且勾选了不再提醒
    public void onSDNeverAskAgain() {
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

    @Override
    public void onSucceed(int what, Response response) {
        if (what == MAIN_URL_CODE) {
            if (beansDao.count() != 0) {
                beansDao.deleteAll();
            }
            try {
                JSONObject jsonObject = new JSONObject(response.get().toString());
                JSONObject featureJSON = jsonObject.getJSONObject("feature");
                Iterator<String> iterator = featureJSON.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    JSONObject JSON = featureJSON.getJSONObject(key);
                    Beans beans = new Beans(Long.parseLong(JSON.getString("id")), JSON.getString("icon"),
                            JSON.getString("name"), JSON.getString("abstract"), JSON.getString("topic"),
                            JSON.getString("description"), JSON.getString("downlink"),
                            JSON.getString("uplink"), JSON.getString("effectlink"),
                            JSON.getString("landscape"), JSON.getString("price"),
                            JSON.getString("online"), JSON.getString("feature"),
                            JSON.getString("rank"), "");
                    beansDao.insert(beans);
                    featureList.add(beans);
                }
                JSONObject effectsJSON = jsonObject.getJSONObject("effects");
                Iterator<String> effectsIterator = effectsJSON.keys();
                while (effectsIterator.hasNext()) {
                    String key = effectsIterator.next();
                    JSONObject JSON = effectsJSON.getJSONObject(key);
                    Beans beans = new Beans(Long.parseLong(JSON.getString("id")), JSON.getString("icon"), JSON
                            .getString("name"), JSON.getString("abstract"), JSON.getString("topic"),
                            JSON.getString("description"), JSON.getString("downlink"), JSON
                            .getString("uplink"), JSON.getString("effectlink"), JSON.getString
                            ("landscape"), JSON.getString("price"), JSON.getString("online"), JSON
                            .getString("feature"), JSON.getString("rank"), "");
                    beansDao.insert(beans);
                    effectsList.add(beans);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OneFragment oneFragment = new OneFragment();
            oneFragment.setFeatureList(featureList);
            fragments.add(oneFragment);

            TwoFragment twoFragment = new TwoFragment();
            twoFragment.setEffectsList(effectsList);
            fragments.add(twoFragment);
            fragmentViewPagerAdapter = new FragmentViewPagerAdapter(manager, fragments, titles);
            viewpager.setAdapter(fragmentViewPagerAdapter);
            tabs.setupWithViewPager(viewpager);
            dismissDialog();
        }
    }

    @Override
    public void onFailed(int what, Response response) {
        dismissDialog();
        MyUtils.showToast(this, getString(R.string.get_data_error));
    }

    private long waitTime = 2000, touchTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 返回键
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - touchTime) >= waitTime) {
                MyUtils.showToast(this, getString(R.string.exit_string));
                touchTime = currentTime;
            } else {
                CallServer.getRequestInstance().getDownloadInstance().stop();
                finish();
            }
            return true;
        }
        return false;
    }
}
