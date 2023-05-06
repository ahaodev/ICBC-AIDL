// ISmartCash.aidl
package com.icbc.selfserviceticketing.deviceservice;

// Declare any non-default types here with import statements
import com.icbc.selfserviceticketing.deviceservice.ICallback;
interface ISmartCash {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    /*
     *
     *打开钱箱
     * @param callback 回调方法
     */
    void openCashBox(in ICallback callback);

    /*
     *
     *获取钱箱打开次数
     */
    int getOpenCashBoxTimes();
}
