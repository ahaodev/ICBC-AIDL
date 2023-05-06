package com.icbc.selfserviceticketing.deviceservice;

/**
* 扫码器扫码结果监听器
*/
interface ScannerListener {
    /**
    * 扫码成功回调
    * @param -  result 扫描结果集合
    *           <ul>
    *               <li>barcode(String) - 扫描条码/二维码的值</li>
    *           </ul>
    */
    void onSuccess(in Bundle result);
    /**
    * 扫码出错
    * @param error - 错误码
    * @param message - 错误描述
    */
    void onError(int error, String message);
    /**
    * 扫码超时回调
    */
    void onTimeout();
    /**
    * 扫码取消回调
    */
    void onCancel();
}