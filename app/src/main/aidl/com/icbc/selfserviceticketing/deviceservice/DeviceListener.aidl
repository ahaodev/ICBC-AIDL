// DeviceListener.aidl
package com.icbc.selfserviceticketing.deviceservice;

// Declare any non-default types here with import statements

interface DeviceListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void callBack(String type, in Bundle data);
}
