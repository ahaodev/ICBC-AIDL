package com.icbc.selfserviceticketing.deviceservice.printer;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;

import com.icbc.selfserviceticketing.deviceservice.IPrinter;

public class PrinterProxy extends IPrinter.Stub {
    IProxyPrinter mProxyPrinter;
    Context context;

    public PrinterProxy(Context context) {
        this.context = context;
        mProxyPrinter = new PagePrinter(context);
    }

    @Override
    public int OpenDevice(int DeviceID, String deviceFile, String szPort, String szParam) throws RemoteException {
        return mProxyPrinter.OpenDevice(DeviceID, deviceFile, szPort, szParam);
    }

    @Override
    public int CloseDevice(int DeviceID) throws RemoteException {
        return mProxyPrinter.CloseDevice(DeviceID);
    }

    @Override
    public int getStatus() throws RemoteException {
        return mProxyPrinter.getStatus();
    }

    @Override
    public int setPageSize(Bundle format) throws RemoteException {
        return mProxyPrinter.setPageSize(format);
    }

    @Override
    public int startPrintDoc() throws RemoteException {
        return mProxyPrinter.startPrintDoc();
    }

    @Override
    public int addText(Bundle format, String text) throws RemoteException {
        return mProxyPrinter.addText(format, text);
    }

    @Override
    public int addQrCode(Bundle format, String qrCode) throws RemoteException {
        return mProxyPrinter.addQrCode(format, qrCode);
    }

    @Override
    public int addImage(Bundle format, String imageData) throws RemoteException {
        return mProxyPrinter.addImage(format, imageData);
    }

    @Override
    public int endPrintDoc() throws RemoteException {
        return mProxyPrinter.endPrintDoc();
    }
}
