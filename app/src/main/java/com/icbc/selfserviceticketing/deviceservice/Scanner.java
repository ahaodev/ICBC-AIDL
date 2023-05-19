package com.icbc.selfserviceticketing.deviceservice;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;

//import com.zkteco.android.util.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import android.serialport.SerialPort;

public class Scanner extends IScanner.Stub implements Runnable {
    //private static final String DEV_PORT = "/dev/ttyS4";
    //private static final String DEV_PORT = "/dev/ttyXRM1";
    //private static final String DEV_PORT = "/dev/ttyXRM0";
    //private static final String DEV_PORT = "/dev/ttyUSB10";
    //private static final String DEV_PORT = "/dev/ttyFIQ0";
    private static final String DEV_PORT = "/dev/ttyACM0";
    //private static final int DEV_BAUDRATE = 115200;
    private static final int DEV_BAUDRATE = 9600;
    private boolean bStart = false;
    private SerialPort mSerialPort;
    private ScannerListener mScannerListener;
    private Context context;

    public Scanner(Context applicationContext) {
        this.context =applicationContext;
    }

    @Override
    public void startScan(Bundle param, long timeout, ScannerListener listener) throws RemoteException {
        if (bStart) return;
        mScannerListener = listener;
        release();
        try {
            mSerialPort = new SerialPort(new File(DEV_PORT), DEV_BAUDRATE);
        } catch (IOException e) {
            onErrorCallback(0, "打开扫码器失败");
            return;
        }
        bStart = true;
        new Thread(this).start();
    }

    @Override
    public void stopScan() throws RemoteException {
        if (!bStart) return;
        bStart = false;
        onCancelCallback();
        release();
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

    private void release() {
        if (mSerialPort != null) {
            try {
                InputStream inputStream = mSerialPort.getInputStream();
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
                OutputStream outputStream = mSerialPort.getOutputStream();
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                    outputStream = null;
                }
                mSerialPort.close();
                mSerialPort = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean write(String msg) {
        if (mSerialPort == null || mSerialPort.getOutputStream() == null) return false;
        OutputStream outputStream = mSerialPort.getOutputStream();
        try {
            byte[] bytes = msg.getBytes("UTF-8");
            outputStream.write(bytes);
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private byte[] read() {
        if (mSerialPort == null || mSerialPort.getInputStream() == null) return null;
        InputStream inputStream = mSerialPort.getInputStream();
        try {
            if (inputStream.available() > 0) {
                try {
                    byte[] buffer = new byte[inputStream.available()];
                    int read = inputStream.read(buffer);
                    return buffer;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        while (bStart) {
            byte[] read = read();
            if (read != null) {
                String result = new String(read);
                onSuccessResult(result);
            }
            SystemClock.sleep(500);
        }
    }
}
