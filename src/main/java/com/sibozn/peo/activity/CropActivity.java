package com.sibozn.peo.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.sibozn.peo.R;
import com.sibozn.peo.utils.MyUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


/**
 * 剪切
 * Created by Administrator on 2016/12/12.
 */
@RuntimePermissions
public class CropActivity extends BaseActivity {

    private final int REQUEST_PICK_IMAGE = 0x1000;
    //private static final int REQUEST_SAF_PICK_IMAGE = 0x1001;
    @BindView(R.id.cropImageView)
    CropImageView cropImageView;
    @BindView(R.id.buttonPickImage)
    ImageButton buttonPickImage;
    @BindView(R.id.buttonRotateLeft)
    ImageButton buttonRotateLeft;
    @BindView(R.id.buttonRotateRight)
    ImageButton buttonRotateRight;
    @BindView(R.id.br_save)
    ImageButton br_save;

    //////////////////////////////////////////// Callbacks/////////////////////////////////////////
    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override
        public void onSuccess() {
            dismissDialog();
        }

        @Override
        public void onError() {
            dismissDialog();
        }
    };
    private final CropCallback mCropCallback = new CropCallback() {
        @Override
        public void onSuccess(Bitmap cropped) {
        }

        @Override
        public void onError() {
            dismissDialog();
            MyUtils.showToast(mContext, getString(R.string.crop_error));
        }
    };
    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override
        public void onSuccess(Uri outputUri) {
            dismissDialog();
            setResult(RESULT_OK, new Intent().putExtra("uri", outputUri));
            finish();
        }

        @Override
        public void onError() {
            dismissDialog();
            MyUtils.showToast(mContext, getString(R.string.crop_error));
        }
    };

    @Override
    public int getContentViewId() {
        return R.layout.activity_crop;
    }

    @Override
    protected void onMyCreate(Bundle savedInstanceState) {
        mContext = this;
        cropImageView.startLoad((Uri) getIntent().getParcelableExtra("uri"), mLoadCallback);
        cropImageView.setCropMode(CropImageView.CropMode.FREE);
        //cropImageView.setAnimationEnabled(true);
        //cropImageView.setAnimationDuration(2000);
        cropImageView.setCompressQuality(80);
        //cropImageView.setMinFrameSizeInDp(200);
        cropImageView.setMinFrameSizeInPx(200);
        cropImageView.setCompressFormat(Bitmap.CompressFormat.JPEG);
        showDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CropActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_PICK_IMAGE);
        }
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                    .setType("image*//*");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_PICK_IMAGE);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setType("image*//*");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_SAF_PICK_IMAGE);
            }
        }*/
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void showRationaleForPick(final PermissionRequest request) {
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
                }).create().show();
    }

    //被拒绝
    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void onPermissionDeniedForPick() {
        MyUtils.showToast(mContext, getString(R.string.refuse_sd_permission_string));
    }

    //被拒绝并且勾选了不再提醒
    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void onNeverAskAgainForPick() {
        builder.setMessage(R.string.sd_permission_setting)
                .setNegativeButton(R.string.cancel, new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.parse("package:" + getPackageName())); // 根据包名打开对应的设置界面
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                }).create().show();
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void cropImage() {
        showDialog();
        cropImageView.startCrop(Uri.fromFile(new File(MyUtils.getCacheDir(this), "cropped.jpg")),
                mCropCallback, mSaveCallback);
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void showRationaleForCropImage(PermissionRequest request) {
        showRationaleForPick(request);
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void onPermissionDeniedForCropImage() {
        onPermissionDeniedForPick();
    }

    //被拒绝并且勾选了不再提醒
    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void onNeverAskAgainForCropImage() {
        onNeverAskAgainForPick();
    }

    @OnClick({R.id.br_save, R.id.buttonPickImage, R.id.buttonRotateLeft, R.id.buttonRotateRight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonPickImage:
                CropActivityPermissionsDispatcher.pickImageWithCheck(this);
                break;
            case R.id.buttonRotateLeft:
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
                break;
            case R.id.buttonRotateRight:
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                break;
            case R.id.br_save:
                CropActivityPermissionsDispatcher.cropImageWithCheck(this);
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
            case REQUEST_PICK_IMAGE:
                cropImageView.startLoad(data.getData(), mLoadCallback);
                break;
           /*
           case REQUEST_SAF_PICK_IMAGE:
                cropImageView.startLoad(Utils.ensureUriPermission(this, data), mLoadCallback);
                break;
                */
        }
    }
}