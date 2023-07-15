package com.icbc.selfserviceticketing.deviceservice.idcard

import android.content.Context
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import com.icbc.selfserviceticketing.deviceservice.DeviceListener
import com.icbc.selfserviceticketing.deviceservice.USBManager.ZKUSBManager
import com.icbc.selfserviceticketing.deviceservice.USBManager.ZKUSBManagerListener
import com.zkteco.android.biometric.core.device.ParameterHelper
import com.zkteco.android.biometric.core.device.TransportType
import com.zkteco.android.biometric.core.utils.LogHelper
import com.zkteco.android.biometric.module.idcard.IDCardReader
import com.zkteco.android.biometric.module.idcard.IDCardReaderFactory
import com.zkteco.android.biometric.module.idcard.IDCardType
import com.zkteco.android.biometric.module.idcard.exception.IDCardReaderException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ZKT id180
 * @date 2022/10/9
 * @author hao
 * @since v1.0
 */
class IDM40(private val context: Context, val cScope: CoroutineScope) : IProxyIDCard {
    private var idJob: Job? = null
    private var icJob: Job? = null
    private var zkusbManager: ZKUSBManager? = null
    private var enableReadID = false
    private var repeatRead = false
    var isOpen = true
    private var idCall: ((Int, String, Bundle) -> Unit)? = null

    companion object {
//        const val VID = 6790 //IDR VID
        const val mVendorId = 1024 //IDR VID
        const val mProductId = 50010 //IDR VID
//        const val PID = 29987 //IDR PID
        const val TAG = "IDM40"
    }

    private var idCardReader: IDCardReader? = null

    private val zkusbManagerListener by lazy {
        object : ZKUSBManagerListener {
            override fun onCheckPermission(result: Int) {
                Log.d(TAG, "onCheckPermission:检查权限")
                openDevice()
            }


            override fun onUSBArrived(device: UsbDevice?) {
                Log.d(TAG, "onUSBArrived:发现阅读器接入")
            }

            override fun onUSBRemoved(device: UsbDevice?) {
                //setResult("阅读器USB被拔出")
                Log.d(TAG, "onUSBRemoved: 阅读器USB被拔出")
            }
        }
    }


    private fun initDevice() {
        zkusbManager = ZKUSBManager(context, zkusbManagerListener)
        zkusbManager?.registerUSBPermissionReceiver()
    }

    private var readICListener: ((String) -> Unit?)? = null

    private fun start(idCall: (Int, String, Bundle) -> Unit) {
        if (enableReadID)
            return
        enableReadID = true
        this.idCall=idCall
        initDevice()
        Thread.sleep(500)
        if (!enumSensor()) {
            Log.d(TAG, "start: 找不到设备")
            return
        }
        zkusbManager?.initUSBPermission(mVendorId, mProductId)
    }


    private fun stop() {
        Log.d(TAG, "stop: 停止读卡")
        closeDevice()
    }

    private fun enumSensor(): Boolean {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        for (device in usbManager.deviceList.values) {
            Log.d(TAG, "enumSensor: ${device}")
            if (device.vendorId == mVendorId && device.productId == mProductId) {
                return true
            }
        }
        return false
    }

    private fun openDevice() {
        startIDCardReader()
        try {
            idCardReader?.open(0)
            idJob = cScope.launch(Dispatchers.IO) {
                while (enableReadID) {
                    delay(600)
                    var cardType = 0
                    try {
                        idCardReader?.findCard(0)
                        delay(50)
                        idCardReader?.selectCard(0)
                        delay(50)
                        cardType = idCardReader!!.readCardEx(0, 0);
                    } catch (e: Exception) {
                        if (e is IDCardReaderException) {
                            if (!repeatRead) {
                                Log.d(TAG, "openDevice: 重复读卡")
                                continue//重复读
                            }
                        }
                        if (e is IDCardReaderException) {
                            Log.d(TAG, "openDevice: 读卡失败，错误信息 ${e.message} ")
                            continue
                        }
                        e.printStackTrace()
                    }
                    if (cardType == IDCardType.TYPE_CARD_SFZ || cardType == IDCardType.TYPE_CARD_PRP || cardType == IDCardType.TYPE_CARD_GAT) {
                        Log.d(TAG, "openDevice: cardType ${cardType}")
                        if (cardType == IDCardType.TYPE_CARD_SFZ || cardType == IDCardType.TYPE_CARD_GAT) {
                            val idCardInfo = idCardReader!!.lastIDCardInfo
                            val name = idCardInfo.name
                            val sex = idCardInfo.sex
                            val nation = idCardInfo.nation
                            val born = idCardInfo.birth
                            val licid = idCardInfo.id
                            val depart = idCardInfo.depart
                            val expireDate = idCardInfo.validityTime
                            val addr = idCardInfo.address
                            val passNo = idCardInfo.passNum
                            val visaTimes = idCardInfo.visaTimes
                            var bmpPhoto: Bitmap? = null
                            if (idCardInfo.photolength > 0) {
//                                val buf = ByteArray(WLTService.imgLength)
//                                if (1 == WLTService.wlt2Bmp(idCardInfo.photo, buf)) {
//                                    bmpPhoto = IDPhotoHelper.Bgr2Bitmap(buf)
//                                }
                            }
                            var bundle = Bundle()
                            bundle.putString("IDNumber", licid)
                            idCall?.let {
                                Log.d(TAG, "id read :${licid} ")
                                it(0, "success", bundle)
                            }

                        }
                    }
                }
            }
            Log.d(TAG, "openDevice:打开设备成功，SAMID: ${idCardReader!!.getSAMID(0)}  ")
        } catch (e: IDCardReaderException) {
            Log.d(TAG, "openDevice:打开设备失败 ")
            e.printStackTrace()
        }
    }

    private fun startIDCardReader() {
        if (null != idCardReader) {
            IDCardReaderFactory.destroy(idCardReader)
            idCardReader = null
        }
        // Define output log level
        LogHelper.setLevel(Log.VERBOSE)
        // Start fingerprint sensor
        val idrparams: MutableMap<String, Any> = HashMap()
        idrparams[ParameterHelper.PARAM_KEY_VID] = mVendorId
        idrparams[ParameterHelper.PARAM_KEY_PID] = mProductId
        idCardReader = IDCardReaderFactory.createIDCardReader(context, TransportType.USB, idrparams)
        //idCardReader?.SetBaudRate(115200)
        idCardReader?.setLibusbFlag(true)
    }

    private fun closeDevice() {
        enableReadID = false
        idJob?.cancel()
        icJob?.cancel()
        try {
            idCardReader?.close(0)
        } catch (e: IDCardReaderException) {
            e.printStackTrace()
        }
        zkusbManager?.unRegisterUSBPermissionReceiver()
        zkusbManager == null
        idCardReader?.let {
            IDCardReaderFactory.destroy(it)
        }

    }

    override fun OpenDevice(
        DeviceID: Int,
        deviceFile: String?,
        szPort: String?,
        szParam: String?
    ): Int {
        return 0
    }

    override fun CloseDevice(DeviceID: Int): Int {
        stop()
        return 0
    }

    override fun GetDeviceStatus(DeviceID: Int): Int {
        return 0
    }

    override fun commandNonBlock(cmd: String, params: Bundle, listener: DeviceListener) {
        Log.d(TAG, "commandNonBlock: 注册监听")
        start { status: Int, type: String, build: Bundle ->
            Log.d(TAG, "commandNonBlock: 读到卡 status=$status type=$type")
            listener.callBack("IDCard_Read_OnSuccess", build)
        }
    }

    override fun commandBlock(cmd: String, params: Bundle): Bundle {
        return params
    }
}