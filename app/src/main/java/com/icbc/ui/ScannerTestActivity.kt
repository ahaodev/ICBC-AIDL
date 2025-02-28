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
import com.icbc.selfserviceticketing.deviceservice.R
import com.icbc.selfserviceticketing.deviceservice.ScannerListener

class ScannerTestActivity : AppCompatActivity() {
    val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = IDeviceService.Stub.asInterface(service)
            try {
                val scanner = binder.getScanner(1)
                scanner.startScan(Bundle(), 100000, object : ScannerListener {
                    @Throws(RemoteException::class)
                    override fun onSuccess(result: Bundle) {
                        val barCode = result.getString("barcode", "")
                        ToastUtils.showLong(barCode)
                        runOnUiThread {
                            editScanner.setText(barCode)
                        }
                    }

                    @Throws(RemoteException::class)
                    override fun onError(error: Int, message: String) {
                        ToastUtils.showLong(message)
                    }

                    @Throws(RemoteException::class)
                    override fun onTimeout() {
                        ToastUtils.showLong("Time out")
                    }

                    @Throws(RemoteException::class)
                    override fun onCancel() {
                    }

                    override fun asBinder(): IBinder? {
                        return null
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
            val intent = Intent(context, ScannerTestActivity::class.java)
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