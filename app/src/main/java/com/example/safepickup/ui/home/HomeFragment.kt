package com.example.safepickup.ui.home

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
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
import com.example.safepickup.AdapterData.StudentAdapter
import com.example.safepickup.AdapterData.StudentData
import com.example.safepickup.Interface.API
import com.example.safepickup.Model.BasicRespond
import com.example.safepickup.Model.FetchNoticesListRespond
import com.example.safepickup.Model.FetchStudentsListRespond
import com.example.safepickup.R
import com.example.safepickup.Utilities
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_dashboard.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private var homeViewModel: HomeViewModel? = null
    val noticeList: ArrayList<NoticeData> = ArrayList()
    val studentList: ArrayList<StudentData> = ArrayList()
    val student_ids:ArrayList<String> = ArrayList()
    var noticeRecyclerView: RecyclerView? = null
    var studentRecyclerView: RecyclerView? = null
    var text_date: TextView? = null
    var text_time: TextView? = null
    val studentAdapter = StudentAdapter(studentList)
    val noticeAdapter = NoticeAdapter(noticeList)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, null)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val timeHandler = Handler(Looper.getMainLooper())
        val helper: SnapHelper = LinearSnapHelper()

        val iv_checkIn: ImageView = view.findViewById(R.id.iv_checkIn)
        val iv_absent: ImageView = view.findViewById(R.id.iv_absent)
        val iv_request: ImageView = view.findViewById(R.id.iv_request)
        val iv_selectDiselectAll: ImageView = view.findViewById(R.id.iv_selectDiselectAll)

        text_date   = view.findViewById(R.id.text_date)
        text_time   = view.findViewById(R.id.text_time)


        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        text_date?.setText(homeViewModel!!.getDate())
        timeHandler.postDelayed(object : Runnable {
            override fun run() {
                text_time?.setText(homeViewModel!!.getTime())
                timeHandler.postDelayed(this, 1000)
            }
        }, 10)

        iv_checkIn.setOnClickListener {
            student_ids.clear()
            for (student in studentList){
                if(student.selected == true && student.attendance == "Undefined") {
                    student_ids.add(student.student_id!!)
                }
            }

            if(student_ids.size <= 0){
                Toast.makeText(context, "Please make sure at least 1 \"Undefined\" student is chosen", Toast.LENGTH_LONG).show()
            }
            else{
                val intent = Utilities.intent_checkIn(requireActivity())
                startActivityForResult(intent, 1)
            }
        }

        iv_absent.setOnClickListener {
            student_ids.clear()
            for (student in studentList){
                if(student.selected == true && student.attendance == "Undefined") {
                    student_ids.add(student.student_id!!)
                }
            }

            if(student_ids.size <= 0){
                Toast.makeText(context, "Please make sure at least 1 \"Undefined\" student is chosen", Toast.LENGTH_LONG).show()
            }
            else {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Are you sure you want to mark all these student as absent?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { dialog, id ->
                            markStudentAbsent(Utilities.getSafePref(requireActivity(), "user_id"), Utilities.getSafePref(requireActivity(), "credential"), student_ids)
                        }
                        .setNegativeButton("No") { dialog, id ->

                        }
                val alert = builder.create()
                alert.show()
            }
        }

        iv_request.setOnClickListener {
            student_ids.clear()
            for (student in studentList){
                if(student.selected == true && student.attendance == "In School") {
                    student_ids.add(student.student_id!!)
                }
            }

            if(student_ids.size <= 0){
                Toast.makeText(context, "Please make sure at least 1 \"In School\" student is chosen", Toast.LENGTH_LONG).show()
            }
            else{
                val intent:Intent = Utilities.intent_faceScan(requireActivity())
                intent.putStringArrayListExtra("student_ids", student_ids)
                startActivityForResult(intent, 2)
            }
        }

        iv_selectDiselectAll.setOnClickListener {
            for(student in studentList){
                student.selected = student.selected !== true
            }
            studentAdapter.selectAll()
        }

        noticeRecyclerView = view.findViewById(R.id.recycler_notice)
        studentRecyclerView = view.findViewById(R.id.recycler_student)

        helper.attachToRecyclerView(noticeRecyclerView)
        reloadList()

        Log.d("firebase", Utilities.getSafePref(requireContext(), "credential") + " | " + Utilities.getSafePref(requireContext(), "user_id") + " | " + Utilities.getSafePref(requireContext(), "face_id"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 1) {
            if (resultCode === RESULT_OK) {
                val encrypted_code: String = data?.getStringExtra("encrypted_code").toString()
                markStudentCheckIn(Utilities.getSafePref(requireActivity(), "user_id"), Utilities.getSafePref(requireActivity(), "credential"), student_ids, encrypted_code)
            }
        }
        else if (requestCode === 2) {
            if (resultCode === RESULT_OK) {
                reloadStudent()
            }
        }
    }

    private fun reloadList(){
        reloadNotice()
        reloadStudent()
    }

    private fun reloadStudent(){
        fetchStudentsList(Utilities.getSafePref(requireActivity(), "user_id"), Utilities.getSafePref(requireActivity(), "credential"))
    }

    private fun reloadNotice(){
        fetchNoticesList(Utilities.getSafePref(requireActivity(), "user_id"), Utilities.getSafePref(requireActivity(), "credential"))
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

                if(noticesListRespond?.authorized != true){
                    startActivity(Utilities.logout(requireContext()))
                }

                noticeList.clear()

                for (notice in noticeListFromRespond!!) {
                    noticeList.add(NoticeData(notice.title, notice.noticeId, notice.description, notice.updatedAt, notice.viewed))
                }

                noticeList.sort()
                noticeList.reverse()

                noticeRecyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                noticeRecyclerView?.adapter = noticeAdapter

                if (noticeList.isEmpty()) {
                    notice_empty_view.visibility = View.VISIBLE
                    noticeRecyclerView?.visibility = View.GONE
                } else {
                    notice_empty_view.visibility = View.GONE
                    noticeRecyclerView?.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<FetchNoticesListRespond?>, t: Throwable) {
                progressDialog.dismiss()
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

                if(studentsListRespond?.authorized != true){
                    startActivity(Utilities.logout(requireContext()))
                }

                studentList.clear()

                for (student in studentListFromRespond!!) {
                    studentList.add(StudentData(student.firstName, student.lastName, student.studentId, student.age.toInt(), student.gender, student.classId, student.className, student.attendance))
                }
                studentList.sort()

                studentRecyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                studentRecyclerView?.adapter = studentAdapter
                studentRecyclerView?.isNestedScrollingEnabled = false

                if (studentList.isEmpty()) {
                    student_empty_view.visibility = View.VISIBLE
                    studentRecyclerView?.visibility = View.GONE
                } else {
                    student_empty_view.visibility = View.GONE
                    studentRecyclerView?.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<FetchStudentsListRespond?>, t: Throwable) {
                progressDialog.dismiss()
            }

        })
    }

    private fun markStudentCheckIn(user_id: String, credential: String, studentList: ArrayList<String>, encrypted_code: String){
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
        val progressDialog = ProgressDialog.show(this.requireActivity(), "", "Marking Student Attendance. Please wait...", true)
        val call: Call<BasicRespond?>? = service.checkInStudent(user_id, credential, studentList, encrypted_code)

        call?.enqueue(object : Callback<BasicRespond?> {
            override fun onResponse(call: Call<BasicRespond?>, response: Response<BasicRespond?>) {
                progressDialog.dismiss()

                val studentsListRespond: BasicRespond? = response.body()

                if(studentsListRespond?.authorized != true){
                    startActivity(Utilities.logout(requireContext()))
                }
                reloadStudent()
                Toast.makeText(requireContext(), studentsListRespond?.message, Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<BasicRespond?>, t: Throwable) {
                progressDialog.dismiss()
            }

        })
    }

    private fun markStudentAbsent(user_id: String, credential: String, studentList: ArrayList<String>) {
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
        val progressDialog = ProgressDialog.show(this.requireActivity(), "", "Marking Student As Absent. Please wait...", true)
        val call: Call<BasicRespond?>? = service.checkAbsentStudent(user_id, credential, studentList)

        call?.enqueue(object : Callback<BasicRespond?> {
            override fun onResponse(call: Call<BasicRespond?>, response: Response<BasicRespond?>) {
                progressDialog.dismiss()

                val studentsListRespond: BasicRespond? = response.body()
                if(studentsListRespond?.authorized != true){
                    startActivity(Utilities.logout(requireContext()))
                }
                reloadStudent()
            }

            override fun onFailure(call: Call<BasicRespond?>, t: Throwable) {
                progressDialog.dismiss()
            }

        })
    }
}