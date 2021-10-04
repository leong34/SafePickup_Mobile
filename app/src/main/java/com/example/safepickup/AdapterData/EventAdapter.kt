package com.example.safepickup.AdapterData

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.safepickup.R
import kotlinx.android.synthetic.main.event_item.view.*

class EventAdapter(private val eventDataList: ArrayList<EventData>): RecyclerView.Adapter<EventAdapter.ViewHolder?>() {
    var context: Context? = null
    inner class ViewHolder(itemView: View)  : RecyclerView.ViewHolder(itemView){
        var tv_title: TextView
        var tv_description: TextView
        init {
            tv_title = itemView.findViewById(R.id.tv_title)
            tv_description = itemView.findViewById(R.id.tv_description)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val eventItem = layoutInflater.inflate(R.layout.event_item, parent, false)

        context = parent.context

        return ViewHolder(eventItem)
    }

    override fun onBindViewHolder(holder: EventAdapter.ViewHolder, position: Int) {
        holder.tv_title.text = eventDataList[position].title
        holder.tv_description.text = eventDataList[position].description
    }

    override fun getItemCount(): Int {
        return eventDataList.size
    }
}