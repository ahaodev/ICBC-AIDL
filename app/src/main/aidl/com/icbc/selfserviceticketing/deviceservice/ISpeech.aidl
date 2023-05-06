// ISpeech.aidl
package com.icbc.selfserviceticketing.deviceservice;

// Declare any non-default types here with import statements

interface ISpeech {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void startSpeech(String speechText); //开始语音播报
    void stopSpeech (); //停止语音播报
}
