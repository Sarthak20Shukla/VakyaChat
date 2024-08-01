package com.example.chatapp.data
data class UserData(
    var userId: String?="",
    var name: String?="",
    var number: String?="",
    var imageUrl: Any? ="",
){
    fun toMap()= mapOf(
        "userId" to userId,
        "name" to name,
        "number" to number,
        "imageUrl" to imageUrl
    )
}
