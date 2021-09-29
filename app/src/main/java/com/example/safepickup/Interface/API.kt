package com.example.safepickup.Interface

import com.example.safepickup.Model.*
import com.mapbox.api.directions.v5.models.DirectionsResponse
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

    @FormUrlEncoded
    @POST("/fyp_web/API/checkInStudents.php")
    open fun checkInStudent(
            @Field("user_id") user_id: String,
            @Field("credential") credential: String,
            @Field("student_ids[]") student_ids: ArrayList<String>,
            @Field("encrypted_code") encrypted_code: String,
    ): Call<BasicRespond?>?

    @FormUrlEncoded
    @POST("/fyp_web/API/checkAbsentStudents.php")
    open fun checkAbsentStudent(
            @Field("user_id") user_id: String,
            @Field("credential") credential: String,
            @Field("student_ids[]") student_ids: ArrayList<String>
    ): Call<BasicRespond?>?

    @Multipart
    @POST("/fyp_web/API/requestPickUpStudents.php")
    fun requestPickUpStudents(@Part("user_id") id: RequestBody,
                              @Part("credential") credential: RequestBody,
                              @Part("face_id") face_id: RequestBody,
                              @Part("student_ids[]") student_ids: ArrayList<String>,
                              @Part image: MultipartBody.Part): Call<RequestPickUpStudentsRespond?>?

    @FormUrlEncoded
    @POST("/fyp_web/API/getOrganizationAddress.php")
    open fun fetchOrganizationAddress(
            @Field("user_id") user_id: String,
            @Field("credential") credential: String
    ): Call<FetchOrganizationAddressRespond?>?

    @FormUrlEncoded
    @POST("directions/v5/mapbox/driving?access_token=pk.eyJ1IjoieXBsZW9uZyIsImEiOiJja2dxY2dlN2Iwdno1MnBvaTVpa3QybGM0In0.QDijj62XUB8z7IVHrzK6Kw")
    open fun mapboxDirectionAPI(
            @Field("coordinates") coordinates: String
    ): Call<DirectionsResponse?>?
}