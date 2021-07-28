package com.example.safepickup

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class KidAdapter (private val kidDataList: Array<KidData?>?): RecyclerView.Adapter<KidAdapter.ViewHolder?>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var text_kid_name: TextView?
        var text_kid_status: TextView?
        var btn_kid_action: MaterialButton?
        var iv_status: ImageView?
        init {
            text_kid_name = itemView.findViewById(R.id.text_kid_name)
            text_kid_status = itemView.findViewById(R.id.text_kid_status)
            btn_kid_action = itemView.findViewById(R.id.btn_kid_action)
            iv_status = itemView.findViewById(R.id.iv_status)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val kidItem = layoutInflater.inflate(R.layout.kid_item, parent, false)
        return ViewHolder(kidItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text_kid_name?.setText(kidDataList?.get(position)?.getName())
        holder.text_kid_status?.setText(kidDataList?.get(position)?.getStatus())
        Log.d("KIDDATAHOLDER", "" + position + " | " + kidDataList!![position]?.getStatus())
//        kidDataList?.forEachIndexed{
//            index, kidData ->  Log.d("KIDDATALSIT", "" + index +" "+ kidData?.getName() + " | " + kidData?.getStatus())
//        }

        when(kidDataList?.get(position)?.getStatus()){
            "-" -> {
                holder.btn_kid_action?.visibility = View.VISIBLE
                holder.btn_kid_action?.setIconResource(R.drawable.ic_user_times_solid_25dp)
                holder.iv_status?.setImageResource(R.drawable.ic_minus_circle_solid_25)
                Log.d("HOLDERCASE", "case 1")
            }
            "Checked In" -> {
                holder.btn_kid_action?.visibility = View.VISIBLE
                holder.btn_kid_action?.setIconResource(R.drawable.ic_car_solid_25)
                holder.iv_status?.setImageResource(R.drawable.ic_chevron_circle_down_solid_25)
                Log.d("HOLDERCASE", "case 2")
            }
            "Checked Out" -> {
                holder.btn_kid_action?.visibility = View.INVISIBLE
                holder.iv_status?.setImageResource(R.drawable.ic_chevron_circle_right_solid_25)
                Log.d("HOLDERCASE", "case 3")
            }
            "Absent" -> {
                holder.btn_kid_action?.visibility = View.INVISIBLE
                holder.iv_status?.setImageResource(R.drawable.ic_times_circle_solid_25)
                Log.d("HOLDERCASE", "case 4")
            }
            "Ready For Pick Up" -> {
                holder.btn_kid_action?.visibility = View.INVISIBLE
                holder.iv_status?.setImageResource(R.drawable.ic_check_double_solid_25)
                Log.d("HOLDERCASE", "case 5")
            }
        }

        holder.btn_kid_action?.setOnClickListener(View.OnClickListener {
            v -> Toast.makeText(v.context, "clicked $position", Toast.LENGTH_SHORT).show()
        })
    }

    override fun getItemCount(): Int {
        return kidDataList?.size!!
    }

}