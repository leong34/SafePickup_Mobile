package com.example.safepickup.Interface

import com.example.safepickup.Model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface API {
    @FormUrlEncoded
    @POST("/fyp_web/API/login.php")
    open fun login(
            @Field("email") email: String,
            @Field("password") password: String,
    ): Call<LoginRespond?>?

    @FormUrlEncoded
    @POST("/fyp_web/API/checkCredential.php")
    open fun checkCredential(
            @Field("user_id") user_id: String,
            @Field("credential") credential: String,
    ): Call<CheckCredentialRespond?>?

    @Multipart
    @POST("/fyp_web/API/insertImage.php")
    fun insertIamge(@Part("user_id") id: RequestBody,
                    @Part("credential") credential: RequestBody,
                    @Part image: MultipartBody.Part): Call<InsertImageRespond?>?

    @FormUrlEncoded
    @POST("/fyp_web/API/fetchNoticesList.php")
    open fun fetchNotices(
            @Field("user_id") user_id: String,
            @Field("credential") credential: String,
    ): Call<FetchNoticesListRespond?>?

    @FormUrlEncoded
    @POST("/fyp_web/API/fetchStudentsList.php")
    open fun fetchStudents(
            @Field("user_id") user_id: String,
            @Field("credential") credential: String,
    ): Call<FetchStudentsListRespond?>?

}