package com.example.safepickup.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.safepickup.R
import kotlinx.android.synthetic.main.activity_event_detail.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class EventDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        supportActionBar?.hide()

        init()
    }

    private fun init(){
        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat: DateFormat = SimpleDateFormat("d-M-yyyy")
        val inputDate = intent.getStringExtra("date")
        val date: Date = inputFormat.parse(inputDate)
        val outputDate: String = outputFormat.format(date)

        tv_className.text           = intent.getStringExtra("class_name")
        tv_eventTitle.text          = intent.getStringExtra("title")
        tv_heldOn.text              = outputDate
        tv_eventDescription.text    = intent.getStringExtra("description")
    }
}