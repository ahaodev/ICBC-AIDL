package com.icbc.selfserviceticketing.deviceservice;

import com.icbc.selfserviceticketing.deviceservice.IDeviceInfo;
import com.icbc.selfserviceticketing.deviceservice.IIDCard;
import com.icbc.selfserviceticketing.deviceservice.IPrinter;
import com.icbc.selfserviceticketing.deviceservice.ISpeech;
import com.icbc.selfserviceticketing.deviceservice.ISmartCash;
import com.icbc.selfserviceticketing.deviceservice.IScanner;
import com.icbc.selfserviceticketing.deviceservice.IFaceDetector;
import com.icbc.selfserviceticketing.deviceservice.IRFReader;
import com.icbc.selfserviceticketing.deviceservice.IGateOperator;
import com.icbc.selfserviceticketing.deviceservice.IDeviceReboot;
import com.icbc.selfserviceticketing.deviceservice.IDeviceShutdown;
import com.icbc.selfserviceticketing.deviceservice.IDeviceWhiteLight;
import com.icbc.selfserviceticketing.deviceservice.IDeviceInfraredLed;
import com.icbc.selfserviceticketing.deviceservice.IDeviceTemperature;

/**
 * 设备服务对象，提供范围终端各外设对象的服务接口
 * @author: icbc
 */
interface IDeviceService {

    /**
    *语音播放对象
    * @return ISpeech 获取语音播报对象，详见ISpeech.aidl定义
    */
    ISpeech getISpeech();

    /**
    *扫码器对象
    @return IScanner 获取扫描器操作对象，详见IScanner.aidl定义
    */
    IScanner getScanner(int cameraId);

    /**
     * 设备信息操作对象
     * @return IDeviceInfo对象，参见IDeviceInfo.aidl类
     */
    IDeviceInfo getDeviceInfo();
    
     /**
     * 获取打印机操作对象
     * @param bule_mac 蓝牙打印机mac地址
     * @return IPrinter对象，参见IPrinter.aidl类
     */
    IPrinter getPrinter(String bule_mac);
    
    /**
     * 获取身份证读卡器对象
     * @return IIDCard 身份证读卡器模块操作对象，详见IIDCard.aidl定义
     */
    IIDCard getIIDCard();


    /**
    *钱箱对象
    * @return ISmartCash 获取钱箱操作对象，详见ISmartCash.aidl定义
    */
    ISmartCash getSmartCash();

    /**
    * 人脸识别操作对象
    * @return IFaceDetector  获取人脸识别操作对象，详见IFaceDetector.aidl定义
    */
    IFaceDetector getFaceDetector();


    /**
    *
    * 非接触式IC卡设备对象
    * @return IRFReader   获取非接触式IC卡设备对象，详见IRFReader.aidl定义
    */
    IRFReader getRFReader();


    /**
    *
    * 闸机设备
    * @return IRFReader   获取闸机设备对象，详见IGateOperator.aidl定义
    */
    IGateOperator getGateOperator();

    /**
    *
    * 设备重启操作
    * @return IDeviceReboot  获取设备重启操作的对象，详见IDeviceReboot.aidl定义
    */
    IDeviceReboot getDeviceReboot();

    /**
    *
    * 设备关机操作
    * @return IDeviceShutdown   获取设备关机操作对象，详见IDeviceShutdown.aidl定义
    */
    IDeviceShutdown getDeviceShutdown();

    /**
    *
    * 设备白色补光灯操作
    * @return IDeviceWhiteLight   获取设备白色补光灯操作对象，详见IDeviceWhiteLight.aidl定义
    */
    IDeviceWhiteLight getDeviceWhiteLight();

    /**
    *
    * 设备红外灯操作
    * @return IDeviceInfraredLed   获取设备红外灯操作对象，详见IDeviceInfraredLed.aidl定义
    */
    IDeviceInfraredLed getDeviceInfraredLed();


    /**
    *
    * 设备测量温度模块操作
    * @return IDeviceInfraredLed   获取设备测量温度模块操作对象，IDeviceTemperature.aidl定义
    */
    IDeviceTemperature getDeviceTemperature();
}
