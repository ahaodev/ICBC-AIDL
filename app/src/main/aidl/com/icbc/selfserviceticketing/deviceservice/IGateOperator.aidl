// IGateOperator.aidl
package com.icbc.selfserviceticketing.deviceservice;

// Declare any non-default types here with import statements
import com.icbc.selfserviceticketing.deviceservice.OpenGateListener;
import com.icbc.selfserviceticketing.deviceservice.CloseGateListener;
/**
* 闸机设备对象
* */
interface IGateOperator {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);


    /*
     * 功能：闸机打开闸门
     */
    void open(in Bundle params , OpenGateListener listener);

    /*
     * 功能：闸机关闭闸门
     */
    void close(in Bundle params , CloseGateListener listener);

    /*
     * 功能：闸机闸门状态
     */
    int GetGateDeviceStatus();

    /*
     * 功能：闸机闸门状态
     */
    boolean setGateTime(long time);
}
