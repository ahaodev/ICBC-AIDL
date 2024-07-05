package com.icbc.selfserviceticketing.deviceservice

import android.content.Context
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.flow.first

object Contains {
    var Rotation = 90

    var isCAP = true
    var margin = 5
    var weight = 80
    var height = 107

    suspend fun load(content: Context) {
        Rotation = DataStoreManager.getRotation(content).first()
        isCAP = DataStoreManager.getIsCap(content, isCAP).first()
        margin = DataStoreManager.getMargin(content, margin).first()
        weight = DataStoreManager.getWeight(content, weight).first()
        height = DataStoreManager.getHeight(content, height).first()
        LogUtils.file("Rotation=${Rotation}, isCAP=${isCAP}, weight${weight}, height${height}")
    }
}