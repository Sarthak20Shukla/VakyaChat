package com.example.chatapp.screens

import android.graphics.Color.rgb
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatapp.CommonImage
import com.example.chatapp.LCViewModel
import com.example.chatapp.data.Message


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
    var chatMessages = vm.chatMessages
    val myuser = vm.userData.value
    val currentChat = vm.chats.value.first { it.chatId == chatId }
    val chatUser =
        if (myuser?.userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1
    LaunchedEffect(key1 = Unit) {
        if (chatId != null) {
            vm.populateMessages(chatId)
        }
    }
    BackHandler {
        vm.decopulateMessages()
    }
    Column {
        ChatHeader(name = chatUser.name ?: "", imageUrl = chatUser.imageUrl ?: "") {
            navController.popBackStack()
            vm.decopulateMessages()
        }
        MessageBox(
            modifier = Modifier.weight(1f).background(Color(rgb(245,245,245))),
            chatMessages = chatMessages.value,
            currentUserId = myuser?.userId ?: ""
        )

        ReplyBox(reply = reply, onReplyChange = { reply = it }, onSendReply = onSendReply)

    }

}

@Composable
fun MessageBox(modifier: Modifier, chatMessages: List<Message>, currentUserId: String) {
    LazyColumn(modifier = modifier) {
        items(chatMessages) { msg ->
            val alignment = if (msg.sendBy == currentUserId) Alignment.End else Alignment.Start
            val color = if (msg.sendBy == currentUserId) Color.Cyan else Color.Magenta


            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = alignment
            ) {
                Text(
                    text = msg.message ?:"",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color).padding(12.dp),
                    color = Color.White,
                    fontWeight =  FontWeight.Bold

                )
            }
        }
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