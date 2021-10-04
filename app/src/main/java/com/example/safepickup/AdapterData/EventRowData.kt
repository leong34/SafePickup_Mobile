package com.example.safepickup.AdapterData

class EventRowData: Comparable<EventRowData> {
    var class_name: String
    var event_list: ArrayList<EventData>

    constructor(class_name: String, event_list: ArrayList<EventData>){
        this.class_name = class_name
        this.event_list = event_list
    }

    override fun compareTo(other: EventRowData): Int = this.class_name?.compareTo(other.class_name!!)!!
}