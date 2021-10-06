package com.example.safepickup.AdapterData

class AttendanceData {
    var date: String
    var status: String
    var check_in_time: String
    var check_out_time: String
    var pick_up_by: String
    var guardian_internal_id: String
    var request_time: String

    constructor(date: String, status: String, check_in_time: String, check_out_time: String, pick_up_by: String, guardian_internal_id: String, request_time: String){
        this.date = date
        this.status = status
        this.check_in_time = check_in_time
        this.check_out_time = check_out_time
        this.pick_up_by = pick_up_by
        this.guardian_internal_id = guardian_internal_id
        this.request_time = request_time
    }
}