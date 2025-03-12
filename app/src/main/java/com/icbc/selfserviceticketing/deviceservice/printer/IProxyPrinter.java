package com.icbc.selfserviceticketing.deviceservice.printer;

import android.os.Bundle;

interface IProxyPrinter {

    /**
     * 设备控制
     */
    int OpenDevice(int DeviceID, String deviceFile, String szPort, String szParam);

    int CloseDevice(int DeviceID);

    /**
     * 获取打印机状态
     *
     * @return 打印机状态
     * <ul>
     * <li>ERROR_NONE(0x00) - 状态正常</li>
     * <li>ERROR_PAPERENDED(0xF0) - 缺纸，不能打印</li>
     * <li>ERROR_HARDERR(0xF2) - 硬件错误</li>
     * <li>ERROR_OVERHEAT(0xF3) - 打印头过热</li>
     * <li>ERROR_BUFOVERFLOW(0xF5) - 缓冲模式下所操作的位置超出范围 </li>
     * <li>ERROR_LOWVOL(0xE1) - 低压保护 </li>
     * <li>ERROR_PAPERENDING(0xF4) - 纸张将要用尽，还允许打印(单步进针打特有返回值)</li>
     * <li>ERROR_MOTORERR(0xFB) - 打印机芯故障(过快或者过慢)</li>
     * <li>ERROR_PENOFOUND(0xFC) - 自动定位没有找到对齐位置,纸张回到原来位置   </li>
     * <li>ERROR_PAPERJAM(0xEE) - 卡纸</li>
     * <li>ERROR_NOBM(0xF6) - 没有找到黑标</li>
     * <li>ERROR_BUSY(0xF7) - 打印机处于忙状态</li>
     * <li>ERROR_BMBLACK(0xF8) - 黑标探测器检测到黑色信号</li>
     * <li>ERROR_WORKON(0xE6) - 打印机电源处于打开状态</li>
     * <li>ERROR_LIFTHEAD(0xE0) - 打印头抬起(自助热敏打印机特有返回值)</li>
     * <li>ERROR_CUTPOSITIONERR(0xE2) - 切纸刀不在原位(自助热敏打印机特有返回值)</li>
     * <li>ERROR_LOWTEMP(0xE3) - 低温保护或AD出错(自助热敏打印机特有返回值)</li>
     * </ul>
     */
    int getStatus();

    /**
     * 打印设置
     *
     * @param format - 打印设置
     *               <ul>
     *               </ul>
     */
    int setPageSize(Bundle format); //打印设置

    /**
     * 创建打印任务
     */
    int startPrintDoc(); //新建打印任务

    /**
     * 添加一行打印文本
     *
     * @param format - 打印字体格式
     *               <ul>
     *               <li>font(int) - 0:small, 1:normal, 2:large</li>
     *               <li>align(int) - 0:left, 1:center, 2:right</li>
     *               </ul>
     * @param text   - 打印文本
     */
    int addText(Bundle format, String text); //打印文本

    /**
     * 添加二维码打印
     *
     * @param format - 打印格式，可设置打印的位置、期望高度
     *               <ul>
     *               <li>offset(int) - 打印起始位置 </li>
     *               <li>expectedHeight(int) - 期望高度</li>
     *               </ul>
     * @param qrCode - 二维码内容
     */
    int addQrCode(Bundle format, String qrCode); //打印二维码

    /**
     * 添加字符串类型的图片打印（字符串默认编码为UTF-8）
     *
     * @param format    - 打印格式，可设置打印的位置、宽度、高度
     *                  <ul>
     *                  <li>rotation(int)：旋转角度，0，90，180，270 四个角度</li>
     *                  <li>iLeft(int): 距离左边距离,单位mm</li>
     *                  <li>iTop(int): 距离顶部距离,单位mm</li>
     *                  <li>iWidth(int): 图像宽度,单位mm</li>
     *                  <li>iHeight(int): 图像高度,单位mm</li>
     *                  </ul>
     * @param imageData - 图片数据
     */
    int addImage(Bundle format, String imageData); //打印图像

    /**
     * 结束打印任务
     */
    int endPrintDoc(); //结束打印任务

    int selfTest(); //自检
}