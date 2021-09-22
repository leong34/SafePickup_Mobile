package com.example.safepickup.Interface

import com.example.safepickup.Model.CheckCredentialRespond
import com.example.safepickup.Model.LoginRespond
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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

}