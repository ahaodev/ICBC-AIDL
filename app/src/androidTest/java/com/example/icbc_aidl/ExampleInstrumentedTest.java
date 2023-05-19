package com.example.icbc_aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.icbc.selfserviceticketing.deviceservice.DeviceService;
import com.icbc.selfserviceticketing.deviceservice.IDeviceService;
import com.icbc.selfserviceticketing.deviceservice.Scanner;
import com.icbc.selfserviceticketing.deviceservice.ScannerListener;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String Tag = "ExampleInstrumentedTest";

    @Test
    public void useAppContext() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.icbc_aidl", appContext.getPackageName());
        ServiceConnection mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // 服务连接成功时的处理逻辑
                IDeviceService binder = IDeviceService.Stub.asInterface(service);
                try {
                    Scanner scanner = (Scanner) binder.getScanner(1);
                    scanner.startScan(null, 1000, new ScannerListener() {
                        @Override
                        public IBinder asBinder() {
                            return null;
                        }

                        @Override
                        public void onSuccess(Bundle result) throws RemoteException {
                            Log.d(Tag, "onSuccess: result" + result.toString());
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
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // 服务连接断开时的处理逻辑
            }
        };
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.icbc.selfserviceticketing.deviceservice", "com.icbc.selfserviceticketing.device_service"));
        appContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Thread.sleep(10000);
    }
}