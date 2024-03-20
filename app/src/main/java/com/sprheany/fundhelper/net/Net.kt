package com.sprheany.fundhelper.net

import com.sprheany.fundhelper.net.services.EAST_MONEY_BASE_URL
import okhttp3.Dns
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.Inet6Address
import java.net.InetAddress
import java.net.UnknownHostException

object Net {

    private val client = OkHttpClient.Builder()
        .addInterceptor(BrowserInterceptor())
        .addInterceptor(
            HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BASIC)
        )
//        .eventListenerFactory(LoggingEventListener.Factory())
        .dns(ApiDns())
        .build()

    val instance: Retrofit = Retrofit.Builder()
        .baseUrl(EAST_MONEY_BASE_URL)
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
}

class ApiDns : Dns {
    @Throws(UnknownHostException::class)
    override fun lookup(hostname: String): List<InetAddress> {
        try {
            // IPv4 first
            return InetAddress.getAllByName(hostname).sortedBy {
                Inet6Address::class.java.isInstance(it)
            }
        } catch (e: NullPointerException) {
            throw UnknownHostException("Broken system behaviour for dns lookup of $hostname").apply {
                initCause(e)
            }
        }
    }
}

class BrowserInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
            .addHeader(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0"
            )
        return chain.proceed(builder.build())
    }
}

class MockInterceptor : Interceptor {
    val headers = mapOf(
        "validmark" to
                "aKVEnBbJF9Nip2Wjf4de/fSvA8W3X3iB4L6vT0Y5cxvZbEfEm17udZKUD2qy37dLRY3bzzHLDv+up/Yn3OTo5Q==",
    )

    private val deviceId = "874C427C-7C24-4980-A835-66FD40B67605";
    val version = "6.5.5";
    private val baseData = mapOf(
        "product" to "EFund",
        "deviceid" to deviceId,
        "MobileKey" to deviceId,
        "plat" to "Iphone",
        "PhoneType" to "IOS15.1.0",
        "OSVersion" to "15.5",
        "version" to version,
        "ServerVersion" to version,
        "Version" to version,
        "appVersion" to version,
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val urlBuilder = chain.request().url.newBuilder()
        baseData.forEach { (key, value) ->
            urlBuilder.addQueryParameter(key, value)
        }

        val builder = chain.request().newBuilder()
            .url(urlBuilder.build())
        headers.forEach { (key, value) ->
            builder.addHeader(key, value)
        }
        return chain.proceed(builder.build())
    }
}