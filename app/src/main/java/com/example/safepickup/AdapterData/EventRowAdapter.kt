package com.example.safepickup.AdapterData

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safepickup.R
import kotlinx.android.synthetic.main.activity_event.*

class EventRowAdapter(private val eventRowData: ArrayList<EventRowData>): RecyclerView.Adapter<EventRowAdapter.ViewHolder?>() {
    var context: Context? = null
    inner class ViewHolder(itemView: View)  : RecyclerView.ViewHolder(itemView){
        var tv_class_name: TextView
        var recycler_child: RecyclerView
        init {
            tv_class_name = itemView.findViewById(R.id.tv_class_name)
            recycler_child = itemView.findViewById(R.id.recycler_child)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventRowAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val eventItem = layoutInflater.inflate(R.layout.event_item_row, parent, false)
        context = parent.context
        return ViewHolder(eventItem)
    }

    override fun onBindViewHolder(holder: EventRowAdapter.ViewHolder, position: Int) {
        holder.tv_class_name.text = eventRowData[position].class_name
        val eventAdapter = EventAdapter(eventRowData[position].event_list)
        holder.recycler_child.adapter = eventAdapter
    }

    override fun getItemCount(): Int {
        return eventRowData.size
    }
}