// IDeviceShutdown.aidl
package com.icbc.selfserviceticketing.deviceservice;

// Declare any non-default types here with import statements
import com.icbc.selfserviceticketing.deviceservice.DeviceShutdownListener;
interface IDeviceShutdown {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);


    /**
    * 设备关机
    */
    void shutDown(in Bundle params , DeviceShutdownListener listener);
}
