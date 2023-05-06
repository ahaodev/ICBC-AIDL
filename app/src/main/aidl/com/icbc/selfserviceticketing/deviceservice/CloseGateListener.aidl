// OpenGateListener.aidl
package com.icbc.selfserviceticketing.deviceservice;

// Declare any non-default types here with import statements

/*
*闸门开闸及后续操作结果监听回调
* */
interface CloseGateListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);



 /*
    *关闭及后续操作的回调
    *@param data --参数集合
    *      closeCode(int) –  关闸结果
    *              0 – 关闸成功
    *              1  - 关闸失败（非0值视为关闸失败，具体代码可由厂商定义）
    *      closeMessage(String): 执行关闭的结果的描述信息
    */
    void closeGateCallBack(in Bundle data);

}
