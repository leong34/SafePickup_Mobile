package com.example.safepickup.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.safepickup.R

class NoticeAdapter(private val noticeData: Array<NoticeData?>?) : RecyclerView.Adapter<NoticeAdapter.ViewHolder?>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView?
        var textView: TextView?
        var cardView: CardView?

        init {
            imageView = itemView.findViewById<ImageView?>(R.id.imageView_notice)
            textView = itemView.findViewById<TextView?>(R.id.text_notice_description)
            cardView = itemView.findViewById(R.id.card_notice)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val noticeItem = layoutInflater.inflate(R.layout.notice_item, parent, false)
        return ViewHolder(noticeItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myNoticeData = noticeData?.get(position)
        holder.textView?.setText(noticeData?.get(position)?.getName())
        //holder.imageView.setImageResource(noticeData[position].getImage());
        holder.cardView?.setOnClickListener(View.OnClickListener { v -> Toast.makeText(v.context, "clicked $position", Toast.LENGTH_SHORT).show() })
    }

    override fun getItemCount(): Int {
        return noticeData?.size!!
    }

}