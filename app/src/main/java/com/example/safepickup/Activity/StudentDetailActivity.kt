package com.example.safepickup.Activity

import android.app.ProgressDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.applandeo.materialcalendarview.EventDay
import com.example.safepickup.AdapterData.EventData
import com.example.safepickup.Interface.API
import com.example.safepickup.Model.FetchEventRespond
import com.example.safepickup.R
import com.example.safepickup.Utilities
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.activity_student_detail.calendarView
import kotlinx.android.synthetic.main.activity_student_detail.recycler_main
import kotlinx.android.synthetic.main.activity_student_detail.tv_date
import kotlinx.android.synthetic.main.activity_student_detail.tv_emptyEvent
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

class StudentDetailActivity : AppCompatActivity() {
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

        if(true) {

            recycler_main.visibility = View.VISIBLE
            tv_emptyEvent.visibility = View.GONE
        }
        else{
            recycler_main.visibility = View.GONE
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
        val progressDialog = ProgressDialog.show(this@StudentDetailActivity, "", "Loading Event. Please wait...", true)
        val call: Call<FetchEventRespond?>? = service.fetchStudentAttendance(Utilities.getSafePref(this, "user_id"), Utilities.getSafePref(this, "credential"), intent.getStringExtra("user_id").toString())

        call?.enqueue(object : Callback<FetchEventRespond?> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<FetchEventRespond?>, response: Response<FetchEventRespond?>) {
                progressDialog.dismiss()

                val eventRespond: FetchEventRespond? = response.body()
                val eventListFromRespond = eventRespond?.event
                val events: MutableList<EventDay> = ArrayList()

//                for (event in eventListFromRespond!!) {
//                    val details = event.details
//                    var eventDataList: ArrayList<EventData> = ArrayList()
//
//                    if (details.isEmpty()) {
//                        continue
//                    }
//
//                    for (detail in details) {
//                        val eventData = EventData(detail.date, detail.description, detail.title, event.classId, event.className)
//                        eventDataList.add(eventData)
//
//                        val date = LocalDate.parse(detail.date, DateTimeFormatter.ISO_DATE)
//                        val calendar: Calendar = Calendar.getInstance()
//                        calendar.set(date.year, date.monthValue - 1, date.dayOfMonth)
//                        events.add(EventDay(calendar, R.drawable.ic_circle_solid))
//
//                        if(event.classId !in class_ids) class_ids[event.classId] = event.className
//
//                        if (detail.date in eventItemsBasedOnDate) {
//                            eventItemsBasedOnDate[detail.date]?.add(eventData)
//                        } else {
//                            val temp:ArrayList<EventData> = ArrayList()
//                            temp.add(eventData)
//                            eventItemsBasedOnDate[detail.date] = temp
//                        }
//                    }
//                }

//                calendarView.setEvents(events)
                showSelectedDateEvent("")

                Log.i("Retrofit", "succss " + eventRespond?.message.toString())
                Toast.makeText(this@StudentDetailActivity, eventRespond?.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<FetchEventRespond?>, t: Throwable) {
                progressDialog.dismiss()
                Log.d("Retrofit", t.message.toString())
                Toast.makeText(this@StudentDetailActivity, "Please Try Again " + t.message.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }
}