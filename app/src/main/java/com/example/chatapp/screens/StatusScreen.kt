package com.example.chatapp.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.chatapp.LCViewModel

@Composable
fun StatusScreen(navController: NavController, vm: LCViewModel) {

    BottomNavigationMenu(selectedItem = BottomNavigationMenu.STATUSLIST, navController = navController)

}