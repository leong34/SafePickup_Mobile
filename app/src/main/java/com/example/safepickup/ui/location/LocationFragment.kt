package com.example.safepickup.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.safepickup.R

class LocationFragment : Fragment() {
    private var locationViewModel: LocationViewModel? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_location, container, false)
//        val textView = root.findViewById<TextView?>(R.id.text_camera)
//        cameraViewModel?.getText()?.observe(viewLifecycleOwner, Observer { s -> textView?.setText(s) })
        return root
    }
}