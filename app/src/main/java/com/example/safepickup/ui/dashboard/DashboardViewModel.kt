package com.example.safepickup.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel : ViewModel() {
    private var calendar: Calendar? = null
    private var dateFormat: SimpleDateFormat? = null
    private var date: String? = null
    private var time: String? = null

    fun getDate(): String? {
        calendar = Calendar.getInstance()
        dateFormat = SimpleDateFormat("dd-MM-yyyy, EEE")
        date = dateFormat!!.format(calendar!!.getTime())
        return date
    }

    fun getTime(): String? {
        calendar = Calendar.getInstance()
        dateFormat = SimpleDateFormat("HH:mm:ss")
        time = dateFormat!!.format(calendar!!.getTime())
        return time
    }
}