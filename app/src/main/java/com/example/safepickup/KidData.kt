package com.example.safepickup

class KidData(private var name: String, private var status: String): Comparable<KidData>{
    override fun compareTo(other: KidData): Int = this.name.compareTo(other.name)

    fun getName(): String{
        return this.name
    }

    fun setName(name: String){
        this.name = name
    }

    fun getStatus(): String{
        return this.status
    }

    fun setStatus(status: String){
        this.status = status
    }
}