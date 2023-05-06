package com.icbc.selfserviceticketing.deviceservice;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.zkteco.android.IDReader.IDPhotoHelper;
import com.zkteco.android.IDReader.WLTService;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.module.idcard.IDCardReader;
import com.zkteco.android.biometric.module.idcard.IDCardReaderFactory;
import com.zkteco.android.biometric.module.idcard.exception.IDCardReaderException;
import com.zkteco.android.biometric.module.idcard.meta.IDCardInfo;
import com.zkteco.android.biometric.module.idcard.meta.IDPRPCardInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class IDCard extends IIDCard.Stub {
    private static final String IDCard_Read_OnProgress = "IDCard_Read_OnProgress";
    private static final String IDCard_Read_OnError = "IDCard_Read_OnError";
    private static final String IDCard_Read_OnSuccess = "IDCard_Read_OnSuccess";
    private static final int VID = 1024;    //IDR VID
    private static final int PID = 50010;     //IDR PID
    private IDCardReader idCardReader = null;
    private boolean bopen = false; //设备是否打开
    private boolean bread = false; //设备是否通信
    private Context mContext;
    private CountDownLatch countdownLatch = null;
    private ReadTask mReadTask;

    public IDCard(Context context) {
        mContext = context.getApplicationContext();
    }

    private int open() {
        if (bopen) return 0;
        try {
            LogHelper.setLevel(Log.ASSERT);
            Map idrparams = new HashMap();
            idrparams.put(ParameterHelper.PARAM_KEY_VID, VID);
            idrparams.put(ParameterHelper.PARAM_KEY_PID, PID);
            idCardReader = IDCardReaderFactory.createIDCardReader(mContext, TransportType.USB, idrparams);
            if (idCardReader != null) {
                idCardReader.open(0);
                bopen = true;
                return 0;
            }
        } catch (IDCardReaderException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int OpenDevice(int DeviceID, String deviceFile, String szPort, String szParam) throws RemoteException {
        return 0;
    }

    @Override
    public int CloseDevice(int DeviceID) throws RemoteException {
        return 0;
    }

    @Override
    public int GetDeviceStatus(int DeviceID) throws RemoteException {
        return 0;
    }

    @Override
    public void commandNonBlock(String cmd, Bundle params, DeviceListener listener) throws RemoteException {
        if (open() < 0) return;
        if (!bopen || bread) return;
        countdownLatch = new CountDownLatch(1);
        mReadTask = new ReadTask(listener);
        mReadTask.start();
        bread = true;
    }

    @Override
    public Bundle commandBlock(String cmd, Bundle params) throws RemoteException {
        close();
        return null;
    }

    private void close() {
        try {
            if (!bopen || !bread) return;
            if (mReadTask != null)
                mReadTask.interrupt();
            mReadTask = null;
            bread = false;
            if (null != countdownLatch) {
                try {
                    countdownLatch.await(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (idCardReader != null) {
                idCardReader.close(0);
                idCardReader.destroy();
            }
            idCardReader = null;
            bopen = false;
        } catch (IDCardReaderException e) {
            e.printStackTrace();
        }
        bopen = false;
    }

    public class ReadTask extends Thread {
        private boolean bStoped = false;
        private DeviceListener mDeviceListener;

        public ReadTask(DeviceListener listener) {
            mDeviceListener = listener;
        }

        @Override
        public void interrupt() {
            super.interrupt();
            bStoped = true;
            mDeviceListener = null;
        }

        @Override
        public void run() {
            while (!bStoped) {
                SystemClock.sleep(500);
                final long nTickstart = System.currentTimeMillis();
                try {
                    if (bStoped) break;
                    if (idCardReader == null) continue;
                    boolean card = idCardReader.findCard(0);
                    boolean selectCard = idCardReader.selectCard(0);
                    if (selectCard && card) {
                        onProgressCallBack(0);
                    } else {
                        errorCallBack(2, "寻卡超时");
                    }
                } catch (IDCardReaderException e) {

                }
                SystemClock.sleep(50);
                int retType = 0;
                try {
                    if (bStoped || idCardReader == null) break;
                    retType = idCardReader.readCardEx(0, 0);
                } catch (IDCardReaderException e) {
                    errorCallBack(6, "读卡失败");
                }
                if (bStoped || idCardReader == null) break;
                if (retType == 1 || retType == 2 || retType == 3) {
                    final long nTickUsed = (System.currentTimeMillis() - nTickstart);
                    final int final_retType = retType;
                    Bundle bundle = new Bundle();
                    if (final_retType == 2) {
                        final IDPRPCardInfo idprpCardInfo = idCardReader.getLastPRPIDCardInfo();
                        //姓名
                        bundle.putString("IDName", idprpCardInfo.getCnName() + "/" + idprpCardInfo.getEnName());
                        //身份证号
                        bundle.putString("IDNumber", idprpCardInfo.getId());
                        //民族
                        bundle.putString("IDNation", "");
                        //性别
                        bundle.putString("IDSex", idprpCardInfo.getSex());
                        //出生日期
                        bundle.putString("IDBirthday", idprpCardInfo.getBirth());
                        //住址
                        bundle.putString("IDAddress", "");
                        //签发机关
                        bundle.putString("IDSignedDepartment", "");
                        //有效期限
                        bundle.putString("IDEffectiveDate", idprpCardInfo.getValidityTime());
                        if (idprpCardInfo.getPhotolength() > 0) {
                            byte[] buf = new byte[WLTService.imgLength];
                            if (1 == WLTService.wlt2Bmp(idprpCardInfo.getPhoto(), buf)) {
                                bundle.putParcelable("IDImage", IDPhotoHelper.Bgr2Bitmap(buf));
                            }
                        }
                    } else {
                        final IDCardInfo idCardInfo = idCardReader.getLastIDCardInfo();
                        //姓名
                        bundle.putString("IDName", idCardInfo.getName());
                        //身份证号
                        bundle.putString("IDNumber", idCardInfo.getId());
                        //民族
                        bundle.putString("IDNation", idCardInfo.getNation());
                        //性别
                        bundle.putString("IDSex", idCardInfo.getSex());
                        //出生日期
                        bundle.putString("IDBirthday", idCardInfo.getBirth());
                        //住址
                        bundle.putString("IDAddress", idCardInfo.getAddress());
                        //签发机关
                        bundle.putString("IDSignedDepartment", idCardInfo.getDepart());
                        //有效期限
                        String validityTime = idCardInfo.getValidityTime();
                        String[] split = validityTime.split("-");
                        bundle.putString("IDEffectiveDate", split[0]);
                        bundle.putString("IDExpiryDate", split[1]);
                        if (idCardInfo.getPhotolength() > 0) {
                            byte[] buf = new byte[WLTService.imgLength];
                            if (1 == WLTService.wlt2Bmp(idCardInfo.getPhoto(), buf)) {
                                bundle.putParcelable("IDImage", IDPhotoHelper.Bgr2Bitmap(buf));
                            }
                        }
                    }
                    successCallBack(bundle);
                }
            }
            if (countdownLatch != null)
                countdownLatch.countDown();
        }

        private void successCallBack(Bundle bundle) {
            if (mDeviceListener != null) {
                try {
                    mDeviceListener.callBack(IDCard_Read_OnSuccess, bundle);
                } catch (RemoteException remoteException) {
                }
            }
        }

        private void onProgressCallBack(int state) {
            if (mDeviceListener != null) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putInt("state", state);
                    mDeviceListener.callBack(IDCard_Read_OnProgress, bundle);
                } catch (RemoteException remoteException) {
                }
            }
        }

        private void errorCallBack(int errCode, String message) {
            if (mDeviceListener != null) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putInt("errCode", errCode);
                    bundle.putString("errMsg", message);
                    mDeviceListener.callBack(IDCard_Read_OnError, bundle);
                } catch (RemoteException remoteException) {
                }
            }
        }
    }
}
