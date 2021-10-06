package com.example.safepickup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.example.safepickup.Activity.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging

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
        fun intent_checkIn(context: Context): Intent {
            return Intent(context, CheckInActivity::class.java)
        }

        @JvmStatic
        fun intent_faceScan(context: Context): Intent {
            return Intent(context, FaceScanActivity::class.java)
        }

        @JvmStatic
        fun intent_navigation(context: Context): Intent {
            return Intent(context, NavigationActivity::class.java)
        }

        @JvmStatic
        fun intent_setting(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }

        @JvmStatic
        fun intent_guardian(context: Context): Intent {
            return Intent(context, GuardianAddActivity::class.java)
        }

        @JvmStatic
        fun intent_guardianList(context: Context): Intent {
            return Intent(context, GuardianListActivity::class.java)
        }

        @JvmStatic
        fun intent_Event(context: Context): Intent {
            return Intent(context, EventActivity::class.java)
        }

        @JvmStatic
        fun intent_studentDetail(context: Context): Intent {
            return Intent(context, StudentDetailActivity::class.java)
        }

        @JvmStatic
        fun intent_noticeDetail(context: Context): Intent {
            return Intent(context, NoticeDetailActivity::class.java)
        }

        @JvmStatic
        fun setSafePref(context: Context, user_id: String, credential: String, faceId: String, organizationId: String, user_type: String){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(context.getString(R.string.FILE_PREF), Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("user_id", user_id)
            editor.putString("credential", credential)
            editor.putString("face_id", faceId)
            editor.putString("organization_id", organizationId)
            editor.putString("user_type", user_type)
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

        @JvmStatic
        fun subscribeTopic(context: Context){
//            FirebaseMessaging.getInstance().subscribeToTopic("News");
//            FirebaseMessaging.getInstance().subscribeToTopic("Movies");
//            FirebaseMessaging.getInstance().subscribeToTopic("etc");
            Firebase.messaging.subscribeToTopic("weather")
                    .addOnCompleteListener { task ->
                        var msg = "Success subscribe to student"
                        if (!task.isSuccessful) {
                            msg = "Failed subscribe to student"
                        }
                        Log.d("subscribe", msg)
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
        }

        @JvmStatic
        fun unsubscribeTopic(context: Context){
            Firebase.messaging.unsubscribeFromTopic("weather")
                    .addOnCompleteListener { task ->
                        var msg = "Success unsubscribe to student"
                        if (!task.isSuccessful) {
                            msg = "Failed unsubscribe to student"
                        }
                        Log.d("subscribe", msg)
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
        }
    }
}