package com.icbc.selfserviceticketing.deviceservice.idcard

import android.content.Context
import android.os.Bundle
import android.os.RemoteException
import com.icbc.selfserviceticketing.deviceservice.DeviceListener
import com.icbc.selfserviceticketing.deviceservice.IIDCard
import kotlinx.coroutines.CoroutineScope

class IDCardProxy(context: Context, scope: CoroutineScope) : IIDCard.Stub() {
    private var iProxyIDCard: IProxyIDCard

    init {
        iProxyIDCard = IDM40(context, scope)
    }


    @Throws(RemoteException::class)
    override fun OpenDevice(
        DeviceID: Int,
        deviceFile: String,
        szPort: String,
        szParam: String
    ): Int {
        return iProxyIDCard.OpenDevice(DeviceID, deviceFile, szPort, szParam)
    }

    @Throws(RemoteException::class)
    override fun CloseDevice(DeviceID: Int): Int {
        return iProxyIDCard.CloseDevice(DeviceID)
    }

    @Throws(RemoteException::class)
    override fun GetDeviceStatus(DeviceID: Int): Int {
        return iProxyIDCard.GetDeviceStatus(DeviceID)
    }

    @Throws(RemoteException::class)
    override fun commandNonBlock(cmd: String, params: Bundle, listener: DeviceListener) {
        iProxyIDCard.commandNonBlock(cmd, params, listener)
    }

    @Throws(RemoteException::class)
    override fun commandBlock(cmd: String, params: Bundle): Bundle {
        return iProxyIDCard.commandBlock(cmd, params)
    }


}