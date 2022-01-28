package wee.digital.sample.data.api

import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.buffer
import okio.sink
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import wee.digital.library.extension.SingleLiveData
import wee.digital.library.extension.flowResult
import wee.digital.sample.BuildConfig
import wee.digital.sample.app
import java.io.*
import java.net.URI
import java.net.URISyntaxException
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

val progressLiveData = MutableLiveData<Boolean>()

val networkErrorLiveData = SingleLiveData<IOException>()

val httpErrorLiveData = SingleLiveData<Throwable>()

fun initClient(block: (OkHttpClient.Builder.() -> Unit)? = null): OkHttpClient {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
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

fun domainName(url: String): String {
    return try {
        val uri = URI(url)
        val domain = uri.host ?: return ""
        if (domain.startsWith("www.")) domain.substring(4) else domain
    } catch (e: URISyntaxException) {
        ""
    }
}

val loggingInterceptor: Interceptor
    get() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE
        return interceptor
    }

fun authInterceptor(token: String): Interceptor {
    return Interceptor { chain: Interceptor.Chain ->
        val request = chain.request().newBuilder()
        request.addHeader("Authorization", token)
        chain.proceed(request.build())
    }
}

fun writeFile(response: Response<ResponseBody>, fileName: String): Flow<Result<File>> {
    return flowResult {
        val source = response.body()?.source()
            ?: throw NullPointerException("download data is empty")
        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(
                app.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath,
                fileName
            )
        } else {
            @Suppress("DEPRECATION")
            (File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            ))
        }
        file.sink().buffer().apply {
            writeAll(source)
            close()
        }
        return@flowResult file
    }
}

private fun convertBitmapToFile(fileName: String, bitmap: Bitmap): File {
    //create a file to write bitmap data
    val file = File(app.cacheDir, fileName)
    file.createNewFile()

    //Convert bitmap to byte array
    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
    val bitMapData = bos.toByteArray()

    //write the bytes in file
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(file)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    try {
        fos?.write(bitMapData)
        fos?.flush()
        fos?.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return file
}

fun getMultipartBody(key: String, bitmap: Bitmap): MultipartBody.Part {
    val file = convertBitmapToFile("img.jpg", bitmap)
    val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(key, file.name, reqFile)
}
