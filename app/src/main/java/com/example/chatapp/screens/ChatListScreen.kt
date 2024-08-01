package com.example.chatapp.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.chatapp.LCViewModel

@Composable
fun ChatListScreen(navController: NavController, vm:LCViewModel) {
    Text(text = "Chat List Screen")
    BottomNavigationMenu(selectedItem = BottomNavigationMenu.CHATLIST, navController = navController)
}