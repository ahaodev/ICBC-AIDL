// IDeviceInfraredLed.aidl
package com.icbc.selfserviceticketing.deviceservice;

// Declare any non-default types here with import statements

interface IDeviceInfraredLed {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);


    /**
    * 开启红外灯
    */
    int openLight();


    /**
    * 关闭红外灯
    */
    int closeLight();


    /**
    * 获取 红外灯的状态
    * 0-已关闭红外灯
    * 1-已经开启红外灯
    */
    int getLightStatus();
}
