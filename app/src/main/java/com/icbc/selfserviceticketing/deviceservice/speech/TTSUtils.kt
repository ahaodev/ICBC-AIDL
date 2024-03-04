package com.icbc.selfserviceticketing.deviceservice.speech

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

/**
 *@date 2022/2/28
 *@author 锅得铁
 *@constructor default constructor
 */
class TTSUtils {
    companion object {
        private lateinit var tts: TextToSpeech
        private var INSTANCE: TTSUtils? = null

        fun getInstance(): TTSUtils =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: TTSUtils()
            }
    }

    fun init(context: Context, block: () -> Unit) {
        tts = TextToSpeech(
            context
        ) {
            Log.d("TTS", "tts init")
            tts.language = Locale.CHINESE
            block()
        }
    }
    /**
     * text 需要转成语音的文字
     * queueMode 队列方式：
     * QUEUE_ADD：播放完之前的语音任务后才播报本次内容
     * QUEUE_FLUSH：丢弃之前的播报任务，立即播报本次内容
     * params 设置TTS参数，可以是null。
     * KEY_PARAM_STREAM：音频通道，可以是：STREAM_MUSIC、STREAM_NOTIFICATION、STREAM_RING等
     * KEY_PARAM_VOLUME：音量大小，0-1f
     * utteranceId：当前朗读文本的id
     */
    fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null)
    }

    fun stop() {
        tts.stop()
        tts.shutdown()
    }

    fun openSystemTTSSettings(ctx: Context) {
        // Got this from: https://stackoverflow.com/a/8688354
        val intent = Intent()
        intent.action = "com.android.settings.TTS_SETTINGS"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ctx.startActivity(intent)
    }


}