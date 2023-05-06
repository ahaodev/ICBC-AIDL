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