package com.example.safepickup.AdapterData

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.safepickup.R
import com.google.android.material.button.MaterialButton

class StudentAdapter(private val studentDataList: ArrayList<StudentData>): RecyclerView.Adapter<StudentAdapter.ViewHolder?>() {
    var context: Context? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var text_kid_name: TextView?
        var text_kid_status: TextView?
        var iv_student_next: ImageView?
        var iv_status: ImageView?
        var card_student: CardView?
        init {
            text_kid_name = itemView.findViewById(R.id.text_kid_name)
            text_kid_status = itemView.findViewById(R.id.text_kid_status)
            iv_student_next = itemView.findViewById(R.id.iv_student_next)
            iv_status = itemView.findViewById(R.id.iv_status)
            card_student = itemView.findViewById(R.id.card_student)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val studentItem = layoutInflater.inflate(R.layout.student_item, parent, false)

        context = parent.context

        return ViewHolder(studentItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text_kid_name?.text = studentDataList.get(position).full_name
        holder.text_kid_status?.text = studentDataList.get(position).attendance
        holder.card_student?.setCardBackgroundColor( if(studentDataList.get(position).selected == true)
            ContextCompat.getColor(context!!, R.color.cyan) else ContextCompat.getColor(context!!, R.color.light_grey))

        holder.itemView.setOnClickListener{
            studentDataList.get(position).selected = studentDataList.get(position).selected != true
            holder.card_student?.setCardBackgroundColor( if(studentDataList.get(position).selected == true)
                ContextCompat.getColor(context!!, R.color.cyan) else ContextCompat.getColor(context!!, R.color.light_grey))
        }

        holder.iv_student_next?.setOnClickListener {
            Toast.makeText(context, "Clicked", Toast.LENGTH_LONG).show()


        }

        when(studentDataList.get(position).attendance){
            "Undefined" -> {
                holder.text_kid_status?.setTextColor(ContextCompat.getColor(context!!, R.color.deep_grey))
            }
            "In School" -> {
                holder.text_kid_status?.setTextColor(ContextCompat.getColor(context!!, R.color.green))
            }
            "Checked Out" -> {
                holder.text_kid_status?.setTextColor(ContextCompat.getColor(context!!, R.color.yellow))
            }
            "Absent" -> {
                holder.text_kid_status?.setTextColor(ContextCompat.getColor(context!!, R.color.red))
            }
            "Requested for pick up" -> {
                holder.text_kid_status?.setTextColor(ContextCompat.getColor(context!!, R.color.orange))
            }
        }
    }

    override fun getItemCount(): Int {
        return studentDataList.size
    }

}