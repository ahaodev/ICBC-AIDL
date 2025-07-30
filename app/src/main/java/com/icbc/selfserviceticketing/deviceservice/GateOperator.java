package com.icbc.selfserviceticketing.deviceservice;

import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class GateOperator extends IGateOperator.Stub {
    //private static final String RELAY_PATH = "/sys/class/custom_class/custom_dev/relay";
    private Timer mTimer;

    @Override
    public void open(Bundle params, OpenGateListener listener) throws RemoteException {
        LogUtils.file("GateOperator open: 开闸操作");
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new Timer();
        File file = new File(getRelayPath());
        if (!file.exists()) {
            if (listener != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("code", 1);
                bundle.putString("message", "设备未找到");
                listener.openGateCallBack(bundle);
            }
        } else {
            boolean success = FileUtils.writeFile(file, "1");
            if (listener != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("code", success ? 0 : 2);
                bundle.putString("message", success ? "开闸成功" : "开闸失败");
                listener.openGateCallBack(bundle);
                if (success) {
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                close(null, null);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }, Long.parseLong(FileUtils.getValueFromProp("persist.sys.relay", "1000")));//5 * 1000 继电器
                }
            }
        }

    }

    @Override
    public void close(Bundle params, CloseGateListener listener) throws RemoteException {
        LogUtils.file("GateOperator close: 关闭闸机操作");
        File file = new File(getRelayPath());
        if (!file.exists()) {
            if (listener != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("closeCode", 1);
                bundle.putString("closeMessage", "设备未找到");
                listener.closeGateCallBack(bundle);
            }
        } else {
            boolean success = FileUtils.writeFile(file, "0");
            if (listener != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("closeCode", success ? 0 : 2);
                bundle.putString("closeMessage", success ? "关闸成功" : "关闸失败");
                listener.closeGateCallBack(bundle);
            }
        }
    }

    @Override
    public int GetGateDeviceStatus() throws RemoteException {
        File file = new File(getRelayPath());
        String s = FileUtils.readFile(file);
        if (TextUtils.isEmpty(s) || !TextUtils.isDigitsOnly(s)) return -1;
        return Integer.parseInt(s);
    }

    @Override
    public boolean setGateTime(long time) throws RemoteException {
        FileUtils.setValueToProp("persist.sys.relay", time + "");
        return true;
    }

    public String getRelayPath() {
        if (Build.PRODUCT.equals("F28V2_2") || Build.PRODUCT.equals("F68V1_0"))
            return "/sys/devices/platform/gpio-ctl/gpio_door/value";
        else
            return "/sys/devices/platform/gpio-ctl/gpio3/value";
    }
}
