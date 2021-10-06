package com.example.safepickup.Activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.safepickup.Interface.API
import com.example.safepickup.Model.FetchNoticeDetailRespond
import com.example.safepickup.R
import com.example.safepickup.Utilities
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_notice_detail.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit

class NoticeDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_detail)
        supportActionBar?.hide()

        getNoticeDetail()
    }

    private fun getNoticeDetail() {
        val gson = GsonBuilder().setLenient().create()
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.7")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
        val service = retrofit.create(API::class.java)
        val progressDialog = ProgressDialog.show(this@NoticeDetailActivity, "", "Loading Notice. Please wait...", true)
        val call: Call<FetchNoticeDetailRespond?>? = service.fetchNoticeDetail(Utilities.getSafePref(this, "user_id"), Utilities.getSafePref(this, "credential"), intent.getStringExtra("notice_id").toString())

        call?.enqueue(object : Callback<FetchNoticeDetailRespond?> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<FetchNoticeDetailRespond?>, response: Response<FetchNoticeDetailRespond?>) {
                progressDialog.dismiss()

                val noticeDetail: FetchNoticeDetailRespond? = response.body()
                val dt = Instant.ofEpochSecond(noticeDetail?.lastUpdate!!.toLong())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                tv_noticeTitle.text = noticeDetail?.title
                tv_noticeDescription.text = noticeDetail?.description
                tv_lastUpdate.text = "" + dt.dayOfMonth + "-" + (dt.monthValue)+"-"+dt.year

                Log.i("Retrofit", "succss " + noticeDetail?.message.toString())
                Toast.makeText(this@NoticeDetailActivity, noticeDetail?.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<FetchNoticeDetailRespond?>, t: Throwable) {
                progressDialog.dismiss()
                Log.d("Retrofit", t.message.toString())
                Toast.makeText(this@NoticeDetailActivity, "Please Try Again " + t.message.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}