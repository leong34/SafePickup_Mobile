package com.example.safepickup.ui.dashboard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safepickup.KidAdapter
import com.example.safepickup.KidData
import com.example.safepickup.R

class DashboardFragment : Fragment() {
    private var dashboardViewModel: DashboardViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val text_date = root.findViewById<TextView?>(R.id.text_date)
        val text_time = root.findViewById<TextView?>(R.id.text_time)

//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        text_date?.setText(dashboardViewModel!!.getDate())
        val timeHandler = Handler(Looper.getMainLooper())
        timeHandler.postDelayed(object : Runnable {
            override fun run() {
                text_time?.setText(dashboardViewModel!!.getTime())
                timeHandler.postDelayed(this, 1000)
            }
        }, 10)

//        Notice
//        sampleData
        val sampleNotice = arrayOf<NoticeData?>(
                NoticeData("notice 1"),
                NoticeData("notice 2"),
                NoticeData("notice 3"),
                NoticeData("notice 4"),
                NoticeData("notice 5")
        )
        val noticeRecyclerView: RecyclerView = root.findViewById(R.id.recycler_notice)

//        scroll 1 tab once
//        SnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(noticeRecyclerView);
        val noticeAdapter = NoticeAdapter(sampleNotice)
        noticeRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        noticeRecyclerView.adapter = noticeAdapter

//        KidList
//        sampleData
        val sampleKid = arrayOf<KidData?>(
                KidData("Peter & Jane", "Checked In"),
                KidData("James Bond", "Checked Out"),
                KidData("Jennifer", "Absent"),
                KidData("Bones Fire", "Ready For Pick Up"),
                KidData("1Mohammad Ahmad Ali Akau Dan Mutu", "-"),
                KidData("Peter & Jane", "Checked In"),
                KidData("James Bond", "Checked Out"),
                KidData("Jennifer", "Absent"),
                KidData("Bones Fire", "Ready For Pick Up"),
                KidData("2Mohammad Ahmad Ali Akau Dan Mutu", "-"),
                KidData("Peter & Jane", "Checked In"),
                KidData("James Bond", "Checked Out"),
                KidData("Jennifer", "Absent"),
                KidData("Bones Fire", "Ready For Pick Up"),
                KidData("3Mohammad Ahmad Ali Akau Dan Mutu", "-")
        )
        sampleKid.sort()
        val kidRecyclerView: RecyclerView = root.findViewById(R.id.recycler_kid)
        val kidAdapter = KidAdapter(sampleKid)
        kidRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        kidRecyclerView.adapter = kidAdapter
        return root
    }
}