package com.icbc.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.icbc.selfserviceticketing.deviceservice.Config
import com.icbc.selfserviceticketing.deviceservice.ConfigProvider
import com.icbc.selfserviceticketing.deviceservice.DeviceService
import com.icbc.selfserviceticketing.deviceservice.IDeviceService
import com.icbc.selfserviceticketing.deviceservice.IPrinter
import com.icbc.selfserviceticketing.deviceservice.PRINT_CSN
import com.icbc.selfserviceticketing.deviceservice.PRINT_T321OR331
import com.icbc.selfserviceticketing.deviceservice.PRINT_TSC310E
import com.icbc.selfserviceticketing.deviceservice.R
import com.icbc.selfserviceticketing.deviceservice.printer.EPSONUSBPrinter
import com.icbc.selfserviceticketing.deviceservice.printer.PrinterProxy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PrinterTestActivity : AppCompatActivity() {
    private var config = Config()
    var iPrinter: IPrinter? = null
    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = IDeviceService.Stub.asInterface(service)
            try {
                iPrinter = binder.getPrinter("auto")
                Log.d("TAG", "onServiceConnected: 绑定到打印服务")
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
            val intent = Intent(context, PrinterTestActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_printer)
        findViewById<Button>(R.id.btnPrinter).setOnClickListener {
            if (null == iPrinter) {
                ToastUtils.showLong("未找到打印机")
                return@setOnClickListener
            }
            lifecycleScope.launch(Dispatchers.IO) {
                printer()
            }
        }

        findViewById<Button>(R.id.selfTestButton).setOnClickListener {
            iPrinter?.let {
                try {
                    if (iPrinter is PrinterProxy) {
                        (iPrinter as PrinterProxy).selfTest()
                    }
                    ToastUtils.showLong("自检中")
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }
        lifecycleScope.launch {
            runCatching {
                val storeConfig = ConfigProvider.readConfig(applicationContext).firstOrNull()
                storeConfig?.let {
                    config = storeConfig
                    findViewById<TextView>(R.id.tvPrinterType).text = storeConfig.toString()
                }
            }
        }
    }

    private fun printer() {
        try {
            when (config.printerType) {
                PRINT_TSC310E -> {
                    val status = printTSC(iPrinter!!)
                    LogUtils.d("Status =${status}")
                    ToastUtils.showLong("打印状态${status}")
                }

                PRINT_CSN -> {
                    val status = printCSN(iPrinter!!)
                    LogUtils.d("Status =${status}")
                    ToastUtils.showLong("打印状态${status}")
                }

                PRINT_T321OR331 -> {
                    val status = printTSC(iPrinter!!)
                    LogUtils.d("Status =${status}")
                    ToastUtils.showLong("打印状态${status}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtils.showLong(e.toString())
        }
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

    private fun printTSC(printer: IPrinter): Int {
        val status = with(printer) {
            OpenDevice(1, "", "", "")
            setPageSize(Bundle().apply {
                putInt("pageW", config.width.toInt())
                putInt("pageH", config.height.toInt())
                putInt("direction", 0)
                putInt("OffsetX", 0)
                putInt("OffsetY", 0)
            })
            addQrCode(Bundle().apply {
                putInt("elementType", 1)
                putInt("iLeft", 1)
                putInt("iTop", 1)
                putInt("expectedHeight", 18)
            }, "60214102006512<MjAwMDAwMTkyNDIwMjMtMDctMTQyMDIzLTA3LTE0>")
            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 23)
                putInt("iTop", 1)
                putInt("align", 1)
                putInt("pageWidth", 8)
            }, "票卷名称")
            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 50)
                putInt("iTop", 50)
                putInt("align", 1)
                putInt("pageWidth", 8)
            }, "末端测试")
            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 23)
                putInt("iTop", 8)
                putInt("align", 1)
                putInt("pageWidth", 8)
            }, "票券编号")

            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 1)
                putInt("iTop", 18)
                putInt("align", 1)
                putInt("pageWidth", 8)
            }, "订单号")

            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 1)
                putInt("iTop", 25)
                putInt("align", 1)
                putInt("pageWidth", 8)
            }, "销售时间")
            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 1)
                putInt("iTop", 30)
                putInt("align", 1)
                putInt("pageWidth", 72)
            }, "从前有座山，山里有座庙，庙里有个老和尚，老和尚给小和尚讲故事")
            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 32)
                putInt("iTop", 2)
                putInt("align", 1)
                putInt("pageWidth", 16)
            }, "hao88打印测试票")
            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 32)
                putInt("iTop", 8)
                putInt("align", 1)
                putInt("pageWidth", 17)
            }, "T2307140030802100001")
            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 8)
                putInt("iTop", 18)
                putInt("align", 1)
                putInt("pageWidth", 16)
            }, "MO202307140000966185")

            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 10)
                putInt("iTop", 25)
                putInt("align", 1)
                putInt("pageWidth", 14)
            }, "14:00:09")
            endPrintDoc()
        }
        return status
    }

    private fun printCSN(printer: IPrinter): Int {
        val status = with(printer) {
            OpenDevice(1, "", "", "")
            setPageSize(Bundle().apply {
                putInt("pageW", 60)
                putInt("pageH", 60)
                putInt("direction", 0)
                putInt("OffsetX", 0)
                putInt("OffsetY", 0)
            })
            addQrCode(Bundle().apply {
                putInt("elementType", 1)
                putInt("iLeft", 1)
                putInt("iTop", 1)
                putInt("expectedHeight", 18)
            }, "60214102006512<MjAwMDAwMTkyNDIwMjMtMDctMTQyMDIzLTA3LTE0>")
            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 23)
                putInt("iTop", 2)
                putInt("align", 1)
                putInt("pageWidth", 8)
            }, "票卷名称")
            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 50)
                putInt("iTop", 50)
                putInt("align", 1)
                putInt("pageWidth", 8)
            }, "末端测试")
            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 23)
                putInt("iTop", 8)
                putInt("align", 1)
                putInt("pageWidth", 8)
            }, "票券编号")

            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 1)
                putInt("iTop", 18)
                putInt("align", 1)
                putInt("pageWidth", 8)
            }, "订单号")

            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 1)
                putInt("iTop", 25)
                putInt("align", 1)
                putInt("pageWidth", 8)
            }, "销售时间")
            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 1)
                putInt("iTop", 30)
                putInt("align", 1)
                putInt("pageWidth", 72)
            }, "从前有座山，山里有座庙，庙里有个老和尚，老和尚给小和尚讲故事")
            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 32)
                putInt("iTop", 2)
                putInt("align", 1)
                putInt("pageWidth", 16)
            }, "hao88打印测试票")
            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 32)
                putInt("iTop", 8)
                putInt("align", 1)
                putInt("pageWidth", 17)
            }, "T2307140030802100001")
            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 8)
                putInt("iTop", 18)
                putInt("align", 1)
                putInt("pageWidth", 16)
            }, "MO202307140000966185")

            addText(Bundle().apply {
                putString("fontName", "")
                putInt("fontSize", 18)
                putInt("rotation", 0)
                putInt("iLeft", 10)
                putInt("iTop", 25)
                putInt("align", 1)
                putInt("pageWidth", 14)
            }, "14:00:09")
            endPrintDoc()
        }
        return status
    }
}