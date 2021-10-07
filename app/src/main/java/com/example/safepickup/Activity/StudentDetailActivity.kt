package com.example.safepickup.Activity

import android.app.ProgressDialog
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.applandeo.materialcalendarview.EventDay
import com.example.safepickup.AdapterData.AttendanceData
import com.example.safepickup.Interface.API
import com.example.safepickup.Model.FetchStudentAttendanceRespond
import com.example.safepickup.R
import com.example.safepickup.Utilities
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_student_detail.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class StudentDetailActivity : AppCompatActivity() {
    val attendanceList: ArrayList<AttendanceData> = ArrayList()
    val attendanceItemsBasedOnDate = HashMap<String, AttendanceData>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_detail)
        supportActionBar?.hide()
        init()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init(){
        calendarView.setHeaderColor(R.color.black)

        calendarView.setOnDayClickListener { eventDay ->
            val clickedDayCalendar = eventDay.calendar
            val clickedDate = "" + clickedDayCalendar.get(Calendar.YEAR) + "-" + (clickedDayCalendar.get(Calendar.MONTH) + 1) + "-" + clickedDayCalendar.get(Calendar.DAY_OF_MONTH)
            val dateFormatter = DateTimeFormatter.ofPattern("u-M-d")
            val stringDate = LocalDate.parse(clickedDate, dateFormatter).toString()
            showSelectedDateEvent(stringDate)
        }

        fetchStudentAttendance()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showSelectedDateEvent(selectedDate: String){
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("d-M-yyyy")
        val date = dateFormat.format(calendar!!.time)
        lateinit var dateString: String

        if(selectedDate === "") {
            tv_date.text = date
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = dateFormat.format(calendar!!.time)
            dateString = date
        }
        else{
            val selectedDay = LocalDate.parse(selectedDate, DateTimeFormatter.ISO_DATE)
            tv_date.text = "" + selectedDay.dayOfMonth + "-" + (selectedDay.monthValue)+"-"+selectedDay.year
            dateString = selectedDate
        }
        Log.i("retrofit", "Checking is there any event $selectedDate")

        if(dateString in attendanceItemsBasedOnDate) {
            card_student.visibility = View.VISIBLE
            tv_emptyEvent.visibility = View.GONE

            tv_studentName.text = intent.getStringExtra("student_name")
            tv_attendance.text = attendanceItemsBasedOnDate[dateString]?.status
            tv_checkInTime.text = if(attendanceItemsBasedOnDate[dateString]?.check_in_time!!.isEmpty()) "-" else attendanceItemsBasedOnDate[dateString]?.check_in_time
            tv_checkOutTime.text = if(attendanceItemsBasedOnDate[dateString]?.check_out_time!!.isEmpty()) "-" else attendanceItemsBasedOnDate[dateString]?.check_out_time
            tv_guardianId.text = if(attendanceItemsBasedOnDate[dateString]?.guardian_internal_id!!.isEmpty()) "-" else attendanceItemsBasedOnDate[dateString]?.guardian_internal_id
            tv_guardianName.text = if(attendanceItemsBasedOnDate[dateString]?.pick_up_by!!.isEmpty()) "-" else attendanceItemsBasedOnDate[dateString]?.pick_up_by
            tv_requestTime.text = if(attendanceItemsBasedOnDate[dateString]?.request_time!!.isEmpty()) "-" else attendanceItemsBasedOnDate[dateString]?.request_time
        }
        else{
            card_student.visibility = View.GONE
            tv_emptyEvent.visibility = View.VISIBLE
        }
    }

    private fun fetchStudentAttendance() {
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
        val progressDialog = ProgressDialog.show(this@StudentDetailActivity, "", "Loading Student. Please wait...", true)
        val call: Call<FetchStudentAttendanceRespond?>? = service.fetchStudentAttendance(Utilities.getSafePref(this, "user_id"), Utilities.getSafePref(this, "credential"), intent.getStringExtra("student_id").toString())

        call?.enqueue(object : Callback<FetchStudentAttendanceRespond?> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<FetchStudentAttendanceRespond?>, response: Response<FetchStudentAttendanceRespond?>) {
                progressDialog.dismiss()

                val attendanceRespond: FetchStudentAttendanceRespond? = response.body()

                if (attendanceRespond?.authorized != true) {
                    startActivity(Utilities.logout(this@StudentDetailActivity))
                }

                val attendanceListFromRespond = attendanceRespond?.attendance
                val events: MutableList<EventDay> = ArrayList()

                for (attendance in attendanceListFromRespond!!) {
                    val attendanceData = AttendanceData(attendance.date, attendance.status, attendance.checkInTime, attendance.checkOutTime, attendance.pickUpBy, attendance.pickUpInternalId, attendance.requestTime)
                    attendanceList.add(attendanceData)
                    attendanceItemsBasedOnDate[attendance.date] = attendanceData

                    val date = LocalDate.parse(attendance.date, DateTimeFormatter.ISO_DATE)
                    val calendar: Calendar = Calendar.getInstance()
                    calendar.set(date.year, date.monthValue - 1, date.dayOfMonth)

                    val userIcon = ContextCompat.getDrawable(this@StudentDetailActivity, R.drawable.ic_baseline_person_24dp)
                    lateinit var statusIcon: Drawable

                    when (attendance.status) {
                        "Absent" -> statusIcon = ContextCompat.getDrawable(this@StudentDetailActivity, R.drawable.ic_circle_solid_red)!!
                        "Late" -> statusIcon = ContextCompat.getDrawable(this@StudentDetailActivity, R.drawable.ic_circle_solid_orange)!!
                        "On Time" -> statusIcon = ContextCompat.getDrawable(this@StudentDetailActivity, R.drawable.ic_circle_solid_green)!!
                    }

                    if(attendance.guardianId == Utilities.getSafePref(this@StudentDetailActivity, "user_id")){
                        val horizontalInset = (statusIcon!!.intrinsicWidth - userIcon!!.intrinsicWidth) / 2
                        val finalDrawable = LayerDrawable(arrayOf<Drawable?>(statusIcon, userIcon))
                        finalDrawable.setLayerInset(0, 0, 0, 0, userIcon!!.intrinsicHeight)
                        finalDrawable.setLayerInset(1, horizontalInset, statusIcon!!.intrinsicHeight, horizontalInset, 0)

                        events.add(EventDay(calendar, finalDrawable))
                    }
                    else
                        events.add(EventDay(calendar, statusIcon))
                }

                calendarView.setEvents(events)
                showSelectedDateEvent("")

                Log.i("Retrofit", "succss " + attendanceRespond?.message.toString())
                Toast.makeText(this@StudentDetailActivity, attendanceRespond?.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<FetchStudentAttendanceRespond?>, t: Throwable) {
                progressDialog.dismiss()
                Log.d("Retrofit", t.message.toString())
                Toast.makeText(this@StudentDetailActivity, "Please Try Again " + t.message.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }
}