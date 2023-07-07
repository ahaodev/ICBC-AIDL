package com.icbc.selfserviceticketing.deviceservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import com.icbc.selfserviceticketing.deviceservice.idcard.IDCardProxy
import com.icbc.selfserviceticketing.deviceservice.printer.PrinterProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class DeviceService : Service() {
    private val serviceJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + serviceJob)
    override fun onBind(intent: Intent): IBinder? {
        return object : IDeviceService.Stub() {
            @Throws(RemoteException::class)
            override fun getISpeech(): ISpeech? {
                return null
            }

            @Throws(RemoteException::class)
            override fun getScanner(cameraId: Int): IScanner {
                return ScannerSuperLead(applicationContext)
            }

            @Throws(RemoteException::class)
            override fun getDeviceInfo(): IDeviceInfo {
                return DeviceInfo(this@DeviceService)
            }

            @Throws(RemoteException::class)
            override fun getPrinter(bule_mac: String): IPrinter {
                return PrinterProxy(applicationContext)
            }

            @Throws(RemoteException::class)
            override fun getIIDCard(): IIDCard {
//                return IDCardProxy(this@DeviceService, scope)
                return IDCard(applicationContext)
            }

            @Throws(RemoteException::class)
            override fun getSmartCash(): ISmartCash? {
                return null
            }

            @Throws(RemoteException::class)
            override fun getFaceDetector(): IFaceDetector? {
                return null
            }

            @Throws(RemoteException::class)
            override fun getRFReader(): IRFReader {
                return RFReader()
            }

            @Throws(RemoteException::class)
            override fun getGateOperator(): IGateOperator {
                return GateOperator()
            }

            @Throws(RemoteException::class)
            override fun getDeviceReboot(): IDeviceReboot? {
                return null
            }

            @Throws(RemoteException::class)
            override fun getDeviceShutdown(): IDeviceShutdown? {
                return null
            }

            @Throws(RemoteException::class)
            override fun getDeviceWhiteLight(): IDeviceWhiteLight? {
                return null
            }

            @Throws(RemoteException::class)
            override fun getDeviceInfraredLed(): IDeviceInfraredLed? {
                return null
            }

            @Throws(RemoteException::class)
            override fun getDeviceTemperature(): IDeviceTemperature {
                return Temperaturer()
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        serviceJob.cancel()
    }
}