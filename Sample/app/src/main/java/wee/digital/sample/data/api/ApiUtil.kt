package wee.digital.sample.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Buffer
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import wee.digital.library.util.Logger
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

object ApiUtil {

    fun initClient(block: (OkHttpClient.Builder.() -> Unit)? = null): OkHttpClient {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
        block?.invoke(client)
        return client.build()
    }

    fun initRetrofit(baseURL: String, block: (OkHttpClient.Builder.() -> Unit)? = null): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(initClient(block))
            .baseUrl(baseURL)
            .build()
    }

    fun <T : Any> initService(
        cls: KClass<T>,
        url: String,
        block: (OkHttpClient.Builder.() -> Unit)? = null
    ): T {
        return initRetrofit(url, block).create(cls.java)
    }

    fun authInterceptor(key: String, value: String): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
            request.addHeader(key, value)
            chain.proceed(request.build())
        }
    }

    val log = Logger("api")

    val debugInterceptor: Interceptor
        get() = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val request: Request = chain.request()

                try {
                    val request = request.newBuilder().build()
                    val response = chain.proceed(request)
                    response.newBuilder().build().apply {
                        log.d("==========================================")
                        val buffer = Buffer()
                        request.body?.writeTo(buffer)
                        val requestBody = buffer.readUtf8()
                        log.d("Request: %s - %s".format(request.method, request.url.toString()))
                        log.d("Headers:")
                        request.headers.forEach { log.d("%s: %s".format(it.first, it.second)) }
                        log.d("Body:\n${requestBody.jsonFormat()}")
                        log.d("Response: %s - %s".format(code, message))
                        val source = body?.source()
                        source?.request(Long.MAX_VALUE)
                        val responseBody = source?.buffer?.clone()?.readUtf8()
                        log.d("Body:\n${responseBody.jsonFormat()}")
                        log.d("==========================================")
                    }
                    return response

                } catch (e: Throwable) {
                    log.d("Throwable:")
                    log.d(e.message ?: e.localizedMessage ?: e.stackTrace.toString())
                    throw e
                }
            }

        }

    private fun String?.jsonFormat(): String? {
        this ?: return null
        return try {
            val obj = JSONObject(this)
            obj.toString(2)
        } catch (ignore: Exception) {
            null
        }
    }

}