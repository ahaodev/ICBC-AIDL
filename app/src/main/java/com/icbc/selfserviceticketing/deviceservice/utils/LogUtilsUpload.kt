package com.icbc.selfserviceticketing.deviceservice.utils

import com.blankj.utilcode.util.LogUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File

class LogUtilsUpload {

    fun uploadLogs(result: (String) -> Unit) {
        val serverUrl = "http://emcs.youchiyun.com/api/oss/file/upload"
        val logs = LogUtils.getLogFiles()
        for (log in logs) {
            uploadFile(serverUrl, log.absolutePath) { code, result ->
                if (code == 0) {
                    result(result)
                } else {
                    LogUtils.d(result)
                    LogUtils.file(result)
                    result(result)
                }

            }
        }
    }

    fun uploadFile(
        serverUrl: String,
        filePath: String,
        call: (code: Int, result: String) -> Unit
    ) {
        val client = OkHttpClient()
        val file = File(filePath)
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", file.name,
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            )
            .build()
        val request: Request = Request.Builder()
            .url(serverUrl)
            .post(requestBody)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                call(1, response.message)
                return
            }
            val body = response.body?.string()
            LogUtils.d("File uploaded successfully$body")
            call(0, body!!)
        }
    }
}