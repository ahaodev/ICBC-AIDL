package com.icbc.selfserviceticketing.deviceservice.printer;

public class PrinterBuilder {
    int maxPx = 576;
    int px = 7;
    int pageW = 0;
    int pageH = 0;
    int direction = 0;
    int fontSize = 24;
    int align = 1;
    int offsetX = 0;
    int offsetY = 0;

    public PrinterBuilder setPageW(int pageW) {
        this.pageW = pageW;
        return this;
    }
    public PrinterBuilder setPx(int px) {
        this.px = px;
        return this;
    }
    public PrinterBuilder setDirection(int direction) {
        this.direction = direction;
        return this;
    }

    public PrinterBuilder setPageH(int pageH) {
        this.pageH = pageH;
        return this;
    }


    public PrinterBuilder setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }


    public PrinterBuilder setAlign(int align) {
        this.align = align;
        return this;
    }


    public PrinterBuilder setOffsetX(int offsetX) {
        this.offsetX = offsetX;
        return this;
    }


    public PrinterBuilder setOffsetY(int offsetY) {
        this.offsetY = offsetY;
        return this;
    }
}
