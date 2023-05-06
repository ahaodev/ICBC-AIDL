// IScanner.aidl
package com.icbc.selfserviceticketing.deviceservice;

// Declare any non-default types here with import statements
import com.icbc.selfserviceticketing.deviceservice.ScannerListener;
interface IScanner {
    /*
    * 启动扫码，返回二维码扫码结果
    * @param  param --提示信息给
    * <ul>
    * <li>upPromptString(String) - 扫描框上方提示信息，最大20汉字，默认中间对齐</li>
    * <li>downPromptString(String) - 扫描框下方提示信息，最大20汉字，默认中间对齐</li>
    * </ul>
    *
    * @param timeout-- 扫码超时时间，单位秒
    * @param listener – 扫码结果监听器
    */
    void startScan(in Bundle param, long timeout, ScannerListener listener);


    /**
    *  停止扫码，取消扫码过程
    *
    */
    void stopScan();
}
