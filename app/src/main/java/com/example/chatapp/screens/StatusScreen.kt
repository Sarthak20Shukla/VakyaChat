package com.example.chatapp.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatapp.CommonRow
import com.example.chatapp.DestinationScreens
import com.example.chatapp.LCViewModel
import com.example.chatapp.TitleText
import com.example.chatapp.navigateTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusScreen(navController: NavController, vm: LCViewModel) {
    val inProgress = vm.inProgressStatus.value
    val context = LocalContext.current
    if (inProgress) {
        /* Column(
             modifier = Modifier
                 .padding(20.dp)
                 .fillMaxWidth()
                 .fillMaxSize(),
             horizontalAlignment = Alignment.CenterHorizontally,
             verticalArrangement = Arrangement.Center
         )
         {

             CircularProgressIndicator(
                 modifier = Modifier.size(50.dp),
                 color = Color.Magenta,
                 strokeWidth = 5.dp
             )
         }*/
    } else {
        val status = vm.status.value
        val userData = vm.userData.value
        val mystatus = status.filter {
            it.user.userId == userData?.userId
        }
        val otherstatus = status.filter {
            it.user.userId != userData?.userId
        }
        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent() ){
            uri ->
            uri?.let {
                vm.uploadStatus(uri)
            }
        }

        Scaffold(
            floatingActionButton =
            {
                FAB {
                   launcher.launch("image/*")
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .background(Color(android.graphics.Color.rgb(248, 248, 248)))
                ) {
                    TitleText(txt = "Status")
                    if (status.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center

                        ) {
                            Text(text = "No Status Available")
                        }
                    } else {
                        if (mystatus.isNotEmpty()) {
                            CommonRow(
                                imageUrl = mystatus[0].user.imageUrl,
                                name = mystatus[0].user.name
                            ) {
                                navigateTo(
                                    navController,
                                    DestinationScreens.SingleStatus.createRoute(mystatus[0].user.userId!!)
                                )
                            }
                            val uniqueUsers = otherstatus.map { it.user }.toSet().toList()
                            LazyColumn(modifier = Modifier.weight(1f)) {
                                items(uniqueUsers) { user ->
                                    CommonRow(imageUrl = user.imageUrl, name = user.name) {
                                        navigateTo(
                                            navController = navController,
                                            DestinationScreens.SingleStatus.createRoute(user.userId!!)
                                        )
                                    }


                                }
                            }

                        }
                    }

                    BottomNavigationMenu(
                        selectedItem = BottomNavigationMenu.STATUSLIST,
                        navController = navController
                    )

                }
            }
        )

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAB(
    onFabClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = { onFabClick.invoke() },
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 40.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Edit,
            contentDescription = "Add Status",
            tint = Color.White
        )
    }


}