package com.example.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.screens.ChatListScreen
import com.example.chatapp.screens.LoginScreen
import com.example.chatapp.screens.ProfileScreen
import com.example.chatapp.screens.SignUpScreen
import com.example.chatapp.screens.SingleChatScreen
import com.example.chatapp.screens.SingleStatusScreen
import com.example.chatapp.screens.StatusScreen
import com.example.chatapp.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint


sealed class  DestinationScreens(var route:String){
    object SignUp :DestinationScreens("signup")
    object Login :DestinationScreens("login")
    object Profile :DestinationScreens("profile")
    object ChatList :DestinationScreens("chatList")
    object SingleChat :DestinationScreens("singleChat/{chatId}"){
        fun createRoute(id : String) = "singleChat/$id"
    }
    object StatusList :DestinationScreens("StatusList")
    object SingleStatus :DestinationScreens("singleStatus/{chatId}"){
        fun createRoute(userid : String) = "singleStatus/$userid"
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatAppNavigation()
                }
            }
        }
    }
    @Composable
    fun ChatAppNavigation(){
        val navController= rememberNavController()
        var vm= hiltViewModel<LCViewModel>()
        NavHost(navController = navController, startDestination = DestinationScreens.Login.route){
            composable(DestinationScreens.SignUp.route){
                SignUpScreen(navController,vm)

            }
            composable(DestinationScreens.Login.route){
                 LoginScreen(navController=navController, vm=vm)

            }
            composable(DestinationScreens.ChatList.route){
                ChatListScreen(navController=navController,vm=vm)

            }
            composable(DestinationScreens.SingleChat.route){
                val chatId=it.arguments?.getString("chatId")
                chatId.let{
                    SingleChatScreen(navController = navController, vm =vm, chatId =chatId)
                }
            }
            composable(DestinationScreens.StatusList.route){
                StatusScreen(navController=navController,vm=vm)

            }
            composable(DestinationScreens.SingleStatus.route){
                val userId=it.arguments?.getString("userId")
                userId?.let {
                    SingleStatusScreen(
                        navController = navController,
                        vm = vm,
                        userId = it
                    )
                }
            }

            composable(DestinationScreens.Profile.route){
                ProfileScreen(navController=navController,vm=vm)

            }

            }
        }


    }



