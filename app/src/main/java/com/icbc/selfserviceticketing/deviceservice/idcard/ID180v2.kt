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
import com.zkteco.android.IDReader.IDPhotoHelper
import com.zkteco.android.IDReader.WLTService
import com.zkteco.android.biometric.core.device.ParameterHelper
import com.zkteco.android.biometric.core.device.TransportType
import com.zkteco.android.biometric.core.utils.LogHelper
import com.zkteco.android.biometric.module.idcard.IDCardReader
import com.zkteco.android.biometric.module.idcard.IDCardReaderFactory
import com.zkteco.android.biometric.module.idcard.IDCardType
import com.zkteco.android.biometric.module.idcard.exception.IDCardReaderException
import com.zkteco.android.biometric.module.idcard.meta.IDCardInfo
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
class ID180v2(private val context: Context, val cScope: CoroutineScope) : IProxyIDCard {
    private var idJob: Job? = null
    private var icJob: Job? = null
    private var zkusbManager: ZKUSBManager? = null
    private var enableReadID = false
    private var repeatRead = false
    var isOpen = true
    private var idCall: ((Int, String, Bundle) -> Unit)? = null

    companion object {
//        const val VID = 6790 //IDR VID
//        const val PID = 29987 //IDR PID
        const val VID = 1024 //IDR VID
        const val PID = 50010 //IDR PID
        const val TAG = "ID180v2"
    }

    private var idCardReader: IDCardReader? = null

    private val zkusbManagerListener by lazy {
        object : ZKUSBManagerListener {
            override fun onCheckPermission(result: Int) {
                openDevice()
                Log.d(TAG, "onCheckPermission:检查权限")
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
        if (enableReadID) return
        this.idCall = idCall
        enableReadID = true
        initDevice()
        if (!enumSensor()) {
            Log.d(TAG, "start: 找不到设备")
            return
        }
        zkusbManager?.initUSBPermission(VID, PID)
    }


    private fun stop() {
        Log.d(TAG, "stop: 停止读卡")
        closeDevice()
    }

    private fun enumSensor(): Boolean {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        for (device in usbManager.deviceList.values) {
            if (device.vendorId == VID && device.productId == PID) {
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
                        idCardReader?.selectCard(0)
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
                            val bundle = createBundle(idCardInfo)
                            idCall?.let {
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

    private fun createBundle(idCardInfo: IDCardInfo): Bundle {
        return with(Bundle()) {
            //姓名
            putString("IDName", idCardInfo.name)
            //身份证号
            putString("IDNumber", idCardInfo.id)
            //民族
            putString("IDNation", idCardInfo.nation)
            //性别
            putString("IDSex", idCardInfo.sex)
            //出生日期
            putString("IDBirthday", idCardInfo.birth)
            //住址
            putString("IDAddress", idCardInfo.address)
            //签发机关
            putString("IDSignedDepartment", idCardInfo.depart)
            //有效期限
            val validityTime = idCardInfo.validityTime
            val split =
                validityTime.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            putString("IDEffectiveDate", split[0])
            putString("IDExpiryDate", split[1])
            if (idCardInfo.photolength > 0) {
                val buf = ByteArray(WLTService.imgLength)
                if (1 == WLTService.wlt2Bmp(idCardInfo.photo, buf)) {
                    val bitmap = IDPhotoHelper.Bgr2Bitmap(buf)
                    putParcelable("IDImage", bitmap)
                }
            }
            this
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
        idrparams[ParameterHelper.PARAM_KEY_VID] = VID
        idrparams[ParameterHelper.PARAM_KEY_PID] = PID
        idCardReader = IDCardReaderFactory.createIDCardReader(context, TransportType.USB, idrparams)
        idCardReader?.SetBaudRate(115200)
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