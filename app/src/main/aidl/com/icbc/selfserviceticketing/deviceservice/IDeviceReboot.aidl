// IDeviceReboot.aidl
package com.icbc.selfserviceticketing.deviceservice;

import com.icbc.selfserviceticketing.deviceservice.DeviceRebootListener;
// Declare any non-default types here with import statements

interface IDeviceReboot {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//             double aDouble, String aString);


    /**
    * 设备重启
    */
    void reboot(in Bundle params , DeviceRebootListener listener);

}
