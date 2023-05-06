// IRFReader.aidl
package com.icbc.selfserviceticketing.deviceservice;

// Declare any non-default types here with import statements

import com.icbc.selfserviceticketing.deviceservice.RFSearchListener;
import com.icbc.selfserviceticketing.deviceservice.NonContactDeviceListener;
/**
* 非接触式IC卡读卡设备
* */
interface IRFReader {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);


    /**
    * 功能：非接触式IC卡读卡设备：自动寻卡并开始读取IC卡数据
    * @param  params - 控制读卡设备的参数集合
    * @param  listener - 读卡结果监听回调
    *
    */
    void readICCardByNonContact(in Bundle params , NonContactDeviceListener listener);

    /**
    * 功能：停止读卡并关闭非接模块
    * @param  params - 参数集合
    * @return Bundle -返回结果集合
    *              result(int): 0-成功, 1-失败
    *              message(String): 执行结果的描述信息
    *
    */
    Bundle stopReadICCardByNonContact(in Bundle params);


//
}
