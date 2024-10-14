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

const val ID_M40 = 0
const val ID_180 = 1

const val SCAN_SUPERLEAD = 0

const val PRINTER_TSC310E = 0
const val PRINTER_CSN = 1

const val PAPER_TYPE_CAP = "CAP"
const val PAPER_TYPE_BLINE = "BLINE"
const val PAPER_TYPE_BLINEDETECT = "BLINEDETECT"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class Config(
    var rotation: Int = 0,
    var idCardType: Int = ID_M40,
    var scannerType: Int = SCAN_SUPERLEAD,
    var printerType: Int = PRINTER_TSC310E,
    var paperType: String = PAPER_TYPE_CAP,
    var margin: Float = 5f,
    var weight: Float = 80f,
    var height: Float = 80f,
    var enableBorder: Boolean = false,
    var csnDevPort: String ="/dev/ttyACM0",
)

object PreferencesKey {
    /**
     * 全局设置
     */
    val SETTINGS = stringPreferencesKey("Settings_json_string")
}

object ConfigProvider {

    private val gson = Gson()

    private fun toJson(config: Config): String {
        return gson.toJson(config)
    }

    private fun fromJson(json: String?): Config {
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