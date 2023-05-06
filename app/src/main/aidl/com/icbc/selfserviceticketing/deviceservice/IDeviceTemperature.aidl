// IDeviceTemperature.aidl
package com.icbc.selfserviceticketing.deviceservice;

// Declare any non-default types here with import statements

interface IDeviceTemperature {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    /**
    * 开启
    */
    int open();


    /**
    * 关闭
    */
    int close();


    /**
    * 温度，精确到小数点后 两位
    */
    float  getTemperature();
}
