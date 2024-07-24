package com.example.chatapp.data

open class Events<out T>(val content:T) {
    var hasBeenHandeled=false
    fun getContentOrNull():T?{
        return if(hasBeenHandeled) null
        else{
            hasBeenHandeled=true
            content
        }
    }


}