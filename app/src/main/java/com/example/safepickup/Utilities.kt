package com.example.safepickup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import com.example.safepickup.Activity.LoginActivity
import com.example.safepickup.Activity.MainActivity
import com.example.safepickup.Camera.CameraActivity

class Utilities {
    companion object {
        @JvmStatic
        fun intent_mainActivity(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }

        @JvmStatic
        fun intent_setupFaceId(context: Context): Intent {
            Toast.makeText(context, "Setup of face id is required for first time user", Toast.LENGTH_SHORT).show()
            return Intent(context, CameraActivity::class.java)
        }

        @JvmStatic
        fun setSafePref(context: Context, user_id: String, credential: String, faceId: String, organizationId: String){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(context.getString(R.string.FILE_PREF), Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("user_id", user_id)
            editor.putString("credential", credential)
            editor.putString("face_id", faceId)
            editor.putString("organization_id", organizationId)
            editor.commit()
        }

        @JvmStatic
        fun getSafePref(context: Context, key: String): String {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(context.getString(R.string.FILE_PREF), Context.MODE_PRIVATE)
            return sharedPreferences.getString(key, "VALUE_MISSING").toString()
        }

        @JvmStatic
        fun setFaceId(context: Context, faceId: String){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(context.getString(R.string.FILE_PREF), Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("face_id", faceId)
            editor.commit()
        }

        @JvmStatic
        fun logout(context: Context): Intent {
            unsetSafePref(context)
            return Intent(context, LoginActivity::class.java)
        }

        @JvmStatic
        fun unsetSafePref(context:Context){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(context.getString(R.string.FILE_PREF), Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.commit()
            Toast.makeText(context, "clear pref", Toast.LENGTH_SHORT).show()
        }
    }
}