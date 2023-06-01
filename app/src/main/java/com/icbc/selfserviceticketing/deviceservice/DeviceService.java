package com.icbc.selfserviceticketing.deviceservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.icbc.selfserviceticketing.deviceservice.printer.PagePrinter;
import com.icbc.selfserviceticketing.deviceservice.printer.Printer;

public class DeviceService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new IDeviceService.Stub() {
            @Override
            public ISpeech getISpeech() throws RemoteException {
                return null;
            }

            @Override
            public IScanner getScanner(int cameraId) throws RemoteException {
                return new ScannerSuperLead(getApplicationContext());
            }

            @Override
            public IDeviceInfo getDeviceInfo() throws RemoteException {
                return new DeviceInfo(DeviceService.this);
            }

            @Override
            public IPrinter getPrinter(String bule_mac) throws RemoteException {
                return new Printer(getApplicationContext());
            }

            @Override
            public IIDCard getIIDCard() throws RemoteException {
                return new IDCard(DeviceService.this);
            }

            @Override
            public ISmartCash getSmartCash() throws RemoteException {
                return null;
            }

            @Override
            public IFaceDetector getFaceDetector() throws RemoteException {
                return null;
            }

            @Override
            public IRFReader getRFReader() throws RemoteException {
                return new RFReader();
            }

            @Override
            public IGateOperator getGateOperator() throws RemoteException {
                return new GateOperator();
            }

            @Override
            public IDeviceReboot getDeviceReboot() throws RemoteException {
                return null;
            }

            @Override
            public IDeviceShutdown getDeviceShutdown() throws RemoteException {
                return null;
            }

            @Override
            public IDeviceWhiteLight getDeviceWhiteLight() throws RemoteException {
                return null;
            }

            @Override
            public IDeviceInfraredLed getDeviceInfraredLed() throws RemoteException {
                return null;
            }

            @Override
            public IDeviceTemperature getDeviceTemperature() throws RemoteException {
                return new Temperaturer();
            }
        };
    }
}
