package com.example.safepickup.Activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.safepickup.Model.InsertImageRespond
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


class CameraActivity : AppCompatActivity() {
    private val pic_id = 123

    // Define the button and imageview type variable
    lateinit var camera_open_id: ImageView
    lateinit var click_image_id: ImageView
    lateinit var currentPhotoPath: String
    lateinit var iv_confirm_image: ImageView

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        supportActionBar?.hide()

        currentPhotoPath = ""

        checkForCameraPermission()

        camera_open_id = findViewById(R.id.camera_button)
        click_image_id = findViewById(R.id.click_image)
        iv_confirm_image = findViewById(R.id.iv_confirm_image)

        camera_open_id.setOnClickListener { // Create the camera_intent ACTION_IMAGE_CAPTURE
            dispatchTakePictureIntent()
        }

        iv_confirm_image.setOnClickListener {
            val file = File(currentPhotoPath)
            uploadImage(file.name, Uri.fromFile(file))
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
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) === PackageManager.PERMISSION_GRANTED)) {
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

    private fun uploadImage(name: String, fromFile: Uri?) {
        val sharedPreferences: SharedPreferences = getSharedPreferences(getString(R.string.FILE_PREF), Context.MODE_PRIVATE)
        val progressDialog = ProgressDialog.show(this@CameraActivity, "",
                "Loading. Please wait...", true)

        // Pass it like this
        val file = File(currentPhotoPath)
        val requestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)

        // MultipartBody.Part is used to send also the actual file name
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestFile)

        // Add another part within the multipart request
        val user_id: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), sharedPreferences.getString("user_id", "VALUE_MISSING").toString())
        val credential: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), sharedPreferences.getString("credential", "VALUE_MISSING").toString())

        val call: Call<InsertImageRespond?>? = service.insertIamge(user_id, credential, body)

        call?.enqueue(object : Callback<InsertImageRespond?> {
            override fun onResponse(call: Call<InsertImageRespond?>, response: Response<InsertImageRespond?>) {
                progressDialog.dismiss()

                val insertImageRespond: InsertImageRespond? = response.body()

                if (insertImageRespond?.authorized == true) {
                    Log.d("Retrofit", insertImageRespond?.rekogMessage.toString())
                    Log.d("Retrofit", insertImageRespond?.message.toString())

                    if (insertImageRespond?.faceId.toString().isNotEmpty()) {
                        deleteImage(fromFile)
                        startActivity(Utilities.intent_mainActivity(this@CameraActivity))
                        Utilities.setFaceId(this@CameraActivity, insertImageRespond?.faceId.toString())
                        finish()
                    } else {
                        Toast.makeText(this@CameraActivity, "Please try again, " + insertImageRespond?.rekogMessage.toString(), Toast.LENGTH_LONG).show()
                    }
                } else {
                    startActivity(Utilities.logout(this@CameraActivity))
                }
            }

            override fun onFailure(call: Call<InsertImageRespond?>, t: Throwable) {
                progressDialog.dismiss()
                Log.d("Retrofit", t.message!!)
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
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, pic_id)
                }
            }
        }
    }

    override fun onBackPressed() {
        if(currentPhotoPath.isNotEmpty()) {
            val file = File(currentPhotoPath)
            deleteImage(Uri.fromFile(file))
        }
        Utilities.logout(this)
        finishAffinity()
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