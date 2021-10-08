package com.example.safepickup.AdapterData

import android.app.ActionBar
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.safepickup.R
import com.example.safepickup.Utilities
import kotlinx.android.synthetic.main.event_item.view.*

class EventAdapter(private val eventDataList: ArrayList<EventData>): RecyclerView.Adapter<EventAdapter.ViewHolder?>() {
    var context: Context? = null
    inner class ViewHolder(itemView: View)  : RecyclerView.ViewHolder(itemView){
        var tv_title: TextView
        var linear_studentNames: LinearLayout
        var card_event: CardView
        init {
            tv_title = itemView.findViewById(R.id.tv_title)
            linear_studentNames = itemView.findViewById(R.id.linear_studentNames)
            card_event = itemView.findViewById(R.id.card_event)
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

        for(student in eventDataList[position].studentInClass){
            val params: ActionBar.LayoutParams = ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT)

            val tv = TextView(context!!)
            tv.layoutParams = params
            tv.text = student
            tv.setTextColor(context!!.resources.getColor(R.color.deep_grey))
            tv.setTypeface(tv.typeface, Typeface.BOLD)
            tv.setPadding(10,0,10,0)
            holder.linear_studentNames.addView(tv)
        }

        holder.card_event.setOnClickListener {
            val studentList: ArrayList<String> = ArrayList()
            for(student in eventDataList[position].studentInClass){
                studentList.add(student)
            }

            val intent = Utilities.intent_EventDetail(context!!)
            intent.putExtra("class_name", eventDataList[position].class_name)
            intent.putExtra("date", eventDataList[position].date)
            intent.putStringArrayListExtra("student_list", studentList)
            intent.putExtra("title", eventDataList[position].title)
            intent.putExtra("description", eventDataList[position].description)
            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return eventDataList.size
    }
}