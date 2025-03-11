package com.icbc.selfserviceticketing.deviceservice

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.map

const val ID_180: String = "ID-180"
const val ID_M40: String = "ID-M40"

const val SCAN_SUPERLEAD = 0

const val PRINT_CSN: String = "CSN"
const val PRINT_TSC310E: String = "TSC-310E"
const val PRINT_T321OR331: String = "T3-321/331"

const val PAPER_TYPE_CAP = "铜版纸"
const val PAPER_TYPE_BLINE = "黑标纸"
const val PAPER_TYPE_BLINEDETECT = "连续纸"
const val PAPER_TYPE_HOT = "热敏纸"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class Config(
    var rotation: Int = 0,
    var idCardType: String = ID_M40,
    var scannerType: Int = SCAN_SUPERLEAD,
    var printerType: String = PRINT_CSN,
    var paperType: String = PAPER_TYPE_CAP,
    var margin: Float = 5f,
    var width: Float = 80f,
    var height: Float = 80f,
    var enableBorder: Boolean = false,
    var csnDevPort: String ="/dev/ttyACM0",
    var enableScannerSuperLeadSerialPortMode: Boolean = false
)

object PreferencesKey {
    /**
     * 全局设置
     */
    val SETTINGS = stringPreferencesKey("CONFIG_JSON")
}

object ConfigProvider {

    private val gson = Gson()

    private fun toJson(config: Config): String {
        return gson.toJson(config)
    }

    private fun fromJson(json: String?): Config {
        if (json.isNullOrEmpty())
            return Config()
        return gson.fromJson(json, Config::class.java)
    }

    fun readConfig(context: Context) = context.dataStore.data.map {
        val config = fromJson(it[PreferencesKey.SETTINGS])
        Log.d("ConfigProvider", "readConfig: config=$config")
        config
    }

    suspend fun saveConfig(context: Context, config: Config) {
        Log.d("ConfigProvider", "saveConfig: config=${config} ")
        context.dataStore.also { store ->
            store.edit {
                it[PreferencesKey.SETTINGS] = toJson(config)
            }
        }
    }
}