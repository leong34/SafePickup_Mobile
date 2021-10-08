package com.example.safepickup.Activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safepickup.AdapterData.StudentAdapter
import com.example.safepickup.AdapterData.StudentData
import com.example.safepickup.Interface.API
import com.example.safepickup.Model.BasicRespond
import com.example.safepickup.Model.FetchAllEmailRespond
import com.example.safepickup.Model.FetchStudentsListRespond
import com.example.safepickup.R
import com.example.safepickup.Utilities
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_guardians_add.*
import kotlinx.android.synthetic.main.activity_guardians_add.btn_update
import kotlinx.android.synthetic.main.activity_guardians_add.et_firstName
import kotlinx.android.synthetic.main.activity_guardians_add.et_lastName
import kotlinx.android.synthetic.main.activity_guardians_add.et_newPassword
import kotlinx.android.synthetic.main.activity_guardians_add.et_repassword
import kotlinx.android.synthetic.main.activity_guardians_add.et_tel
import kotlinx.android.synthetic.main.activity_guardians_add.iv_PasswordVisibility2
import kotlinx.android.synthetic.main.activity_guardians_add.iv_PasswordVisibility3
import kotlinx.android.synthetic.main.activity_guardians_add.tv_firstName
import kotlinx.android.synthetic.main.activity_guardians_add.tv_lastName
import kotlinx.android.synthetic.main.activity_guardians_add.tv_newPassword
import kotlinx.android.synthetic.main.activity_guardians_add.tv_rePassword
import kotlinx.android.synthetic.main.activity_guardians_add.tv_tel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GuardianAddActivity : AppCompatActivity() {
    val studentList: ArrayList<StudentData> = ArrayList()
    val studentAdapter = StudentAdapter(studentList, false)
    var emailList: ArrayList<String> = ArrayList()
    val student_ids:ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardians_add)
        supportActionBar?.hide()

        et_email.addTextChangedListener(mTextWatcher)
        et_newPassword.addTextChangedListener(mTextWatcher)
        et_repassword.addTextChangedListener(mTextWatcher)
        et_lastName.addTextChangedListener(mTextWatcher)
        et_firstName.addTextChangedListener(mTextWatcher)
        et_tel.addTextChangedListener(mTextWatcher)

        iv_PasswordVisibility2.setOnClickListener { viewPassword(iv_PasswordVisibility2) }
        iv_PasswordVisibility3.setOnClickListener { viewPassword(iv_PasswordVisibility3) }

        btn_update.setOnClickListener {
            student_ids.clear()
            for (student in studentList){
                if(student.selected == true) {
                    student_ids.add(student.student_id!!)
                }
            }

            if(student_ids.size <= 0){
                Toast.makeText(this, "Please make sure at least 1 student is chosen", Toast.LENGTH_LONG).show()
            }
            else{
                addFamilyMember(
                        et_lastName.text.toString(),
                        et_firstName.text.toString(),
                        et_email.text.toString(),
                        et_tel.text.toString(),
                        et_newPassword.text.toString(),
                        student_ids
                )
            }
        }

        fetchStudentsList()
        fetchAllEmailAddress()
        checkFieldsForEmptyValues()
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

    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun checkFieldsForEmptyValues() {
        val b = findViewById<View>(R.id.btn_update) as Button
        val email:String  = et_email?.text.toString()
        val newPassword: String = et_newPassword?.text.toString()
        val rePassword: String = et_repassword?.text.toString()
        val lastName: String = et_lastName?.text.toString()
        val firstName: String = et_firstName?.text.toString()
        val tel: String = et_tel?.text.toString()

        var passwordCheck = true
        var isInvalidEmail = true

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

        if(newPassword == ""){
            invalid(et_newPassword, tv_newPassword)
            passwordCheck = false
        }

        if(rePassword == ""){
            invalid(et_repassword, tv_rePassword)
            passwordCheck = false
        }

        if(email== "" || !email.isValidEmail()){
            isInvalidEmail = true
            invalid(et_email, tv_email)
        }
        else{
            if(emailList.contains(email)){
                isInvalidEmail = true
                invalid(et_email, tv_email)
            }
            else{
                isInvalidEmail = false
                valid(et_email, tv_email)
            }
        }

        b.isClickable = (passwordCheck && !(lastName == "" || firstName == "" || isNotValidTelNumber(tel) || isInvalidEmail))

//        if(email == "") tv_email.setTextColor(ContextCompat.getColor(this, R.color.red)) else tv_email.setTextColor(ContextCompat.getColor(this, R.color.deep_grey))

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

    private fun fetchStudentsList() {
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
        val progressDialog = ProgressDialog.show(this@GuardianAddActivity, "", "Loading Students. Please wait...", true)
        val call: Call<FetchStudentsListRespond?>? = service.fetchStudents(Utilities.getSafePref(this,"user_id"), Utilities.getSafePref(this,"credential"))

        call?.enqueue(object : Callback<FetchStudentsListRespond?> {
            override fun onResponse(call: Call<FetchStudentsListRespond?>, response: Response<FetchStudentsListRespond?>) {
                progressDialog.dismiss()

                val studentsListRespond: FetchStudentsListRespond? = response.body()

                if(studentsListRespond?.authorized != true){
                    startActivity(Utilities.logout(this@GuardianAddActivity))
                }

                val studentListFromRespond = studentsListRespond?.students

                studentList.clear()

                for (student in studentListFromRespond!!) {
                    studentList.add(StudentData(student.firstName, student.lastName, student.studentId, student.age.toInt(), student.gender, student.classId, student.className, student.attendance))
                }
                studentList.sort()

                recycler_student.layoutManager = LinearLayoutManager(this@GuardianAddActivity, RecyclerView.VERTICAL, false)
                recycler_student.adapter = studentAdapter
                recycler_student.isNestedScrollingEnabled = false
            }

            override fun onFailure(call: Call<FetchStudentsListRespond?>, t: Throwable) {
                progressDialog.dismiss()
            }

        })
    }

    private fun fetchAllEmailAddress(){
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
        val progressDialog = ProgressDialog.show(this@GuardianAddActivity, "", "Please wait...", true)
        val call: Call<FetchAllEmailRespond?>? = service.getAllUserEmail(Utilities.getSafePref(this,"user_id"), Utilities.getSafePref(this,"credential"))

        call?.enqueue(object : Callback<FetchAllEmailRespond?> {
            override fun onResponse(call: Call<FetchAllEmailRespond?>, response: Response<FetchAllEmailRespond?>) {
                progressDialog.dismiss()

                val emailRespond: FetchAllEmailRespond? = response.body()
                if(emailRespond?.authorized != true){
                    startActivity(Utilities.logout(this@GuardianAddActivity))
                }

                emailList = emailRespond?.userEmails as ArrayList<String>
            }

            override fun onFailure(call: Call<FetchAllEmailRespond?>, t: Throwable) {
                progressDialog.dismiss()
            }

        })
    }

    private fun addFamilyMember(lastName: String, firstName: String, email:String, tel: String, password: String, student_ids: ArrayList<String>){
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
        val progressDialog = ProgressDialog.show(this@GuardianAddActivity, "", "Adding new guardian. Please wait...", true)
        val call: Call<BasicRespond?>? = service.addFamilyMember(
                Utilities.getSafePref(this,"user_id"),
                Utilities.getSafePref(this,"credential"),
                lastName,
                firstName,
                email,
                tel,
                password,
                student_ids
        )

        call?.enqueue(object : Callback<BasicRespond?> {
            override fun onResponse(call: Call<BasicRespond?>, response: Response<BasicRespond?>) {
                progressDialog.dismiss()

                val respond: BasicRespond? = response.body()
                if(respond?.authorized != true){
                    startActivity(Utilities.logout(this@GuardianAddActivity))
                }
                val intent: Intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }

            override fun onFailure(call: Call<BasicRespond?>, t: Throwable) {
                progressDialog.dismiss()
            }

        })
    }
}