package com.icbc.selfserviceticketing.deviceservice;

/**
* 打开钱箱的监听器
*/
interface ICallback {

    /*
     *钱箱回调接口
     *@param isSuccess:true 执行成功，false 执行失败
     *@param code:消息的编码
     *@param msg:编码对应消息
     * oneway 主要有两个特性：异步调用和串行化处理
    */
   oneway void onRunResult(boolean isSuccess,int code, String msg);
}