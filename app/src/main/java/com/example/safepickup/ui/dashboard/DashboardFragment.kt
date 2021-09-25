package com.example.safepickup.ui.dashboard

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.safepickup.AdapterData.NoticeAdapter
import com.example.safepickup.AdapterData.NoticeData
import com.example.safepickup.Interface.API
import com.example.safepickup.AdapterData.StudentAdapter
import com.example.safepickup.AdapterData.StudentData
import com.example.safepickup.Model.FetchNoticesListRespond
import com.example.safepickup.Model.FetchStudentsListRespond
import com.example.safepickup.R
import com.example.safepickup.Utilities
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class DashboardFragment : Fragment() {
    private var dashboardViewModel: DashboardViewModel? = null
    val noticeList: ArrayList<NoticeData> = ArrayList()
    val studentList: ArrayList<StudentData> = ArrayList()
    var noticeRecyclerView: RecyclerView? = null
    var studentRecyclerView: RecyclerView? = null
    var text_date: TextView? = null
    var text_time: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, null)
        val timeHandler = Handler(Looper.getMainLooper())

        val iv_checkIn: ImageView = root.findViewById(R.id.iv_checkIn)
        val iv_absent: ImageView = root.findViewById(R.id.iv_absent)
        val iv_request: ImageView = root.findViewById(R.id.iv_request)

        text_date   = root.findViewById(R.id.text_date)
        text_time   = root.findViewById(R.id.text_time)


        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        text_date?.setText(dashboardViewModel!!.getDate())
        timeHandler.postDelayed(object : Runnable {
            override fun run() {
                text_time?.setText(dashboardViewModel!!.getTime())
                timeHandler.postDelayed(this, 1000)
            }
        }, 10)

        iv_checkIn.setOnClickListener {
            Toast.makeText(context, "Clicked checkin", Toast.LENGTH_LONG).show()
        }

        iv_absent.setOnClickListener {
            Toast.makeText(context, "Clicked absent", Toast.LENGTH_LONG).show()
        }

        iv_request.setOnClickListener {
            Toast.makeText(context, "Clicked request", Toast.LENGTH_LONG).show()
        }

        noticeRecyclerView = root.findViewById(R.id.recycler_notice)
        fetchNoticesList(Utilities.getSafePref(requireActivity(), "user_id"), Utilities.getSafePref(requireActivity(), "credential"))

        studentRecyclerView = root.findViewById(R.id.recycler_student)
        fetchStudentsList(Utilities.getSafePref(requireActivity(), "user_id"), Utilities.getSafePref(requireActivity(), "credential"))

        return root
    }

    private fun fetchNoticesList(user_id: String, credential: String) {
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
        val progressDialog = ProgressDialog.show(this.requireActivity(), "", "Loading Notices. Please wait...", true)
        val call: Call<FetchNoticesListRespond?>? = service.fetchNotices(user_id, credential)

        call?.enqueue(object : Callback<FetchNoticesListRespond?> {
            override fun onResponse(call: Call<FetchNoticesListRespond?>, response: Response<FetchNoticesListRespond?>) {
                progressDialog.dismiss()

                val noticesListRespond: FetchNoticesListRespond? = response.body()
                val noticeListFromRespond = noticesListRespond?.notices

                noticeList.clear()

                for (notice in noticeListFromRespond!!) {
                    noticeList.add(NoticeData(notice.title, notice.noticeId, notice.description, notice.updatedAt))
                }

                val helper: SnapHelper = LinearSnapHelper()
                val noticeAdapter = NoticeAdapter(noticeList)

                helper.attachToRecyclerView(noticeRecyclerView)
                noticeRecyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                noticeRecyclerView?.adapter = noticeAdapter

                Log.i("Retrofit", "succss " + noticesListRespond?.message.toString())
                Toast.makeText(requireActivity(), noticesListRespond?.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<FetchNoticesListRespond?>, t: Throwable) {
                progressDialog.dismiss()
                Log.d("Retrofit", t.message.toString())
                Toast.makeText(requireActivity(), "Please Try Again " + t.message.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun fetchStudentsList(user_id: String, credential: String) {
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
        val progressDialog = ProgressDialog.show(this.requireActivity(), "", "Loading Students. Please wait...", true)
        val call: Call<FetchStudentsListRespond?>? = service.fetchStudents(user_id, credential)

        call?.enqueue(object : Callback<FetchStudentsListRespond?> {
            override fun onResponse(call: Call<FetchStudentsListRespond?>, response: Response<FetchStudentsListRespond?>) {
                progressDialog.dismiss()

                val studentsListRespond: FetchStudentsListRespond? = response.body()
                val studentListFromRespond = studentsListRespond?.students

                studentList.clear()

                for (student in studentListFromRespond!!) {
                    studentList.add(StudentData(student.firstName, student.lastName, student.studentId, student.age.toInt(), student.gender, student.classId, student.className, student.attendance))
                }
                studentList.sort()

                val studentAdapter = StudentAdapter(studentList)
                studentRecyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                studentRecyclerView?.adapter = studentAdapter

                Log.i("Retrofit", "succss " + studentsListRespond?.message.toString())
                Toast.makeText(requireActivity(), studentsListRespond?.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<FetchStudentsListRespond?>, t: Throwable) {
                progressDialog.dismiss()
                Log.d("Retrofit", t.message.toString())
                Toast.makeText(requireActivity(), "Please Try Again " + t.message.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun checkInStudent(user_id: String, credential: String, studentList: ArrayList<StudentData>){
        
    }
}