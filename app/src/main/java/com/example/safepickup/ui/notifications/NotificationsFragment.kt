package com.example.safepickup.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.safepickup.R

class NotificationsFragment : Fragment() {
    private var notificationsViewModel: NotificationsViewModel? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        val textView = root.findViewById<TextView?>(R.id.text_notifications)
        notificationsViewModel?.getText()?.observe(viewLifecycleOwner, Observer { s -> textView?.setText(s) })
        return root
    }
}