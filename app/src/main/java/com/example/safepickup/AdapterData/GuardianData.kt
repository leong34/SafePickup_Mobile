package com.example.safepickup.AdapterData

class GuardianData: Comparable<GuardianData> {
    var first_name: String?
    var last_name: String?
    var full_name: String?
    var guardian_id: String?
    var user_id: String?
    var selected: Boolean?

    constructor(first_name: String?, last_name: String?, guardian_id: String?, user_id: String?){
        this.first_name     = first_name
        this.last_name      = last_name
        this.full_name      = this.last_name + " " + this.first_name
        this.guardian_id    = guardian_id
        this.user_id        = user_id
        this.selected       = false
    }

    override fun compareTo(other: GuardianData): Int = this.full_name?.compareTo(other.full_name!!)!!
}