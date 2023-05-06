// OpenGateListener.aidl
package com.icbc.selfserviceticketing.deviceservice;

// Declare any non-default types here with import statements

/*
*闸门开闸及后续操作结果监听回调
* */
interface OpenGateListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    /*
    *开闸及后续操作的回调
    *@param data --参数集合
    *      code(int) – 开闸结果
    *              0 – 开闸成功
    *              1  -开闸失败（非0值视为开闸失败，具体代码可由厂商定义）
    *      message(String): 执行开闸的结果的描述信息
    *      isPast(boolean) –是否通过闸门
    *             true  - 已经通过
    *             false – 没有通过
    */
    void openGateCallBack(in Bundle data);

}
