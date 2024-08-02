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
data class  ChatData(
    val chatId:String?="",
    val user1:ChatUser= ChatUser(),
    val user2:ChatUser=ChatUser(),

    )
data class ChatUser(
    var userId: String?="",
    var name: String?="",
    var imageUrl: Any? ="",
    var number: String?="",

    )
