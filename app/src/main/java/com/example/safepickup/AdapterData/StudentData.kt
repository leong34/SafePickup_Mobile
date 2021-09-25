package com.example.safepickup.AdapterData

class StudentData: Comparable<StudentData>{
    var first_name: String?
    var last_name: String?
    var full_name: String?
    var student_id: String?
    var age: Int?
    var gender: String?
    var class_id: String?
    var class_name: String?
    var attendance: String?
    var selected: Boolean?

    constructor(first_name: String?, last_name: String?, student_id: String?, age: Int?, gender: String?, class_id: String?, class_name: String?, attendance: String?){
        this.first_name     = first_name
        this.last_name      = last_name
        this.full_name      = this.last_name + " " + this.first_name
        this.student_id     = student_id
        this.age            = age
        this.gender         = gender
        this.class_id       = class_id
        this.class_name     = class_name
        this.attendance     = attendance
        this.selected       = false
    }

    override fun compareTo(other: StudentData): Int = this.full_name?.compareTo(other.full_name!!)!!
}