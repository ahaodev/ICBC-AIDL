package com.icbc.selfserviceticketing.deviceservice.utils

import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.LogUtils

class CrashListener:CrashUtils.OnCrashListener {
    override fun onCrash(crashInfo: CrashUtils.CrashInfo?) {
        crashInfo?.let {
            LogUtils.file(crashInfo.toString())
        }
    }
}