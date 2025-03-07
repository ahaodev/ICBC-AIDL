package com.icbc.selfserviceticketing.deviceservice.printer

import androidx.annotation.Size

object Constants {
    const val OK: Int = 0 // - 状态正常
    const val QUEZHI: Int = 240 //  - 缺纸，不能打印
    const val ERROR: Int = 242 // - 硬件错误
    const val GUORE: Int = 243 // - 打印头过热
    const val BUFFEROO: Int = 245 // - 缓冲模式下所操作的位置超出范围
    const val DIYABAOHU: Int = 225 //  - 低压保护
    const val MEIZHILE: Int = 244 // - 纸张将要用尽，还允许打印(单步进针打特有返回值)
    const val GUZHANG: Int = 251 // - 打印机芯故障(过快或者过慢)
    const val MEIDUIQI: Int = 252 // - 自动定位没有找到对齐位置,纸张回到原来位置
    const val KAZHI: Int = 238 // - 卡纸
    const val MEIHEIBIAO: Int = 246 // - 没有找到黑标
    const val MANG: Int = 247 // - 打印机处于忙状态
    const val HEIBIAO: Int = 248 // - 黑标探测器检测到黑色信号
    const val DIANYUANDAKAI: Int = 236 // - 打印机电源处于打开状态
    const val DAYINGJITAITOU: Int = 224 //   - 打印头抬起(自助热敏打印机特有返回值)
    const val QIEZHIBUZAIYUANWEI: Int = 226 //  - 切纸刀不在原位(自助热敏打印机特有返回值)
    const val DIWENBAOHUAAD: Int = 227 //  - 低温保护或 AD 出错(自助热敏打印机特有返回值)
}

internal val PRINT_FONT_SCALE = 1.4f

internal fun fontScale(density: Int, fontSize: Int): Float {
    return (density / 72f) * fontSize // normal 72f
}
internal val TITLE_FIELDS = listOf(
    "票券名称", "票券编号", "姓名",
    "证件类型", "证件号码", "有效期",
    "订单号", "固定文字", "使用人数",
    "票型", "子票信息", "销售日期",
    "销售时间", "打印日期", "打印时间",
    "销售时间", "打印日期", "打印时间",
    "票价", "区域", "座位",
    "演出日期", "打印人员", "出行时段",
    "接待单位", "可提前入场...", "场次",
    "场地", "分销商名称", "销售渠道",
    "可用票数", "购票人姓名", "购票人手机",
    "售价", "下单人"
)