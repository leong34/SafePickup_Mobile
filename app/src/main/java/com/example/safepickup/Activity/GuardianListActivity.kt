package com.example.safepickup.Activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safepickup.AdapterData.GuardianAdapter
import com.example.safepickup.AdapterData.GuardianData
import com.example.safepickup.AdapterData.StudentData
import com.example.safepickup.Interface.API
import com.example.safepickup.Model.BasicRespond
import com.example.safepickup.Model.FetchGuardiansListRespond
import com.example.safepickup.R
import com.example.safepickup.Utilities
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_guardian_list.*
import kotlinx.android.synthetic.main.activity_guardians_add.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GuardianListActivity : AppCompatActivity() {
    val guardianList: ArrayList<GuardianData> = ArrayList()
    val guardianAdapter = GuardianAdapter(guardianList)
    val guardian_ids:ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardian_list)
        supportActionBar?.hide()

        iv_add.setOnClickListener {
            startActivityForResult(Utilities.intent_guardian(this), 1)
        }

        iv_delete.setOnClickListener {
            guardian_ids.clear()
            for (guardian in guardianList){
                if(guardian.selected == true) {
                    guardian_ids.add(guardian.user_id!!)
                }
            }

            if(guardian_ids.size <= 0){
                Toast.makeText(this, "Please make sure at least 1 guardian is chosen", Toast.LENGTH_LONG).show()
            }
            else{
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Are you sure you want to delete these guardians?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { dialog, id ->
                            deleteGuardians(guardian_ids)
                        }
                        .setNegativeButton("No") { dialog, id ->

                        }
                val alert = builder.create()
                alert.show()
            }
        }

        fetchGuardianList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode === 1){
            if(resultCode === RESULT_OK){
                fetchGuardianList()
            }
        }
    }

    private fun fetchGuardianList() {
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
        val progressDialog = ProgressDialog.show(this@GuardianListActivity, "", "Loading Guardians. Please wait...", true)
        val call: Call<FetchGuardiansListRespond?>? = service.fetchGuardiansList(Utilities.getSafePref(this,"user_id"), Utilities.getSafePref(this,"credential"))

        call?.enqueue(object : Callback<FetchGuardiansListRespond?> {
            override fun onResponse(call: Call<FetchGuardiansListRespond?>, response: Response<FetchGuardiansListRespond?>) {
                progressDialog.dismiss()

                val guardiansListRespond: FetchGuardiansListRespond? = response.body()
                val guardiansListFromRespond = guardiansListRespond?.guardians

                guardianList.clear()

                for (guardian in guardiansListFromRespond!!) {
                    val guardianStudents = guardian.students
                    var studentList: ArrayList<StudentData> = ArrayList()
                    for(guardian_student in guardianStudents){
                        studentList.add(StudentData(guardian_student.firstName, guardian_student.lastName, guardian_student.studentId, guardian_student.age.toInt(), guardian_student.gender, guardian_student.classId, guardian_student.className, guardian_student.attendance))
                    }
                    studentList.sort()
                    guardianList.add(GuardianData(guardian.firstName, guardian.lastName, guardian.userInternalId, guardian.userId, guardian.verifiedAt, studentList))
                }
                guardianList.sort()

                recycler_guardianList.layoutManager = LinearLayoutManager(this@GuardianListActivity, RecyclerView.VERTICAL, false)
                recycler_guardianList.adapter = guardianAdapter
                recycler_guardianList.isNestedScrollingEnabled = false

                if(guardianList.isEmpty()){
                    guardian_empty_view.visibility = View.VISIBLE
                    recycler_guardianList.visibility = View.GONE
                }
                else{
                    guardian_empty_view.visibility = View.GONE
                    recycler_guardianList.visibility = View.VISIBLE
                }

                Log.i("Retrofit", "succss " + guardiansListRespond?.message.toString())
                Toast.makeText(this@GuardianListActivity, guardiansListRespond?.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<FetchGuardiansListRespond?>, t: Throwable) {
                progressDialog.dismiss()
                Log.d("Retrofit", t.message.toString())
                Toast.makeText(this@GuardianListActivity, "Please Try Again " + t.message.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun deleteGuardians(guardian_ids: ArrayList<String>) {
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
        val progressDialog = ProgressDialog.show(this@GuardianListActivity, "", "Deleting. Please wait...", true)
        val call: Call<BasicRespond?>? = service.deleteGuardians(Utilities.getSafePref(this,"user_id"), Utilities.getSafePref(this,"credential"), guardian_ids)

        call?.enqueue(object : Callback<BasicRespond?> {
            override fun onResponse(call: Call<BasicRespond?>, response: Response<BasicRespond?>) {
                progressDialog.dismiss()
                val basicRespond: BasicRespond? = response.body()

                Log.i("Retrofit", "succss " + basicRespond?.message.toString())
                Toast.makeText(this@GuardianListActivity, basicRespond?.message.toString(), Toast.LENGTH_SHORT).show()

                fetchGuardianList()
            }

            override fun onFailure(call: Call<BasicRespond?>, t: Throwable) {
                progressDialog.dismiss()
                Log.d("Retrofit", t.message.toString())
                Toast.makeText(this@GuardianListActivity, "Please Try Again " + t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}