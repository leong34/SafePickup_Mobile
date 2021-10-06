package com.example.safepickup.AdapterData

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.safepickup.R
import com.example.safepickup.Utilities

class NoticeAdapter(private val noticeData: ArrayList<NoticeData>) : RecyclerView.Adapter<NoticeAdapter.ViewHolder?>() {
    var context: Context? = null
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView?
        var iv_viewed: ImageView?
        var textView: TextView?
        var cardView: CardView?

        init {
            imageView = itemView.findViewById<ImageView?>(R.id.imageView_notice)
            iv_viewed = itemView.findViewById<ImageView?>(R.id.iv_viewed)
            textView = itemView.findViewById<TextView?>(R.id.text_notice_description)
            cardView = itemView.findViewById(R.id.card_notice)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val noticeItem = layoutInflater.inflate(R.layout.notice_item, parent, false)
        context = parent.context
        return ViewHolder(noticeItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView?.setText(noticeData?.get(position)?.title)
        if(noticeData[position].viewed!!) holder.iv_viewed?.visibility = View.GONE else holder.iv_viewed?.visibility = View.VISIBLE
        holder.cardView?.setOnClickListener{
            val intent = Utilities.intent_noticeDetail(context!!)
            intent.putExtra("notice_id", noticeData[position]?.notice_id)
            context!!.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return noticeData?.size!!
    }

}