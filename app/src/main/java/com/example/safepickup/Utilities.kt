package com.example.safepickup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import com.example.safepickup.Activity.*

class Utilities {
    companion object {
        @JvmStatic
        fun intent_mainActivity(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }

        @JvmStatic
        fun intent_setupFaceId(context: Context): Intent {
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
        fun intent_EventDetail(context: Context): Intent {
            return Intent(context, EventDetailActivity::class.java)
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
        }
    }
}