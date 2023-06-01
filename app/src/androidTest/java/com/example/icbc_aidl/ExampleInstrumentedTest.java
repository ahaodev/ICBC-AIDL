package com.example.icbc_aidl;

import static org.junit.Assert.assertEquals;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.icbc.selfserviceticketing.deviceservice.DeviceService;
import com.icbc.selfserviceticketing.deviceservice.IDeviceService;
import com.icbc.selfserviceticketing.deviceservice.IScanner;
import com.icbc.selfserviceticketing.deviceservice.ScannerListener;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * ICBC AIDL Service 层单元测试
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = "ExampleInstrumentedTest";

    @Test
    public void useAppContext() throws InterruptedException {

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.icbc.selfserviceticketing.deviceservice", appContext.getPackageName());
        ServiceConnection mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IDeviceService binder = IDeviceService.Stub.asInterface(service);
                try {
                    IScanner scanner = binder.getScanner(1);
                    Log.d("Scanner", "onServiceConnected: ");
                    scanner.startScan(null, 1000, new ScannerListener() {
                        @Override
                        public IBinder asBinder() {
                            return null;
                        }

                        @Override
                        public void onSuccess(Bundle result) throws RemoteException {
                            Log.d(TAG, "onSuccess: result" + result.toString());
                        }

                        @Override
                        public void onError(int error, String message) throws RemoteException {
                            Log.d(TAG, "onError: ");
                        }

                        @Override
                        public void onTimeout() throws RemoteException {
                            Log.d(TAG, "onTimeout: ");
                        }

                        @Override
                        public void onCancel() throws RemoteException {
                            Log.d(TAG, "onCancel: ");
                        }
                    });
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
    }
}