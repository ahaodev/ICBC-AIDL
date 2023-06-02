package com.icbc.selfserviceticketing.deviceservice.printer;

public class Utils {
    public static double millimetersToDots(int millimeters) {
        return millimeters * 8;
    }

    public static double millimetersToInches(double millimeters) {
        return millimeters / 25.4;
    }

    public static int inchesToPixels(double inches) {
        double pixelsPerInch = 203; // 每英寸的像素数
        return (int) (inches * pixelsPerInch);
    }

    public static double millimetersToPixels(double millimeters) {
        return inchesToPixels(millimetersToInches(millimeters));
    }

    public static double pixelsToMillimeters(int pixels) {
        double pixelsPerInch = 203; // 每英寸的像素数
        double millimetersPerInch = 25.4; // 每英寸的毫米数
        double inches = pixels / pixelsPerInch;
        return inches * millimetersPerInch;
    }
}
