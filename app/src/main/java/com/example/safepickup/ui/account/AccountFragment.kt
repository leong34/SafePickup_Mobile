package com.example.safepickup.ui.account

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safepickup.AdapterData.StudentData
import com.example.safepickup.Interface.API
import com.example.safepickup.Model.FetchStudentsListRespond
import com.example.safepickup.Model.FetchUserDetailRespond
import com.example.safepickup.R
import com.example.safepickup.Utilities
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_account.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AccountFragment : Fragment() {
    private lateinit var guardian_btn: CardView
    private lateinit var setting_btn: CardView
    private lateinit var report_btn: CardView
    private lateinit var signout_btn: CardView

    private lateinit var tv_fullName: TextView
    private lateinit var tv_userId: TextView
    private lateinit var tv_userEmail: TextView
    private lateinit var tv_userTel: TextView

    private lateinit var first_name: String
    private lateinit var last_name: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        guardian_btn    = view.findViewById(R.id.guardians)
        setting_btn     = view.findViewById(R.id.setting)
        report_btn      = view.findViewById(R.id.report)
        signout_btn     = view.findViewById(R.id.sign_out)

        tv_fullName     = view.findViewById(R.id.tv_fullName)
        tv_userId       = view.findViewById(R.id.tv_userId)
        tv_userEmail    = view.findViewById(R.id.tv_userEmail)
        tv_userTel      = view.findViewById(R.id.tv_userTel)

        guardian_btn.setOnClickListener {

        }

        setting_btn.setOnClickListener {
            val i = Utilities.intent_setting(requireContext())
            i.putExtra("first_name", first_name)
            i.putExtra("last_name", last_name)
            i.putExtra("user_inner_id", tv_userId.text)
            i.putExtra("email", tv_userEmail.text)
            i.putExtra("tel", tv_userTel.text)
            startActivity(i)
        }

        report_btn.setOnClickListener {

        }

        signout_btn.setOnClickListener {
            startActivity(Utilities.logout(requireContext()))
        }

        fetchUserDetail()
    }

    fun fetchUserDetail(){
        val gson = GsonBuilder().setLenient().create()
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.7")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
        val service = retrofit.create(API::class.java)
        val progressDialog = ProgressDialog.show(this.requireActivity(), "", "Loading User Details. Please wait...", true)
        val call: Call<FetchUserDetailRespond?>? = service.getUserDetail(Utilities.getSafePref(requireContext(), "user_id"), Utilities.getSafePref(requireContext(), "credential"))

        call?.enqueue(object : Callback<FetchUserDetailRespond?> {
            override fun onResponse(call: Call<FetchUserDetailRespond?>, response: Response<FetchUserDetailRespond?>) {
                progressDialog.dismiss()

                val userDetail: FetchUserDetailRespond? = response.body()

                tv_fullName.text    = "Hi, ${userDetail?.firstName} ${userDetail?.lastName}"
                tv_userId.text      = userDetail?.userInnerId
                tv_userEmail.text   = userDetail?.email
                tv_userTel.text     = userDetail?.telNum

                first_name = userDetail?.firstName.toString()
                last_name  = userDetail?.lastName.toString()

                Log.i("Retrofit", "succss " + userDetail?.message.toString())
                Toast.makeText(requireActivity(), userDetail?.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<FetchUserDetailRespond?>, t: Throwable) {
                progressDialog.dismiss()
                Log.d("Retrofit", t.message.toString())
                Toast.makeText(requireActivity(), "Please Try Again " + t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}