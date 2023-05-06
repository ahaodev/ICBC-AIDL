package com.icbc.selfserviceticketing.deviceservice;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.TelephonyManager;

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
        return Build.SERIAL;
    }

    @Override
    public String getIMSI() throws RemoteException {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = telephonyManager.getSubscriberId();
        return imsi;
    }

    @Override
    public String getIMEI() throws RemoteException {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        return deviceId;
    }

    @Override
    public String getICCID() throws RemoteException {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSimSerialNumber();
    }

    @Override
    public String getManufacture() throws RemoteException {
        return Build.MANUFACTURER;
    }

    @Override
    public String getModel() throws RemoteException {
        return Build.MODEL;
    }

    @Override
    public String getAndroidOSVersion() throws RemoteException {
        return Build.VERSION.RELEASE;
    }

    @Override
    public String getAndroidKernelVersion() throws RemoteException {
        return DeviceInfoUtils.getFormattedKernelVersion();
    }

    @Override
    public String getROMVersion() throws RemoteException {
        return null;
    }

    @Override
    public String getFirmwareVersion() throws RemoteException {
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
