// DeviceRebootListener.aidl
package com.icbc.selfserviceticketing.deviceservice;

// Declare any non-default types here with import statements

interface DeviceRebootListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

     /**
     * 设备重启的结果监听回调s
     */
    void rebootCallBack(in Bundle data);
}
