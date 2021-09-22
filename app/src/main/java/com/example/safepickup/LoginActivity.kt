package com.example.safepickup

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.safepickup.Interface.API
import com.example.safepickup.Model.CheckCredentialRespond
import com.example.safepickup.Model.LoginRespond
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {
    val TAG:String = "Login Activity"
    lateinit var et_loginEmail:EditText
    lateinit var et_loginPassword:EditText
    lateinit var iv_PasswordVisibility: ImageView
    lateinit var btn_login:Button
    val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.7")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val service = retrofit.create(API::class.java)

    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun afterTextChanged(editable: Editable) {
            // check Fields For Empty Values
            checkFieldsForEmptyValues()
        }
    }

    fun checkFieldsForEmptyValues() {
        val b = findViewById<View>(R.id.btn_login) as Button
        val s1: String = et_loginEmail?.text.toString()
        val s2: String = et_loginPassword?.text.toString()
        b.isClickable = !(s1 == "" || s2 == "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        val sharedPreferences: SharedPreferences = getSharedPreferences(getString(R.string.FILE_PREF), Context.MODE_PRIVATE)

        et_loginEmail = findViewById(R.id.et_loginEmail)
        et_loginPassword = findViewById(R.id.et_loginPassword)
        iv_PasswordVisibility = findViewById(R.id.iv_PasswordVisibility)
        btn_login = findViewById(R.id.btn_login)

        et_loginEmail.addTextChangedListener(mTextWatcher)
        et_loginPassword.addTextChangedListener(mTextWatcher)

        iv_PasswordVisibility?.setOnClickListener{
            if(et_loginPassword?.transformationMethod == HideReturnsTransformationMethod.getInstance()){
                et_loginPassword?.transformationMethod = PasswordTransformationMethod.getInstance()
                iv_PasswordVisibility!!.setImageResource(R.drawable.ic_baseline_visibility_off_24)
            }
            else{
                et_loginPassword?.transformationMethod = HideReturnsTransformationMethod.getInstance()
                iv_PasswordVisibility!!.setImageResource(R.drawable.ic_baseline_visibility_24)
            }
        }

        btn_login?.setOnClickListener{
            Log.i(TAG, et_loginEmail?.text.toString())
            Log.i(TAG, et_loginPassword?.text.toString())
            urlEncoded(et_loginEmail?.text.toString(), et_loginPassword?.text.toString())
        }

        authorized(sharedPreferences.getString("user_id", "VALUE_MISSING").toString(), sharedPreferences.getString("credential", "VALUE_MISSING").toString())
        checkFieldsForEmptyValues()
    }

    fun urlEncoded(email: String, password: String) {
        val progressDialog = ProgressDialog.show(this@LoginActivity, "",
                "Loading. Please wait...", true)

        val call: Call<LoginRespond?>? = service.login(email, password)

        call?.enqueue(object : Callback<LoginRespond?> {
            override fun onFailure(call: Call<LoginRespond?>, t: Throwable) {
                progressDialog.dismiss()
                Log.d("Retrofit", t.message.toString())
                Toast.makeText(applicationContext, "Please Try Again " + t.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<LoginRespond?>, response: Response<LoginRespond?>) {
                progressDialog.dismiss()
                val loginRespond: LoginRespond? = response.body()

                Log.i("Retrofit", "succss " + loginRespond?.message.toString())
                Toast.makeText(applicationContext, loginRespond?.message.toString(), Toast.LENGTH_SHORT).show()

                if (loginRespond?.data?.userId?.isEmpty() == false) {
                    Utilities.setSafePref(this@LoginActivity, loginRespond?.data?.userId.toString(), loginRespond?.data?.credential.toString())
                    if (loginRespond?.data?.emptyFaceId == true) {
                        startActivity(Utilities.intent_setupFaceId(this@LoginActivity))
                    }
                    else {
                        startActivity(Utilities.intent_mainActivity(this@LoginActivity))
                    }
                }
            }
        })
    }

    fun authorized(user_id: String, credential: String){
        val progressDialog = ProgressDialog.show(this@LoginActivity, "",
                "Loading. Please wait...", true)
        val call: Call<CheckCredentialRespond?>? = service.checkCredential(user_id, credential)

        call?.enqueue(object : Callback<CheckCredentialRespond?> {
            override fun onResponse(call: Call<CheckCredentialRespond?>, response: Response<CheckCredentialRespond?>) {
                progressDialog.dismiss()
                val credentialRespond: CheckCredentialRespond? = response.body()

                if (credentialRespond?.authorized != true) {
                    Utilities.logout(this@LoginActivity)
                } else {
                    if (credentialRespond?.emptyFaceId == true) {
                        startActivity(Utilities.intent_setupFaceId(this@LoginActivity))
                    } else {
                        startActivity(Utilities.intent_mainActivity(this@LoginActivity))
                    }
                }
                Log.i("Retrofit", "succss " + credentialRespond?.message.toString())
                Toast.makeText(applicationContext, credentialRespond?.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<CheckCredentialRespond?>, t: Throwable) {
                progressDialog.dismiss()
                Log.d("Retrofit", t.message.toString())
                Toast.makeText(applicationContext, "Please Try Again " + t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}
