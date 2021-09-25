package com.example.safepickup.AdapterData

class NoticeData {
    private var title: String?
    private var notice_id: String?
    private var description: String?
    private var updated_at: String?

    constructor(title: String?, notice_id: String?, description: String?, updated_at: String?) {
        this.title = title
        this.notice_id = notice_id
        this.description = description
        this.updated_at = updated_at
    }

    fun getTitle(): String? {
        return this.title
    }

    fun setTitle(title:String){
        this.title = title
    }

    fun getNotice_id():String?{
        return this.notice_id
    }

    fun setNotice_id(notice_id: String?){
        this.notice_id = notice_id
    }

    fun getDescription(): String? {
        return this.description
    }

    fun setDescription(description: String?){
        this.description = description
    }

    fun getUpdated_at(): String? {
        return this.updated_at
    }

    fun setUpdated_at(updated_at: String?){
        this.updated_at = updated_at
    }



}