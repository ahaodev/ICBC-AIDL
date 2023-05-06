package com.icbc.selfserviceticketing.deviceservice;

import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.ys.wiegand.Wiegand;
import com.zkteco.android.util.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class RFReader extends IRFReader.Stub {
    private ICTask mICTask;
    private NFCTask mNFCTask;
    private ISerialPortTask mISerialPortTask;
    private boolean wiegandEnale;

    @Override
    public void readICCardByNonContact(Bundle params, NonContactDeviceListener listener) throws RemoteException {
        release();
        wiegandEnale = true;
//        mICTask = new ICTask(listener);
//        mICTask.start();

//        mNFCTask = new NFCTask(listener);
//        mNFCTask.start();

        mISerialPortTask = new ISerialPortTask(listener);
        mISerialPortTask.start();
    }

    @Override
    public Bundle stopReadICCardByNonContact(Bundle params) throws RemoteException {
        release();
        return null;
    }

    private void release() {
        wiegandEnale = false;
        if (mICTask != null) mICTask.interrupt();
        mICTask = null;
        if (mNFCTask != null) mNFCTask.release();
        mNFCTask = null;
        if (mISerialPortTask != null) mISerialPortTask.release();
        mISerialPortTask = null;
    }

    private class NFCTask extends Thread {
        private static final String DEV_PORT = "/dev/ttyS2";
        private static final int DEV_BAUDRATE = 9600;
        private SerialPort mSerialPort;
        private boolean bStart = true;
        private NonContactDeviceListener mDeviceListener;

        public NFCTask(NonContactDeviceListener listener) {
            mDeviceListener = listener;
            try {
                mSerialPort = new SerialPort(new File(DEV_PORT), DEV_BAUDRATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void release() {
            bStart = false;
            interrupt();
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

        public boolean write(String msg) {
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

        public byte[] read() {
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
                    try {
                        Bundle bundle = new Bundle();
                        bundle.putString("ICCardType", "NFC");
                        bundle.putString("ICCardContent", result);
                        bundle.putByteArray("ICCardContent_byte", read);
                        mDeviceListener.readCardCallBack("ICCard_Read_OnSuccess", bundle);
                    } catch (RemoteException e) {

                    }
                }
                SystemClock.sleep(500);
            }
        }
    }

    private class ISerialPortTask extends Thread {
        private static final String DEV_PORT = "/dev/ttyS3";
        private static final int DEV_BAUDRATE = 9600;
        private SerialPort mSerialPort;
        private boolean bStart = true;
        private NonContactDeviceListener mDeviceListener;

        public ISerialPortTask(NonContactDeviceListener listener) {
            mDeviceListener = listener;
            try {
                mSerialPort = new SerialPort(new File(DEV_PORT), DEV_BAUDRATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void release() {
            bStart = false;
            interrupt();
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

        public boolean write(String msg) {
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

        public byte[] read() {
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
                    String result = null;
                    if (((read[0] & 0xFF) == 0xAA
                            && (read[1] & 0xFF) == 0xBB)) {
                        int length = read[2] & 0xff - 4;
                        byte[] cardBy = new byte[length];
                        System.arraycopy(read, 3, cardBy, 0, length);
                        result = DataFormatUtil.bytesToHex(cardBy);
                    } else if ((read[0] & 0xFF) == 0x7F) {
                        byte[] cardBy = new byte[4];
                        System.arraycopy(read, 6, cardBy, 0, cardBy.length);
                        result = DataFormatUtil.bytesToHex(cardBy).toUpperCase();
                    }
                    try {
                        Bundle bundle = new Bundle();
                        if (result != null) {
                            bundle.putString("ICCardType", "PSAM");
                            bundle.putString("ICCardContent", result);
                            mDeviceListener.readCardCallBack("ICCard_Read_OnSuccess", bundle);
                        }
                    } catch (RemoteException e) {

                    }
                }
                SystemClock.sleep(500);
            }
        }
    }

    private class ICTask extends Thread {
        private Wiegand mWiegand;
        private NonContactDeviceListener mDeviceListener;

        public ICTask(NonContactDeviceListener listener) {
            mWiegand = new Wiegand();
            mDeviceListener = listener;
        }

        @Override
        public void interrupt() {
            super.interrupt();
            mWiegand.closeInput();
        }

        @Override
        public void run() {
            while (wiegandEnale) {
                int inputOpen = mWiegand.openInput();
                if (inputOpen > 0) {
                    long inputResult = mWiegand.readInput();
                    Log.e("heef", "inputResult:" + inputResult + " bStart:" + wiegandEnale + "  ==" + this);
                    if (wiegandEnale) {
                        try {
                            Bundle bundle = new Bundle();
                            if (inputResult <= 0) {
                                bundle.putInt("errCode", 3);
                                bundle.putString("errMsg", "接收数据失败");
                                mDeviceListener.readCardCallBack("ICCard_Read_OnError", bundle);
                            } else {
                                bundle.putString("ICCardType", "PSAM");
                                bundle.putString("ICCardContent", String.valueOf(inputResult));
                                mDeviceListener.readCardCallBack("ICCard_Read_OnSuccess", bundle);
                            }
                        } catch (RemoteException e) {

                        }
                    }
                }
                mWiegand.closeInput();
            }
        }
    }
}
