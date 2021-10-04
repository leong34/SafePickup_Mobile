package com.example.safepickup.AdapterData

class EventData {
    var date: String
    var description: String
    var title: String
    var class_id: String
    var class_name: String

    constructor(date: String, description: String, title: String, class_id: String, class_name: String){
        this.date = date
        this.description = description
        this.title = title
        this.class_id = class_id
        this.class_name = class_name
    }
}