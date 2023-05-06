package com.icbc.selfserviceticketing.deviceservice;

/**
 * 身份证读卡器对象<br/>
 * 实现读取身份证姓名、身份证号、地址、图像等功能。
 * @author: icbc
 */
import com.icbc.selfserviceticketing.deviceservice.DeviceListener;

interface IIDCard {   

    /**
        设备控制
    */
    int OpenDevice(int DeviceID, String deviceFile, String szPort, String szParam);
    int CloseDevice(int DeviceID);
    int GetDeviceStatus(int DeviceID);

	/**
	 * 读取身份证卡片信息
	 * @param format - 读取身份证卡片信息
	 * <ul>
	 * </ul>
	 */
    void commandNonBlock(String cmd, in Bundle params, DeviceListener listener) ;

	/**
	 * 停止读卡
	 */ 
    Bundle commandBlock(String cmd, in Bundle params); //停止读卡
    	
}
