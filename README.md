# ICBC-AIDL

Hardware layer interface aidl

硬件层AIDL服务实现,提供给上层应用

80mm热敏打印测试

```kotlin
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
```

800x600 标签打印

```json
{
  "pageW": 80,
  "pageH": 60,
  "direction": 0,
  "OffsetX": 0,
  "OffsetY": 0,
  "elements": [
    {
      "elementType": 1,
      "iLeft": 1,
      "iTop": 1,
      "expectedHeight": 18,
      "qrCode": "60214102006512<MjAwMDAwMTkyNDIwMjMtMDctMTQyMDIzLTA3LTE0>"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 23,
      "iTop": 2,
      "align": 1,
      "pageWidth": 8,
      "text": "票券名称"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 23,
      "iTop": 8,
      "align": 1,
      "pageWidth": 8,
      "text": "票券编号"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 1,
      "iTop": 18,
      "align": 1,
      "pageWidth": 6,
      "text": "订单号"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 1,
      "iTop": 25,
      "align": 1,
      "pageWidth": 8,
      "text": "销售时间"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 1,
      "iTop": 30,
      "align": 1,
      "pageWidth": 72,
      "text": "从前有座山，山里有座庙，庙里有个老和尚，老和尚给小和尚讲故事"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 32,
      "iTop": 2,
      "align": 1,
      "pageWidth": 16,
      "text": "hao88打印测试票"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 32,
      "iTop": 8,
      "align": 1,
      "pageWidth": 17,
      "text": "T2307140030802100001"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 8,
      "iTop": 18,
      "align": 1,
      "pageWidth": 16,
      "text": "MO202307140000966185"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 10,
      "iTop": 25,
      "align": 1,
      "pageWidth": 14,
      "text": "14:00:09"
    }
  ]
}

```

2023-07-14 14:16:06.488 27742-27894 AIDLHelper com.icbc.ticketing D 打印数据:

```json
{
  "pageW": 80,
  "pageH": 60,
  "direction": 0,
  "OffsetX": 0,
  "OffsetY": 0,
  "elements": [
    {
      "elementType": 1,
      "iLeft": 1,
      "iTop": 1,
      "expectedHeight": 18,
      "qrCode": "60214102006512<MjAwMDAwMTkyNDIwMjMtMDctMTQyMDIzLTA3LTE0>"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 23,
      "iTop": 2,
      "align": 1,
      "pageWidth": 8,
      "text": "票券名称"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 23,
      "iTop": 8,
      "align": 1,
      "pageWidth": 8,
      "text": "票券编号"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 1,
      "iTop": 18,
      "align": 1,
      "pageWidth": 6,
      "text": "订单号"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 1,
      "iTop": 25,
      "align": 1,
      "pageWidth": 8,
      "text": "销售时间"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 1,
      "iTop": 30,
      "align": 1,
      "pageWidth": 72,
      "text": "从前有座山，山里有座庙，庙里有个老和尚，老和尚给小和尚讲故事"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 32,
      "iTop": 2,
      "align": 1,
      "pageWidth": 16,
      "text": "hao88打印测试票"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 32,
      "iTop": 8,
      "align": 1,
      "pageWidth": 17,
      "text": "T2307140030802100001"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 8,
      "iTop": 18,
      "align": 1,
      "pageWidth": 16,
      "text": "MO202307140000966185"
    },
    {
      "elementType": 0,
      "fontName": "",
      "fontSize": 18,
      "rotation": 0,
      "iLeft": 10,
      "iTop": 25,
      "align": 1,
      "pageWidth": 14,
      "text": "14:00:09"
    }
  ]
}

```

