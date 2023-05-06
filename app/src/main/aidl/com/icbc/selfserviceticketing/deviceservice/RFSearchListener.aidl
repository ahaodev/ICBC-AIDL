// RFSearchListener.aidl
package com.icbc.selfserviceticketing.deviceservice;

// Declare any non-default types here with import statements

/**
 * 非接触式IC卡读卡设备 寻卡过程监听接口定义
 *
 */
interface RFSearchListener {
    /**
     * 检测到RF卡
     * @param cardType - 卡类型
     *        0 -  S50卡(M1)
     *        1 -  S70卡(M1)
     *        2 -  PRO卡(M1)
     *        3 -  PRO卡 支持S50驱动与PRO驱动
     *        4 -  PRO卡 支持S70驱动与PRO驱动
     *        5 - cpu卡
     */
     void onCardPass(int cardType);


    /**
     * 寻卡失败，回调
     * @param error - 卡类型
     *        162 -  通讯错误
     *        163 -  卡片返回数据不符合规范要求
     *        164 -  感应区内有多卡存在
     *        179 -  PRO卡 或TypeB 卡未激活
     *        65281 -  主控服务异常
     *        65282 - 请求异常
     * @param message - 错误描述
     */
     void onFail(int cardType,String message);
}
