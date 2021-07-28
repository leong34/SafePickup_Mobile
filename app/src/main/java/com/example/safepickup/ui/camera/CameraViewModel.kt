package com.example.safepickup.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {
    private val mText: MutableLiveData<String?>?
    fun getText(): LiveData<String?>? {
        return mText
    }

    init {
        mText = MutableLiveData()
        mText.setValue("This is camera fragment")
    }
}