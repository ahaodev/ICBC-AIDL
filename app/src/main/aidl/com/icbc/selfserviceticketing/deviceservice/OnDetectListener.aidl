// DeviceListener.aidl
package com.icbc.selfserviceticketing.deviceservice;

// Declare any non-default types here with import statements
/**
* 人脸识别检测器结果监听器
* */
interface OnDetectListener {
    /**
    * 检测成功
    * @param result 检测结果
    * <ul>
    *   <li>imageData(byte[]) - 一张分辨率640x480，大小小于100K的图片数据</li>
    *   <li>faceRect(double[]) - 图片中人脸的坐标， 数组中的坐标顺序为{left, top, right, bottom}</li>
    *   <li>faceScore(String) - 人脸评分值， 返回人脸的可信度。取值范围0~99</li>
    *   <li>aliveInfo(String) – 活检信息，具体内容见补充说明部分</li>
            aliveInfo-活检信息（定长50字节）：
            1.算法厂商标识码（3字节）：
              当未进行活检时，本字段填写‘000’，后续字段以空格填充。(具体编码由中国银联认证办公室根据算法 检测情况统一分配)
            2.活检方式（2字节）：
              01:3D结构光，02:红外双目，03:TOF
            3.活检算法能力（1字节）
              A-基本级，B-增强级(依据配套技术规范对活检评级要求)
            4.活检算法信息（20字节）：
               算法名称、版本，应与算法检测时相关信息一致，不足位以空格填充
            5.摄像头信息（20字节）：
              摄像头厂商、型号，应与算法检测时相关信息一致，不足为以空格填充
            6.活检通过阈值（2字节）：
              取值范围: 00-99(算法为受理终端配置的活检通过分值)
            7.活检评分（2字节）：
              取值范围: 00-99，如实际评分为100，以99上送(每次识别时实际活检评分)

    * </ul>
    */
    void onSuccess(in Bundle result);

    /**
    * 检测出错
    * @param error - 错误码
    * @param message - 错误描述
    */
    void onError(int error, String message);
}
