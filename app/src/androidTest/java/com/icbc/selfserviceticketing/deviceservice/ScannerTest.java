package com.icbc.selfserviceticketing.deviceservice;

import static org.junit.Assert.assertEquals;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ScannerTest {
    private static final String TAG = "IScanner";

    @Test
    public void scannerTest() throws InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ServiceConnection mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IDeviceService binder = IDeviceService.Stub.asInterface(service);
                try {
                    IScanner scanner = binder.getScanner(1);
                    scanner.startScan(new android.os.Bundle(), 10, new ScannerListener() {
                        @Override
                        public void onSuccess(Bundle result) throws RemoteException {
                            String barCode = result.getString("barcode");
                            Log.d(TAG, "onSuccess: " + barCode);
                        }

                        @Override
                        public void onError(int error, String message) throws RemoteException {

                        }

                        @Override
                        public void onTimeout() throws RemoteException {

                        }

                        @Override
                        public void onCancel() throws RemoteException {

                        }

                        @Override
                        public IBinder asBinder() {
                            return null;
                        }
                    });

                    //testScanner(scanner);
                    Log.d("iIdCard", "idcard binder: ");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onServiceDisconnected(ComponentName name) {
                // 服务连接断开时的处理逻辑
                Log.d("TEST", "onServiceDisconnected: ");
            }
        };
        Intent intent = new Intent(appContext, DeviceService.class);
        appContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE | Context.BIND_DEBUG_UNBIND);
        Thread.sleep(100000);
        assertEquals("com.icbc.selfserviceticketing.deviceservice", appContext.getPackageName());
    }
}
