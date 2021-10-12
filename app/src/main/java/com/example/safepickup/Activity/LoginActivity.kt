package com.example.safepickup.Activity

import android.app.ProgressDialog
import android.content.Context
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
import com.example.safepickup.R
import com.example.safepickup.Utilities
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity() {
    val TAG:String = "Login Activity"

    var gson = GsonBuilder()
            .setLenient()
            .create()

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
    lateinit var messagingToken: String

    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun afterTextChanged(editable: Editable) {
            // check Fields For Empty Values
            checkFieldsForEmptyValues()
        }
    }

    fun checkFieldsForEmptyValues() {
        val b = findViewById<View>(R.id.btn_update) as Button
        val s1: String = et_loginEmail.text.toString()
        val s2: String = et_loginPassword.text.toString()
        b.isClickable = !(s1 == "" || s2 == "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        et_loginEmail.addTextChangedListener(mTextWatcher)
        et_loginPassword.addTextChangedListener(mTextWatcher)

        iv_PasswordVisibility?.setOnClickListener{
            if(et_loginPassword.transformationMethod == HideReturnsTransformationMethod.getInstance()){
                et_loginPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                iv_PasswordVisibility.setImageResource(R.drawable.ic_baseline_visibility_off_24)
            }
            else{
                et_loginPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                iv_PasswordVisibility.setImageResource(R.drawable.ic_baseline_visibility_24)
            }
        }

        btn_update.setOnClickListener{
            Log.i(TAG, et_loginEmail.text.toString())
            Log.i(TAG, et_loginPassword.text.toString())
            loggingIn(et_loginEmail.text.toString(), et_loginPassword.text.toString())
        }

        authorized(Utilities.getSafePref(this, "user_id"), Utilities.getSafePref(this, "credential"))
        checkFieldsForEmptyValues()
        getFirebaseToken()
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    fun getFirebaseToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Firebase", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            messagingToken = token
        })
    }

    fun loggingIn(email: String, password: String) {
        val progressDialog = ProgressDialog.show(this@LoginActivity, "",
                "Loading. Please wait...", true)

        val call: Call<LoginRespond?>? = service.login(email, password, messagingToken)

        call?.enqueue(object : Callback<LoginRespond?> {
            override fun onFailure(call: Call<LoginRespond?>, t: Throwable) {
                progressDialog.dismiss()
            }

            override fun onResponse(call: Call<LoginRespond?>, response: Response<LoginRespond?>) {
                progressDialog.dismiss()
                val loginRespond: LoginRespond? = response.body()

                if (loginRespond?.data?.userId?.isEmpty() == false) {
                    Utilities.setSafePref(this@LoginActivity,
                            loginRespond?.data?.userId.toString(),
                            loginRespond?.data?.credential.toString(),
                            loginRespond?.data?.faceId.toString(),
                            loginRespond?.data?.organizationId.toString(),
                            loginRespond?.data?.userType.toString())
                    if (loginRespond?.data?.faceId!!.isEmpty()) {
                        startActivity(Utilities.intent_setupFaceId(this@LoginActivity))
                    } else {
                        startActivity(Utilities.intent_mainActivity(this@LoginActivity))
                    }
                }
                else{
                    Toast.makeText(this@LoginActivity, loginRespond?.message, Toast.LENGTH_LONG).show()
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
            }

            override fun onFailure(call: Call<CheckCredentialRespond?>, t: Throwable) {
                progressDialog.dismiss()
            }
        })
    }
}
