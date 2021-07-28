package com.example.safepickup.ui.account

import android.content.Context
import android.widget.SimpleAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.safepickup.R
import java.util.*

class AccountViewModel : ViewModel() {
    private val mText: MutableLiveData<String?>?
    private var listAdapter: SimpleAdapter? = null
    fun getText(): LiveData<String?>? {
        return mText
    }

    fun getListData(baseContext: Context?): SimpleAdapter? {
        val a = arrayOf<String?>("a", "b", "c", "d", "e")
        val b = arrayOf<String?>("1", "2", "3", "4", "5")
        val aList: MutableList<HashMap<String?, String?>?> = ArrayList()
        for (i in a.indices) {
            val hm = HashMap<String?, String?>()
            hm["txt1"] = a[i]
            hm["txt2"] = b[i]
            aList.add(hm)
        }
        val from = arrayOf<String?>("txt1", "txt2")
        val to = intArrayOf(R.id.textView, R.id.textView2)
        listAdapter = SimpleAdapter(baseContext, aList, R.layout.activity_listview, from, to)
        return listAdapter
    }

    init {
        mText = MutableLiveData()
        mText.setValue("This is account fragment")
    }
}