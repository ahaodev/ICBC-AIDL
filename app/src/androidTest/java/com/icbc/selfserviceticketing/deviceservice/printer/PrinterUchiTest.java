package com.icbc.selfserviceticketing.deviceservice.printer;

import android.os.Bundle;

public class PrinterUchiTest {

    private IProxyPrinter printer;

    public PrinterUchiTest(IProxyPrinter printer) {
        this.printer = printer;
    }

    public void printer() {
        android.os.Bundle pageBundle = new Bundle();
        pageBundle.putInt("pageW", 72);
        pageBundle.putInt("pageH", 100);
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
        /**
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
        /**
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
        /**
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
