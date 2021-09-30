package com.example.safepickup.Activity

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.safepickup.Interface.API
import com.example.safepickup.Model.BasicRespond
import com.example.safepickup.R
import com.example.safepickup.Utilities
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_setting.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        supportActionBar?.hide()

        et_lastName.setText(intent.getStringExtra("last_name").toString())
        et_firstName.setText(intent.getStringExtra("first_name").toString())
        et_tel.setText(intent.getStringExtra("tel").toString())

        et_oldPassword.addTextChangedListener(mTextWatcher)
        et_newPassword.addTextChangedListener(mTextWatcher)
        et_repassword.addTextChangedListener(mTextWatcher)
        et_lastName.addTextChangedListener(mTextWatcher)
        et_firstName.addTextChangedListener(mTextWatcher)
        et_tel.addTextChangedListener(mTextWatcher)

        iv_PasswordVisibility1.setOnClickListener { viewPassword(iv_PasswordVisibility1) }
        iv_PasswordVisibility2.setOnClickListener { viewPassword(iv_PasswordVisibility2) }
        iv_PasswordVisibility3.setOnClickListener { viewPassword(iv_PasswordVisibility3) }

        btn_update.setOnClickListener {
            updateUserDetail()
        }
    }

    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun afterTextChanged(editable: Editable) {
            checkFieldsForEmptyValues()
        }
    }

    fun isNotValidTelNumber(s: String): Boolean{
        val specialCharactersString = "!@#$%&*()'+,./:;<=>?[]^_`{|}"

        for(specialChar in specialCharactersString){
            if(s.contains(specialChar))
                return true
        }

        if(s == "" || s.length < 11 || s[3] != '-' || (s.indexOf('-') != s.lastIndexOf('-'))){
            return true
        }
        return false
    }

    fun invalid(et: EditText, tv: TextView){
        et.setTextColor(ContextCompat.getColor(this, R.color.red))
        tv.setTextColor(ContextCompat.getColor(this, R.color.red))
    }

    fun valid(et: EditText, tv: TextView){
        et.setTextColor(ContextCompat.getColor(this, R.color.white))
        tv.setTextColor(ContextCompat.getColor(this, R.color.deep_grey))
    }

    fun checkFieldsForEmptyValues() {
        val b = findViewById<View>(R.id.btn_update) as Button
        val oldPassword: String = et_oldPassword?.text.toString()
        val newPassword: String = et_newPassword?.text.toString()
        val rePassword: String = et_repassword?.text.toString()
        val lastName: String = et_lastName?.text.toString()
        val firstName: String = et_firstName?.text.toString()
        val tel: String = et_tel?.text.toString()

        var passwordCheck = true

        if(oldPassword != "" || newPassword != "" || rePassword != ""){
            when {
                newPassword != rePassword -> {
                    invalid(et_newPassword, tv_newPassword)
                    invalid(et_repassword, tv_rePassword)
                    passwordCheck = false
                }
                newPassword == rePassword -> {
                    valid(et_newPassword, tv_newPassword)
                    valid(et_repassword, tv_rePassword)
                    passwordCheck = true
                }
            }

            if(oldPassword == ""){
                invalid(et_oldPassword, tv_oldPassword)
                passwordCheck = false
            }
            else{
                valid(et_oldPassword, tv_oldPassword)
            }

            if(newPassword == ""){
                invalid(et_newPassword, tv_newPassword)
                passwordCheck = false
            }

            if(rePassword == ""){
                invalid(et_repassword, tv_rePassword)
                passwordCheck = false
            }
        }
        else{
            valid(et_oldPassword, tv_oldPassword)
            valid(et_newPassword, tv_newPassword)
            valid(et_repassword, tv_rePassword)
        }

        b.isClickable = (passwordCheck && !(lastName == "" || firstName == "" || isNotValidTelNumber(tel)))

        if(lastName == "") tv_lastName.setTextColor(ContextCompat.getColor(this, R.color.red)) else tv_lastName.setTextColor(ContextCompat.getColor(this, R.color.deep_grey))

        if(firstName == "") tv_firstName.setTextColor(ContextCompat.getColor(this, R.color.red)) else tv_firstName.setTextColor(ContextCompat.getColor(this, R.color.deep_grey))

        if(isNotValidTelNumber(tel)) {
            tv_tel.setTextColor(ContextCompat.getColor(this, R.color.red))
            et_tel.setTextColor(ContextCompat.getColor(this, R.color.red))
        } else {
            tv_tel.setTextColor(ContextCompat.getColor(this, R.color.deep_grey))
            et_tel.setTextColor(ContextCompat.getColor(this, R.color.light_grey))
        }
    }

    fun viewPassword(view: View) {
        when (view.id) {
            R.id.iv_PasswordVisibility1 -> {
                passwordViewAndIconChanger(et_oldPassword, iv_PasswordVisibility1)
            }
            R.id.iv_PasswordVisibility2 -> {
                passwordViewAndIconChanger(et_newPassword, iv_PasswordVisibility2)
            }
            R.id.iv_PasswordVisibility3 -> {
                passwordViewAndIconChanger(et_repassword, iv_PasswordVisibility3)
            }
        }
    }

    private fun passwordViewAndIconChanger(et: EditText, iv: ImageView){
        if(et?.transformationMethod == HideReturnsTransformationMethod.getInstance()){
            et?.transformationMethod = PasswordTransformationMethod.getInstance()
            iv!!.setImageResource(R.drawable.ic_baseline_visibility_off_24)
        }
        else{
            et?.transformationMethod = HideReturnsTransformationMethod.getInstance()
            iv!!.setImageResource(R.drawable.ic_baseline_visibility_24)
        }
    }

    private fun updateUserDetail(){
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
        val progressDialog = ProgressDialog.show(this, "", "Updating user detail. Please wait...", true)

        val call: Call<BasicRespond?>? = service.updateUserDetail(
                Utilities.getSafePref(this, "user_id"),
                Utilities.getSafePref(this, "credential"),
                et_lastName.text.toString(),
                et_firstName.text.toString(),
                et_tel.text.toString(),
                et_oldPassword.text.toString(),
                et_newPassword.text.toString()
        )

        call?.enqueue(object : Callback<BasicRespond?> {
            override fun onResponse(call: Call<BasicRespond?>, response: Response<BasicRespond?>) {
                progressDialog.dismiss()

                val userDetail: BasicRespond? = response.body()

                Log.i("Retrofit", "succss " + userDetail?.message.toString())
                Toast.makeText(this@SettingActivity, userDetail?.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<BasicRespond?>, t: Throwable) {
                progressDialog.dismiss()
                Log.d("Retrofit", t.message.toString())
                Toast.makeText(this@SettingActivity, "Please Try Again " + t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}