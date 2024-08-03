package com.example.chatapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatapp.CommonImage
import com.example.chatapp.LCViewModel


@Composable
fun SingleChatScreen(navController: NavController, vm: LCViewModel, chatId: String?) {

    var reply by rememberSaveable {
        mutableStateOf("")
    }
    val onSendReply = {
        if (chatId != null) {
            vm.onSendReply(chatId, reply)
        }
        reply = ""
    }
    val myuser=vm.userData.value
    val currentChat=vm.chats.value.first { it.chatId == chatId }
    val chatUser= if(myuser?.userId==currentChat.user1.userId) currentChat.user2 else currentChat.user1
    LaunchedEffect(key1 =Unit) {
        if (chatId != null) {
            vm.populateMessages(chatId)
        }
    }
    BackHandler {
            vm.decopulateMessages()
    }
    Column {
        ChatHeader(name = chatUser.name?: "" , imageUrl =  chatUser.imageUrl?: "") {
            navController.popBackStack()
            vm.decopulateMessages()
        }
        Text(text = vm.chatMessages.value.toString())
        ReplyBox(reply = reply, onReplyChange = { reply = it }, onSendReply = onSendReply)

    }

}

@Composable
fun ChatHeader(
    name: String,
    imageUrl: Any,
    onBackedClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Rounded.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    onBackedClick.invoke()
                }
                .padding(8.dp))
        CommonImage(
            data = imageUrl,
            modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(CircleShape)
        )
        Text(text = name, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyBox(reply: String, onReplyChange: (String) -> Unit, onSendReply: () -> Unit) {

    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(value = reply, onValueChange = onReplyChange, maxLines = 4)
            Button(onClick = onSendReply) {
                Text(text = "Send")

            }

        }

    }

}