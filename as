getPrinter
getStatus

-OnOpen
startPrintDoc

setPageSize
pageW=90
fontSize=24
align=1
pageWidth=67

addText
fontSize=18
rotation=0
iLeft=33
iTop =13
align=1
pageWidth=42

addQrCode
iLeft=-2
iTop=15
expectedHeight=33

addText
fontSize=18
iLeft=33
iTop=26
align=1

endPrintDoc

  openDevice: device =  VID:0FE6 PID:811EVendorId=4070ProductId=33054
  OnOpen: 0
  setPageSize: pageW=80
  setPageSize: pageH=75
  setPageSize: direction=0
  setPageSize: OffsetX=0
  setPageSize: OffsetY=0

  addText: text=票券名称
  addText: fontSize=24 rotation=0 iLeft=0 iTop=0 align=1 pageWidth=82

  addText: text=使用人数
  addText: fontSize=18 rotation=0 iLeft=16 iTop=6 align=1 pageWidth=21

  addText: text=测试使用
  addText: fontSize=18 rotation=0 iLeft=16 iTop=13 align=1 pageWidth=21

  addText: text=票型
  addText: fontSize=18 rotation=0 iLeft=15 iTop=18 align=0 pageWidth=19

  addText: text=原价
  addText: fontSize=18 rotation=0 iLeft=16 iTop=23 align=0 pageWidth=19

  addText: text=有效期
  addText: fontSize=22 rotation=0 iLeft=0 iTop=28 align=0 pageWidth=36

  addText: text=测试票禁止作为非法用途，
  \n后果自负。
  addText: fontSize=11 rotation=0 iLeft=0 iTop=33 align=0 pageWidth=33

  addText: text=订单号
  addText: fontSize=12 rotation=0 iLeft=0 iTop=57 align=1 pageWidth=33

  addText: text=联系电话：13354397333
  addText: fontSize=14 rotation=0 iLeft=0 iTop=61 align=1 pageWidth=33

  addText: text=打印人员
  addText: fontSize=14 rotation=0 iLeft=0 iTop=67 align=1 pageWidth=16

  addText: text=打印时间
  addText: fontSize=14 rotation=0 iLeft=16 iTop=68 align=1 pageWidth=16

  addText: text=票券编号
  addText: fontSize=14 rotation=0 iLeft=0 iTop=75 align=1 pageWidth=33

  addText: text=测试票单票
  addText: fontSize=24 rotation=0 iLeft=0 iTop=2 align=0 pageWidth=82

  addText: text=1
  addText: fontSize=18 rotation=0 iLeft=16 iTop=9 align=0 pageWidth=21

  addText: text=全票
  addText: fontSize=18 rotation=0 iLeft=15 iTop=20 align=0 pageWidth=19

  addText: text=0.00
  addText: fontSize=18 rotation=0 iLeft=16 iTop=26 align=0 pageWidth=19

  addText: text=2023-04-28
  addText: fontSize=22 rotation=0 iLeft=0 iTop=31 align=0 pageWidth=36

  addText: text=MO202304280000269076
  addText: fontSize=12 rotation=0 iLeft=0 iTop=59 align=0 pageWidth=33

  addText: text=阮皓
  addText: fontSize=14 rotation=0 iLeft=0 iTop=70 align=0 pageWidth=16

  addText: text=14:27:24
  addText: fontSize=14 rotation=0 iLeft=16 iTop=71 align=0 pageWidth=16

  addText: text=T2304280004766200001
  addText: fontSize=14 rotation=0 iLeft=0 iTop=78 align=0 pageWidth=33
  ---------------------工行的测试票-------------------------

   openDevice: device =  VID:0FE6 PID:811EVendorId=4070ProductId=33054
   OnOpen: 0
   setPageSize: pageW=155
   setPageSize: pageH=125
   setPageSize: direction=0
   setPageSize: OffsetX=0
   setPageSize: OffsetY=0
   setPageSize: Builder{pageW=576, pageH=125, direction=0, offsetX=0, offsetY=0}
   addText: text=票券名称
   addText: fontSize=44 rotation=0 iLeft=0 iTop=14 align=1 pageWidth=92
   addText: text=票券编号
   addText: fontSize=14 rotation=0 iLeft=3 iTop=35 align=1 pageWidth=29
   addText: text=订单号
   addText: fontSize=44 rotation=0 iLeft=3 iTop=56 align=1 pageWidth=69
   addQrCode: iLeft=98 iTop=77 expectedHeight=45
   addText: text=销售渠道
   addText: fontSize=14 rotation=0 iLeft=7 iTop=80 align=0 pageWidth=29
   addQrCode: iLeft=14 iTop=94 expectedHeight=24
   addText: text=0元门票
   addText: fontSize=44 rotation=0 iLeft=92 iTop=14 align=1 pageWidth=48
   addText: text=T2306020016500600001
   addText: fontSize=14 rotation=0 iLeft=32 iTop=35 align=1 pageWidth=54
   addText: text=MO202306020000080470
   addText: fontSize=44 rotation=0 iLeft=72 iTop=56 align=1 pageWidth=47
   addText: text=自助售票机
   addText: fontSize=14 rotation=0 iLeft=37 iTop=80 align=0 pageWidth=54

   openDevice: device =  VID:0FE6 PID:811EVendorId=4070ProductId=33054
   OnOpen: 0
   setPageSize: pageW=72
   setPageSize: pageH=120
   setPageSize: direction=0
   setPageSize: OffsetX=0
   setPageSize: OffsetY=

   addText: text=票券名称 fontSize=20 rotation=0 iLeft=0 iTop=0 align=1 pageWidth=71
   addQrCode: iLeft=6 iTop=6 expectedHeight=63 qrCode=27636652205751<MjAwMDAwMTkyNDIwMjMtMDYtMDIyMDIzLTA2LTAy>
   addText: text=证件类型 fontSize=14 rotation=0 iLeft=20 iTop=73 align=1 pageWidth=30
   addText: text=有效期 fontSize=14 rotation=0 iLeft=20 iTop=81 align=1 pageWidth=30
   addText: text=固定文字 fontSize=14 rotation=0 iLeft=20 iTop=90 align=1 pageWidth=30
   addText: text=hao88打印测试票 fontSize=20 rotation=0 iLeft=0 iTop=6 align=0 pageWidth=71
   addText: text= fontSize=14 rotation=0 iLeft=20 iTop=79 align=0 pageWidth=71
   addText: text=2023-06-02 fontSize=14 rotation=0 iLeft=20 iTop=87 align=0 pageWidth=71
   endPrintDoc: 结束打印任务