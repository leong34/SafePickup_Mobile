package com.example.safepickup.AdapterData

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.safepickup.R


class GuardianAdapter(private val guardianDataList: ArrayList<GuardianData>): RecyclerView.Adapter<GuardianAdapter.ViewHolder?>() {
    var context: Context? = null
    inner class ViewHolder(itemView: View)  : RecyclerView.ViewHolder(itemView){
        var tv_guardianName: TextView?
        var tv_verifiedAt: TextView?
        var iv_guardianNext: ImageView?
        var card_guardian: CardView?
        var linear_student: LinearLayout?
        init {
            tv_guardianName = itemView.findViewById(R.id.tv_guardianName)
            tv_verifiedAt = itemView.findViewById(R.id.tv_verified)
            iv_guardianNext = itemView.findViewById(R.id.iv_guardianNext)
            card_guardian = itemView.findViewById(R.id.card_guardian)
            linear_student = itemView.findViewById(R.id.linear_student)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuardianAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val studentItem = layoutInflater.inflate(R.layout.guardian_item, parent, false)

        context = parent.context

        return ViewHolder(studentItem)
    }

    override fun onBindViewHolder(holder: GuardianAdapter.ViewHolder, position: Int) {
        holder.tv_guardianName?.text = guardianDataList[position].full_name
        for(student in guardianDataList[position].students!!){
            val tv1 = TextView(context)
            tv1.text = student.last_name + " " +student.first_name
            tv1.setTextColor(ContextCompat.getColor(context!!, R.color.deep_grey))
            tv1.setPadding(10,0,0,0)
            holder.linear_student?.addView(tv1)
        }

        if(guardianDataList[position].verified_at == "") holder.tv_verifiedAt?.text = "Unactivated" else holder.tv_verifiedAt?.text = "Activated"

        holder.itemView.setOnClickListener{
            guardianDataList[position].selected = guardianDataList[position].selected != true
            holder.card_guardian?.setCardBackgroundColor(if (guardianDataList[position].selected == true)
                ContextCompat.getColor(context!!, R.color.cyan) else ContextCompat.getColor(context!!, R.color.light_grey))
        }

        holder.iv_guardianNext?.setOnClickListener {
            Toast.makeText(context, "Clicked", Toast.LENGTH_LONG).show()
            if(holder.linear_student?.visibility == View.GONE) {
                holder.iv_guardianNext?.rotation = -90.0f
                holder.linear_student?.visibility = View.VISIBLE
            } else {
                holder.iv_guardianNext?.rotation = 90.0f
                holder.linear_student?.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return guardianDataList.size
    }
}