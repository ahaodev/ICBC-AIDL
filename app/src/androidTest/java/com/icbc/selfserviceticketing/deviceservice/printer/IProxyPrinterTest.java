package com.icbc.selfserviceticketing.deviceservice.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class IProxyPrinterTest {
    @Test
    public void printTest() throws InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        IProxyPrinter printer = new CanvasPrinter(appContext);
        Thread.sleep(1000);
        PrinterUchiTest uchiTest = new PrinterUchiTest(printer);
        uchiTest.printer();
        //testPrinterICBCTest(printer);
        printer.endPrintDoc();
        printer.CloseDevice(1);
    }

    @Test
    public void printHaoImg() throws InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        IProxyPrinter printer = new HaoPrinter(appContext);
        Thread.sleep(1000);
        PrinterUchiTest uchiTest = new PrinterUchiTest(printer);
        uchiTest.printer();
        //testPrinterICBCTest(printer);
        printer.endPrintDoc();
        printer.CloseDevice(1);
    }

    @Test
    public void printBitmapTest() throws InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        IProxyPrinter printer = new LinePrinter(appContext);
        Thread.sleep(1000);
        android.os.Bundle format = new Bundle();
        format.putInt("rotation", 1);
        format.putInt("iLeft", 1);
        format.putInt("iTop", 1);
        format.putInt("iWidth", 576);
        format.putInt("iHeight", 1);
//        QRCodeUtils qrCodeUtils = new QRCodeUtils();
//        Bitmap bitmap = qrCodeUtils.generateImage("HAHAH", 200);
//        Bitmap printerImg = qrCodeUtils.createCustomBitmap(bitmap, "卧槽");
        BitmapPrinter bPrinter = new BitmapPrinter();
//        bPrinter.drawQrCode("卧槽你打野",200,10,10);
//        bPrinter.drawText("卧槽你打野",10,10);
        Bitmap printerImg = bPrinter.drawEnd();
        String bas64 = bitmapToBase64(printerImg);
        printer.addImage(format, bas64);
        printer.CloseDevice(1);
        //bPrinter.recycle();
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(byteArray, byteArray.length);
    }

    private void testPrinterICBCTest(IProxyPrinter printer) {
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
        android.os.Bundle pageBundle = new Bundle();
        pageBundle.putInt("pageW", 155);
        pageBundle.putInt("pageH", 125);
        pageBundle.putInt("OffsetX", 0);
        pageBundle.putInt("OffsetY", 0);
        pageBundle.putInt("direction", 0);
        printer.setPageSize(pageBundle);


        TextBuilder textBuilder = new TextBuilder();
        String text = "票券名称";
        textBuilder.fontSize = 44;
        textBuilder.rotation = 0;
        textBuilder.iLeft = 0;
        textBuilder.iTop = 14;
        textBuilder.align = 1;
        textBuilder.pageWidth = 92;
        printerText(printer, text, textBuilder);
        /*
         * addText: text=票券编号
         * addText: fontSize=14 rotation=0 iLeft=3 iTop=35 align=1 pageWidth=29
         */
        TextBuilder numberBuilder = new TextBuilder();
        String noTitle = "票券编号";
        textBuilder.fontSize = 14;
        textBuilder.rotation = 0;
        textBuilder.iLeft = 3;
        textBuilder.iTop = 35;
        textBuilder.align = 1;
        textBuilder.pageWidth = 29;
        printerText(printer, noTitle, numberBuilder);
        /*
         * addText: text=订单号
         * addText: fontSize=44 rotation=0 iLeft=3 iTop=56 align=1 pageWidth=69
         */
        TextBuilder dingdanBuilder = new TextBuilder();
        String dingdanTitle = "订单号";
        textBuilder.fontSize = 44;
        textBuilder.rotation = 0;
        textBuilder.iLeft = 3;
        textBuilder.iTop = 56;
        textBuilder.align = 1;
        textBuilder.pageWidth = 69;
        printerText(printer, dingdanTitle, dingdanBuilder);
        /*
         *  addText: text=销售渠道
         *  addText: fontSize=14 rotation=0 iLeft=7 iTop=80 align=0 pageWidth=29
         */
        TextBuilder qdBuilder = new TextBuilder();
        String qdTitle = "订单号";
        textBuilder.fontSize = 14;
        textBuilder.rotation = 0;
        textBuilder.iLeft = 7;
        textBuilder.iTop = 80;
        textBuilder.align = 0;
        textBuilder.pageWidth = 29;
        printerText(printer, qdTitle, qdBuilder);
        /*
         *   addQrCode: iLeft=14 iTop=94 expectedHeight=24
         */
        android.os.Bundle qrBundle = new Bundle();
        qrBundle.putInt("iLeft", 14);
        qrBundle.putInt("iTop", 95);
        qrBundle.putInt("expectedHeight", 24);
        printer.addQrCode(qrBundle, "草泥马");
        /*    addText: text=0元门票
         *    addText: fontSize=44 rotation=0 iLeft=92 iTop=14 align=1 pageWidth=48
         */
        TextBuilder moneyBuilder = new TextBuilder();
        String money = "0元门票";
        textBuilder.fontSize = 44;
        textBuilder.rotation = 0;
        textBuilder.iLeft = 92;
        textBuilder.iTop = 14;
        textBuilder.align = 1;
        textBuilder.pageWidth = 48;
        printerText(printer, money, moneyBuilder);
        /*    addText: text=T2306020016500600001
         *    addText: fontSize=14 rotation=0 iLeft=32 iTop=35 align=1 pageWidth=54
         */
        TextBuilder tBuilder = new TextBuilder();
        String t = "T2306020016500600001";
        textBuilder.fontSize = 14;
        textBuilder.rotation = 0;
        textBuilder.iLeft = 32;
        textBuilder.iTop = 35;
        textBuilder.align = 1;
        textBuilder.pageWidth = 54;
        printerText(printer, t, tBuilder);
        /*    addText: text=MO202306020000080470
         *    addText: fontSize=44 rotation=0 iLeft=72 iTop=56 align=1 pageWidth=47
         */
        TextBuilder mBuilder = new TextBuilder();
        String m = "MO202306020000080470";
        textBuilder.fontSize = 44;
        textBuilder.rotation = 0;
        textBuilder.iLeft = 72;
        textBuilder.iTop = 56;
        textBuilder.align = 1;
        textBuilder.pageWidth = 47;
        printerText(printer, m, mBuilder);

        /*    addText: text=自助售票机
         *    addText: fontSize=14 rotation=0 iLeft=37 iTop=80 align=0 pageWidth=54
         */
        TextBuilder zzBuilder = new TextBuilder();
        String zz = "MO202306020000080470";
        textBuilder.fontSize = 14;
        textBuilder.rotation = 0;
        textBuilder.iLeft = 37;
        textBuilder.iTop = 80;
        textBuilder.align = 0;
        textBuilder.pageWidth = 54;
        printerText(printer, zz, zzBuilder);

        printer.startPrintDoc();
        printer.CloseDevice(1);
    }

    private void printerText(IProxyPrinter printer, String text, TextBuilder textBuilder) {
        android.os.Bundle textBuild = new Bundle();
        textBuild.putInt("fontSize", textBuilder.fontSize);
        textBuild.putInt("rotation", textBuilder.rotation);
        textBuild.putInt("iLeft", textBuilder.iLeft);
        textBuild.putInt("iTop", textBuilder.iTop);
        textBuild.putInt("pageWidth", textBuilder.pageWidth);
        printer.addText(textBuild, text);
    }
}