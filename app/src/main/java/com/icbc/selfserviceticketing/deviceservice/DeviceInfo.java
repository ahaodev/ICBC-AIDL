package com.icbc.selfserviceticketing.deviceservice;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.TelephonyManager;

import com.utils.SystemUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeviceInfo extends IDeviceInfo.Stub {
    private Context mContext;

    public DeviceInfo(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public String getSerialNo() throws RemoteException {
        String sn = Build.SERIAL;
        if (null == sn || sn.isEmpty()) {
            sn =SystemUtil.INSTANCE.getSN();
        }
        if (sn !=null && sn.length()>18){
            sn = sn.substring(0,18); // 截取最后18位
        }
        return sn;
    }

    @Override
    public String getIMSI() throws RemoteException {
        String imsi;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imsi=  SystemUtil.INSTANCE.getIMSI();
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            imsi = telephonyManager.getSubscriberId();
        }
        return imsi;
    }

    @Override
    public String getIMEI() throws RemoteException {
        String deviceID ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            deviceID=SystemUtil.INSTANCE.getIMEI();
        }else{
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            deviceID = telephonyManager.getDeviceId();
        }
        return deviceID;
    }

    @Override
    public String getICCID() throws RemoteException {
        String simSerialNumber;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            simSerialNumber=SystemUtil.INSTANCE.getICCID();
        }else{
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            simSerialNumber = telephonyManager.getSimSerialNumber();
        }
        return simSerialNumber;
    }

    @Override
    public String getManufacture() throws RemoteException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return "rockchip";
        }
        return Build.MANUFACTURER;
    }

    @Override
    public String getModel() throws RemoteException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return "rk3568";
        }
        return Build.MODEL;
    }

    @Override
    public String getAndroidOSVersion() throws RemoteException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return "11";
        }
        return Build.VERSION.RELEASE;
    }

    @Override
    public String getAndroidKernelVersion() throws RemoteException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return "11";
        }
        return DeviceInfoUtils.getFormattedKernelVersion();
    }

    @Override
    public String getROMVersion() throws RemoteException {
        return null;
    }

    @Override
    public String getFirmwareVersion() throws RemoteException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return "HARDWARE";
        }
        return Build.HARDWARE;
    }

    @Override
    public String getHardwareVersion() throws RemoteException {
        return null;
    }

    @Override
    public boolean updateSystemTime(String date, String time) throws RemoteException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
            Date dt = sdf.parse(date + " " + time);
            return SystemClock.setCurrentTimeMillis(dt.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setSystemFunction(Bundle bundle) throws RemoteException {
        if(bundle == null) return false;
        boolean homekey = bundle.getBoolean("HOMEKEY");
        boolean statusbarkey = bundle.getBoolean("STATUSBARKEY");
        boolean functionkey = bundle.getBoolean("FUNCTIONKEY");
        return false;
    }
}
