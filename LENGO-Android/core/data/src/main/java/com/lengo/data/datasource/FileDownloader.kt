package com.lengo.data.datasource

import android.content.Context
import logcat.logcat
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

data class FileDownloader @Inject constructor(@Named("defaultClient") val okHttpClient: OkHttpClient) {

    fun downloadFile(
        context: Context,
        fileName: String,
        dirName: String,
        url: String
    ): String? {
        try {
            val downloadedDirectory = File("${context.cacheDir}", dirName)
            if (!downloadedDirectory.exists()) {
                downloadedDirectory.mkdir()
            }
            val downloadedFile = File("$downloadedDirectory", fileName)
            if (downloadedFile.exists()) {
                logcat("image repo:") { "FILE EXIST ${fileName}" }
                return downloadedFile.absolutePath
            }
            logcat("image repo:") { "FILE NOT EXIST ${url}" }
            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("Unexpected code: $response");
            }
            if (!downloadedDirectory.exists()) {
                downloadedDirectory.mkdir()
            }

            val sink = downloadedFile.sink().buffer()
            response.body.source().let { input ->
                sink.writeAll(input)
                sink.close()
            }
            return downloadedFile.absolutePath
        } catch (e: Exception) {
            throw IOException("Unexpected code: ${e.localizedMessage}");
        }
    }
}