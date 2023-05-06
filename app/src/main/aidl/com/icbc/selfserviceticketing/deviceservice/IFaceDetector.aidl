// IFaceDetector.aidl
package com.icbc.selfserviceticketing.deviceservice;

import com.icbc.selfserviceticketing.deviceservice.OnDetectListener;
// Declare any non-default types here with import statements

/**
* 人脸检测操作对象
* */
interface IFaceDetector {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    /**
    * 功能：启动人脸检测
    * params - 请求参数：
    *     timeOut int:超时时间（单位：秒），默认60s（如果值<=0,则设置不超时）
    *     aliveEnable boolean：是否支持活体检测，默认支持。 (如果机型不支持活检， 入参 true,会返回失败)
    *     autoFlashLight boolean：在光线不足时，自动开启闪光灯。 默认 false
    *     voicePrompt boolean：识别过程的语音提示开关，默认false关闭
    *     voiceVolume int：语音提示音量[0, 100]。 为0时表示随终端的多媒体音量，默认 0。
    *     confirmFace boolean：是否显示【确认】按钮。识别到人脸后，点击后才会退出页面。
    *     titleText String：在预览页面顶部的水平居中方向添加一个标题文本
    *     amount long：金额(分)，非空时在UI上显示金额
    *
    * listener - 脸识别检测器结果 回调对象
    * */
    void startDetect(in Bundle params, OnDetectListener listener);

    /**
    * 功能：停止检测,退出识别人脸的预览页面
    * */
    void stopDetect();


    /**
    * 功能：获取人脸识别固件信息
    * @return Bundle-返回参数，具体如下所示
                sdkVersion String：人脸识别sdk版本号
                cameraFactory String：摄像头厂商
                cameraModel String：摄像头模组（型号）
                algorithmicVersion String：算法版本,该摄像头对应使用的apk版本号
                algorithmicFactory String：提供人脸算法的厂商识别码
                aliveStyle String：活检方式，01-3D结构光，02-双目，03-TOF
                aliveThreshold String：活检通过阀值，取值范围：00-99，算法为受理终端配置的活检通过分值
                aliveAlgorithmicClass String：活检算法能力，A-基本级，B-增强级
                aliveAlgorithmicInfo String：活检算法信息：算法名称、版本，应与刷分检测时相关信息一致
    * */
    Bundle getFirmwareInfo();
}
