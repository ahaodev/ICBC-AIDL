package com.icbc.selfserviceticketing.deviceservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 *    openDevice: device =  VID:0FE6 PID:811EVendorId=4070ProductId=33054
 *    OnOpen: 0
 *    setPageSize: pageW=155
 *    setPageSize: pageH=125
 *    setPageSize: direction=0
 *    setPageSize: OffsetX=0
 *    setPageSize: OffsetY=0
 *    setPageSize: Builder{pageW=576, pageH=125, direction=0, offsetX=0, offsetY=0}
 *    addText: text=票券名称
 *    addText: fontSize=44 rotation=0 iLeft=0 iTop=14 align=1 pageWidth=92
 *    addText: text=票券编号
 *    addText: fontSize=14 rotation=0 iLeft=3 iTop=35 align=1 pageWidth=29
 *    addText: text=订单号
 *    addText: fontSize=44 rotation=0 iLeft=3 iTop=56 align=1 pageWidth=69
 *    addQrCode: iLeft=98 iTop=77 expectedHeight=45
 *    addText: text=销售渠道
 *    addText: fontSize=14 rotation=0 iLeft=7 iTop=80 align=0 pageWidth=29
 *    addQrCode: iLeft=14 iTop=94 expectedHeight=24
 *    addText: text=0元门票
 *    addText: fontSize=44 rotation=0 iLeft=92 iTop=14 align=1 pageWidth=48
 *    addText: text=T2306020016500600001
 *    addText: fontSize=14 rotation=0 iLeft=32 iTop=35 align=1 pageWidth=54
 *    addText: text=MO202306020000080470
 *    addText: fontSize=44 rotation=0 iLeft=72 iTop=56 align=1 pageWidth=47
 *    addText: text=自助售票机
 *    addText: fontSize=14 rotation=0 iLeft=37 iTop=80 align=0 pageWidth=54
 */
@RunWith(AndroidJUnit4::class)
class PrinterTest {
    private val TAG = "IPrinter"

    @Test
    fun tscPrinterTest() {
        val mConnection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder = IDeviceService.Stub.asInterface(service)
                val printer = binder.getPrinter("TSC")
                Log.d(TAG, "onServiceConnected: 绑定到打印服务${printer.javaClass.simpleName}")
                val status = with(printer) {
                    OpenDevice(1, "", "", "")
                    setPageSize(Bundle().apply {
                        putInt("pageW", 80)
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
                Assert.assertEquals(0, status)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                // 服务连接断开时的处理逻辑
                Log.d(TAG, "onServiceDisconnected: ")
            }
        }
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            DeviceService::class.java
        )
        InstrumentationRegistry.getInstrumentation().targetContext.bindService(
            intent, mConnection, Context.BIND_AUTO_CREATE or Context.BIND_DEBUG_UNBIND
        )
        Thread.sleep(20000)
    }

}