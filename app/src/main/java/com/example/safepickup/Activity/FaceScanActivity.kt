package com.example.safepickup.Activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.safepickup.Interface.API
import com.example.safepickup.Model.RequestPickUpStudentsRespond
import com.example.safepickup.R
import com.example.safepickup.Utilities
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class FaceScanActivity : AppCompatActivity() {
    private val pic_id = 111
    lateinit var camera_button: ImageView
    lateinit var click_image_id: ImageView
    lateinit var currentPhotoPath: String
    lateinit var iv_confirm_image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_scan)
        supportActionBar?.hide()

        val intent: Intent = intent
        val student_ids: ArrayList<String> = intent.getStringArrayListExtra("student_ids") as ArrayList<String>

        camera_button       = findViewById(R.id.camera_button)
        click_image_id      = findViewById(R.id.click_image)
        iv_confirm_image    = findViewById(R.id.iv_confirm_image)
        currentPhotoPath    = ""

        camera_button.setOnClickListener { // Create the camera_intent ACTION_IMAGE_CAPTURE
            checkForCameraPermission()
        }

        iv_confirm_image.setOnClickListener {
            val file = File(currentPhotoPath)
            sendRequest(file.name, Uri.fromFile(file), student_ids)
        }
    }

    fun checkForCameraPermission(){
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) !==
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA), 1)
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA), 1)
            }
        }
        else{
            dispatchTakePictureIntent()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.CAMERA) ===
                                    PackageManager.PERMISSION_GRANTED)) {
                    }
                } else {
                    finish()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === pic_id) {
            if(resultCode == Activity.RESULT_OK){
                val file = File(currentPhotoPath)
                click_image_id.setImageURI(Uri.fromFile(file))
                click_image_id.visibility = View.VISIBLE
                iv_confirm_image.visibility = View.VISIBLE
                Log.i("IMG URI", Uri.fromFile(file).toString())

                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                    mediaScanIntent.data = Uri.fromFile(file)
                    sendBroadcast(mediaScanIntent)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(currentPhotoPath.isNotEmpty()) {
            val file = File(currentPhotoPath)
            deleteImage(Uri.fromFile(file))
        }
    }

    private fun sendRequest(name: String, fromFile: Uri?, student_ids: ArrayList<String>) {
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
        val progressDialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true)

        // Pass it like this
        val file = File(currentPhotoPath)
        val requestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)

        // MultipartBody.Part is used to send also the actual file name
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestFile)

        // Add another part within the multipart request
        val user_id: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), Utilities.getSafePref(this, "user_id"))
        val credential: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), Utilities.getSafePref(this, "credential"))
        val face_id: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), Utilities.getSafePref(this, "face_id"))

        val call: Call<RequestPickUpStudentsRespond?>? = service.requestPickUpStudents(user_id, credential, face_id, student_ids, body)

        call?.enqueue(object : Callback<RequestPickUpStudentsRespond?> {
            override fun onResponse(call: Call<RequestPickUpStudentsRespond?>, response: Response<RequestPickUpStudentsRespond?>) {
                progressDialog.dismiss()

                val insertImageRespond: RequestPickUpStudentsRespond? = response.body()

                if (insertImageRespond?.authorized == true) {
                    Log.d("Retrofit", insertImageRespond?.rekogMessage.toString())
                    Log.d("Retrofit", insertImageRespond?.message.toString())

                    if(insertImageRespond.faceIdVerified == true){
                        deleteImage(fromFile)
                        val intent:Intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    }else{
                        Toast.makeText(this@FaceScanActivity, insertImageRespond.rekogMessage, Toast.LENGTH_LONG).show()
                    }
                } else {
                    startActivity(Utilities.logout(this@FaceScanActivity))
                }
            }

            override fun onFailure(call: Call<RequestPickUpStudentsRespond?>, t: Throwable) {
                progressDialog.dismiss()
            }
        })
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.example.android.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, pic_id)
                }
            }
        }
    }

    //new added
    private fun deleteImage(uri: Uri?){
        val fdelete: File = File(uri?.getPath())
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.i("file Deleted :", uri?.getPath().toString())
            } else {
                Log.i("file not Deleted :", uri?.getPath().toString())
            }
        }
    }
}