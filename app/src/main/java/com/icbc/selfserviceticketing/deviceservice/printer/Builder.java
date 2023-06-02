package com.icbc.selfserviceticketing.deviceservice.printer;

public class Builder {
    int pageW = 576;
    int pageH = 0;
    int direction = 0;
    int offsetX = 0;
    int offsetY = 0;

    public int getPageW() {
        return pageW;
    }

    public Builder setPageW(int pageW) {
        this.pageW = pageW;
        return this;
    }

    public int getPageH() {
        return pageH;
    }

    public Builder setPageH(int pageH) {
        this.pageH = pageH;
        return this;
    }

    public int getDirection() {
        return direction;
    }

    public Builder setDirection(int direction) {
        this.direction = direction;
        return this;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public Builder setOffsetX(int offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public Builder setOffsetY(int offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    @Override
    public String toString() {
        return "Builder{" +
                "pageW=" + pageW +
                ", pageH=" + pageH +
                ", direction=" + direction +
                ", offsetX=" + offsetX +
                ", offsetY=" + offsetY +
                '}';
    }
}
