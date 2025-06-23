package com.icbc.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils
import com.icbc.selfserviceticketing.deviceservice.DeviceService
import com.icbc.selfserviceticketing.deviceservice.IDeviceService
import com.icbc.selfserviceticketing.deviceservice.NonContactDeviceListener
import com.icbc.selfserviceticketing.deviceservice.R
import com.icbc.selfserviceticketing.deviceservice.ScannerListener

class RFTestActivity : AppCompatActivity() {
    val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = IDeviceService.Stub.asInterface(service)
            try {
                val rfReader = binder.rfReader
                rfReader.readICCardByNonContact(null, object : NonContactDeviceListener {
                    override fun asBinder(): IBinder? {
                        return null
                    }

                    override fun readCardCallBack(type: String?, data: Bundle?) {
                        ToastUtils.showLong(type,data.toString())
                    }

                })
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            // 服务连接断开时的处理逻辑
            Log.d("TEST", "onServiceDisconnected: ")
        }
    }
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RFTestActivity::class.java)
            context.startActivity(intent)
        }
    }
    private val editScanner: EditText by lazy { findViewById(R.id.tvNumber) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, DeviceService::class.java)
        bindService(intent, mConnection, BIND_AUTO_CREATE or BIND_DEBUG_UNBIND)
    }

    override fun onDestroy() {
        super.onDestroy()
        runCatching {
            unbindService(mConnection)
        }
    }
}