package com.example.librarytest

import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object RequestManager {

    private const val TIMEOUT = 15 * 1000

    fun sendRequest(request: String?): Boolean {
        var inputStream: InputStream? = null
        return try {
            val url = URL(request)
            val connection = url.openConnection() as HttpURLConnection
            connection.readTimeout = TIMEOUT
            connection.connectTimeout = TIMEOUT
            connection.requestMethod = "POST"
            connection.connect()
            inputStream = connection.inputStream
            while (inputStream.read() != -1) {}
            true
        } catch (error: IOException) {
            false
        } finally {
            try {
                inputStream?.close()
            } catch (secondError: IOException) {
                Log.w(RequestManager::class.java.simpleName, secondError)
            }
        }
    }

    fun sendRequestAsync(request: String, handler: RequestHandler) {
        RequestAsyncTask(handler).execute(request)
    }

    interface RequestHandler {
        fun onComplete(success: Boolean)
    }

    private class RequestAsyncTask(private val handler: RequestHandler) : AsyncTask<String, Unit, Boolean>() {

        override fun doInBackground(vararg request: String): Boolean {
            return sendRequest(request[0])
        }

        override fun onPostExecute(result: Boolean) {
            handler.onComplete(result)
        }
    }
}