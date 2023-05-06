package com.icbc.selfserviceticketing.deviceservice;

import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.zkteco.android.util.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class Temperaturer extends IDeviceTemperature.Stub {
    private static final byte[] ORDER_DATA_OUTPUT = new byte[]{(byte) 0xA5, (byte) 0x55, 0x01, (byte) 0xFB};
    private static final String DEV_PORT = "/dev/ttyS2";
    private static final int DEV_BAUDRATE = 115200;
    private SerialPort mSerialPort;

    @Override
    public int open() throws RemoteException {
        close();
        try {
            mSerialPort = new SerialPort(new File(DEV_PORT), DEV_BAUDRATE);
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int close() throws RemoteException {
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
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else return 0;
        return 0;
    }

    @Override
    public float getTemperature() throws RemoteException {
        long ts = System.currentTimeMillis();
        byte[] result = null;
        write(ORDER_DATA_OUTPUT);
        while (System.currentTimeMillis() - ts < 500) {
            result = read();
            if (result != null) break;
        }
        if (result != null && result.length == 7) {
            return ((result[2] & 0xFF) + 256 * (result[3] & 0xFF)) / 100f;
        }
        return 0;
    }

    private boolean write(byte[] cmd) {
        if (mSerialPort == null || mSerialPort.getOutputStream() == null) return false;
        OutputStream outputStream = mSerialPort.getOutputStream();
        try {
            outputStream.write(cmd);
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public byte[] read() {
        if (mSerialPort == null) return null;
        InputStream inputStream = mSerialPort.getInputStream();
        if (inputStream == null) return null;
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
}
