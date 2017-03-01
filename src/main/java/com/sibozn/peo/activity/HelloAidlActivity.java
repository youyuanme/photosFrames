package com.sibozn.peo.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sibozn.peo.R;
import com.sibozn.peo.helloaidl.AdditionService;
import com.sibozn.peo.helloaidl.IAdditionService;

import java.util.List;

/**
 * Created by Administrator on 2017/2/28.
 */

public class HelloAidlActivity extends Activity {

    private EditText value1, value2;
    private TextView tv_result;
    private IAdditionService service;
    private AdditionServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_aidl);
        initView();
        String serviceName = AdditionService.class.getName();
        Log.e("----------", "onCreate: " + serviceName);
        if (!isServiceWork(this, serviceName)) {
            startService(new Intent().setClassName("com.sibozn.peo", AdditionService.class.getName()));
        }
        bindAIDLService();
    }

    private void initView() {
        value1 = (EditText) findViewById(R.id.value1);
        value2 = (EditText) findViewById(R.id.value2);
        tv_result = (TextView) findViewById(R.id.tv_result);
        findViewById(R.id.bt_result).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number1 = Integer.parseInt(value1.getText().toString().trim());
                int number2 = Integer.parseInt(value2.getText().toString().trim());
                try {
                    service.basicTypes(10, 1001, true, 1.1f, 2.23, "hello,你好");
                    int sum = service.add(number1, number2);
                    tv_result.setText(sum + "");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseService();
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    private boolean isServiceWork(Context mContext, String serviceName) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = activityManager.getRunningServices(Integer.MAX_VALUE);
        Log.e("=============", "isServiceWork: " + myList.size());
        if (myList.size() <= 0) return false;
        for (ActivityManager.RunningServiceInfo serviceInfo : myList) {
            String myName = serviceInfo.service.getClassName().toString();
            Log.e("serviceName----->>", "isServiceWork: " + myName);
            if (myName.equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This function disconnects the Activity from the service
     */
    private void releaseService() {
        unbindService(connection);
        connection = null;
    }

    /**
     * 连接AIDL服务
     * This function connects the Activity to the service
     */
    private void bindAIDLService() {
        connection = new AdditionServiceConnection();
        bindService(new Intent().setClassName("com.sibozn.peo", AdditionService.class.getName())
                , connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * This inner class is used to connect to the service
     */
    class AdditionServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = IAdditionService.Stub.asInterface(iBinder);
            Toast.makeText(HelloAidlActivity.this, "Service connected", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
            Toast.makeText(HelloAidlActivity.this, "Service disconnected", Toast.LENGTH_LONG).show();
        }
    }
}
