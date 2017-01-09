package com.sibozn.peo.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.sibozn.peo.R;
import com.sibozn.peo.dialog.WaitDialog;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG = this.getClass().getSimpleName();
    protected Context mContext;
    protected WaitDialog mWaitDialog;
    protected AlertDialog.Builder builder;

    //子类具体实现处理逻辑
    public abstract int getContentViewId();

    protected abstract void onMyCreate(Bundle savedInstanceState);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getString(R.string.xiaoxitishi));
        builder.setCancelable(false);
        onMyCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    protected void showDialog() {
        if (mContext != null && mWaitDialog == null) {
            mWaitDialog = new WaitDialog(mContext);
            mWaitDialog.setMessage(mContext.getText(R.string.wait_dialog_title));
            mWaitDialog.setCancelable(false);
        }
        if (!mWaitDialog.isShowing()) {
            mWaitDialog.show();
        }
    }

    protected void dismissDialog() {
        if (mWaitDialog != null && mWaitDialog.isShowing()) {
            mWaitDialog.dismiss();
        }
    }
}
