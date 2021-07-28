package com.example.safepickup.ui.dashboard

class NoticeData {
    private var name: String?
    private var image: Int

    constructor(name: String?, image: Int) {
        this.name = name
        this.image = image
    }

    constructor(name: String?) {
        this.name = name
        image = 0
    }

    fun getName(): String?  {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getImage(): Int {
        return image
    }

    fun setImage(image: Int) {
        this.image = image
    }
}