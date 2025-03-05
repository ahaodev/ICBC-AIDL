package com.icbc.selfserviceticketing.deviceservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.LogUtils
import com.icbc.selfserviceticketing.deviceservice.idcard.IDCardProxy
import com.icbc.selfserviceticketing.deviceservice.printer.PrinterProxy
import com.icbc.selfserviceticketing.deviceservice.scanner.ScannerSuperLead
import com.icbc.selfserviceticketing.deviceservice.speech.SpeechProxy
import com.icbc.selfserviceticketing.deviceservice.speech.TTSUtils
import com.icbc.selfserviceticketing.deviceservice.utils.CrashListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class DeviceService : Service() {
    private val serviceJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onCreate() {
        super.onCreate()
//        CrashUtils.init(CrashListener())
    }

    override fun onBind(intent: Intent): IBinder {
        LogUtils.file("onBind")
        var config =Config()
        runBlocking {
            runCatching {
                config=ConfigProvider.readConfig(applicationContext).first()
            }
        }
        LogUtils.file(config)
        val tts = TTSUtils.getInstance()
        return object : IDeviceService.Stub() {
            @Throws(RemoteException::class)
            override fun getISpeech(): ISpeech? {
                tts.init(applicationContext) {
                    LogUtils.d("init tts")
                }
                return SpeechProxy(tts)
            }

            @Throws(RemoteException::class)
            override fun getScanner(cameraId: Int): IScanner {
                LogUtils.file(cameraId)
                val ttys =config.csnDevPort
                if (config.enableScannerSuperLeadSerialPortMode){
                    return Scanner(applicationContext,ttys)
                }
                return ScannerSuperLead(
                    applicationContext
                )
            }

            @Throws(RemoteException::class)
            override fun getDeviceInfo(): IDeviceInfo {
                Log.d("DeviceService", "getDeviceInfo${intent}")
                LogUtils.file("getDevice info ")
                return DeviceInfo(this@DeviceService)
            }

            @Throws(RemoteException::class)
            override fun getPrinter(bule_mac: String): IPrinter {
                Log.d("DeviceService", "getPrinter${bule_mac}")
                LogUtils.file("getPrinter")
                return PrinterProxy(applicationContext,config)
            }

            @Throws(RemoteException::class)
            override fun getIIDCard(): IIDCard {
                Log.d("DeviceService", "getIIDCard")
                LogUtils.file("getIIDCard")
                return IDCardProxy(this@DeviceService, scope,config)
                //return IDCard(applicationContext)
            }

            @Throws(RemoteException::class)
            override fun getSmartCash(): ISmartCash? {
                Log.d("DeviceService", "getSmartCash")
                LogUtils.file("getSmartCash")
                return null
            }

            @Throws(RemoteException::class)
            override fun getFaceDetector(): IFaceDetector? {
                LogUtils.file("getFaceDetector")
                return null
            }

            @Throws(RemoteException::class)
            override fun getRFReader(): IRFReader {
                LogUtils.file("getRFReader")
                return RFReader()
            }

            @Throws(RemoteException::class)
            override fun getGateOperator(): IGateOperator {
                Log.d("DeviceService", "getGateOperator")
                LogUtils.file("getGateOperator")
                return GateOperator()
            }

            @Throws(RemoteException::class)
            override fun getDeviceReboot(): IDeviceReboot? {
                LogUtils.file("getDeviceReboot")
                return null
            }

            @Throws(RemoteException::class)
            override fun getDeviceShutdown(): IDeviceShutdown? {
                LogUtils.file("getDeviceShutdown")
                return null
            }

            @Throws(RemoteException::class)
            override fun getDeviceWhiteLight(): IDeviceWhiteLight? {
                LogUtils.file("getDeviceWhiteLight")
                return null
            }

            @Throws(RemoteException::class)
            override fun getDeviceInfraredLed(): IDeviceInfraredLed? {
                LogUtils.file("getDeviceInfraredLed")
                return null
            }

            @Throws(RemoteException::class)
            override fun getDeviceTemperature(): IDeviceTemperature {
                LogUtils.file("getDeviceTemperature")
                return Temperaturer()
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("DeviceService", "onUnbind${intent}")
        LogUtils.file("onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d("DeviceService", "onDestroy")
        LogUtils.file("onDestroy")
        super.onDestroy()
        scope.cancel()
        serviceJob.cancel()
    }
}