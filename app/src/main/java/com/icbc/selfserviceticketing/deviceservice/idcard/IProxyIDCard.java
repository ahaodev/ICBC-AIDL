package com.icbc.selfserviceticketing.deviceservice.idcard;

import android.os.Bundle;

import com.icbc.selfserviceticketing.deviceservice.DeviceListener;

interface IProxyIDCard {

    /**
     * 设备控制
     */
    int OpenDevice(int DeviceID, String deviceFile, String szPort, String szParam);

    int CloseDevice(int DeviceID);

    int GetDeviceStatus(int DeviceID);

    /**
     * 读取身份证卡片信息
     *
     * @param format - 读取身份证卡片信息
     *               <ul>
     *               </ul>
     */
    void commandNonBlock(String cmd, Bundle params, DeviceListener listener);

    /**
     * 停止读卡
     */
    Bundle commandBlock(String cmd, Bundle params); //停止读卡

}
