package ru.greus.binance.nft.mysterybot.network

import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpResponseStatus
import net.lightbody.bmp.filters.RequestFilter
import okhttp3.*
import okhttp3.Request
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.http2.StreamResetException
import okhttp3.logging.HttpLoggingInterceptor
import okio.GzipSource
import okio.IOException
import okio.buffer
import org.json.JSONException
import org.json.JSONObject
import ru.greus.binance.nft.mysterybot.Utils
import ru.greus.binance.nft.mysterybot.config.ConfigurationService
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class WebClient(private val isWaitForHeaders: Boolean = false, isLoggingEnabled: Boolean = false) {
    companion object {
        private val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    }
    private val client = if (isLoggingEnabled) OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor { message -> println("OkHttp $message") }
            .apply { level = HttpLoggingInterceptor.Level.BODY })
        .build() else OkHttpClient()
    private val calls = object : CopyOnWriteArrayList<Call>() {
        override fun remove(element: Call): Boolean {
            element.cancel()
            val result = super.remove(element)
            if (isEmpty() && result) {
                client.dispatcher.executorService.shutdown()
            }
            return result
        }
    }
    private var headers: Headers? = null
    val headersListener = RequestFilter { request, _, _ ->
        if (request?.uri()!!.contains("/bapi/nft/v1/private/nft/nft-trade/product-onsale")) {
            println("Received template request.")
            //Use DefaultFullHttpResponse
            val httpResponse = DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK)
            //Close the connection so it doesn't pass through
            httpResponse.headers().add("CONNECTION", "Close")
            val requestHeaders = request.headers()

            headers = Headers.Builder()
                .add("Host", "www.binance.com")
                .add("Accept", "*/*",)
                .add("Accept-Language", "en-US,en;q=0.5")
                .add("Accept-Encoding", "gzip, deflate, br")
                .add("clienttype", "web")
                .add("x-nft-checkbot-token", requestHeaders!!.get("x-nft-checkbot-token"))
                .add("x-nft-checkbot-sitekey", requestHeaders.get("x-nft-checkbot-sitekey"))
                .add("x-trace-id", requestHeaders.get("x-trace-id"))
                .add("x-ui-request-trace", requestHeaders.get("x-ui-request-trace"))
                .add("content-type", "application/json")
                .add("cookie", requestHeaders.get("Cookie"))
                .add("csrftoken", requestHeaders.get("csrftoken"))
                .add("device-info", requestHeaders.get("device-info"))
                .add("user-agent", requestHeaders.get("User-agent"))
                .build()

            return@RequestFilter httpResponse;
        }
        null
    }

    fun syncTimeWithServer(url: String, retries: Int = 1): Boolean {
        println("Getting time from server...")
        var retry = 1
        val request = Request.Builder().url(url).get().build()

        while (retry <= retries) {
            try {
                val responseBodyString = client.newCall(request).execute().body!!.string()
                val body = JSONObject(responseBodyString)
                if (body.has("serverTime")) {
                    val serverTime = body.getLong("serverTime")
                    val localTime = Date().time
                    val timeDelta = localTime - serverTime
                    println("Local time: $localTime Server time: $serverTime Delta: $timeDelta")
                    Utils.timeDelta = timeDelta
                    return true
                }
            } catch (ioe: IOException) {
                retry++
            }
        }
        println("Unable to load time from server. Use local time.")
        return false
    }

    fun send(url: String, body: String = "", requestsCount: Int = 1) {
        repeat(requestsCount) {
            run(it + 1, buildCall(url, body))
        }
    }

    private fun buildCall(url: String, body: String): Call {
        if (isWaitForHeaders)
            // Waiting request with captcha headers
            Utils.waitUntilNull(::headers, ConfigurationService.connection.captchaHeadersTimeout)
        else
            headers = Headers.Builder().build()
        val postBody = body.toRequestBody(JSON)
        return client.newCall(Request.Builder()
            .url(url)
            .headers(headers!!)
            .post(postBody)
            .build())
    }

    private fun run(id: Int, call: Call) {
        calls.add(call)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                println("Request #$id ends with error: ${e.message}")
                calls.remove(call)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body!!
                    val responseBodyString = if ("gzip" == response.headers["Content-Encoding"]) {
                        GzipSource(responseBody.source()).buffer().readString(Charsets.UTF_8)
                    } else {
                        responseBody.string()
                    }
                    val success = try {
                        val body = JSONObject(responseBodyString)
                        println("Request #$id ends with: $body")
                        if (body.has("success")) body.getBoolean("success") else false
                    } catch (ignored: JSONException) {
                        println("Request #$id ends with: $responseBodyString")
                        false
                    }
                    if (success) {
                        calls.forEach { c -> calls.remove(c) } // Do not use this ArrayList.clear()
                    } else {
                        calls.remove(call)
                    }
                } catch (e: StreamResetException) {
                } finally {
                    response.close()
                }
            }
        })
    }
}