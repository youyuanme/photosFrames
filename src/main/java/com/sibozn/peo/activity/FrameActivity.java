package com.sibozn.peo.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sibozn.peo.R;
import com.sibozn.peo.bean.DetailBean;
import com.sibozn.peo.nohttp.CallServer;
import com.sibozn.peo.nohttp.HttpListener;
import com.sibozn.peo.utils.Constants;
import com.sibozn.peo.utils.ImageUtil;
import com.sibozn.peo.utils.MiPictureHelper;
import com.sibozn.peo.utils.MyUtils;
import com.sibozn.peo.utils.PictureScanner;
import com.sibozn.peo.view.FilterImageView;
import com.sibozn.peo.view.TouchImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yolanda.nohttp.BasicBinary;
import com.yolanda.nohttp.BitmapBinary;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadRequest;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static com.sibozn.peo.R.id.iv_camera;
import static com.sibozn.peo.R.id.iv_share;
import static com.sibozn.peo.utils.MyUtils.bitmapToFile;

@RuntimePermissions
public class FrameActivity extends BaseActivity implements HttpListener {

    private final int GET_DETAIL_CODE = 0x1000;
    private final int UP_DATA_PHOTO = 0x1001;
    private final int UP_DATA_EFFECT = 0x1002;
    private final int DOWNLOAD_FRAME_IMAGE = 0x1003;
    private final int PHOTO_ZOOM_CODE = 0x1004;
    private final String SAVE_IMAGE_APP = "/PEO/";
    private final int IMAGE_PHOTO_CODE = 0x100;
    private final int IMAGE_CAPTURE_CODE = 0x101;
    private final int SHARE_CODE = 0x102;
    private final int IMAGE_SAVE_CODE = 0x103;
    private final int ONCLICK_SHARE_CODE = 0x104;
    private final int ONCLICK_SAVE_CODE = 0x105;
    @BindView(R.id.touch_img)
    TouchImageView touch_img;
    @BindView(R.id.iv_frames_online)
    ImageView iv_frames_online;
    @BindView(iv_camera)
    ImageView ivCamera;
    @BindView(R.id.iv_photos)
    ImageView ivPhotos;
    @BindView(R.id.iv_frames)
    ImageView ivFrames;
    @BindView(iv_share)
    ImageView ivShare;
    @BindView(R.id.iv_save)
    ImageView ivSave;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;
    @BindView(R.id.rl_menus)
    RelativeLayout rl_menus;
    @BindView(R.id.hsv_landscape)
    HorizontalScrollView hsv_landscape;
    @BindView(R.id.ll_landscape)
    LinearLayout ll_landscape;
    @BindView(R.id.fl_bg)
    FrameLayout fl_bg;
    private boolean hasMeasured;
    private int rightWidth = 0, bottomHigh = 0;
    private ProgressDialog progressDialog;
    private String shareTempPath, saveImageFilePath, downloadFilePath, upLoadingPath;
    private String landscape, online, id, uplink, effectlink, photoId, photoUrl, downlink;
    private boolean isLandscape, isOnline;
    private List<DetailBean> detailBeen;
    private List<String> frameLists;
    private File cameraTemp, photoZoomFile;

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            switch (msg.what) {
                case SHARE_CODE:
                    Uri uri = Uri.fromFile(new File(shareTempPath));
                    Intent intent = new Intent(Intent.ACTION_SEND)
                            .setType("image/*")
                            .putExtra(Intent.EXTRA_STREAM, uri)
                            //.putExtra(Intent.EXTRA_SUBJECT, "Share")
                            .putExtra(Intent.EXTRA_TEXT, getString(R.string.share))
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent, getString(R.string.share)));
                    break;
                case IMAGE_SAVE_CODE:
                    builder.setMessage(String.format(getString(R.string.save_successful), ((String) msg.obj)))
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setPositiveButton(R.string.go_look, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //打开系统相册浏览照片
                                    new PictureScanner(mContext, saveImageFilePath);
                                }
                            })
                            .create().show();
                    break;
            }
        }
    };

    @Override
    public int getContentViewId() {
        Intent intent = getIntent();
        String beanString = intent.getStringExtra("beans");
        String[] str = beanString.split("#");
        id = str[0];
        online = str[1];
        downlink = str[6];
        uplink = str[7];
        landscape = str[9];
        effectlink = str[13];
        if (TextUtils.equals("1", landscape)) {//landscape  =1为横屏，=0为竖屏
            isLandscape = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置为横屏
            return R.layout.activity_frame_landscape;
        } else {
            isLandscape = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return R.layout.activity_frame_vertical;
        }
    }

    @Override
    protected void onMyCreate(Bundle savedInstanceState) {
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        cameraTemp = new File(MyUtils.getExternalCacheDir(this) + "/camera_temp.jpg");
        photoZoomFile = new File(MyUtils.getExternalCacheDir(this));
        shareTempPath = MyUtils.getExternalCacheDir(mContext) + File.separator + "share.jpg";
        saveImageFilePath = MyUtils.getSDCardPath() + SAVE_IMAGE_APP;
        if (TextUtils.equals("1", online)) { // online = 1是在线，0是本地
            isOnline = true;
        } else {
            isOnline = false;
        }
        //判断是否是在线，如果是在线就获取在线需要合成的图片，否则加载本地已经下载好的本地图片
        if (isOnline) {//显示在线视图
            touch_img.setVisibility(View.INVISIBLE);
            iv_frames_online.setVisibility(View.VISIBLE);
            fl_bg.setBackground(null);
            detailBeen = new ArrayList<>();
            Request<String> request = NoHttp.createStringRequest(Constants.DETAIL_URL
                    + "online=" + online + "&parent_id=" + id, RequestMethod.GET);
            CallServer.getRequestInstance().add(this, GET_DETAIL_CODE, request, this, false, true);
        } else {// 本地
            touch_img.setVisibility(View.VISIBLE);
            iv_frames_online.setVisibility(View.INVISIBLE);
            frameLists = new ArrayList<>();
            String downloadFileName = MyUtils.getUrlFileName(downlink);
            downloadFilePath = MyUtils.getUpZipFileFolder(mContext)
                    + File.separator + downloadFileName + File.separator
                    + downloadFileName + File.separator;
            String readmeString = MyUtils.readFile(downloadFilePath + "readme.txt");
            try {
                JSONObject jsonObject = new JSONObject(readmeString);
                int pic_num = Integer.parseInt(jsonObject.getString("pic_num"));
                JSONObject json = jsonObject.getJSONObject("e_list");
                Iterator<String> iterator = json.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    JSONObject jsonObject1 = json.getJSONObject(key);
                    String pic = jsonObject1.getString("pic");
                    frameLists.add(pic);
                }
                addFrameImages1(frameLists);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(getString(R.string.xiaoxitishi));
        progressDialog.setMessage(getString(R.string.save_loading));
        //getOtherShareData(this);
        rl_menus.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (hasMeasured == false) {
                    hasMeasured = true;
                    if (isLandscape) {//计算横屏时减去的宽度
                        rightWidth = rl_menus.getMeasuredWidth();
                        int windowWidth = MyUtils.getWindowWidth(mContext);
                        touch_img.setLayoutParams(new RelativeLayout.LayoutParams(windowWidth - rightWidth,
                                RelativeLayout.LayoutParams.MATCH_PARENT));
                        //获取到宽度和高度后，可用于计算
                    } else {// 计算竖屏是减去的高度
                        bottomHigh = rl_menus.getMeasuredHeight();
                        int windowHigh = MyUtils.getWindowHeight(mContext);
                        touch_img.setLayoutParams(new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT, windowHigh - bottomHigh));
                    }
                }
                return true;
            }
        });
        touch_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        FrameActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)//权限申请成功
    public void WriteSDPermission(int type) {
        switch (type) {
            case ONCLICK_SHARE_CODE://分享
                if (isOnline) {// 在线
                    DownloadRequest downloadRequest = NoHttp.createDownloadRequest(photoUrl,
                            MyUtils.getExternalCacheDir(mContext), "/share.png", true, true);
                    CallServer.getRequestInstance().getDownloadInstance().add(DOWNLOAD_FRAME_IMAGE,
                            downloadRequest, new DownloadListener() {
                                @Override
                                public void onDownloadError(int what, Exception exception) {
                                    dismissDialog();
                                    MyUtils.showToast(mContext, getString(R.string.download_image_error));
                                }

                                @Override
                                public void onStart(int what, boolean isResume, long rangeSize, Headers
                                        responseHeaders, long allCount) {
                                    showDialog();
                                }

                                @Override
                                public void onProgress(int what, int progress, long fileCount) {

                                }

                                @Override
                                public void onFinish(int what, String filePath) {
                                    dismissDialog();
                                    Uri uri = Uri.fromFile(new File(filePath));
                                    Intent i = new Intent(Intent.ACTION_SEND)
                                            .setType("image/*")
                                            .putExtra(Intent.EXTRA_STREAM, uri)
                                            //.putExtra(Intent.EXTRA_SUBJECT, "Share")
                                            .putExtra(Intent.EXTRA_TEXT, getString(R.string.share))
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    if (i.resolveActivity(getPackageManager()) != null) {
                                        startActivity(Intent.createChooser(i, getString(R.string.share)));
                                    }
                                }

                                @Override
                                public void onCancel(int what) {

                                }
                            });
                } else {//本地
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean isSave = bitmapToFile(MyUtils.GetCurrentImage((Activity) mContext,
                                    rightWidth, bottomHigh), shareTempPath);
                            if (isSave) {
                                handler.sendEmptyMessage(SHARE_CODE);
                            } else {
                                Log.e(TAG, "onClick: " + "保存到本地失败");
                            }
                        }
                    }).start();
                }
                break;
            case ONCLICK_SAVE_CODE: //保存存到本地
                if (isOnline) {
                    DownloadRequest downloadRequest = NoHttp.createDownloadRequest(photoUrl,
                            saveImageFilePath, MyUtils.getUrlFileName1(photoUrl), true, true);
                    CallServer.getRequestInstance().getDownloadInstance().add(DOWNLOAD_FRAME_IMAGE,
                            downloadRequest, new DownloadListener() {
                                @Override
                                public void onDownloadError(int what, Exception exception) {
                                    dismissDialog();
                                    MyUtils.showToast(mContext, getString(R.string.download_image_error));
                                }

                                @Override
                                public void onStart(int what, boolean isResume, long rangeSize, Headers
                                        responseHeaders, long allCount) {
                                    showDialog();
                                }

                                @Override
                                public void onProgress(int what, int progress, long fileCount) {

                                }

                                @Override
                                public void onFinish(int what, final String filePath) {
                                    dismissDialog();
                                    builder.setMessage(String.format(getString(R.string.save_successful), filePath))
                                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            })
                                            .setPositiveButton(R.string.go_look, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //打开系统相册浏览照片
                                                    new PictureScanner(mContext, saveImageFilePath);
                                                }
                                            })
                                            .create().show();
                                    updataPhoto(filePath);
                                }

                                @Override
                                public void onCancel(int what) {

                                }
                            });
                } else {
                    final String saveImageFilePathName = MyUtils.getSDCardPath() + SAVE_IMAGE_APP
                            + MyUtils.getDataTime("yyyy-MM-dd_HH-mm:ss") + ".jpg";
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean isSave = bitmapToFile(MyUtils.GetCurrentImage((Activity) mContext,
                                    rightWidth, bottomHigh), saveImageFilePathName);
                            if (isSave) {
                                Message message = new Message();
                                message.what = IMAGE_SAVE_CODE;
                                message.obj = saveImageFilePathName;
                                handler.sendMessage(message);
                                updataPhoto(saveImageFilePathName);
                            }
                        }
                    }).start();
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
                .setCancelable(false)
                .create()
                .show();
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
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                // 根据包名打开对应的设置界面
                                .setData(Uri.parse("package:" + mContext.getPackageName()));
                        startActivity(intent);
                    }
                })
                .create().show();
    }

    @NeedsPermission(Manifest.permission.CAMERA)//权限申请成功
    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraTemp));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, IMAGE_CAPTURE_CODE);
        }
    }

    @OnShowRationale(Manifest.permission.CAMERA) //申请前告知用户为什么需要该权限
    public void showRationaleForCamera(final PermissionRequest request) {
        builder.setMessage(R.string.camera_permission)
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
    @OnPermissionDenied(Manifest.permission.CAMERA)
    public void onCameraDenied() {
        MyUtils.showToast(this, getString(R.string.refuse_sd_permission_string));
    }

    //被拒绝并且勾选了不再提醒
    @OnNeverAskAgain(Manifest.permission.CAMERA)
    public void onCameraNeverAskAgain() {
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
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                // 根据包名打开对应的设置界面
                                .setData(Uri.parse("package:" + mContext.getPackageName()));
                        startActivity(intent);

                    }
                })
                .create().show();
    }

    @OnClick({R.id.iv_camera, R.id.iv_photos, R.id.iv_frames, R.id.iv_share, R.id.iv_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_camera:
                FrameActivityPermissionsDispatcher.openCameraWithCheck(this);
                break;
            case R.id.iv_photos:
                // 打开相册并返回指定url
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                        .setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, IMAGE_PHOTO_CODE);
                }
                break;
            case R.id.iv_frames:
                if (hsv_landscape.getVisibility() == View.VISIBLE) {
                    hideBottomFrame();
                } else {
                    displayBottomFrame();
                }
                break;
            case R.id.iv_share:
                if (isOnline) {
                    if (TextUtils.isEmpty(photoId)) {
                        MyUtils.showToast(mContext, getString(R.string
                                .select_load_iamge));
                        return;
                    } else if (TextUtils.isEmpty(photoUrl)) {
                        MyUtils.showToast(mContext, getString(R.string.select_mate_iamge));
                        return;
                    }
                    hideBottomFrame();
                    showDialog();
                } else {
                    progressDialog.show();
                }
                FrameActivityPermissionsDispatcher.WriteSDPermissionWithCheck(this, ONCLICK_SHARE_CODE);
                break;
            case R.id.iv_save:
                // 保存截图
                if (isOnline) {//在线
                    if (TextUtils.isEmpty(photoId)) {
                        MyUtils.showToast(this, getString(R.string
                                .select_load_iamge));
                        return;
                    } else if (TextUtils.isEmpty(photoUrl)) {
                        MyUtils.showToast(this, getString(R.string.select_mate_iamge));
                        return;
                    }
                    hideBottomFrame();
                } else {//本地
                    if (!MyUtils.checkSDcard()) {
                        MyUtils.showToast(this, getString(R.string.sd_ka_no));
                        return;
                    }
                    hideBottomFrame();
                    progressDialog.show();
                }
                FrameActivityPermissionsDispatcher.WriteSDPermissionWithCheck(this, ONCLICK_SAVE_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case IMAGE_CAPTURE_CODE:// 拍照
                if (isOnline) {//在线
                    Intent intent = new Intent(this, CropActivity.class)
                            .putExtra("uri", Uri.fromFile(cameraTemp));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, PHOTO_ZOOM_CODE);
                    }
                } else {//本地
                    showDialog();
                    Picasso.with(this)
                            //.load(Uri.fromFile(cameraTemp))
                            .load(cameraTemp)
                            .skipMemoryCache()
                            .into(touch_img, new Callback() {
                                @Override
                                public void onSuccess() {
                                    dismissDialog();
                                }

                                @Override
                                public void onError() {
                                    dismissDialog();
                                    MyUtils.showToast(FrameActivity.this, getString(R.string.error_iamge));
                                }
                            });
                }
                break;
            case IMAGE_PHOTO_CODE://相册
                Uri imageUri = data.getData();
                //content://com.sec.android.gallery3d.provider/picasa/item/5991690410398672098
                //https://lh3.googleusercontent.com/
                // -0So8YduLEZ8/UybCrtPtZOI/AAAAAAAADK8/hy54Bdk2J4UOJEqVJp8EpCX5JL1fxAkFACHM/I/17%2B-%2B1
                // 获取图片路径的方法调用
                String pickPath = MiPictureHelper.getPath(mContext, imageUri);
                if (TextUtils.isEmpty(pickPath)) {
                    MyUtils.showToast(this, getString(R.string.image_return_error));
                    break;
                }
                if (pickPath.toString().startsWith("https://")) {
                    MyUtils.showToast(this, getString(R.string.select_local_image));
                    Log.e(TAG, "onActivityResult: " + "网络图片");
                    break;
                }
                if (isOnline) {
                    Intent intent = new Intent(this, CropActivity.class)
                            .putExtra("uri", imageUri);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, PHOTO_ZOOM_CODE);
                    }
                } else {
                    showDialog();
                    Picasso.with(this)
                            .load(imageUri)
                            .skipMemoryCache()
                            .into(touch_img, new Callback() {
                                @Override
                                public void onSuccess() {
                                    dismissDialog();
                                }

                                @Override
                                public void onError() {
                                    dismissDialog();
                                    MyUtils.showToast(FrameActivity.this, getString(R.string.error_iamge));
                                }
                            });
                }
                break;
            case PHOTO_ZOOM_CODE:// 剪切
                Uri uri = (Uri) data.getParcelableExtra("uri");
                if (!TextUtils.isEmpty(photoId)) {
                    photoId = null;
                    photoUrl = null;
                }
                showDialog();
                Picasso.with(this)
                        .load(uri)
                        .skipMemoryCache()
                        .into(iv_frames_online, new Callback() {
                            @Override
                            public void onSuccess() {
                                dismissDialog();
                            }

                            @Override
                            public void onError() {
                                dismissDialog();
                                MyUtils.showToast(FrameActivity.this, getString(R.string.error_iamge));
                            }
                        });
                upLoadingPath = ImageUtil.saveBitmapToFile(new File(MiPictureHelper.getPath(mContext, uri)),
                        MyUtils.getExternalCacheDir(mContext) + "/cropped.jpg");
                upLoadingPhoto(upLoadingPath);
                break;
        }

    }

    /**
     * 隐藏底部图片
     */
    private void hideBottomFrame() {
        if (hsv_landscape.getVisibility() == View.VISIBLE) {
            hsv_landscape.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 显示底部图片
     */
    private void displayBottomFrame() {
        if (hsv_landscape.getVisibility() == View.INVISIBLE) {
            hsv_landscape.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 发送广播通知图片更新
     *
     * @param filePath
     */
    private void updataPhoto(String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        mContext.sendBroadcast(intent);
    }

    /**
     * 上传图片
     */
    private void upLoadingPhoto(String savePath) {
        Request<String> request = NoHttp.createStringRequest(uplink, RequestMethod.POST);
        if (request != null) {
            BasicBinary binary = new BitmapBinary(BitmapFactory.decodeFile(savePath), "android.jpg");
            //BasicBinary binary = new FileBinary(new File(savePath));
            request.add("file", binary);// 添加1个文件
            // 添加到请求队列`
            CallServer.getRequestInstance().add(this, UP_DATA_PHOTO,
                    request, this, false, false);
        }
    }

    @Override
    public void onSucceed(int what, Response response) {
        switch (what) {
            case GET_DETAIL_CODE:// 获取底部图片
                //String online = null, parent_id = null;
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.get().toString());
                    //online = jsonObject.getString("online");
                    //parent_id = jsonObject.getString("parent_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isOnline) {
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
                } /*else {
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
                }*/
                addFrameImages(detailBeen);
                break;
            case UP_DATA_PHOTO:
                //Log.e(TAG, "上传onSucceed: " + response.get().toString());
                dismissDialog();
                try {
                    JSONObject jsonObject1 = new JSONObject(response.get().toString());
                    photoId = jsonObject1.getString("photoId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (TextUtils.isEmpty(photoId) || TextUtils.equals("null", photoId)) {
                    builder.setMessage(R.string.upload_failed)
                            .setNegativeButton(R.string.no_string, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    iv_frames_online.setImageBitmap(null);
                                    dialogInterface.cancel();
                                }
                            })
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (!TextUtils.isEmpty(upLoadingPath)) {
                                        showDialog();
                                        upLoadingPhoto(upLoadingPath);
                                    }
                                }
                            })
                            .create().show();
                }
                break;
            case UP_DATA_EFFECT:
                //Log.e(TAG, "在线生成onSucceed: " + response.get().toString());
                try {
                    JSONObject jsonObject1 = new JSONObject(response.get().toString());
                    photoUrl = jsonObject1.getString("photoUrl");
                    showDialog();
                    Picasso.with(this)
                            .load(photoUrl)
                            .placeholder(iv_frames_online.getDrawable())
                            .into(iv_frames_online, new Callback() {
                                @Override
                                public void onSuccess() {
                                    dismissDialog();
                                }

                                @Override
                                public void onError() {
                                    dismissDialog();
                                    MyUtils.showToast(FrameActivity.this, getString(R.string.error_iamge));
                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onFailed(int what, Response response) {
        MyUtils.showToast(this, getString(R.string.data_download_defeated) + response.get());
        Log.e(TAG, "onFailed: " + response.get());
        switch (what) {
            case UP_DATA_PHOTO:
                break;
        }
    }

    /**
     * 本地
     *
     * @param frameLists
     */
    private void addFrameImages1(List<String> frameLists) {
        for (int i = 0; i < frameLists.size(); i++) {
            final String feilePath = downloadFilePath + frameLists.get(i);
            FilterImageView filterImageView = new FilterImageView(this);
            final int finalI = i;
            filterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalI == 0) {
                        fl_bg.setBackground(null);
                        return;
                    }
                    hideBottomFrame();
                    fl_bg.setBackground(new BitmapDrawable(feilePath));
                }
            });
            filterImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                    MyUtils.dip2px(this, 80), LinearLayout.LayoutParams.FILL_PARENT);
            int margins = MyUtils.dip2px(this, 5);
            mLayoutParams.setMargins(margins, margins, margins, margins);
            if (i == 0) {
                Picasso.with(this)
                        .load(R.mipmap.reset)
                        .into(filterImageView);
            } else {
                Picasso.with(this)
                        .load(Uri.fromFile(new File(feilePath)))
                        .into(filterImageView);
            }
            ll_landscape.addView(filterImageView, mLayoutParams);
        }

    }

    /**
     * 在线
     *
     * @param detailBeen
     */
    private void addFrameImages(final List<DetailBean> detailBeen) {
        for (int i = 0; i < detailBeen.size(); i++) {
            final FilterImageView filterImageView = new FilterImageView(this);
            final int finalI = i;
            filterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(photoId)) {
                        MyUtils.showToast(FrameActivity.this, getString(R.string.select_mate_iamge));
                        return;
                    }
                   /* if (finalI == 0) {
                        photoId = null;
                        iv_frames_online.setImageBitmap(null);
                        return;
                    }
                    */
                    showDialog();
                    hideBottomFrame();
                    DetailBean detailBean = detailBeen.get(finalI);
                    Request<String> request = NoHttp.createStringRequest(effectlink, RequestMethod.POST);
                    if (request != null) {
                        request.add("photoId", photoId);// 添加1个文件
                        request.add("effectId", detailBean.getApi_id());// 添加1个文件
                        // 添加到请求队列
                        CallServer.getRequestInstance().add(mContext, UP_DATA_EFFECT,
                                request, FrameActivity.this, false, false);
                    }
                }
            });
            filterImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                    MyUtils.dip2px(this, 80), LinearLayout.LayoutParams.FILL_PARENT);
            int margins = MyUtils.dip2px(this, 5);
            mLayoutParams.setMargins(margins, margins, margins, margins);
            Picasso.with(this)
                    .load(detailBeen.get(i).getLow_pic())
                    .into(filterImageView);
            /*if (i == 0) {
                Picasso.with(this).load(R.mipmap.reset).into(filterImageView);
            } else {
                Picasso.with(this).load(detailBeen.get(i).getLow_pic()).into(filterImageView);
            }*/
            ll_landscape.addView(filterImageView, mLayoutParams);
        }
    }
}
/////////////////////////////////////////////////////////////////////
/*
//打开系统相册
Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
intent.setType("image/*");
startActivity(intent);

//打开指定的一张照片
Intent intent = new Intent();
intent.setAction(android.content.Intent.ACTION_VIEW);
intent.setDataAndType(Uri.fromFile(pictureFilepath), "image/*");
startActivity(intent);

//打开系统相册浏览照片
Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"));
startActivity(intent);

    //保存当前屏幕图片
    private void saveCurrentScreen(final int what, final String tempPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = MyUtils.GetCurrentImage((Activity) mContext, rightWidth, bottomHigh);
                boolean isSave = bitmapToFile(bitmap, tempPath);
                if (isSave) {
                    handler.sendEmptyMessage(what);
                }
            }
        }).start();
    }
    //剪切图片
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoZoomFile));
        //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, PHOTO_ZOOM_CODE);
    }// 跳转剪切图片

    //获取其他分享的图片
    private void getOtherShareData(Activity activity) {
        Intent intent = activity.getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (action.equals(Intent.ACTION_SEND) && type != null) {
            if (TextUtils.equals(type, "text/platin")) {
                handleSendText(intent);
            } else if (TextUtils.equals(type, "image/")) {
                handleSendImage(intent);
            }
        } else if (action.equals(Intent.ACTION_SEND_MULTIPLE) && type != null) {
            if (TextUtils.equals(type, "image/")) {
                handleSendMultipleImage(intent);
            }
        }
    }

    */
/*
    处理获取多张图片的
     *//*

    private void handleSendMultipleImage(Intent intent) {

    }

    */
/*
    处理获取的图片信息
     *//*

    private void handleSendImage(Intent intent) {

    }

    */
/*
    处理获取的文本内容
     *//*

    private void handleSendText(Intent intent) {

    }

     final String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
                // some devices (OS versions return an URI of com.android instead of com.google.android
                Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                if (imageUri.toString().startsWith("content://com.android.gallery3d.provider")) {
                    // use the com.google provider, not the com.android provider.
                    imageUri = Uri.parse(imageUri.toString().replace("com.android.gallery3d",
                            "com.google.android.gallery3d"));
                }
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                    // if it is a picasa image on newer devices with OS 3.0 and up
                    if (imageUri.toString().startsWith("content://com.google.android.gallery3d")) {
                        columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                        if (columnIndex != -1) {
                            progress_bar.setVisibility(View.VISIBLE);
                            final Uri uriurl = imageUri;
                            // Do this in a background thread, since we are fetching a large image from the web
                            new Thread(new Runnable() {
                                public void run() {
                                    Bitmap the_image = getBitmap("image_file_name.jpg", uriurl);
                                }
                            }).start();
                        }
                    } else { // it is a regular local image file
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();
                        Bitmap the_image = decodeFile(new File(filePath));
                    }
                }
                // If it is a picasa image on devices running OS prior to 3.0
                else if (selectedImage != null && selectedImage.toString().length() > 0) {
                    progress_bar.setVisibility(View.VISIBLE);
                    final Uri uriurl = selectedImage;
                    // Do this in a background thread, since we are fetching a large image from the web
                    new Thread(new Runnable() {
                        public void run() {
                            Bitmap the_image = getBitmap("image_file_name.jpg", uriurl);
                        }
                    }).start();
                }
        }


    */
