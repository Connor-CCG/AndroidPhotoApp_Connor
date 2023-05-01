package com.example.androidcameraapplication.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcameraapplication.model.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)

    var loginError: String by mutableStateOf("")
    fun signInWithEmailAndPassword(email: String, password: String, complete: (Boolean) -> Unit){
        viewModelScope.launch{
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{ task ->
                        if(task.isSuccessful){
                            Log.d("FB sign in success", "signInWithEmailAndPassword: ${task.result.toString()}")
                            loginError = ""
                            complete(true)
                        } else {
                            loginError = "Login failed. Try again."
                            complete(false)
                        }
                    }
            } catch(ex: Exception){
                Log.d("FB sign in", "signInWithEmailAndPassword: ${ex.message}")
            }
        }
    }


    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid
        val user = FirebaseUser(
            userId = userId.toString(),
            name = displayName.toString()
        ).toMap()
        FirebaseFirestore.getInstance().collection("users")
            .add(user)
    }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        home: () -> Unit
    ){
        if(_loading.value == false){
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val displayName = task.result?.user?.email?.split("@")?.get(0)
                        createUser(displayName)
                        home()
                    } else {

                    }
                    _loading.value = false
                }
        }
    }
}