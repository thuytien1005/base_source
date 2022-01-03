package wee.digital.sample.data.api

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import wee.digital.library.app
import wee.digital.library.extension.*
import wee.digital.sample.api.ApiResponse
import wee.digital.sample.api.ApiService
import wee.digital.sample.api.ApiUtil
import java.io.*
import kotlin.reflect.KClass

const val baseUrl = "https://lianbackend-o3ugcqo5sa-uc.a.run.app/"

var isMockNetworkError = false

var apiService = ApiUtil.initService(ApiService::class, baseUrl) {
    addInterceptor(ApiUtil.debugInterceptor)
}

val networkErrorLiveData = SingleLiveData<IOException>()

val httpErrorLiveData = SingleLiveData<Throwable>()

val progressLiveData = MutableLiveData<Boolean>()

fun initAuthService() {
    apiService = ApiUtil.initService(ApiService::class, baseUrl) {
        addInterceptor(ApiUtil.debugInterceptor)
    }
}

fun <T : Any> JsonObject.apiResponse(cls: KClass<T>): ApiResponse<T> {
    return ApiResponse(int("code"), str("message"), obj("result").parse(cls))
}

fun <T : Any> JsonObject.apiResponse(t: T?): ApiResponse<T> {
    return ApiResponse(int("code"), str("message"), t)
}

fun <T : Any> JsonObject.dataList(cls: KClass<Array<T>>): List<T> {
    return obj("result").array("data").parse(cls) ?: listOf()
}

typealias FlowResult<T> = Flow<Result<T>>

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

fun toByteArray(body: ResponseBody?): ByteArray? {
    if (body == null)
        return null
    var input: InputStream? = null
    try {
        input = body.byteStream()
        return input.readBytes()
    } catch (e: Exception) {
        Log.e("saveFile", e.toString())
    } finally {
        input?.close()
    }
    return null
}

