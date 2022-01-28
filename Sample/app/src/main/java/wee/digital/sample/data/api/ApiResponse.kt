package wee.digital.sample.data.api

import androidx.annotation.Nullable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class ApiResponse<T>(
    @SerializedName("code")
    var code: Int,

    @SerializedName("message")
    @Expose
    var message: String? = null,

    @SerializedName("result")
    @Expose
    @Nullable
    var result: T? = null
) {

    companion object {

        fun <T> success(t: T?): ApiResponse<T> {
            return ApiResponse(0, null, t)
        }

        fun <T> failure(code: Int): ApiResponse<T> {
            return ApiResponse(code, null, null)
        }
    }
}

