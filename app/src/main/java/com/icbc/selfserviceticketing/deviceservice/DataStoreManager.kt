package com.icbc.selfserviceticketing.deviceservice

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * DataStoreManager
 * @date 2022/5/9
 * @author 锅得铁
 * @since v1.0
 */

internal object DataStoreManager {
    /**
     * 旋转角
     */
    private val ROTATION = intPreferencesKey("rotation")
    private val ID_CARD = intPreferencesKey("IDCard")
    private val ISCAP = booleanPreferencesKey("ISCAP")
    private val MARGIN = intPreferencesKey("MARGIN")
    private val WEIGHT = intPreferencesKey("WEIGHT")
    private val HEIGHT = intPreferencesKey("HEIGHT")
    const val ID_M40 = 0
    const val ID_180 = 1

    suspend fun setRotation(context: Context, tel: Int) {
        context.dataStore.edit {
            it[ROTATION] = tel
        }
    }

    suspend fun getRotation(context: Context): Flow<Int> {
        return context.dataStore.data.mapNotNull { it[ROTATION] ?: 0 }
    }

    suspend fun setIsCap(context: Context, tel: Boolean) {
        context.dataStore.edit {
            it[ISCAP] = tel
        }
    }

    suspend fun getIsCap(context: Context,def :Boolean): Flow<Boolean> {
        return context.dataStore.data.mapNotNull { it[ISCAP] ?: def }
    }

    suspend fun setMargin(context: Context, tel: Int) {
        context.dataStore.edit {
            it[MARGIN] = tel
        }
    }

    suspend fun getMargin(context: Context,def:Int): Flow<Int> {
        return context.dataStore.data.mapNotNull { it[MARGIN] ?: def }
    }

    suspend fun setWeight(context: Context, tel: Int) {
        context.dataStore.edit {
            it[WEIGHT] = tel
        }
    }

    suspend fun getWeight(context: Context,def:Int): Flow<Int> {
        return context.dataStore.data.mapNotNull { it[WEIGHT] ?: def }
    }


    suspend fun setHeight(context: Context, tel: Int) {
        context.dataStore.edit {
            it[HEIGHT] = tel
        }
    }

    suspend fun getHeight(context: Context,def:Int): Flow<Int> {
        return context.dataStore.data.mapNotNull { it[HEIGHT] ?: def }
    }



    suspend fun setIdCard(context: Context, id: Int) {
        context.dataStore.edit {
            it[ID_CARD] = id
        }
    }


    suspend fun getIDCard(context: Context,def: Int): Flow<Int> {
        return context.dataStore.data.mapNotNull { it[ID_CARD] ?: def }
    }
}