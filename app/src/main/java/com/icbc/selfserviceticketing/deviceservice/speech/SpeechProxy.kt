package com.icbc.selfserviceticketing.deviceservice.speech

import com.icbc.selfserviceticketing.deviceservice.ISpeech

class SpeechProxy(private val ttsUtils: TTSUtils) : ISpeech.Stub() {

    override fun startSpeech(speechText: String) {
        ttsUtils.speak(speechText)
    }

    override fun stopSpeech() {
        ttsUtils.stop()
    }
}