package com.icbc.selfserviceticketing.deviceservice.printer;

public class Constants {
    public static final int OK = 0;// - 状态正常
    public static final int QUEZHI = 240;//  - 缺纸，不能打印
    public static final int ERROR = 242;// - 硬件错误
    public static final int GUORE = 243;// - 打印头过热
    public static final int BUFFEROO = 245;// - 缓冲模式下所操作的位置超出范围
    public static final int DIYABAOHU = 225;//  - 低压保护
    public static final int MEIZHILE = 244;// - 纸张将要用尽，还允许打印(单步进针打特有返回值)
    public static final int GUZHANG = 251;// - 打印机芯故障(过快或者过慢)
    public static final int MEIDUIQI = 252;// - 自动定位没有找到对齐位置,纸张回到原来位置
    public static final int KAZHI = 238;// - 卡纸
    public static final int MEIHEIBIAO = 246;// - 没有找到黑标
    public static final int MANG = 247;// - 打印机处于忙状态
    public static final int HEIBIAO = 248;// - 黑标探测器检测到黑色信号
    public static final int DIANYUANDAKAI = 236;// - 打印机电源处于打开状态
    public static final int DAYINGJITAITOU = 224;//   - 打印头抬起(自助热敏打印机特有返回值)
    public static final int QIEZHIBUZAIYUANWEI = 226;//  - 切纸刀不在原位(自助热敏打印机特有返回值)
    public static final int DIWENBAOHUAAD = 227;//  - 低温保护或 AD 出错(自助热敏打印机特有返回值)
}
