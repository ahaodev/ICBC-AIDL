package com.icbc.selfserviceticketing.deviceservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.icbc.selfserviceticketing.deviceservice.idcard.IDCardProxy
import com.icbc.selfserviceticketing.deviceservice.printer.PrinterProxy
import com.icbc.selfserviceticketing.deviceservice.scanner.ScannerSuperLead
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
                return ScannerSuperLead(
                    applicationContext
                )
            }

            @Throws(RemoteException::class)
            override fun getDeviceInfo(): IDeviceInfo {
                Log.d("DeviceService", "getDeviceInfo${intent}")
                return DeviceInfo(this@DeviceService)
            }

            @Throws(RemoteException::class)
            override fun getPrinter(bule_mac: String): IPrinter {
                Log.d("DeviceService", "getPrinter${bule_mac}")
                return PrinterProxy(applicationContext)
            }

            @Throws(RemoteException::class)
            override fun getIIDCard(): IIDCard {
                Log.d("DeviceService", "getIIDCard")
                return IDCardProxy(this@DeviceService, scope)
                //return IDCard(applicationContext)
            }

            @Throws(RemoteException::class)
            override fun getSmartCash(): ISmartCash? {
                Log.d("DeviceService", "getSmartCash")
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
                Log.d("DeviceService", "getGateOperator")
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
        Log.d("DeviceService", "onUnbind${intent}")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d("DeviceService", "onDestroy")
        super.onDestroy()
        scope.cancel()
        serviceJob.cancel()
    }
}