package com.icbc.selfserviceticketing.deviceservice;

import static android.content.Context.USB_SERVICE;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.List;


public class ScannerSuperLead extends IScanner.Stub {
    private static final int DEFAULT_VENDOR_ID = 11734;
    private static final String TAG = "ScannerSuperLead";
    private boolean enable = false;
    private ScannerListener mScannerListener;
    private Context context;
    private UsbSerialPort port;
    private Runnable runnable = () -> read();

    Thread thread = new Thread(runnable);

    public ScannerSuperLead(Context applicationContext) {
        this.context = applicationContext;
        UsbManager manager = (UsbManager) context.getSystemService(USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            return;
        }
        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        port = driver.getPorts().get(0);// Most devices have just one port (port 0)
        try {
            port.open(connection);
            port.setParameters(DEFAULT_VENDOR_ID, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startScan(Bundle param, long timeout, ScannerListener listener) throws RemoteException {
        Log.d(TAG, "startScan: enable=" + enable);
        if (enable) return;
        enable = true;
        mScannerListener = listener;
        Log.d(TAG, "开始扫码" + enable);
        thread.start();
    }

    @Override
    public void stopScan() throws RemoteException {
        Log.d(TAG, "stopScan: ");
        enable = false;
        onCancelCallback();
        try {
            port.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onSuccessResult(String result) {
        if (mScannerListener != null) {
            try {
                Bundle bundle = new Bundle();
                bundle.putString("barcode", result);
                mScannerListener.onSuccess(bundle);
            } catch (RemoteException e) {

            }
        }
    }

    private void onErrorCallback(int error, String message) {
        if (mScannerListener != null) {
            try {
                mScannerListener.onError(error, message);
            } catch (RemoteException e) {
            }
        }
    }

    private void onCancelCallback() {
        if (mScannerListener != null) {
            try {
                mScannerListener.onCancel();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    private void read() {
        Log.d(TAG, "Thread:  run");
        while (enable) {
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            try {
                bytesRead = port.read(buffer, 200);
            } catch (IOException e) {
                e.printStackTrace();
                onErrorCallback(-1, e.getMessage());
            }
            Log.d(TAG, "read: data size =" + bytesRead);
            if (bytesRead > 0) {
                String hex = HexDump.toHexString(buffer);
                String result = HexDump.hexToAscii(hex);
                Log.d("read data", result);
                onSuccessResult(result);
            }
            SystemClock.sleep(600);
        }
    }
}
