package com.example.safepickup.AdapterData

class NoticeData: Comparable<NoticeData> {
    var title: String?
    var notice_id: String?
    var description: String?
    var updated_at: String?
    var viewed: Boolean?

    constructor(title: String?, notice_id: String?, description: String?, updated_at: String?, viewed: Boolean?) {
        this.title = title
        this.notice_id = notice_id
        this.description = description
        this.updated_at = updated_at
        this.viewed = viewed
    }

    override fun compareTo(other: NoticeData): Int = this.updated_at?.compareTo(other.updated_at!!)!!
}