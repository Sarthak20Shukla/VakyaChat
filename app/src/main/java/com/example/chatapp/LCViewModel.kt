package com.example.chatapp

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chatapp.data.Events
import com.example.chatapp.data.USER_NODE
import com.example.chatapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db:FirebaseFirestore,
    val storage: FirebaseStorage
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

    @SuppressLint("SuspiciousIndentation")
    fun signUp(name:String, number : String, email:String, password:String, context: Context){
    inProcess.value=true
        if(name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()){
            handleException(customMessage = "Fill out all fields")
            Toast.makeText(context,"Please fill all the  fields!!!",Toast.LENGTH_SHORT).show()

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
                            Toast.makeText(context,"Sign up Successful!!!",Toast.LENGTH_SHORT).show()

                        }
                        else{
                            handleException(it.exception, customMessage = "Sign up failed!!!")
                            Toast.makeText(context,"Sign up failed!!!",Toast.LENGTH_SHORT).show()

                        }
                    }
            } else {
                handleException(customMessage = "Number already exist")
                inProcess.value=false
                Toast.makeText(context,"Number already exist",Toast.LENGTH_SHORT).show()

            }
        }


    }
    fun logIn( email:String,password: String,context: Context){
        if(email.isEmpty() or password.isEmpty()){
            handleException( customMessage = "Please fill all the  fields")
            Toast.makeText(context,"Please fill all the  fields!!!",Toast.LENGTH_SHORT).show()

            return
        } else{
            inProcess.value=true
            auth .signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        signin.value=true
                        inProcess.value=false
                        auth.currentUser?.uid?.let {
                            getUserData(it)

                        }
                        Toast.makeText(context,"Login Successful, Welcome to Vakyachat app",Toast.LENGTH_SHORT).show()

                    } else {
                        handleException(it.exception, customMessage = "Sign in Failed!!!")
                        Toast.makeText(context,"Credentials not correct!!!",Toast.LENGTH_SHORT).show()

                    }
                }
        }
    }
     fun createOrUpdateProfile(
         name: String?=null, number: String?=null,
         imageurl: String? = null) {
         var uid =auth.currentUser?.uid
         val userData=UserData(
             userId = uid,
             name =name?: userData.value?.name,
             number = number?:userData.value?.number,
             imageUrl =imageurl?:userData.value?.imageUrl

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

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri){
            createOrUpdateProfile(imageurl = it.toString())

        }
    }

     fun uploadImage(uri:Uri,onSuccess:(Uri)->Unit) {
         inProcess.value = true
         val storageRef = storage.reference
         val uuid = UUID.randomUUID()
         val imageRef = storageRef.child("image/$uuid")
         val uploadTask = imageRef.putFile(uri)
         uploadTask.addOnSuccessListener {
             val result = it.metadata?.reference?.downloadUrl
             result?.addOnSuccessListener(onSuccess)
             inProcess.value = false
         }
             .addOnFailureListener {
                 handleException(it)
             }

    }
}
