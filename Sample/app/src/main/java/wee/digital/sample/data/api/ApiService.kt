package wee.digital.sample.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("guest/reg/device")
    suspend fun deviceRegister(@Body body: JsonObject): JsonObject

    @GET("guest/req/jwt")
    suspend fun refreshDeviceToken(@Header(REFRESH_TOKEN) refreshToken: String): JsonObject

    @GET("user/req/jwt")
    suspend fun refreshUserToken(@Header(REFRESH_TOKEN) refreshToken: String): JsonObject

    /**
     *
     */
    @GET("geolocation/all/province")
    suspend fun provinceList(): JsonObject

    @GET("geolocation/all/district")
    suspend fun districtList(): JsonObject

    @GET("geolocation/all/ward")
    suspend fun wardList(): JsonObject

    /**
     *
     */
    @POST("otp/req")
    suspend fun otpRequest(@Body body: JsonObject): JsonObject

    @POST("otp/verify")
    suspend fun otpVerify(@Body body: JsonObject): JsonObject

    /**
     *
     */
    @POST("face/register")
    suspend fun userRegister(@Body body: JsonObject): JsonObject

    @POST("face/verify")
    suspend fun userLogin(@Body body: JsonObject): JsonObject

    @GET("user/info")
    suspend fun userProfile(): JsonObject

    @GET("face/get/avatar")
    suspend fun userAvatar(): JsonObject

    @GET("insurance/policies")
    suspend fun userPolicies(@Query("id") idList: List<String>): JsonObject

    @POST("/user/basic")
    suspend fun userUpdate(@Body body: JsonObject): JsonObject

    /**
     *
     */
    @GET("insurance/type/all")
    suspend fun insuranceGroupList(): JsonObject

    @GET("insurance/product/all")
    suspend fun insuranceChildList(): JsonObject

    @GET("insurance/rider/groups")
    suspend fun insuranceInfo(@Query("productId") id: String): JsonObject

    @POST("insurance/set/policy")
    suspend fun insurancePolicyCreate(@Body body: JsonObject): JsonObject


    /**
     *
     */
    @POST("ocr/image")
    @Multipart
    suspend fun ocrInfo(@Part body: MultipartBody.Part): JsonObject

    /**
     *
     */

    @GET("store/get/promotions")
    suspend fun promotions(): JsonObject

    @GET("store/get/categories")
    suspend fun categories(): JsonObject

    @GET("store/get/product")
    suspend fun productDetail(@Query("productID") id: String): JsonObject

    @GET("store/get/brand/products")
    suspend fun productByBrand(@Query("brandID") id: String): JsonObject

    @GET("store/search/products/name")
    suspend fun productSearch(
        @Query("keySearch") key: String,
        @Query("keySort") keySort: String,
        @Query("isAsc") isAsc: Boolean,
        @Query("valueCursor") valueCursor: String = "",
        @Query("countOfPage") limit: Int = 20
    ): JsonObject

    @GET("store/get/products/specific")
    suspend fun productSort(
        @Query("keySort") orderBy: String? = "date_created", //brand_name
        @Query("isAsc") isAsc: Boolean = true,
        @Query("countOfPage") count: Int = 1000,
        @Query("valueCursor") valueCursor: String? = null,
        @Query("offset") offset: Int? = null,
    ): JsonObject

    @GET("store/get/highlight/categories")
    suspend fun categoryHighlight(): JsonObject

    @GET("store/get/highlight/product")
    suspend fun productSuggest(
        @Query("valueCursor") valueCursor: String,
        @Query("countOfPage") limit: Int = 20
    ): JsonObject

    @GET("store/get/category/products/spec")
    suspend fun productByCategory(
        @Query("cateId") cateId: String,
        @Query("keySort") keySort: String = "price",
        @Query("isAsc") isAsc: Boolean = true,
        @Query("valueCursor") valueCursor: String = "",
        @Query("countOfPage") limit: Int = 20
    ): JsonObject

    @POST("store/set/log/search")
    suspend fun logProductSearch(
        @Body body: JsonObject
    ): JsonObject

    /**
     *      StreamFile
     */

    @GET
    @Streaming
    fun streamFile(@Url url: String): Call<ResponseBody>

    /**
     *      Basket
     */

    @POST("user/add/basket")
    suspend fun addBasket(@Body obj: JsonObject): JsonObject

    @POST("store/checkout/basket")
    suspend fun createOrder(@Body body: JsonObject): JsonObject

    /**
     *      Order
     */

    @GET("store/get/order_detail")
    suspend fun getOrder(@Query("id") id: Long): JsonObject

    @GET("store/get/order_detail/all")
    suspend fun getOrders(
        @Query("offset") offset: Int = 0,
        @Query("countOfPage") limit: Int = 20
    ): JsonObject

    /**
     *      delivery info
     */

    @POST("/store/order_info")
    suspend fun deliveryInfo(@Body body: JsonObject): JsonObject


    companion object {
        const val AUTH_TOKEN = "lian-jwt"
        const val REFRESH_TOKEN = "lian-refesh-jwt"
        const val LANG_CODE = "langCode"
    }
}
