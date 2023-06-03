package com.icbc.selfserviceticketing.deviceservice.printer;

import android.os.Bundle;

public class PrinterUchiTest {

    private IProxyPrinter printer;

    public PrinterUchiTest(IProxyPrinter printer) {
        this.printer = printer;
    }

    public void printer() {
        Bundle pageBundle = new Bundle();
        pageBundle.putInt("pageW", 72);
        pageBundle.putInt("pageH", 72);
        pageBundle.putInt("OffsetX", 0);
        pageBundle.putInt("OffsetY", 0);
        pageBundle.putInt("direction", 0);
        printer.setPageSize(pageBundle);

        // addText: text=票券名称 fontSize=14 rotation=0 iLeft=0 iTop=0 align=1 pageWidth=72
        TextBuilder pjBuilder = new TextBuilder();
        String text = "票券名称";
        pjBuilder.fontSize = 14;
        pjBuilder.rotation = 0;
        pjBuilder.iLeft = 0;
        pjBuilder.iTop = 0;
        pjBuilder.align = 1;
        pjBuilder.pageWidth = 70;
        printerText(printer, text, pjBuilder);
        /*
            addQrCode: iLeft=1 iTop=6 expectedHeight=25 qrCode=34027290657570<MjAwMDAwMTkyNDIwMjMtMDYtMDMyMDIzLTA2LTAz>
         */
        Bundle qrBundle = new Bundle();
        qrBundle.putInt("iLeft", 1);
        qrBundle.putInt("iTop", 6);
        qrBundle.putInt("expectedHeight", 25);
        printer.addQrCode(qrBundle, "34027290657570<MjAwMDAwMTkyNDIwMjMtMDYtMDMyMDIzLTA2LTAz>");

        /**
         addText: text=票券编号 fontSize=14 rotation=0 iLeft=29 iTop=8 align=1 pageWidth=30
         */
        TextBuilder numberBuilder = new TextBuilder();
        String noTitle = "票券编号";
        numberBuilder.fontSize = 14;
        numberBuilder.rotation = 0;
        numberBuilder.iLeft = 29;
        numberBuilder.iTop = 8;
        numberBuilder.align = 1;
        numberBuilder.pageWidth = 30;
        printerText(printer, noTitle, numberBuilder);
        /**
         addText: text=证件号码 fontSize=14 rotation=0 iLeft=29 iTop=18 align=1 pageWidth=30
         */
        TextBuilder dingdanBuilder = new TextBuilder();
        String dingdanTitle = "证件号码";
        dingdanBuilder.fontSize = 14;
        dingdanBuilder.rotation = 0;
        dingdanBuilder.iLeft = 29;
        dingdanBuilder.iTop = 18;
        dingdanBuilder.align = 1;
        dingdanBuilder.pageWidth = 30;
        printerText(printer, dingdanTitle, dingdanBuilder);
        /*
       addText: text=从前有座山山里有做吧 fontSize=14 rotation=0 iLeft=3 iTop=35 align=1 pageWidth=66
         */
        TextBuilder qdBuilder = new TextBuilder();
        String qdTitle = "从前有座山山里有做吧";
        qdBuilder.fontSize = 14;
        qdBuilder.rotation = 0;
        qdBuilder.iLeft = 3;
        qdBuilder.iTop = 35;
        qdBuilder.align = 1;
        qdBuilder.pageWidth = 66;
        printerText(printer, qdTitle, qdBuilder);

        /*
          票券编号
          addText: text=hao88打印测试票 fontSize=14 rotation=0 iLeft=0 iTop=6 align=0 pageWidth=71
         */
        TextBuilder moneyBuilder = new TextBuilder();
        String money = "hao88打印测试票";
        moneyBuilder.fontSize = 14;
        moneyBuilder.rotation = 0;
        moneyBuilder.iLeft = 0;
        moneyBuilder.iTop = 6;
        moneyBuilder.align = 0;
        moneyBuilder.pageWidth = 71;
        printerText(printer, money, moneyBuilder);
        /*
        addText: text=T2306030010502600002 fontSize=14 rotation=0 iLeft=29 iTop=14 align=0 pageWidth=71
         */
        TextBuilder tBuilder = new TextBuilder();
        String t = "T2306030010502600002";
        tBuilder.fontSize = 14;
        tBuilder.rotation = 0;
        tBuilder.iLeft = 29;
        tBuilder.iTop = 14;
        tBuilder.align = 0;
        tBuilder.pageWidth = 71;
        printerText(printer, t, tBuilder);
        /*
       addText: text= fontSize=14 rotation=0 iLeft=29 iTop=24 align=0 pageWidth=71
         */
        TextBuilder mBuilder = new TextBuilder();
        String m = "aaa";
        mBuilder.fontSize = 14;
        mBuilder.rotation = 0;
        mBuilder.iLeft = 29;
        mBuilder.iTop = 24;
        mBuilder.align = 0;
        mBuilder.pageWidth = 71;
        printerText(printer, m, mBuilder);

        printer.endPrintDoc();
        printer.CloseDevice(1);
    }

    private void printerText(IProxyPrinter printer, String text, TextBuilder textBuilder) {
        Bundle textBuild = new Bundle();
        textBuild.putInt("fontSize", textBuilder.fontSize);
        textBuild.putInt("rotation", textBuilder.rotation);
        textBuild.putInt("iLeft", textBuilder.iLeft);
        textBuild.putInt("iTop", textBuilder.iTop);
        textBuild.putInt("pageWidth", textBuilder.pageWidth);
        textBuild.putInt("align", textBuilder.align);
        printer.addText(textBuild, text);
    }
}
