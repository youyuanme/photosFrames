package com.sibozn.peo.helloaidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by Administrator on 2017/2/28.
 */

public class AdditionService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("serviceTAG", "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("serviceTAG", "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("serviceTAG", "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.e("serviceTAG", "onRebind: ");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new IAdditionService.Stub() {
            @Override
            public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat
                    , double aDouble, String aString) throws RemoteException {
                Log.e("serviceTAG", "basicTypes: anInt=" + anInt + "aLong=" + aLong
                        + "aBoolean=" + aBoolean + "aFloat=" + aFloat
                        + "aDouble=" + aDouble + "aString=" + aString);
            }

            @Override
            public int add(int value1, int value2) throws RemoteException {
                return value1 + value2;
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("serviceTAG", "onDestroy: ");
    }
}
