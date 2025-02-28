package com.icbc.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.icbc.selfserviceticketing.deviceservice.DeviceListener
import com.icbc.selfserviceticketing.deviceservice.DeviceService
import com.icbc.selfserviceticketing.deviceservice.IDeviceService
import com.icbc.selfserviceticketing.deviceservice.R

class IDCardTestActivity : AppCompatActivity() {
    val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = IDeviceService.Stub.asInterface(service)
            try {
                val iIdCard = binder.getIIDCard()
                iIdCard.OpenDevice(1, "", "", "s")
                iIdCard.commandNonBlock("cmd", Bundle(), object : DeviceListener {
                    @Throws(RemoteException::class)
                    override fun callBack(type: String, data: Bundle) {
                        val idName = data.getString("IDName")
                        val idNumber = data.getString("IDNumber")
                        val idImage = data.getParcelable<Bitmap>("IDImage")
                        runOnUiThread {
                            tvName.text = idName
                            tvNumber.text = idNumber
                            imgHead.setImageBitmap(idImage)
                        }
                    }

                    override fun asBinder(): IBinder? {
                        return null
                    }
                })

                //testScanner(scanner);
                Log.d("iIdCard", "idcard binder: ")
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
            val intent = Intent(context, IDCardTestActivity::class.java)
            context.startActivity(intent)
        }
    }
    private val tvName: TextView by lazy { findViewById(R.id.tvName) }
    private val tvNumber: TextView by lazy { findViewById(R.id.tvNumber) }
    private val imgHead: ImageView by lazy { findViewById(R.id.imgHead) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idcard)

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