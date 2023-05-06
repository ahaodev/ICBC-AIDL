// IDeviceWhiteLight.aidl
package com.icbc.selfserviceticketing.deviceservice;

// Declare any non-default types here with import statements

interface IDeviceWhiteLight {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    /**
    * 开启白色补光灯
    */
    int openLight();


    /**
    * 关闭白色补光灯
    */
    int closeLight();


    /**
    * 获取 白色补光灯的状态
    * 0-已关闭补光灯
    * 1-已经开启补光灯
    */
    int getLightStatus();
}
