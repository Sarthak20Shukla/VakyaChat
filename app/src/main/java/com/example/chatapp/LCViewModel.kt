package com.example.chatapp

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chatapp.data.Events
import com.example.chatapp.data.USER_NODE
import com.example.chatapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db:FirebaseFirestore
) :ViewModel() {

    var inProcess= mutableStateOf(false)
    val eventMutableState = mutableStateOf<Events<String>?>(null)
    var signin= mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    init {
         val currentUser = auth.currentUser
          signin.value=currentUser!=null
          currentUser?.uid?.let {
              getUserData(it)
          }
    }

    fun signUp(name:String, number : String, email:String, password:String){
    inProcess.value=true
        if(name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()){
            handleException(customMessage = "Fill out all fields")
            return
        }
        inProcess.value=true
        db.collection(USER_NODE).whereEqualTo("number",number).get().addOnSuccessListener {
            if(it.isEmpty){
                auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener {

                        if(it.isSuccessful){
                            signin.value=true
                            createOrUpdateProfile(name,number)
                        }
                        else{
                            handleException(it.exception, customMessage = "Sign up failed")
                        }
                    }
            } else {
                handleException(customMessage = "Number already exist")
                inProcess.value=false
            }
        }


    }
    fun logIn( email:String,password: String){

    }
     fun createOrUpdateProfile(name: String?=null , number: String?=null,imageurl:String?=null) {
         var uid =auth.currentUser?.uid
         val userData=UserData(
             userId = uid,
             name=name?: userData.value?.name,
             number = number?:userData.value?.number,
             imageUrl=imageurl?:userData.value?.imageUrl

         )
uid?.let {
    inProcess.value=true
    db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
        if(it.exists()){

        } else {
            db.collection(USER_NODE).document(uid).set(userData)
            inProcess.value=false
            getUserData(uid)
        }

    }.addOnFailureListener{
        handleException(it," Cannot Retrive User ")
    }
}
    }

     fun getUserData(uid :String) {
        inProcess.value=true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if(error !=null){
                handleException(error, " cannot Retrieve User")
            }
            if(value !=null){
                var user=value.toObject<UserData>()
                userData.value=user
                inProcess.value=false
            }
        }
    }

     fun handleException(exception: Exception? =null, customMessage:String=""){
        Log.e("Vakyachat app","Vakya chat exception: ",exception)
        exception?.printStackTrace()
        val errorMsg=exception?.localizedMessage?:""
        val message = if(customMessage.isNullOrEmpty()) errorMsg else customMessage
        eventMutableState.value= Events(message)
        inProcess.value=false
    }
}
